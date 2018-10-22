package test.scalalg.me.libme.module.hbase

import java.util.concurrent.atomic.AtomicInteger

/**
  * Created by J on 2018/10/15.
  */
object TestScala {


  def main(args: Array[String]): Unit = {

    val str="-tesT"
    var obj=""
    val index:AtomicInteger=new AtomicInteger(1)

    obj=value(str)
    println(index.incrementAndGet() + obj)

    obj=value(str,s=>s.toUpperCase)
    println(index.incrementAndGet() +obj)

    obj=value {println("into expressioin : ") ; str.take(3)}
    println(index.incrementAndGet() +obj)

    obj=value(str, {println("into expressioin : ") ; str.take(2)})
    println(index.incrementAndGet() +obj)

    obj=sum(5,6).toString
    println(index.incrementAndGet() + obj)

    obj=sumK(5)(5).toString
    println(index.incrementAndGet() + obj)

    val fn=sumK(5)_
    println(fn.toString()+"   "+fn.getClass)
    obj=fn(9).toString
    println(index.incrementAndGet() + obj)

    obj=eqAny(5)(5).toString
    println(index.incrementAndGet() + obj)


    obj=boolReduce(List(46,19,92),false){(a,i)=>if(a) a else (i==19)} toString

    println(index.incrementAndGet() + obj)

  }


  /**
    *
    * @param str
    * @param fn  函数字面量
    * @return
    */
  def value(str: String,fn:String=>String):String={
    value(fn(str))
  }

  /**
    *
    * @param str
    * @param fn  传名参数，如果方法体没有调用此参数，参数不会求值 ； （也就是说此参数传的是函数字面量，不是函数值）， 可以传入表达式块
    * @return
    */
  def value(str: String,fn: =>String):String={
    value(fn)
  }


  /**
    *
    * @param a  可以传入表达式块（因为表达式块有返回值） ， 或者传入值
    * @return
    */
  def value(a:String):String={
    a+"-val"
  }


  def sum(x:Int,y:Int):Int={
    x+y
  }

  def sumK(x:Int)(y:Int):Int={x+y}

  def eqAny[A](x:A)(y:A):Boolean={
    x.equals(y)
  }


  def boolReduce(l:List[Int],start:Boolean)(f:(Boolean,Int)=>Boolean):Boolean={
    var a=start
    for(i<-l) a=f(a,i)
    a
  }






}
