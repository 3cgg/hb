package test.scalalg.me.libme.module.hbase

import java.awt.image.BufferedImage
import java.awt.{Color, Graphics2D}
import java.io.File
import javax.imageio.ImageIO

import scala.collection.mutable


/**
  * Created by J on 2018/10/22.
  */

object Config{
  val width:Int=96
  val arrowLength:Int=227
}

trait Render{

  def render()(implicit graphics:Graphics2D)

}



object Image{

  def image:BufferedImage={
//    val font = new Font("微软雅黑", Font.BOLD, 32);
//    val content = "你好Java!";
    val width=4080
    val height=2880
    new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB)
  }

  def write(bufferedImage: BufferedImage): Unit ={
    ImageIO.write(bufferedImage,"PNG",new File("d:/test-image.png"))
  }


}

case class Part(x:Int,y:Int,width:Int,height:Int) extends Render{

  var rendered=false

  override def render()(implicit graphics:Graphics2D): Unit = {
    if (rendered) return ;
    rendered=true
    import java.awt.RenderingHints
    graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
    graphics.setColor(Color.GREEN)
    // 2. 填充一个矩形
    graphics.fillRect(x, y, width,height);

    println(this)

  }

}



case class Point(x:Int,y:Int,var up:Line=null,var down:Line=null,var left:Line=null,var right:Line=null)extends Render{

  var rendered=false

  override def render()(implicit graphics:Graphics2D): Unit = {
    if (rendered) return ;
    rendered=true
    emptyPart.render()
    if(up!=null) up.render()
    if(down!=null) down.render()
    if(left!=null) left.render()
    if(right!=null) right.render()

  }

  def emptyPart={new Part(x,y,0,0)}

}

case class Line(parts:List[Part]) extends Render{

  var next:Point=null

  var rendered=false

  override def render()(implicit graphics:Graphics2D): Unit = {
    if (rendered) return ;
    rendered=true
    parts.foreach(part=>part.render())
    next.render()

  }

  def splitNum():Int=parts.size


}


abstract class LinkPoint{

  def link(start:Point,end:Point):Line

}

class StraightLink(lengthFun:Int=>List[Int]) extends LinkPoint{

  override def link(start:Point,end:Point): Line = {
    val _x = Math.abs(end.x - start.x)
    val _y = Math.abs(end.y - start.y)
    val length=Math.sqrt(_x * _x + _y * _y).toInt

    var direction= ""
    if (end.x-start.x>0 && end.y==start.y){
      direction="R"
    }else if (end.x-start.x<0 && end.y==start.y){
      direction="L"
    }else if (end.y-start.y<0 && end.x==start.x){
      direction="U"
    }else if (end.y-start.y>0 && end.x==start.x){
      direction="D"
    }

    def nextPart(part:Part,partLength:Int): Part ={

      direction match {
        case "L"=>{
          new Part(part.x-partLength,part.y,partLength,Config.width)
        }
        case "R"=>{
          new Part(part.x+part.width,part.y,partLength,Config.width)
        }
        case "U"=>{
          new Part(part.x,part.y-partLength,Config.width,partLength)
        }
        case "D"=>{
          new Part(part.x,part.y+part.height,Config.width,partLength)
        }
      }


    }

    var currentPart:Part=null

    val parts:List[Part]=lengthFun(length).map(partLength=>{
      currentPart=nextPart(if (currentPart==null) start.emptyPart else currentPart,partLength)
      currentPart
    })

    val line= new Line(parts)
    line.next=end

    direction match {
      case "L"=>{
        start.left=line
        end.right=line
      }
      case "R"=>{
        start.right=line
        end.left=line
      }
      case "U"=>{
        start.up=line
        end.down=line
      }
      case "D"=>{
        start.down=line
        end.up=line
      }
    }


    return line

  }

}




object TestGraphics2D{


  def main(args: Array[String]): Unit = {
    start()
  }


  def start():Unit={

    val bufferedImage=Image.image
    implicit val graphics2D= bufferedImage.createGraphics()

    val start=(3573,2345)


    val pointMap=List(
      start->(3573,1397)
      ,
      (3573,1397)->(3573,460),
      (3573,1397)-> (849,1397),
      (3573,460)->(849,460),

      start->(849,2345),
      (849,2345)->(849,1397),
      (849,1397)->(849,460)
      )

    println(pointMap)

    val startPoint=Point(start._1,start._2)

    val instanceMap=new mutable.HashMap[AnyRef,Point]()
    instanceMap.put(start,startPoint)

    def point(x:Int,y:Int):Point={

      if(!instanceMap.contains((x,y))){
        instanceMap.put((x,y),new Point(x,y))
      }
      return instanceMap.get((x,y)).get
    }


    for((start,end)<-pointMap){
      val startPoint=point(start._1,start._2)
      val endPoint=point(end._1,end._2)
      new StraightLink(value=>List[Int]{value})
        .link(startPoint,endPoint)
    }

    startPoint.render()

    graphics2D.dispose()

    Image.write(bufferedImage)


  }


}





































































