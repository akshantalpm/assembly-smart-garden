package org.tw.rpi.assemblysmartgardendeploy

import csw.framework.deploy.containercmd.ContainerCmd

object AssemblysmartgardenContainerCmdApp extends App {

  ContainerCmd.start("assembly-smart-garden-container-cmd-app", args)

}
