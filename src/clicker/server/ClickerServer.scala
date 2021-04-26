package clicker.server

import akka.actor.{Actor, ActorRef, ActorSystem, Props}
import clicker.{GameState, Update, UpdateGames}
import clicker.game.GameActor
import com.corundumstudio.socketio.{Configuration, SocketIOClient, SocketIOServer}

import scala.io.Source


class ClickerServer(val configuration: String) extends Actor {

  var usernameToSocket: Map[String, SocketIOClient] = Map()
  var socketToUsername: Map[SocketIOClient, String] = Map()
  var socketToActor: Map[SocketIOClient, ActorRef] = Map()
  var actorToSocket: Map[ActorRef, SocketIOClient] = Map()
  val system = ActorSystem("MyGame")

  val config: Configuration = new Configuration {
    setHostname("localhost")
    setPort(8080)
  }

  val server: SocketIOServer = new SocketIOServer(config)

  server.start()

  server.addConnectListener(new ConnectionListener)
  server.addDisconnectListener(new DisconnectionListener)

  server.addEventListener("startGame", classOf[String], new GameStart(this))
  server.addEventListener("click", classOf[Nothing], new ClickListener(this))
  server.addEventListener("buy", classOf[String], new BuyResponse(this))


  override def receive: Receive = {

    //for (loop through all clients) then send the UpdateGame message client ! updateGame
    case UpdateGames =>
      for (players <- socketToActor.values){
        players ! Update
      }


    case gamestate: GameState =>
      val currentGameState: String = gamestate.gameState
      val actor: ActorRef = sender()
      val receiver: SocketIOClient = actorToSocket(actor)
      receiver.sendEvent("gameState", currentGameState)
      //print("this should show up")
  }


  override def postStop(): Unit = {
    println("stopping server")
    server.stop()
  }
}

object ClickerServer {

  def main(args: Array[String]): Unit = {
    val actorSystem = ActorSystem()
    import actorSystem.dispatcher

    import scala.concurrent.duration._

    val configuration: String = Source.fromFile("codeConfig.json").mkString

    val server = actorSystem.actorOf(Props(classOf[ClickerServer], configuration))

    actorSystem.scheduler.schedule(0.milliseconds, 100.milliseconds, server, UpdateGames)
  }
}
