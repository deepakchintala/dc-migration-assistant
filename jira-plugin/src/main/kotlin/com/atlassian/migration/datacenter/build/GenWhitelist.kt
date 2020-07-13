/*
 * Copyright 2020 Atlassian
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.atlassian.migration.datacenter.build

import io.github.classgraph.ClassGraph

fun main(args: Array<String>) {
//    val pkg = args[0]
//    val target = args[1]
    val pkg = "com.atlassian.migration.datacenter.analytics.events"
    val event = "com.atlassian.analytics.api.annotations.EventName"
    val scanned = ClassGraph()
            .enableAllInfo()
            .acceptPackages(pkg)
            .scan()

    for (c in scanned.getClassesWithAnnotation(event)) {
        println(c.name)
        for (f in c.fieldInfo) {
            val td = f.typeDescriptor
            val fc = try {
                ClassLoader.getSystemClassLoader().loadClass(td.toString())
            } catch (e: Exception) {
                null
                // Probably native, skip
            }

            println("\t${f.name}\t${f.typeDescriptor}")
            if (fc != null && fc.isEnum) {
                for (ev in fc.enumConstants) {
                    println("\t\t\t${ev.toString().toUpperCase()}")
                }
            }
        }
    }



//    File(target).writeText(str)
}
