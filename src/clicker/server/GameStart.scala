package clicker.server

import clicker.game.GameActor
import com.corundumstudio.socketio.{AckRequest, SocketIOClient}
import com.corundumstudio.socketio.listener.DataListener
import akka.actor.{Actor, ActorRef, ActorSystem, Props}

class GameStart(server: ClickerServer) extends DataListener[String]{

  override def onData(client: SocketIOClient, username: String, ackSender: AckRequest): Unit = {
    val player: ActorRef = server.system.actorOf(Props(new GameActor(username, server.configuration)))
    server.socketToUsername += client -> username
    server.usernameToSocket += username -> client
    server.actorToSocket += player -> client
    server.socketToActor += client -> player
    client.sendEvent("initialize", server.configuration)
  }

}
