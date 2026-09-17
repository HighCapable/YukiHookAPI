/*
 * YukiHookAPI - An efficient Hook API and Xposed Module solution built in Kotlin.
 * Copyright (C) 2019 HighCapable
 * https://github.com/HighCapable/YukiHookAPI
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
package com.highcapable.yukihookapi.runtime

import com.highcapable.kavaref.KavaRef.Companion.resolve
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.log.YLog
import com.highcapable.yukihookapi.hook.param.HookChain
import com.highcapable.yukihookapi.hook.param.HookParam
import com.highcapable.yukihookapi.runtime.bridge.hooker.ChainHookerBridge
import com.highcapable.yukihookapi.runtime.bridge.hooker.ClassicHookerBridge
import com.highcapable.yukihookapi.runtime.bridge.hooker.HookPoint
import com.highcapable.yukihookapi.runtime.bridge.hooker.HookerBridge
import com.highcapable.yukihookapi.runtime.libxposed.adapter.ClassicInvocationAdapter
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import java.lang.reflect.Member

/**
 * Exercises the user-facing classic and chain APIs through the runtime registration boundary.
 */
class MemberHookerTest {

    private val scope = object : YukiBaseHooker() {
        override fun onHook() = Unit
    }
    private val method = String::class.resolve().firstMethod {
        name = "substring"
        parameters(Int::class)
    }.self
    private val otherMethod = String::class.resolve().firstMethod {
        name = "length"
        emptyParameters()
    }.self
    private val voidMethod = StringBuilder::class.resolve().firstMethod {
        name = "setLength"
        parameters(Int::class)
    }.self
    private val hookers = mutableListOf<HookerBridge>()
    private val removed = mutableListOf<Member>()
    private val previousHook = YukiHookBridge.hookCallback
    private val previousEnabled = YLog.Configs.isEnable

    @Before
    fun installRuntime() {
        YLog.Configs.isEnable = false
        YukiHookBridge.hookCallback = { target, _, hooker ->
            val member = (target as HookPoint.Executable).value
            hookers += hooker
            object : HookerBridge {
                override fun remove() { removed += member }
            }
        }
    }

    @After
    fun restoreRuntime() {
        YukiHookBridge.hookCallback = previousHook
        YLog.Configs.isEnable = previousEnabled
    }

    @Test
    fun typedAndUntypedProceedOverloadsForwardArgumentsAndReceiver() {
        val chain = RecordingChain(method)
        val arguments = arrayOf<Any?>(2)
        val receiver = "another"
        scope.run {
            method.intercept {
                val invocation: HookChain = this
                Assert.assertSame(method, invocation.member)
                Assert.assertEquals("value", invocation.instance<String>())
                Assert.assertEquals(1, invocation.arg<Int>(0))
                Assert.assertSame(chain.args, args)

                Assert.assertEquals("downstream", proceed())
                Assert.assertEquals("downstream", proceed<String>())
                Assert.assertEquals("downstream", proceed(arguments))
                Assert.assertEquals("downstream", proceed<String>(arguments))
                Assert.assertSame(arguments, chain.forwardedArgs)

                Assert.assertEquals("downstream", proceedWith(receiver))
                Assert.assertEquals("downstream", proceedWith<String>(receiver))
                Assert.assertEquals("downstream", proceedWith(receiver, arguments))
                val result = proceedWith<String>(receiver, arguments)
                Assert.assertSame(receiver, chain.forwardedInstance)
                Assert.assertSame(arguments, chain.forwardedArgs)
                Assert.assertEquals(listOf(1), args)
                result
            }
        }
        Assert.assertEquals("downstream", (hookers.single() as ChainHookerBridge).intercept(chain))
        Assert.assertEquals(8, chain.calls)
    }

    @Test
    fun chainShortCircuitAndFailureDoNotRetryTheRemainingChain() {
        val shortCircuit = RecordingChain(method)
        val failing = RecordingChain(method)
        val failure = IllegalStateException("after proceed")
        var observed: HookChain? = null
        var observedFailure: Throwable? = null
        val registration = scope.run {
            method.intercept { "short-circuit" }
            method.intercept {
                proceed()
                throw failure
            }.onConductFailure { invocation, throwable ->
                observed = invocation
                Assert.assertSame(failure, throwable)
            }.onInvocationFailure {
                observedFailure = it
            }
        }
        Assert.assertEquals("short-circuit", (hookers[0] as ChainHookerBridge).intercept(shortCircuit))
        Assert.assertEquals(0, shortCircuit.calls)
        Assert.assertSame(failure, Assert.assertThrows(IllegalStateException::class.java) {
            (hookers[1] as ChainHookerBridge).intercept(failing)
        })
        Assert.assertEquals(1, failing.calls)
        Assert.assertSame(method, checkNotNull(observed).member)
        Assert.assertSame(failure, observedFailure)

        registration.ignoredInvocationFailure()
        observed = null
        observedFailure = null
        Assert.assertSame(failure, Assert.assertThrows(IllegalStateException::class.java) {
            (hookers[1] as ChainHookerBridge).intercept(failing)
        })
        Assert.assertEquals(2, failing.calls)
        Assert.assertSame(method, checkNotNull(observed).member)
        Assert.assertNull(observedFailure)
    }

    @Test
    fun failedRegistrationDoesNotInterruptLaterMembersAndHookers() {
        val failure = UnsupportedOperationException("unsupported hooker")
        val register = checkNotNull(YukiHookBridge.hookCallback)
        var attempts = 0
        YukiHookBridge.hookCallback = { target, priority, hooker ->
            attempts++
            if (attempts == 1) throw failure
            register(target, priority, hooker)
        }
        scope.run {
            listOf(method, otherMethod).interceptAll {
                removeSelf()
                proceed()
            }
            method.hook { after {} }
        }
        Assert.assertEquals(3, attempts)
        Assert.assertEquals(2, hookers.size)
        val chain = RecordingChain(otherMethod)
        (hookers.first() as ChainHookerBridge).intercept(chain)
        Assert.assertEquals(listOf(otherMethod), removed)
        Assert.assertTrue(hookers.last() is ClassicHookerBridge)
    }

    @Test
    fun classicCallbacksKeepMutableArgumentsResultsAndRemoval() {
        var observed: HookParam? = null
        val registration = scope.run {
            method.hook {
                before {
                    observed = this
                    arg(0).set(2)
                    Assert.assertEquals(2, arg(0).get<Int>())
                }
                after {
                    Assert.assertEquals("lue", result<String>())
                    result = "changed"
                    removeSelf()
                }
            }.onConductFailure { invocation, throwable ->
                observed = invocation
                throw throwable
            }.apply {
                onInvocationFailure { throw it }
            }
        }
        val invocation = ClassicInvocationAdapter.from(method, "value", arrayOf(1)) { throw it }
        Assert.assertEquals("changed", invocation.intercept(hookers.single() as ClassicHookerBridge) {
            "value".substring(it[0] as Int)
        })
        Assert.assertSame(method, checkNotNull(observed).member)
        Assert.assertEquals(listOf(method), removed)
        registration.remove { Assert.assertFalse(it) }
    }

    @Test
    fun hookCanRemoveItselfWhileNativeRegistrationIsCompleting() {
        var removalReported = false
        YukiHookBridge.hookCallback = { target, _, hooker ->
            val member = (target as HookPoint.Executable).value
            ClassicInvocationAdapter.from(member, "value", arrayOf(1)) { throw it }
                .intercept(hooker as ClassicHookerBridge) { "original" }
            object : HookerBridge {
                override fun remove() {
                    removed += member
                }
            }
        }

        scope.run {
            method.hook().before {
                removeSelf { removalReported = it }
            }
        }

        Assert.assertFalse(removalReported)
        Assert.assertEquals(listOf(method), removed)
    }

    @Test
    fun classicInterceptionSkipsOriginalAndNormalizesVoidResult() {
        var callbackInvoked = false
        var valueOriginalInvoked = false
        var voidOriginalInvoked = false
        scope.run {
            method.hook { intercept("changed") }
            voidMethod.hook {
                intercept {
                    callbackInvoked = true
                    "ignored"
                }
            }
        }

        val valueInvocation = ClassicInvocationAdapter.from(method, "value", arrayOf(1)) { throw it }
        val value = valueInvocation.intercept(hookers[0] as ClassicHookerBridge) {
            valueOriginalInvoked = true
            "original"
        }
        val voidInvocation = ClassicInvocationAdapter.from(voidMethod, StringBuilder("value"), arrayOf(1)) { throw it }
        val void = voidInvocation.intercept(hookers[1] as ClassicHookerBridge) {
            voidOriginalInvoked = true
        }

        Assert.assertEquals("changed", value)
        Assert.assertNull(void)
        Assert.assertTrue(callbackInvoked)
        Assert.assertFalse(valueOriginalInvoked)
        Assert.assertFalse(voidOriginalInvoked)
    }

    @Test
    fun resolverBlockHooksDeferRegistrationAndBuilderHooksRemainImmediate() {
        val resolver = String::class.resolve().firstMethod {
            name = "substring"
            parameters(Int::class)
        }
        val resolvers = listOf(resolver, StringBuilder::class.resolve().firstConstructor { emptyParameters() })
        scope.run {
            resolver.hook {
                before {}
                after {}
                Assert.assertTrue(hookers.isEmpty())
            }
            Assert.assertEquals(1, hookers.size)

            resolvers.hookAll {
                before {}
                after {}
                Assert.assertEquals(1, hookers.size)
            }
            Assert.assertEquals(3, hookers.size)

            val single = resolver.hook()
            val batch = resolvers.hookAll()
            Assert.assertEquals(3, hookers.size)
            single.before {}
            Assert.assertEquals(4, hookers.size)
            batch.after {}
            Assert.assertEquals(6, hookers.size)

            single.after {}
            batch.before {}
            Assert.assertEquals(6, hookers.size)
        }
    }

    private class RecordingChain(override val member: Member) : ChainHookerBridge.Chain {

        override val instance = "value"
        override val args = listOf(1)
        var calls = 0
        var forwardedInstance: Any? = null
        var forwardedArgs: Array<out Any?>? = null

        override fun getArg(index: Int) = args[index]
        override fun proceed() = record(instance, null)
        override fun proceed(args: Array<out Any?>) = record(instance, args)
        override fun proceedWith(instance: Any) = record(instance, null)
        override fun proceedWith(instance: Any, args: Array<out Any?>) = record(instance, args)

        private fun record(instance: Any?, args: Array<out Any?>?): String {
            calls++
            forwardedInstance = instance
            forwardedArgs = args
            return "downstream"
        }
    }
}