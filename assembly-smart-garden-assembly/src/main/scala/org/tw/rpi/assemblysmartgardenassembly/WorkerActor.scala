package org.tw.rpi.assemblysmartgardenassembly

import akka.actor.typed.scaladsl.{ActorContext, Behaviors, MutableBehavior}
import akka.actor.typed.{ActorRef, Behavior}
import akka.util.Timeout
import com.amazonaws.services.iot.client.{AWSIotMessage, AWSIotQos}
import csw.framework.scaladsl.CurrentStatePublisher
import csw.messages.commands.{CommandName, Observe}
import csw.messages.location.AkkaLocation
import csw.messages.params.generics.KeyType
import csw.messages.params.models.Prefix
import csw.messages.params.states.{CurrentState, StateName}
import csw.services.command.scaladsl.CommandService
import csw.services.config.api.models.ConfigData
import org.eclipse.paho.client.mqttv3.MqttMessage
import org.tw.rpi.assemblysmartgardenassembly.WorkerActorMsgs.{Publish, Start, Status, Subscribe}

import scala.concurrent.duration.DurationLong

trait WorkerActorMsg
object WorkerActorMsgs {
  case class Subscribe(topics: Set[String]) extends WorkerActorMsg
  case class Publish(topic: String, payload: String) extends WorkerActorMsg
  case object Start extends WorkerActorMsg
  case object Status extends WorkerActorMsg
}

object WorkerActor {
  def make(commandService: CommandService): Behavior[WorkerActorMsg] = {
    Behaviors.setup(ctx ⇒ new WorkerActor(ctx, commandService))
  }
}

class WorkerActor(ctx: ActorContext[WorkerActorMsg],
                  commandService: CommandService,
                  var topics: Set[String] = Set.empty[String]
                 ) extends MutableBehavior[WorkerActorMsg] {

  private def mqttCallback(commandService: CommandService) = { (message: AWSIotMessage, topic: String) =>
    implicit val timeout = Timeout(5.seconds)
    val param = KeyType.StringKey.make("data").set(message.getStringPayload)
    commandService.oneway(Observe(Prefix("dms.topic.data"), CommandName(topic), null).madd(param))
  }

  override def onMessage(msg: WorkerActorMsg): Behavior[WorkerActorMsg] = {
    msg match {
      case Subscribe(myTopics) =>
        topics = topics ++ myTopics
        myTopics.foreach(x => AwsMqttClient.subscribe(new AwsMqttTopic(x, AWSIotQos.QOS0, mqttCallback(commandService))))
      case Publish(topic, payload) => AwsMqttClient.publish(topic, payload)
      case Start =>
        if(!AwsMqttClient.isConnected) AwsMqttClient.init()
    }
    this
  }
}
