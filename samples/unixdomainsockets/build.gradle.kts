plugins {
  kotlin("jvm")
}

dependencies {
  implementation(project(":okhttp"))
  implementation(project(":mockwebserver-deprecated"))
  implementation(Dependencies.jnrUnixsocket)
}
