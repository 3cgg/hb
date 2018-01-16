package scalalg.me.libme.module.hbase


import java.util

import me.libme.kernel._c.util.JStringUtils
import org.apache.hadoop.hbase.client._
import org.apache.hadoop.hbase.util.Bytes
import org.apache.hadoop.hbase.{HBaseConfiguration, HColumnDescriptor, HTableDescriptor, TableName}
import org.slf4j.{Logger, LoggerFactory}

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

    def connect():HBaseExecutor={

      val conf = HBaseConfiguration.create
      conf.set("hbase.zookeeper.property.clientPort", "2181")
      //    conf.set("zookeeper.znode.parent", "/hbase-unsecure")
      conf.set("hbase.zookeeper.quorum", config connectString)

      this.conn= ConnectionFactory.createConnection(conf)

      return this
    }

    /**
      * be aware of column family, we recommend the number of that is up to two
      */
    object TableOperations{

      val logger:Logger=LoggerFactory.getLogger(TableOperations.getClass)

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
          admin.enableTable(tn)
          admin.deleteTable(tn)
          logger.info("enable table : "+tableName)
        }
      }

    }



    /**
      *
      */
    object ColumnOperations {

      val logger: Logger = LoggerFactory.getLogger(ColumnOperations.getClass)


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


    object QueryOperations {

      val logger: Logger = LoggerFactory.getLogger(QueryOperations.getClass)


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
          var result:Result=null
          while((result=scanner.next())!=null){
            map.put(Bytes.toString(result.getRow),Bytes.toString(result.getValue(family.getBytes(),column.getBytes())))
          }
          return map
        }finally{
          if(table!=null)
            table.close()
          if(scanner!=null)
            scanner.close()
        }
      }

      def getRow(tableName:String,family:String,row:String): util.NavigableMap[Array[Byte], util.NavigableMap[Array[Byte], util.NavigableMap[Long, Array[Byte]]]]={
        var table:Table=null
        try{
          val userTable = TableName.valueOf(tableName)
          table=conn.getTable(userTable)
          val get=new Get(row.getBytes())
          val result=table.get(get)
          val value=result.getMap
          return value
        }finally{
          if(table!=null)table.close()
        }

      }



    }
















    }




}
