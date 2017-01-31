version := "1.0-SNAPSHOT"

// *****************************************************************************
// Projects
// *****************************************************************************

lazy val `profits` =
  project
    .in(file("."))
    .enablePlugins(GitVersioning, PlayScala)
    .settings(settings)
    .settings(
      libraryDependencies ++= Seq(
        library.scalaTestPlay % Test
      )
    )

// *****************************************************************************
// Library dependencies
// *****************************************************************************

lazy val library =
  new {
    object Version {
      val scalaTestPlay = "1.5.1"
    }
    val scalaTestPlay = "org.scalatestplus.play" %% "scalatestplus-play" % Version.scalaTestPlay
  }

// *****************************************************************************
// Settings
// *****************************************************************************        |

lazy val settings =
  commonSettings ++
  gitSettings

lazy val commonSettings =
  Seq(
    // scalaVersion and crossScalaVersions from .travis.yml via sbt-travisci
     scalaVersion := "2.11.8",
    // crossScalaVersions := Seq(scalaVersion.value, "2.11.8"),
    organization := "it.impossible",
    licenses += ("MIT",
                 url("https://opensource.org/licenses/MIT")),
    mappings.in(Compile, packageBin) +=
      baseDirectory.in(ThisBuild).value / "LICENSE" -> "LICENSE",
    scalacOptions ++= Seq(
      "-unchecked",
      "-deprecation",
      "-language:_",
      "-target:jvm-1.8",
      "-encoding", "UTF-8"
    ),
    javacOptions ++= Seq(
      "-source", "1.8",
      "-target", "1.8"
    ),
    javaOptions in Test += "-Dconfig.file=conf/application.test.conf",
    unmanagedSourceDirectories.in(Compile) :=
      Seq(scalaSource.in(Compile).value),
    unmanagedSourceDirectories.in(Test) :=
      Seq(scalaSource.in(Test).value)
)

lazy val gitSettings =
  Seq(
    git.useGitDescribe := true
  )
