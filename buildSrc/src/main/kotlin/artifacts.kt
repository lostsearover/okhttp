/*
 * Copyright (C) 2021 Square, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import aQute.bnd.gradle.BundleTaskConvention
import java.io.File
import org.gradle.api.Project
import org.gradle.api.plugins.ExtensionAware
import org.gradle.api.tasks.SourceSetContainer
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.extra
import org.gradle.kotlin.dsl.get
import org.gradle.kotlin.dsl.withConvention

fun String.publishedArtifactId(): String? {
  return when (this) {
    "okhttp-logging-interceptor" -> "logging-interceptor"
    "mockwebserver" -> "mockwebserver3"
    "mockwebserver-junit4" -> "mockwebserver3-junit4"
    "mockwebserver-junit5" -> "mockwebserver3-junit5"
    "mockwebserver-deprecated" -> "mockwebserver"
    "okcurl",
    "okhttp",
    "okhttp-bom",
    "okhttp-brotli",
    "okhttp-dnsoverhttps",
    "okhttp-sse",
    "okhttp-tls",
    "okhttp-urlconnection" -> this
    else -> null
  }
}

fun Project.applyOsgi(vararg bndProperties: String) {
  apply(plugin = "biz.aQute.bnd.builder")
  sourceSets.create("osgi")
  tasks["jar"].withConvention(BundleTaskConvention::class) {
    setClasspath(sourceSets["osgi"].compileClasspath + project.sourceSets["main"].compileClasspath)
    bnd(*bndProperties)
  }
  dependencies.add("osgiApi", Dependencies.kotlinStdlibOsgi)
}

/**
 * Returns a .jar file for the golden version of this project.
 * https://github.com/Visistema/Groovy1/blob/ba5eb9b2f19ca0cc8927359ce414c4e1974b7016/gradle/binarycompatibility.gradle#L48
 */
fun Project.baselineJar(version: String = "3.14.1"): File? {
  val originalGroup = group
  val artifactId = extra["artifactId"]
  return try {
    val jarFile = "$artifactId-${version}.jar"
    group = "virtual_group_for_japicmp"
    val dependency = dependencies.create("$originalGroup:$artifactId:$version@jar")
    configurations.detachedConfiguration(dependency).files.find { (it.name == jarFile) }
  } catch (e: Exception) {
    null
  } finally {
    group = originalGroup
  }
}

val Project.sourceSets: SourceSetContainer
  get() = (this as ExtensionAware).extensions.getByName("sourceSets") as SourceSetContainer
