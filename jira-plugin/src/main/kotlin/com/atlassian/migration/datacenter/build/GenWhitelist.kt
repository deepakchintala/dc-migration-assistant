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

import com.google.gson.Gson
import com.google.gson.GsonBuilder
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

    val whitelist = HashMap<String, List<Any>>()

    for (event in scanned.getClassesWithAnnotation(event)) {
        val eventName = event.annotationInfo[0].parameterValues[0].value
        val parmList = event.fieldInfo.map {
            val fclass = try {
                ClassLoader.getSystemClassLoader().loadClass(it.typeDescriptor.toString())
            } catch (e: Exception) {
                null  // Probably native, skip
            }
            if (fclass != null && fclass.isEnum) {
                // FIXME: Slight hack; we override the enum toString() to make it simpler to serialise,
                //  so we need to uppercase it here. We should really make the serialisation more elegant.
                mapOf(it.name to fclass.enumConstants.map { it.toString().toUpperCase() })
            } else {
                it.name
            }
        }
        whitelist.put(eventName.toString(), parmList)
    }

    val json = GsonBuilder()
            .setPrettyPrinting()
            .create()
    println("${json.toJson(whitelist)}")

//    File(target).writeText(str)
}
