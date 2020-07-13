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

import com.google.gson.GsonBuilder
import io.github.classgraph.ClassGraph
import java.io.File

fun main(args: Array<String>) {
    val pkg = args[0]
    val target = args[1]

    val eventAnno = "com.atlassian.analytics.api.annotations.EventName"
    val scanned = ClassGraph()
            .enableAllInfo()
            .acceptPackages(pkg)
            .scan()

    val whitelist = scanned.getClassesWithAnnotation(eventAnno).map { event ->
        val eventName = event.annotationInfo[0].parameterValues[0].value
        val parmList = event.fieldInfo.map { field ->
            val fclass = try {
                ClassLoader.getSystemClassLoader().loadClass(field.typeDescriptor.toString())
            } catch (e: Exception) {
                println(e)
                null  // Probably native, skip
            }
            // Each parameter is either map of the name and valid values, or just the name
            val param: Any = if (fclass != null && fclass.isEnum) {
                mapOf(field.name to fclass.enumConstants.map { enumVal ->
                    if (fclass.name.endsWith("MigrationStage")) {
                        // FIXME: Slight hack; we override the MigrationStage toString() to make it simpler to serialise,
                        //  so we need to uppercase it here. We should really make the serialisation more elegant.
                        enumVal.toString().toUpperCase()
                    } else {
                        enumVal.toString()
                }
                })
            } else {
                field.name
            }
            param
        }
        Pair(eventName, parmList)
    }.toMap()

    val json = GsonBuilder()
            .setPrettyPrinting()
            .create()

    val fd = File(target)
    fd.parentFile.mkdirs()
    fd.writeText(json.toJson(whitelist))
}
