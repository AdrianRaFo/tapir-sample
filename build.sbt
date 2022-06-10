ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.8"
// Scala
val http4sV           = "0.23.11"
val circeV            = "0.14.1"
val tapirV            = "0.20.1"
val log4catsV         = "2.3.0"
val tapirGoldenTestV  = "0.3.0"
val munitV            = "0.7.29"
val munitCEV          = "1.0.7"
val http4sMunitV      = "0.9.3"
// Java
val logbackClassicV = "1.2.11"
// Compiler plugins
val kindProjectorV    = "0.13.2"
val betterMonadicForV = "0.3.1"

lazy val root = (project in file("."))
  .settings(
    name := "tapir-sample",
    addCompilerPlugin("org.typelevel" % "kind-projector"     % kindProjectorV cross CrossVersion.full),
    addCompilerPlugin("com.olegpy"   %% "better-monadic-for" % betterMonadicForV),
    libraryDependencies ++= Seq(
      "ch.qos.logback"              % "logback-classic"                       % logbackClassicV,
      "org.typelevel"               %% "log4cats-slf4j"                       % log4catsV,
      "org.http4s"                  %% "http4s-ember-server"                  % http4sV,
      "org.http4s"                  %% "http4s-circe"                         % http4sV,
      "com.softwaremill.sttp.tapir" %% "tapir-json-circe"                     % tapirV,
      "com.softwaremill.sttp.tapir" %% "tapir-http4s-server"                  % tapirV,
      "com.softwaremill.sttp.tapir" %% "tapir-enumeratum"                     % tapirV,
      "com.softwaremill.sttp.tapir" %% "tapir-openapi-docs"                   % tapirV,
      "com.softwaremill.sttp.tapir" %% "tapir-openapi-circe-yaml"             % tapirV,
      "com.softwaremill.sttp.tapir" %% "tapir-swagger-ui-bundle"              % tapirV,
      "io.circe"                    %% "circe-generic"                        % circeV,
      "io.circe"                    %% "circe-generic-extras"                 % circeV,
      "io.circe"                    %% "circe-parser"                         % circeV,
      "org.typelevel"               %% "munit-cats-effect-3"                  % munitCEV         % Test,
      "com.alejandrohdezma"         %% "http4s-munit"                         % http4sMunitV     % Test,
      "com.alejandrohdezma"         %% "tapir-golden-openapi-munit"           % tapirGoldenTestV % Test,
      "com.alejandrohdezma"         %% "tapir-golden-openapi-munit-validator" % tapirGoldenTestV % Test
    )
  )
