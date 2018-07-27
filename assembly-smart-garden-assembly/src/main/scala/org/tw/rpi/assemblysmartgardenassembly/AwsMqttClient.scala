package org.tw.rpi.assemblysmartgardenassembly

import com.amazonaws.services.iot.client.{AWSIotConnectionStatus, AWSIotMessage, AWSIotMqttClient, AWSIotQos}
import com.amazonaws.services.iot.client.sample.sampleUtil.SampleUtil
import com.typesafe.config.ConfigFactory
import csw.messages.commands.CommandResponse
import org.eclipse.paho.client.mqttv3._
import play.api.libs.json.Json

import scala.concurrent.Future

object AwsMqttClient {

  private val config = ConfigFactory.load()
  private val pair = SampleUtil.getKeyStorePasswordPair(config.getString("certificateFile"), config.getString("privateKeyFile"))
  private val client =
    new AWSIotMqttClient(config.getString("clientEndpoint"), MqttClient.generateClientId(), pair.keyStore, pair.keyPassword)

  def init() = {
    client.connect()
  }

  def isConnected = client.getConnectionStatus == AWSIotConnectionStatus.CONNECTED

  def subscribe(topic: AwsMqttTopic) = {
    client.subscribe(topic)
  }

  def publish(topic: String, message: String) = {
    client.publish(topic, message.getBytes())
  }
}
