package clicker.server

import clicker.Click
import com.corundumstudio.socketio.{AckRequest, SocketIOClient}
import com.corundumstudio.socketio.listener.DataListener

class ClickListener(server: ClickerServer) extends DataListener[Nothing]{

  override def onData(client: SocketIOClient, data: Nothing, ackSender: AckRequest): Unit = {
    //send a Click message to the GameActor associated with this player
    //gets the username from the SocketIOClient
    //sends a Click message to that username
    val playerRef = server.socketToActor(client)
    playerRef ! Click
  }

}
