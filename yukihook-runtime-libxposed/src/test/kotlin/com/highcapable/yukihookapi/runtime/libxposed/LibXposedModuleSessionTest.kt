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
 * This file is created by fankes on 2026/9/27.
 */
package com.highcapable.yukihookapi.runtime.libxposed

import com.highcapable.yukihookapi.runtime.bridge.session.ModuleSessionBridge
import org.junit.Assert
import org.junit.Test
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

/**
 * Checks retirement against actual concurrent module uses, without mocking Android or native hooks.
 */
class LibXposedModuleSessionTest {

    @Test
    fun busyGenerationRefusesRetirementAndKeepsAcceptingWork() {
        val session: ModuleSessionBridge = LibXposedModuleSession()
        val entered = CountDownLatch(1)
        val release = CountDownLatch(1)
        val executor = Executors.newSingleThreadExecutor()
        val invocation = executor.submit {
            check(session.enter())
            entered.countDown()
            try {
                check(release.await(5, TimeUnit.SECONDS))
            } finally {
                session.leave()
            }
        }

        try {
            Assert.assertTrue(entered.await(5, TimeUnit.SECONDS))
            Assert.assertFalse(session.freeze())
            Assert.assertTrue(session.enter())
            session.leave()
            release.countDown()
            invocation.get(5, TimeUnit.SECONDS)
            Assert.assertTrue(session.freeze())
            Assert.assertFalse(session.enter())
        } finally {
            release.countDown()
            executor.shutdownNow()
        }
    }

    @Test
    fun refusalBeforeDisposalRestoresCallbackAdmission() {
        val session: ModuleSessionBridge = LibXposedModuleSession()
        Assert.assertTrue(session.freeze())
        Assert.assertFalse(session.enter())

        session.resume()

        Assert.assertTrue(session.enter())
        Assert.assertFalse(session.freeze())
        session.leave()
        Assert.assertTrue(session.freeze())
    }

    @Test
    fun closedInstanceRejectsNewWorkAndRetiresWhenIdle() {
        val session: ModuleSessionBridge = LibXposedModuleSession()
        session.close()

        Assert.assertFalse(session.enter())
        Assert.assertTrue(session.freeze())
    }

    @Test
    fun closeLeavesInFlightCallbacksRunningAndStillRetiresAfterTheyLeave() {
        val session: ModuleSessionBridge = LibXposedModuleSession()
        Assert.assertTrue(session.enter())

        session.close()

        Assert.assertFalse(session.freeze())
        session.leave()
        Assert.assertTrue(session.freeze())
    }

    @Test
    fun concurrentAcquireAndRetireNeverBothSucceed() {
        val executor = Executors.newFixedThreadPool(2)
        try {
            repeat(100) {
                val session: ModuleSessionBridge = LibXposedModuleSession()
                val start = CountDownLatch(1)
                val acquire = executor.submit<Boolean> {
                    check(start.await(5, TimeUnit.SECONDS))
                    session.enter()
                }
                val retire = executor.submit<Boolean> {
                    check(start.await(5, TimeUnit.SECONDS))
                    session.freeze()
                }
                start.countDown()

                val acquired = acquire.get(5, TimeUnit.SECONDS)
                val retired = retire.get(5, TimeUnit.SECONDS)
                Assert.assertTrue("Callback admission and retirement must be exclusive", acquired.xor(retired))
                if (acquired) session.leave()
            }
        } finally {
            executor.shutdownNow()
        }
    }
}