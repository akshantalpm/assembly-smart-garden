addSbtPlugin("org.scalastyle"   %%  "scalastyle-sbt-plugin" % "1.0.0")
addSbtPlugin("com.geirsson"     %   "sbt-scalafmt"          % "1.4.0")
addSbtPlugin("org.scoverage"    %   "sbt-scoverage"         % "1.5.1")
addSbtPlugin("com.typesafe.sbt" %   "sbt-native-packager"   % "1.3.3")
addSbtPlugin("com.eed3si9n"     %   "sbt-buildinfo"         % "0.8.0")
addSbtPlugin("net.virtual-void" % "sbt-dependency-graph" % "0.9.0")

classpathTypes += "maven-plugin"

scalacOptions ++= Seq(
  "-encoding",
  "UTF-8",
  "-feature",
  "-unchecked",
  "-deprecation",
  //"-Xfatal-warnings",
  "-Xlint",
  "-Yno-adapted-args",
  "-Ywarn-dead-code",
  "-Xfuture"
)
