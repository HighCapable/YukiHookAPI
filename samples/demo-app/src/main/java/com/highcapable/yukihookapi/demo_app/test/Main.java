/*
 * YukiHookAPI - An efficient Hook API and Xposed Module solution built in Kotlin.
 * Copyright (C) 2019-2023 HighCapable
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
 * This file is created by fankes on 2022/7/27.
 */
package com.highcapable.yukihookapi.demo_app.test;

public class Main extends SuperMain {

    private final String content;

    public Main() {
        super("");
        content = "";
    }

    public Main(String content) {
        super(content);
        this.content = content;
    }

    public String getTestResultFirst() {
        return "The world is beautiful";
    }

    public String getTestResultFirst(String string) {
        return string;
    }

    public String getTestResultLast() {
        return "The world is fantastic";
    }

    public final String getTestResultLast(String string) {
        return string;
    }

    public String getContent() {
        return content;
    }
}