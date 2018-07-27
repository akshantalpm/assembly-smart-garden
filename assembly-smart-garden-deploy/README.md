# assembly-smart-garden-deploy

This module contains apps and configuration files for host deployment using 
HostConfig (https://tmtsoftware.github.io/csw-prod/apps/hostconfig.html) and 
ContainerCmd (https://tmtsoftware.github.io/csw-prod/framework/deploying-components.html).

An important part of making this work is ensuring the host config app (AssemblysmartgardenHostConfigApp) is built
with all of the necessary dependencies of the components it may run.  This is done by adding settings to the
built.sbt file:

```
lazy val `assembly-smart-garden-deploy` = project
  .dependsOn(
    `assembly-smart-garden-assembly`,
    `assembly-smart-garden-hcd`
  )
  .enablePlugins(JavaAppPackaging)
  .settings(
    libraryDependencies ++= Dependencies.AssemblysmartgardenDeploy
  )
```

and in Libs.scala:

```

  val `csw-framework`  = "org.tmt" %% "csw-framework"  % Version

```

To start assembly-smart-garden Assembly and HCD, follow below steps:

 - Run `sbt assembly-smart-garden-deploy/universal:packageBin`, this will create self contained zip in target/universal directory
 - Unzip generate zip and enter into bin directory
 - Run container cmd script or host config app script
 - Ex.  `./assemblysmartgarden-host-config-app --local ../../../../assembly-smart-garden-deploy/src/main/resources/AssemblysmartgardenHostConfig.conf -s ./assemblysmartgarden-container-cmd-app`

Note: the CSW Location Service cluster seed must be running, and appropriate environment variables set to run apps.
See https://tmtsoftware.github.io/csw-prod/apps/cswclusterseed.html .