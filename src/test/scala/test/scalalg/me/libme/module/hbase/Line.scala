package test.scalalg.me.libme.module.hbase

import java.awt.image.BufferedImage
import java.awt.{Color, Graphics2D, Polygon}
import java.io.File
import javax.imageio.ImageIO

import scala.collection.mutable
import scala.collection.mutable.ListBuffer


/**
  * Created by J on 2018/10/22.
  */

object Config{
  val width:Int=96
  val arrowLength:Int=227
  val width45=130
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

case class Coordinate(x:Int,y:Int){}

abstract class Shape extends Render{

}

case class Block(x:Int,y:Int,width:Int,height:Int) extends Shape{

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

case class Point(x:Int,y:Int,var up:Line=null,var down:Line=null,var left:Line=null,
                 var right:Line=null,var upRight:Line=null,var upLeft:Line=null)extends Render{

  var rendered=false

  override def render()(implicit graphics:Graphics2D): Unit = {
    if (rendered) return ;
    rendered=true
    emptyBlock.render()
    if(up!=null) up.render()
    if(down!=null) down.render()
    if(left!=null) left.render()
    if(right!=null) right.render()
    if(upRight!=null) upRight.render()
    if(upLeft!=null) upLeft.render()


  }

  def emptyBlock={new Block(x,y,0,0)}

}

case class Ramp(listBuffer: ListBuffer[Coordinate]) extends Shape{


  override def render()(implicit graphics: Graphics2D): Unit = {
    import java.awt.Color
    val polygon = new Polygon()

    listBuffer.foreach(coordinate=>{
      polygon.addPoint(coordinate.x,coordinate.y)
    })
    graphics.setColor(Color.yellow)
    graphics.fillPolygon(polygon)

  }


}

case class Line(shapes:List[Shape]) extends Shape{

  var next:Point=null

  var rendered=false

  override def render()(implicit graphics:Graphics2D): Unit = {
    if (rendered) return ;
    rendered=true
    shapes.foreach(shape=>shape.render())
    next.render()

  }

  def splitNum():Int=shapes.size


}


abstract class LinkPoint{

  def link(start:Point,end:Point):Line

}

class RampDownLink extends LinkPoint{


  override def link(start: Point, end: Point): Line = {
    var length=0
    var direction= ""
    if (end.x>start.x && end.y<start.y){
      direction="R"
      length=(start.y-end.y)/4
    }else if (end.x<start.x && end.y<start.y){
      direction="U"
      length=(start.x-end.x)/4
    }


    direction match {
      case "R" =>{
        val sixPoint= new ListBuffer[Coordinate]
        val oneStart=new Coordinate(start.x+Config.width,start.y-Config.width45/2)
        sixPoint+=oneStart
        val two=new Coordinate(end.x,end.y+Config.width+length/2)
        sixPoint+=two
        val three=new Coordinate(end.x,end.y+Config.width)
        sixPoint+=three
        val four=new Coordinate(three.x+Config.width,three.y)
        sixPoint+=four
        val five=new Coordinate(four.x,four.y+length)
        sixPoint+=five
        val six=new Coordinate(oneStart.x,oneStart.y+Config.width45)
        sixPoint+=six

        sixPoint+=oneStart

        println(sixPoint)

        val line= new Line(List(new Ramp(sixPoint)))
        line.next=end
        start.upRight=line
        end.down=line
        return line
      }

      case "U" => {


        return null
      }

    }





//    direction match {
//      case "R" =>{
//
//      }
//
//      case "U" => {
//        start.up=line
//        end.right=line
//      }
//
//    }

//    return line

  }




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

    def nextBlock(block:Block,blockLength:Int): Block ={

      direction match {
        case "L"=>{
          new Block(block.x-blockLength,block.y,blockLength,Config.width)
        }
        case "R"=>{
          new Block(block.x+block.width,block.y,blockLength,Config.width)
        }
        case "U"=>{
          new Block(block.x,block.y-blockLength,Config.width,blockLength)
        }
        case "D"=>{
          new Block(block.x,block.y+block.height,Config.width,blockLength)
        }
      }


    }

    var currentBlock:Block=null

    val blocks:List[Block]=lengthFun(length).map(blockLength=>{
      currentBlock=nextBlock(if (currentBlock==null) start.emptyBlock else currentBlock,blockLength)
      currentBlock
    })

    val line= new Line(blocks)
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

    val start=(849,2345)


    val pointMap=List(
      (849,2345)->(3573,2345)
      ,
      (849,2345)->(849,1397),
      (849,1397)->(849,460),

      (3573,2345)->(3573,1397)
      ,
      (3573,1397)->(3573,460),
      (3573,1397)-> (849,1397),
      (3573,460)->(849,460)


      )

    println(pointMap)


    val instanceMap=new mutable.HashMap[AnyRef,Point]()

    def point(x:Int,y:Int):Point={

      if(!instanceMap.contains((x,y))){
        instanceMap.put((x,y),new Point(x,y))
      }
      return instanceMap.get((x,y)).get
    }

    //val startPoint=point(start._1,start._2)

    for((start,end)<-pointMap){
      val startPoint=point(start._1,start._2)
      val endPoint=point(end._1,end._2)
      new StraightLink(value=>List[Int]{value})
        .link(startPoint,endPoint)
    }

    val rampStart=point(580,2680)
    val rampEnd=point(849,2345)

    val secondRampStart=point(580,800)



    new RampDownLink link(rampStart,rampEnd)
    new StraightLink(value=>List[Int]{value})
      .link(rampStart,secondRampStart)


    rampStart.render()

    graphics2D.dispose()

    Image.write(bufferedImage)


  }


}





































































