package clicker.game

class Equipment(id: String, incomePerClick: Double, incomePerSec: Double, initialPrice: Double, priceExponent: Double, player: GameActor) {

  var amountOwned: Int = 0
  var idName: String = this.id
  var incomePerClickVar: Double = this.incomePerClick * amountOwned
  var incomePerSecVar: Double = this.incomePerSec * amountOwned
  var price: Double = this.initialPrice
  val exponent: Double = this.priceExponent

  def buyNewItem(): Unit = {
    amountOwned += 1
    price = price * exponent
    incomePerClickVar = this.incomePerClick * amountOwned
    incomePerSecVar = this.incomePerSec * amountOwned
    player.totalIncomePerSec += incomePerSec
    player.totalIncomePerClick += incomePerClick
  }

}
