package clicker.server

import clicker.{BuyEquipment, Click}
import com.corundumstudio.socketio.{AckRequest, SocketIOClient}
import com.corundumstudio.socketio.listener.DataListener

class BuyResponse(server: ClickerServer) extends DataListener[String]{

  override def onData(client: SocketIOClient, equipmentID: String, ackSender: AckRequest): Unit = {
    //client ! BuyEquipment(equipmentID)
    val playerRef = server.socketToActor(client)
    playerRef ! BuyEquipment(equipmentID)
  }

}
