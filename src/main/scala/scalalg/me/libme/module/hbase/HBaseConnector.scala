package scalalg.me.libme.module.hbase

import java.util.concurrent.CountDownLatch

import org.apache.hadoop.hbase.{HBaseConfiguration, TableName}
import org.apache.hadoop.hbase.client.{Connection, ConnectionFactory}

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

      def create(tableName:String,cfName1:String): Unit ={
        create(tableName,cfName1,null)
      }


      def create(tableName:String,cfName1:String,cfName2 :String): Unit ={

        val admin=conn.getAdmin

        val tn=TableName.valueOf(tableName)
        if(admin.tableExists(tn)){
          throw new RuntimeException("table ["+tableName+"] already exists.")
        }





      }




    }


















  }




}
