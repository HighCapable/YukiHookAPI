/*
 * YukiHook - A modern hook API and Xposed module solution.
 * Copyright (C) 2019 HighCapable
 * https://github.com/HighCapable/YukiHook
 *
 * Apache License Version 2.0
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * This file is created by fankes on 2026/9/12.
 */
package com.highcapable.yukihookapi.runtime.xposed82.adapter

import com.highcapable.kavaref.KavaRef.Companion.resolve
import com.highcapable.yukihookapi.runtime.bridge.hooker.ChainHookerBridge
import com.highcapable.yukihookapi.runtime.bridge.hooker.ClassicHookerBridge
import com.highcapable.yukihookapi.runtime.bridge.hooker.HookerBridge
import org.junit.Assert
import org.junit.Test
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Member

/**
 * Exercises the chain contract through classic callback inputs and observable invocation outcomes.
 */
class ChainInvocationAdapterTest {

    private val member = String::class.resolve().firstMethod {
        name = "substring"
        parameters(Int::class)
    }.self

    private fun ClassicHookerBridge.invoke(
        member: Member = this@ChainInvocationAdapterTest.member,
        instance: Any? = "value",
        args: Array<Any?> = arrayOf(1)
    ) = Invocation(member, instance, args).also { beforeHooked(it) }

    @Test
    fun priorityNestsInterceptorsAndForwardsNewReceiverAndArguments() {
        val order = mutableListOf<String>()
        val adapter = ChainInvocationAdapter(member) { instance, args ->
            order += "original"
            (instance as String).substring(args[0] as Int)
        }
        adapter.hookMember(0) { chain ->
            order += "inner-before"
            Assert.assertEquals("other", chain.instance)
            Assert.assertEquals(2, chain.getArg(0))
            val result = chain.proceed()
            order += "inner-after"
            "$result!"
        }
        adapter.hookMember(100) { chain ->
            order += "outer-before"
            val result = chain.proceedWith("other", arrayOf(2))
            Assert.assertEquals("value", chain.instance)
            Assert.assertEquals(listOf(1), chain.args)
            order += "outer-after"
            "<$result>"
        }

        val invocation = adapter.invoke()

        Assert.assertNull(invocation.throwable)
        Assert.assertEquals("<her!>", invocation.result)
        Assert.assertEquals(listOf("outer-before", "inner-before", "original", "inner-after", "outer-after"), order)
    }

    @Test
    fun repeatedProceedRestartsTheRemainingChainWithoutChangingTheCurrentInputs() {
        var downstreamCalls = 0
        val adapter = ChainInvocationAdapter(member) { instance, args -> (instance as String).substring(args[0] as Int) }
        adapter.hookMember(0) { chain ->
            downstreamCalls++
            chain.proceed()
        }
        adapter.hookMember(100) { chain ->
            val otherReceiver = chain.proceedWith("other")
            val otherArguments = chain.proceed(arrayOf(2))
            val originalInputs = chain.proceed()
            "$otherReceiver|$otherArguments|$originalInputs"
        }

        val invocation = adapter.invoke()

        Assert.assertNull(invocation.throwable)
        Assert.assertEquals("ther|lue|alue", invocation.result)
        Assert.assertEquals(3, downstreamCalls)
    }

    @Test
    fun returningNullSkipsBothTheRemainingChainAndTheOriginal() {
        val adapter = ChainInvocationAdapter(member) { _, _ -> error("Original must not run.") }
        adapter.hookMember(0) { error("Remaining chain must not run.") }
        adapter.hookMember(100) { null }

        val invocation = adapter.invoke()

        Assert.assertNull(invocation.throwable)
        Assert.assertNull(invocation.result)
    }

    @Test
    fun outerInterceptorCanRecoverTheUnwrappedOriginalFailure() {
        val failure = IllegalStateException("original")
        val adapter = ChainInvocationAdapter(member) { _, _ -> throw InvocationTargetException(failure) }
        adapter.hookMember(0) { chain ->
            try {
                chain.proceed()
            } catch (throwable: Throwable) {
                Assert.assertSame(failure, throwable)
                "recovered"
            }
        }

        val invocation = adapter.invoke()

        Assert.assertNull(invocation.throwable)
        Assert.assertEquals("recovered", invocation.result)
    }

    @Test
    fun interceptorFailureIsNotUnwrappedOrRecoveredByCallingTheOriginal() {
        val failure = InvocationTargetException(IllegalArgumentException("interceptor"))
        val adapter = ChainInvocationAdapter(member) { _, _ -> error("Original must not run.") }
        adapter.hookMember(0) { throw failure }

        val invocation = adapter.invoke()

        Assert.assertSame(failure, invocation.throwable)
    }

    @Test
    fun failureAfterProceedDoesNotExecuteTheOriginalAgain() {
        val failure = IllegalStateException("after proceed")
        var originalCalls = 0
        val adapter = ChainInvocationAdapter(member) { _, _ ->
            originalCalls++
            "original"
        }
        adapter.hookMember(0) { chain ->
            chain.proceed()
            throw failure
        }

        val invocation = adapter.invoke()

        Assert.assertSame(failure, invocation.throwable)
        Assert.assertEquals(1, originalCalls)
    }

    @Test
    fun registrationChangesDoNotAlterTheChainAlreadyInProgress() {
        val order = mutableListOf<String>()
        val adapter = ChainInvocationAdapter(member) { _, _ ->
            order += "original"
            "original"
        }
        lateinit var removed: HookerBridge
        var firstCall = true
        adapter.hookMember(100) { chain ->
            removed.remove()
            if (firstCall) {
                firstCall = false
                adapter.hookMember(0) { added ->
                    order += "added"
                    added.proceed()
                }
            }
            chain.proceed()
        }
        removed = adapter.hookMember(0) { chain ->
            order += "removed"
            chain.proceed()
        }

        val first = adapter.invoke()
        val second = adapter.invoke()

        Assert.assertNull(first.throwable)
        Assert.assertNull(second.throwable)
        Assert.assertEquals("original", first.result)
        Assert.assertEquals("original", second.result)
        Assert.assertEquals(listOf("removed", "original", "added", "original"), order)
    }

    @Test
    fun duplicateHookersHaveIndependentHandlesAndAnEmptyAdapterLeavesTheCallUntouched() {
        var interceptorCalls = 0
        var originalCalls = 0
        val adapter = ChainInvocationAdapter(member) { _, _ ->
            originalCalls++
            "original"
        }
        val hooker = ChainHookerBridge { chain ->
            interceptorCalls++
            chain.proceed()
        }
        val first = adapter.hookMember(0, hooker)
        val second = adapter.hookMember(0, hooker)

        first.remove()
        first.remove()
        val active = adapter.invoke()
        second.remove()
        val inactive = adapter.invoke()

        Assert.assertNull(active.throwable)
        Assert.assertEquals("original", active.result)
        Assert.assertNull(inactive.throwable)
        Assert.assertNull(inactive.result)
        Assert.assertEquals(1, interceptorCalls)
        Assert.assertEquals(1, originalCalls)
    }

    @Test
    fun voidAndConstructorInvocationsIgnoreInterceptorReturnValues() {
        val members = listOf(
            StringBuffer::class.resolve().firstMethod {
                name = "setLength"
                parameters(Int::class)
            }.self to arrayOf<Any?>(0),
            StringBuffer::class.resolve().firstConstructor { emptyParameters() }.self to emptyArray()
        )
        members.forEach { (member, args) ->
            var originalCalls = 0
            val adapter = ChainInvocationAdapter(member) { _, _ -> originalCalls++; null }
            adapter.hookMember(0) { chain -> chain.proceed(); "ignored downstream" }
            adapter.hookMember(100) { chain ->
                Assert.assertNull(chain.proceed())
                "ignored outer"
            }

            val invocation = adapter.invoke(member, StringBuffer(), args)

            Assert.assertNull(invocation.throwable)
            Assert.assertNull(invocation.result)
            Assert.assertEquals(1, originalCalls)
        }
    }

    private class Invocation(
        override val member: Member,
        override val instance: Any?,
        override val args: Array<Any?>
    ) : ClassicHookerBridge.Param {

        override var result: Any? = null
        override var throwable: Throwable? = null
    }
}