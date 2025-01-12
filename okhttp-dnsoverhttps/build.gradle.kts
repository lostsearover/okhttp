plugins {
  kotlin("jvm")
  id("org.jetbrains.dokka")
}

project.applyOsgi(
  "Export-Package: okhttp3.dnsoverhttps",
  "Automatic-Module-Name: okhttp3.dnsoverhttps",
  "Bundle-SymbolicName: com.squareup.okhttp3.dnsoverhttps"
)

dependencies {
  api(project(":okhttp"))
  compileOnly(Dependencies.jsr305)

  testImplementation(project(":okhttp-testing-support"))
  testImplementation(project(":mockwebserver-deprecated"))
  testImplementation(project(":mockwebserver-junit5"))
  testImplementation(Dependencies.okioFakeFileSystem)
  testImplementation(Dependencies.conscrypt)
  testImplementation(Dependencies.junit)
  testImplementation(Dependencies.assertj)
}

afterEvaluate {
  tasks.dokka {
    outputDirectory = "$rootDir/docs/4.x"
    outputFormat = "gfm"
  }
}
