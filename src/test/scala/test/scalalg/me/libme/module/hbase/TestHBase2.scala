package test.scalalg.me.libme.module.hbase

import me.libme.kernel._c.util.CliParams

import scala.collection.JavaConversions
import scalalg.me.libme.module.hbase.{HBaseCliParam, HBaseConfig, HBaseConnector}

/**
  * Created by J on 2018/1/15.
  */
object TestHBase2 {

  def main(args: Array[String]): Unit = {
   
    val hBaseConfig=new HBaseConfig
    val cliParams=new CliParams(args)
      .append(HBaseCliParam.__CONNECT_STRING__,"one.3cgg.rec")
    hBaseConfig.connectString=HBaseCliParam.connectString(cliParams)

    val tableName = "blog"

    val executor=new HBaseConnector(hBaseConfig).connect()

    executor.tableOperations.delete(tableName)
    executor.tableOperations.create(tableName,"artitle","author")


    executor.columnOperations.insert(tableName,"artitle","engish","002","c++ for me")
    executor.columnOperations.insert(tableName,"artitle","engish","003","python for me")
    executor.columnOperations.insert(tableName,"artitle","chinese","004","C++ for china")
    //删除记录
    // deleteRecord(connection,"blog","artitle","chinese","002")
    //扫描整个表


    val map=executor.queryOperations.scan(tableName,"artitle","engish")
    println(map)

    val rowMap=JavaConversions.mapAsScalaMap(map)
      .map(entry=>{entry._1->executor.queryOperations.getRow(tableName,"artitle",entry._1)})

    println(rowMap)

  }























}
