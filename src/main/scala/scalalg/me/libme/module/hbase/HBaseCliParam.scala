package scalalg.me.libme.module.hbase

import me.libme.kernel._c.util.CliParams

/**
  * Created by J on 2018/1/11.
  */
object HBaseCliParam {



  val __CONNECT_STRING__ :String="--hbase.zk.connectString"



  def connectString(cliParam: CliParams):String={
    cliParam.getString(__CONNECT_STRING__)
  }

}
