enablePlugins(ScalaJSPlugin)

val scala3Version = "3.3.1"
scalaJSUseMainModuleInitializer := true

lazy val root = project
  .in(file("."))
  .settings(
    name := "pp-assignment-3",
    version := "0.1.0-SNAPSHOT",
    scalaVersion := scala3Version,
    libraryDependencies += "org.scalameta" %% "munit" % "1.0.0-M10" % Test,
    libraryDependencies += "org.scalafx" %% "scalafx" % "21.0.0-R32",
    libraryDependencies += "org.scala-js" %%% "scalajs-dom" % "2.4.0",
    cancelable in Global := true
  )
