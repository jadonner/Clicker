package clicker.game

import akka.actor.Actor
import clicker.{BuyEquipment, Click, GameState, Update, UpdateGames}
import play.api.libs.json.{JsValue, Json}

class GameActor(username: String, configuration: String) extends Actor {

  //println(configuration)
  val jsondata: JsValue = Json.parse(configuration)
  val currency: String = (jsondata \ "currency").as[String]
  val equipmentListJS: List[JsValue] = (jsondata \ "equipment").as[List[JsValue]]
  //var timePassedInSeconds: Long = 0
  var lastUpdate: Long = System.nanoTime()

  var gold: Double = 0.0
  var totalIncomePerClick: Double = 0.0
  var totalIncomePerSec: Double = 0.0
  //var equipmentList: List[Map[String, Equipment]] = List()
  //number owned * incomePer = total
  var equipMap: Map[String, Equipment] = Map() //maps equipment id to equipment with accessible stats

  for (dictionary <- equipmentListJS){
    val id: String = (dictionary \ "id").as[String]
    val incomeperclick: Double = (dictionary \ "incomePerClick").as[Double]
    val incomepersec: Double = (dictionary \ "incomePerSecond").as[Double]
    val cost: Double = (dictionary \ "initialCost").as[Double]
    val priceExponent: Double = (dictionary \ "priceExponent").as[Double]
    equipMap += id -> new Equipment(id, incomeperclick, incomepersec, cost, priceExponent, this)
    //equipmentList = equipmentList + newDict
  }
  println(currency)
  System.nanoTime()

  override def receive: Receive = {
    //increase the current gold by the current goldPerClick
    case Click =>
      gold += 1 + totalIncomePerClick
      println("Player: " + username + " has " + gold + " gold.")


    case purchase: BuyEquipment =>
      val equpID: String = purchase.equipmentId
      val costOfItem: Double = equipMap(equpID).price
      val itemRef: Equipment = equipMap(equpID)
      if (gold >= costOfItem){
        itemRef.buyNewItem()
        gold -= costOfItem
      }

    case Update =>
      var timePassedInSeconds = (System.nanoTime() - lastUpdate)
      //println(timePassedInSeconds)
      val updatedGold: Double = totalIncomePerSec * timePassedInSeconds / 1000000000.0 //I need to add the total income per second EVERY second
      lastUpdate = System.nanoTime()
      //println(totalIncomePerSec)
      //println(updatedGold)
      gold += updatedGold
      var finalJsonMap: Map[String, JsValue] = Map()
      var gameStateList: List[JsValue] = List()
      for ((id, tool) <- equipMap){
        var gameStateMap: Map[String, JsValue] = Map()
        val jsonID: JsValue = Json.toJson(id)
        val jsonPrice: JsValue = Json.toJson(tool.price)
        val jsonAmountOwned: JsValue = Json.toJson(tool.amountOwned)
        gameStateMap += "id" -> jsonID
        gameStateMap += "numberOwned" -> jsonAmountOwned
        gameStateMap += "cost" -> jsonPrice
        val jsonMap: JsValue = Json.toJson(gameStateMap)
        gameStateList = gameStateList :+ jsonMap
        //println(gameStateList)
      }
      finalJsonMap += "username" -> Json.toJson(this.username)
      finalJsonMap += "currency" -> Json.toJson(this.gold)
      finalJsonMap += "equipment" -> Json.toJson(gameStateList)
      val returnMap: JsValue = Json.toJson(finalJsonMap)
      val stringMap: String = Json.stringify(returnMap)
      sender ! GameState(stringMap)
  }

}
