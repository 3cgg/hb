package test.scalalg.me.libme.module.hbase._trait

/**
  * Created by J on 2018/10/18.
  */
class User(val name:String){
  def suffix=""

  override def toString: String = s"$name$suffix"
}

trait Attorney{
  self:User=>
  override def suffix: String = ", esq."
}

trait Wizard{
  self:User=>
  override def suffix: String = ", Wizard"
}

trait Reverser{
  self:User=>
  override def toString: String = super.toString.reverse
}

class A {
  def hi="hi"
}

trait B{
  self:A=>
  override def toString: String = "B: "+hi
}

class C extends A with B{

}

class TestSuite(suiteName:String){
  def start(){}
}

trait RandomSeeded{
  self:TestSuite=>
  def randomStart(): Unit ={
    util.Random.setSeed(System.currentTimeMillis())
    self.start()
  }
}

class IdSpec extends TestSuite("ID Tests") with RandomSeeded{
  def testId(): Unit ={
    println(util.Random.nextInt()!=1)
  }

  override def start(): Unit = testId()

  println("Starting...")
  randomStart()


}


object TestA {

  def main(args: Array[String]): Unit = {

    println(new C)

    new IdSpec

    val h=new User("Harry P") with Wizard
    println(h)

    val g=new User("Ginny W") with Attorney
    println(g)

    val l=new User("Luna L") with Wizard with Reverser
    println(l)

  }

}


class Car ; class Volvo extends Car; class VolvoWagon extends Volvo;

class Item[+A](a:A){def get:A=a}  //协变支持之类到基类的转变

class Check[-A]{def check(a:A)={}} //函数参数仅支持逆变，逆变指一个参数类型可以调整为一个子类型

object TestVolvo{

  def item(v:Item[Volvo]){val c:Car=v.get}  // 因为是协变类型参数，这边定义了类型的上界是Volvo

  def check(v:Check[Volvo]){v.check(new VolvoWagon)}  // 因为是逆变类型参数，这边定义了类型的下届是Volvo

  def main(args: Array[String]): Unit = {
//    item(new Item[Car](new Car))
    item(new Item[Volvo](new Volvo))
    item(new Item[VolvoWagon](new VolvoWagon))

    check(new Check[Car])
    check(new Check[Volvo])
//    check(new Check[VolvoWagon])


  }


}




















