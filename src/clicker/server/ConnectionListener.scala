package clicker.server

import com.corundumstudio.socketio.{AckRequest, SocketIOClient}
import com.corundumstudio.socketio.listener.{ConnectListener, DataListener}

class ConnectionListener extends ConnectListener{

  override def onConnect(client: SocketIOClient): Unit = {
    println("User: " + client + " has joined the server")
  }

}
