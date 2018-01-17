package test.scalalg.me.libme.module.hbase

import scala.collection.mutable

/**
  * Created by J on 2018/1/17.
  */
object TestObject {


  def main(args: Array[String]): Unit = {


    val map=mutable.Map(1->1,2->2,3->3)

    map.foreach(k=>{println(k)})


    val mapRes=map.map(k=>k._1 +k._2)

    mapRes.foreach(k=>{println(k)})


    val mapRes2=map.map(k=>{k._1->(k._1 +k._2)})

    mapRes2.foreach(k=>{println(k)})


    var flag:Int=1

    while((flag=5)!=1){
      println("AKA")
    }


    println("E")


  }

}
