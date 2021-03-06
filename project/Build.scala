import sbt._
import Keys._

object Build extends Build {

  lazy val project = Project("root", file(".")).settings(

    // basics
    name := "scala-map-benchmarking",
    organization := "cat.dvmlls",
    version := "1.0.0-SNAPSHOT",
    scalaVersion := "2.11.6",
    scalacOptions ++= Seq("-deprecation", "-feature"),

    // dependencies
    libraryDependencies ++= Seq(
        "com.carrotsearch" % "hppc" % "0.7.1",
        "net.sf.trove4j" % "trove4j" % "3.0.3",
        "com.google.code.java-allocation-instrumenter" % "java-allocation-instrumenter" % "2.0",
        "com.google.code.gson" % "gson" % "1.7.1",
        "org.scalatest" %% "scalatest" % "2.2.1" % "test",
        "junit" % "junit" % "4.12" % "test",
        "org.scalacheck" %% "scalacheck" % "1.12.2" % "test"
    ),
    resolvers += "sonatypeSnapshots" at "http://oss.sonatype.org/content/repositories/snapshots",

    // enable forking in run
    fork in run := true,

    // we need to add the runtime classpath as a "-cp" argument to the `javaOptions in run`, otherwise caliper
    // will not see the right classpath and die with a ConfigurationException
    javaOptions in run ++= Seq("-cp",
      Attributed.data((fullClasspath in Runtime).value).mkString(":"))
  )
}
