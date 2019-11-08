

// *****************************************************************************
// Projects
// *****************************************************************************

lazy val `aws-zio-s3` =
  project
    .in(file("."))
    .enablePlugins(AutomateHeaderPlugin)
    .settings(settings)
    .settings(
      libraryDependencies ++= Seq(
        library.awsS3Async,
        library.zio,
        library.scalaCheck % Test,
        library.scalaTest  % Test,
      )
    )

// *****************************************************************************
// Library dependencies
// *****************************************************************************

lazy val library =
  new {
    object Version {
      val awsS3      = "2.5.29" 
      val zio        = "1.0.0-RC15"
      val scalaCheck = "1.14.0"
      val scalaTest  = "3.0.8"
    }
    val awsS3Async = "software.amazon.awssdk" %  "s3"         % Version.awsS3
    val zio        = "dev.zio"                %% "zio"        % Version.zio
    val scalaCheck = "org.scalacheck"         %% "scalacheck" % Version.scalaCheck
    val scalaTest  = "org.scalatest"          %% "scalatest"  % Version.scalaTest
  }

// *****************************************************************************
// Settings
// *****************************************************************************

lazy val settings =
  commonSettings ++
  scalafmtSettings ++
  sonatypeSettings

lazy val commonSettings =
  Seq(
    version  := "0.1.0",
    // scalaVersion from .travis.yml via sbt-travisci
    // scalaVersion := "2.12.8",
    organization := "com.github.branislavlazic",
    organizationName := "Branislav Lazic",
    startYear := Some(2019),
    licenses += ("Apache-2.0", url("http://www.apache.org/licenses/LICENSE-2.0")),
    scalacOptions ++= Seq(
      "-unchecked",
      "-deprecation",
      "-language:_",
      "-target:jvm-1.8",
      "-encoding", "UTF-8",
    ),
    Compile / unmanagedSourceDirectories := Seq((Compile / scalaSource).value),
    Test / unmanagedSourceDirectories := Seq((Test / scalaSource).value),
)

lazy val scalafmtSettings =
  Seq(
    scalafmtOnCompile := true,
  )

lazy val sonatypeSettings =
  Seq(
    sonatypeProfileName := "com.github.branislavlazic",
    scmInfo := Some(
      ScmInfo(
        url("https://github.com/BranislavLazic/aws-zio-s3"),
        "scm:git@github.com:BranislavLazic/aws-zio-s3.git"
      )
    ),
    developers := List(
      Developer(
        id    = "BranislavLazic",
        name  = "Branislav Lazic",
        email = "brano2411@hotmail.com",
        url   = url("http://github.com/BranislavLazic")
      )
    ),
    description := "ZIO wrapper for AWS S3 SDK async client.",
    pomIncludeRepository := { _ => false },
    publishTo := sonatypePublishToBundle.value,
  )