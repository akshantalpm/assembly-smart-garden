package org.tw.rpi.assemblysmartgardenassembly

import akka.actor.typed.scaladsl.ActorContext
import akka.util.Timeout
import com.amazonaws.services.iot.client.{AWSIotMessage, AWSIotQos}
import csw.framework.scaladsl.{ComponentHandlers, CurrentStatePublisher}
import csw.messages.commands.{CommandName, CommandResponse, ControlCommand, Observe}
import csw.messages.framework.ComponentInfo
import csw.messages.location._
import csw.messages.params.generics.KeyType
import csw.messages.params.models.Prefix
import csw.messages.scaladsl.TopLevelActorMessage
import csw.services.command.scaladsl.{CommandResponseManager, CommandService}
import csw.services.event.scaladsl.EventService
import csw.services.location.scaladsl.LocationService
import csw.services.logging.scaladsl.LoggerFactory
import play.api.libs.json.Json

import scala.concurrent.duration.DurationDouble
import scala.concurrent.{ExecutionContextExecutor, Future}

/**
 * Domain specific logic should be written in below handlers.
 * This handlers gets invoked when component receives messages/commands from other component/entity.
 * For example, if one component sends Submit(Setup(args)) command to AssemblysmartgardenHcd,
 * This will be first validated in the supervisor and then forwarded to Component TLA which first invokes validateCommand hook
 * and if validation is successful, then onSubmit hook gets invoked.
 * You can find more information on this here : https://tmtsoftware.github.io/csw-prod/framework.html
 */
class AssemblysmartgardenAssemblyHandlers(
    ctx: ActorContext[TopLevelActorMessage],
    componentInfo: ComponentInfo,
    commandResponseManager: CommandResponseManager,
    currentStatePublisher: CurrentStatePublisher,
    locationService: LocationService,
    eventService: EventService,
    loggerFactory: LoggerFactory
) extends ComponentHandlers(ctx,
                              componentInfo,
                              commandResponseManager,
                              currentStatePublisher,
                              locationService,
                              eventService,
                              loggerFactory) {

  implicit val ec: ExecutionContextExecutor = ctx.executionContext
  private val log                           = loggerFactory.getLogger
  private val awsMqttClient                 = new AwsMqttClient()

  implicit val timeout = Timeout(5.seconds)

  override def initialize(): Future[Unit] = {
    componentInfo.connections.map(locationService.track)

    resolveHcd().map {
      case Some(hcd) ⇒
        val commandService = new CommandService(hcd)(ctx.system)

        if(!awsMqttClient.isConnected) {
           awsMqttClient.init(mqttCallback(commandService))
        }

        commandService.subscribeCurrentState { currentState =>
          val payload = currentState.parameter(KeyType.StringKey.make("data")).items.headOption

          payload match {
            case Some(data) => awsMqttClient.publish(currentState.stateName.name, data)
            case None       =>  println("Not received")
          }
        }
      case None ⇒ // do something
    }

    Future.successful({})
  }

  private def mqttCallback(commandService: CommandService) = { (message: AWSIotMessage, topic: String) =>
    val param = KeyType.StringKey.make("data").set(message.getStringPayload)
    commandService.oneway(Observe(Prefix("dms.topic.data"), CommandName(topic), null).madd(param))
  }

  private def resolveHcd(): Future[Option[AkkaLocation]] = {
    val maybeConnection = componentInfo.connections.find(connection ⇒ connection.componentId.componentType == ComponentType.HCD)
    maybeConnection match {
      case Some(hcd) ⇒
        locationService.resolve(hcd.of[AkkaLocation], 5.seconds).map {
          case loc @ Some(akkaLocation) ⇒ loc
          case None                     ⇒ throw new RuntimeException("HCD Not Found")
        }
      case _ ⇒ Future.successful(None)
    }
  }

  override def onLocationTrackingEvent(trackingEvent: TrackingEvent): Unit =   trackingEvent match {
    case LocationUpdated(location) => log.info("Reconnecting")
      initialize()

    case _ : LocationRemoved => log.info("HCD no longer available")
  }

  override def validateCommand(controlCommand: ControlCommand): CommandResponse = ???

  override def onSubmit(controlCommand: ControlCommand): Unit = ???

  override def onOneway(controlCommand: ControlCommand): Unit = ???

  override def onShutdown(): Future[Unit] = {
    Future.successful({})
  }

  override def onGoOffline(): Unit = ???

  override def onGoOnline(): Unit = ???

}
