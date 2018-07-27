package org.tw.rpi.assemblysmartgardenassembly

import akka.actor.typed.scaladsl.ActorContext
import akka.util.Timeout
import com.amazonaws.services.iot.client.{AWSIotMessage, AWSIotQos, AWSIotTopic}
import csw.messages.commands.{CommandName, Observe}
import csw.messages.location.AkkaLocation
import csw.messages.params.generics.KeyType
import csw.messages.params.models.Prefix
import csw.messages.scaladsl.TopLevelActorMessage
import csw.services.command.scaladsl.CommandService

import scala.concurrent.duration.DurationDouble

class AwsMqttTopicListener(hcd: AkkaLocation, ctx: ActorContext[TopLevelActorMessage]) {}
