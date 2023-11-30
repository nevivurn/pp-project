val scala3Version = "3.3.1"

lazy val root = project
  .in(file("."))
  .settings(
    name := "pp-project",
    version := "0.1.0-SNAPSHOT",
    scalaVersion := scala3Version,
    libraryDependencies += "org.scalameta" %% "munit" % "1.0.0-M10" % Test,
    libraryDependencies += "org.parboiled" %% "parboiled" % "2.5.0",
    cancelable in Global := true
  )
