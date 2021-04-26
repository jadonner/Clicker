package clicker.server

import com.corundumstudio.socketio.SocketIOClient
import com.corundumstudio.socketio.listener.{ConnectListener, DisconnectListener}

class DisconnectionListener extends DisconnectListener{

  override def onDisconnect(client: SocketIOClient): Unit = {
    println("User: " + client + " has left the server")
  }

}
