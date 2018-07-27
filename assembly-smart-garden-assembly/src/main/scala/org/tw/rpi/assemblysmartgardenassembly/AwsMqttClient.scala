package org.tw.rpi.assemblysmartgardenassembly

import com.amazonaws.services.iot.client.{AWSIotConnectionStatus, AWSIotMessage, AWSIotMqttClient, AWSIotQos}
import com.amazonaws.services.iot.client.sample.sampleUtil.SampleUtil
import com.typesafe.config.ConfigFactory
import csw.messages.commands.CommandResponse
import org.eclipse.paho.client.mqttv3._
import play.api.libs.json.Json

import scala.concurrent.Future

class AwsMqttClient {

  private val config = ConfigFactory.load()
  private val ALARM_TIME_TOPIC              = "alarmTime"
  private val DEVICE_PREFERENCES_TOPIC      = "devicePreferences"

  private val pair = SampleUtil.getKeyStorePasswordPair(config.getString("certificateFile"), config.getString("privateKeyFile"))
  private val client =
    new AWSIotMqttClient(config.getString("clientEndpoint"), MqttClient.generateClientId(), pair.keyStore, pair.keyPassword)

  def init(mqttCallback: (AWSIotMessage, String) => Future[CommandResponse]) = {
    client.connect()
    client.subscribe(new AwsMqttTopic(ALARM_TIME_TOPIC, AWSIotQos.QOS0, mqttCallback))
    client.subscribe(new AwsMqttTopic(DEVICE_PREFERENCES_TOPIC, AWSIotQos.QOS0, mqttCallback))
  }

  def isConnected = client.getConnectionStatus == AWSIotConnectionStatus.CONNECTED

  def subscribe(topic: AwsMqttTopic) = {
    client.subscribe(topic)
  }

  def publish(topic: String, message: String) = {
    client.publish(topic, message.getBytes())
  }
}
