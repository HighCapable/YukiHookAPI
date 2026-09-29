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
 * This file is created by fankes on 2026/9/6.
 */
package com.highcapable.yukihookapi.runtime.libxposed.adapter

import com.highcapable.kavaref.KavaRef.Companion.resolve
import com.highcapable.yukihookapi.runtime.bridge.hooker.ClassicHookerBridge
import org.junit.Assert
import org.junit.Test

/**
 * Behavioral contract of the classic invocation adapter, without requiring a framework process.
 */
class ClassicInvocationAdapterTest {

    private val member = String::class.resolve().firstMethod {
        name = "substring"
        parameters(Int::class)
    }.self

    private fun invocation(vararg args: Any?) = ClassicInvocationAdapter.from(member, "value", arrayOf(*args)) { throw it }

    private fun ClassicInvocationAdapter.intercept(
        before: (ClassicHookerBridge.Param) -> Unit,
        after: (ClassicHookerBridge.Param) -> Unit,
        proceed: (Array<Any?>) -> Any?
    ) = intercept(object : ClassicHookerBridge {
        override fun beforeHooked(param: ClassicHookerBridge.Param) = before(param)
        override fun afterHooked(param: ClassicHookerBridge.Param) = after(param)
    }, proceed)

    @Test
    fun beforeMutatesArgumentsAndAfterSeesResult() {
        val order = mutableListOf<String>()
        val result = invocation(1).intercept(
            before = { order += "before"; it.args[0] = 2 },
            after = { order += "after"; Assert.assertEquals("lue", it.result); it.result = "changed" },
            proceed = { order += "original"; "value".substring(it[0] as Int) }
        )
        Assert.assertEquals("changed", result)
        Assert.assertEquals(listOf("before", "original", "after"), order)
    }

    @Test
    fun nullResultStillShortCircuitsAndRunsAfter() {
        var afterCalled = false
        val result = invocation().intercept(
            before = { it.result = null },
            after = { afterCalled = true; Assert.assertNull(it.result) },
            proceed = { error("Original must not run") }
        )
        Assert.assertNull(result)
        Assert.assertTrue(afterCalled)
    }

    @Test
    fun intentionalThrowableSkipsOriginal() {
        val failure = IllegalArgumentException("intentional")
        val thrown = Assert.assertThrows(IllegalArgumentException::class.java) {
            invocation().intercept({ it.throwable = failure }, { Assert.assertSame(failure, it.throwable) }) { error("must not run") }
        }
        Assert.assertSame(failure, thrown)
    }

    @Test
    fun afterCanRecoverOriginalFailure() {
        val failure = IllegalStateException("original")
        val result = invocation().intercept({}, {
            Assert.assertSame(failure, it.throwable)
            it.result = "recovered"
            Assert.assertNull(it.throwable)
        }) { throw failure }
        Assert.assertEquals("recovered", result)
    }

    @Test
    fun originalFailurePropagatesAfterCallback() {
        val failure = IllegalStateException("original")
        var afterCalled = false
        val thrown = Assert.assertThrows(IllegalStateException::class.java) {
            invocation().intercept({}, { afterCalled = true }) { throw failure }
        }
        Assert.assertSame(failure, thrown)
        Assert.assertTrue(afterCalled)
    }

    @Test
    fun afterCanReplaceResultWithThrowable() {
        val failure = IllegalArgumentException("after")
        Assert.assertSame(failure, Assert.assertThrows(IllegalArgumentException::class.java) {
            invocation().intercept({}, {
                it.throwable = failure
                Assert.assertNull(it.result)
            }) { "original" }
        })
    }

    @Test
    fun clearingThrowableInBeforeStillSkipsOriginal() {
        Assert.assertNull(invocation().intercept({ it.throwable = null }, {}) { error("must not run") })
    }

    @Test
    fun clearingThrowableInAfterReturnsNull() {
        Assert.assertNull(invocation().intercept({}, { it.throwable = null }) { error("original") })
    }

    @Test
    fun nestedCallbacksHaveIndependentStateAndReverseAfterOrder() {
        val order = mutableListOf<String>()
        val result = invocation(1).intercept(
            { order += "outer-before" },
            { order += "outer-after"; Assert.assertEquals("inner", it.result) }
        ) {
            invocation(*it).intercept(
                { order += "inner-before"; it.result = "inner" },
                { order += "inner-after" }
            ) { error("must not run") }
        }
        Assert.assertEquals("inner", result)
        Assert.assertEquals(listOf("outer-before", "inner-before", "inner-after", "outer-after"), order)
    }

    @Test
    fun receiverAndConstructorIdentityArePreserved() {
        val constructor = StringBuilder::class.resolve().firstConstructor { emptyParameters() }.self
        val receiver = StringBuilder()
        val invocation = ClassicInvocationAdapter.from(constructor, receiver, emptyArray()) { throw it }
        Assert.assertNull(invocation.intercept({
            Assert.assertSame(constructor, it.member)
            Assert.assertSame(receiver, it.instance)
        }, {}) { null })
    }

    @Test
    fun failedBeforeRestoresExecutionButKeepsArgumentChanges() {
        val failures = mutableListOf<Throwable>()
        val invocation = ClassicInvocationAdapter.from(member, "value", arrayOf(0)) { failures += it }
        val result = invocation.intercept({
            it.args[0] = 2
            it.result = "discarded"
            error("before failed")
        }, {}) { "value".substring(it[0] as Int) }
        Assert.assertEquals("lue", result)
        Assert.assertEquals(1, failures.size)
    }

    @Test
    fun failedAfterRestoresOriginalResult() {
        val failures = mutableListOf<Throwable>()
        val invocation = ClassicInvocationAdapter.from(member, "value", emptyArray()) { failures += it }
        val result = invocation.intercept({}, {
            it.result = "discarded"
            error("after failed")
        }) { "original" }
        Assert.assertEquals("original", result)
        Assert.assertEquals(1, failures.size)
    }

    @Test
    fun failedAfterRestoresOriginalThrowable() {
        val failure = IllegalArgumentException("original")
        val invocation = ClassicInvocationAdapter.from(member, "value", emptyArray()) {}
        Assert.assertSame(failure, Assert.assertThrows(IllegalArgumentException::class.java) {
            invocation.intercept({}, {
                it.result = "discarded"
                error("after failed")
            }) { throw failure }
        })
    }
}