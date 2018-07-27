package org.tw.rpi.assemblysmartgardenassembly

import com.amazonaws.services.iot.client.{AWSIotMessage, AWSIotQos, AWSIotTopic}
import csw.messages.commands.CommandResponse

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class AwsMqttTopic(topic: String, qos: AWSIotQos, callback: (AWSIotMessage, String) => Future[CommandResponse])
    extends AWSIotTopic(topic, qos) {
  override def onMessage(message: AWSIotMessage): Unit = {
    callback(message, topic).map { response =>
      println(response.runId)
    }
  }
}
