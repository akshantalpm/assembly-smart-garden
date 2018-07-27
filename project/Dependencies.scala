import sbt._

object Dependencies {

  val AssemblysmartgardenAssembly = Seq(
    CSW.`csw-framework`,
    CSW.`csw-command`,
    CSW.`csw-location`,
    CSW.`csw-messages`,
    CSW.`csw-logging`,
    Libs.`mqtt`,
    Libs.`aws-iot`,
    Libs.`sample-aws-iot`,
    Libs.`scalatest` % Test,
    Libs.`junit` % Test,
    Libs.`junit-interface` % Test
  )

  val AssemblysmartgardenHcd = Seq(
    CSW.`csw-framework`,
    CSW.`csw-command`,
    CSW.`csw-location`,
    CSW.`csw-messages`,
    CSW.`csw-logging`,
    Libs.`scalatest` % Test,
    Libs.`junit` % Test,
    Libs.`junit-interface` % Test
  )

  val AssemblysmartgardenDeploy = Seq(
    CSW.`csw-framework`
  )
}
