package test.scalalg.me.libme.module.hbase

/**
  * Created by J on 2018/10/17.
  */
object DBConnection {

  private val dbUrl="jdbc://localhost"

  private val dbUser="J"

  def apply(): DBConnection = {println("--in--");  new DBConnection()}

}


class DBConnection{
  private val props=Map(

    "url"->DBConnection.dbUrl,
    "user"->DBConnection.dbUser
  )

  println(s"Created new connection for "+props("url"))
}

case class User(name:String,age:Int)

object Test{

  def main(args: Array[String]): Unit = {
    val conn=new DBConnection()

    val user=User("J",20)
    val res=user match {
      case User(name,age)=>s"name is $name"
    }

    println(res)











    println("----end----")
  }


}

