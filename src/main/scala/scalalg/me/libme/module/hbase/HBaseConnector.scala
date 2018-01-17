package scalalg.me.libme.module.hbase


import java.util

import me.libme.kernel._c.util.JStringUtils
import org.apache.hadoop.hbase.client._
import org.apache.hadoop.hbase.util.Bytes
import org.apache.hadoop.hbase.{HBaseConfiguration, HColumnDescriptor, HTableDescriptor, TableName}
import org.slf4j.{Logger, LoggerFactory}

import scala.collection.{JavaConversions, mutable}

/**
  * Created by J on 2018/1/16.
  */
class HBaseConnector(hbaseConfig:HBaseConfig) {


  var config:HBaseConfig=hbaseConfig

  def connect():HBaseExecutor={
    new HBaseExecutor {} connect()
  }





  abstract class HBaseExecutor{

    var conn:Connection=_

    var tableOperations:TableOperations=_

    var columnOperations:ColumnOperations=_

    var queryOperations:QueryOperations=_

    def connect():HBaseExecutor={

      val conf = HBaseConfiguration.create
//      conf.set("hbase.zookeeper.property.clientPort", "2181")
      //    conf.set("zookeeper.znode.parent", "/hbase-unsecure")
      conf.set("hbase.zookeeper.quorum", config connectString)

      this.conn= ConnectionFactory.createConnection(conf)
      this.tableOperations=new TableOperations
      this.columnOperations=new ColumnOperations
      this.queryOperations=new QueryOperations

      return this
    }



    /**
      * be aware of column family, we recommend the number of that is up to two
      */
    class TableOperations{

      private[this] val logger:Logger=LoggerFactory.getLogger(classOf[TableOperations])


      def create(tableName:String): Unit ={
        create(tableName,null,null)
      }

      def create(tableName:String,cfName1:String): Unit ={
        create(tableName,cfName1,null)
      }

      def create(tableName:String,cfName1:String,cfName2 :String): Unit ={

        val admin=conn.getAdmin

        val tn=TableName.valueOf(tableName)
        if(admin.tableExists(tn)){
          throw new RuntimeException("table ["+tableName+"] already exists.")
        }

        val tableDescriptor = new HTableDescriptor(tableName)
        if(JStringUtils.isNotNullOrEmpty(cfName1)){
          tableDescriptor.addFamily(new HColumnDescriptor(cfName1.getBytes()))
        }
        if(JStringUtils.isNotNullOrEmpty(cfName2)){
          tableDescriptor.addFamily(new HColumnDescriptor(cfName2.getBytes()))
        }
        admin.createTable(tableDescriptor)

        logger.info("create table : "+tableName+",with column family ["+cfName1+"]")

      }

      def disable(tableName: String): Unit ={
        val tn = TableName.valueOf(tableName)
        val admin = conn.getAdmin
        if (admin.tableExists(tn)){
          admin.disableTable(tn)
          logger.info("disable table : "+tableName)
        }
      }


      def enable(tableName: String): Unit ={
        val tn = TableName.valueOf(tableName)
        val admin = conn.getAdmin
        if (admin.tableExists(tn)){
          admin.enableTable(tn)
          logger.info("enable table : "+tableName)
        }
      }

      def delete(tableName: String): Unit ={
        val tn = TableName.valueOf(tableName)
        val admin = conn.getAdmin
        if (admin.tableExists(tn)){
          admin.disableTable(tn)
          admin.deleteTable(tn)
          logger.info("enable table : "+tableName)
        }
      }

    }



    /**
      *
      */
    class ColumnOperations {

      val logger: Logger = LoggerFactory.getLogger(classOf[ColumnOperations])


      def insert(tableName:String,family:String,column:String,key:String,value:String): Unit ={
        val userTable = TableName.valueOf(tableName)
        val table=conn.getTable(userTable)
        val p=new Put(key.getBytes)
        p.addColumn(family.getBytes,column.getBytes,value.getBytes())
        table.put(p)
      }

      def delete(tableName:String,family:String,column:String,row:String): Unit ={
        var table:Table=null
        try{
          val userTable=TableName.valueOf(tableName)
          table=conn.getTable(userTable)
          val delete=new Delete(row.getBytes())
          delete.addColumn(family.getBytes(),column.getBytes())
          table.delete(delete)
          logger.debug("delete column : "+tableName+","+family+":"+column)
        }finally{
          if(table!=null)table.close()
        }
      }


      /**
        *
        * @param tableName
        * @param family
        * @param column
        * @param row
        * @return
        */
      def get(tableName:String,family:String,column:String,row:String):String={
        var table:Table=null
        try{
          val userTable = TableName.valueOf(tableName)
          table=conn.getTable(userTable)
          val get=new Get(row.getBytes())
          get.addColumn(Bytes.toBytes(family),Bytes.toBytes(column))
          val result=table.get(get)
          val value=Bytes.toString(result.value())
          return value
        }finally{
          if(table!=null)table.close()
        }

      }



    }


    class QueryOperations {

      private[this] val logger: Logger = LoggerFactory.getLogger(classOf[QueryOperations])


      /**
        * return a map , key is row , value is column value
        * @param tableName
        * @param family
        * @param column
        * @return
        */
      def scan(tableName:String,family:String,column:String): util.HashMap[String,String] ={

        val map=new util.HashMap[String,String]

        var table:Table=null
        var scanner:ResultScanner=null
        try{
          val userTable=TableName.valueOf(tableName)
          table=conn.getTable(userTable)
          val scan=new Scan()
          scan.addColumn(family.getBytes(),column.getBytes())
          scanner=table.getScanner(scan)
          var result:Result=scanner.next()
          while(result!=null){
            map.put(Bytes.toString(result.getRow),Bytes.toString(result.getValue(family.getBytes(),column.getBytes())))
            result=scanner.next()
          }
          return map
        }finally{
          if(table!=null)
            table.close()
          if(scanner!=null)
            scanner.close()
        }
      }


      /**
        *
        * latest version for all columns of this family
        * Map&lt;rowid,&lt;column,value>>
        * @param tableName
        * @param row
        * @return
        */
      def getRow(tableName:String,family:String,row:String): mutable.Map[String,Any]={
        var table:Table=null
        try{
          val userTable = TableName.valueOf(tableName)
          table=conn.getTable(userTable)
          val get=new Get(row.getBytes())
          get.addFamily(Bytes.toBytes(family))
          val result=table.get(get)
          val value=JavaConversions.mapAsScalaMap(result.getMap)
            .map(entry=>{Bytes.toString(entry._1)-> ( JavaConversions.mapAsScalaMap(entry._2).map(cEntry=>{Bytes.toString(cEntry._1)->cEntry._2.lastEntry().getValue}) )})
          val map=new mutable.HashMap[String,Any]
          map.put(row,value)
          return map
        }finally{
          if(table!=null)table.close()
        }

      }



    }
















    }




}
