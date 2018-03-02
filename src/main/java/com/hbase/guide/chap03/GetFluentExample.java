package com.hbase.guide.chap03;
//Creates a get request using its fluent interface
import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.util.Bytes;

import com.hbase.guide.utils.Helper;


public class GetFluentExample {

  public static void main(String[] args) throws IOException {
    Configuration conf = HBaseConfiguration.create();
    
    /*
   	 * if not exist===> ERROR [main] (ConnectionManager.java:900) - The node /hbase
   	 * is not in ZooKeeper. It should have been written by the master. Check the
   	 * value configured in 'zookeeper.znode.parent'. There could be a mismatch with
   	 * the one configured in the master.
   	 */
   	conf.set("zookeeper.znode.parent", "/hbase-unsecure");

    Helper helper = Helper.getHelper(conf);
    helper.dropTable("testtable");
    helper.createTable("testtable", 5, "colfam1", "colfam2");
    helper.put("testtable",
      new String[] { "row1" },
      new String[] { "colfam1", "colfam2" },
      new String[] { "qual1", "qual1", "qual2", "qual2" },
      new long[]   { 1, 2, 3, 4 },
      new String[] { "val1", "val1", "val2", "val2" });
    System.out.println("Before get call...");
    helper.dump("testtable", new String[]{ "row1" }, null, null);
    Connection connection = ConnectionFactory.createConnection(conf);
    Table table = connection.getTable(TableName.valueOf("testtable"));

    // vv GetFluentExample
    Get get = new Get(Bytes.toBytes("row1")) // co GetFluentExample-1-Create Create a new get using the fluent interface.
      .setId("GetFluentExample")
      .setMaxVersions()
      .setTimeStamp(1)
      .addColumn(Bytes.toBytes("colfam1"), Bytes.toBytes("qual1"))
      .addFamily(Bytes.toBytes("colfam2"));

    Result result = table.get(get);
    System.out.println("Result: " + result);
    // ^^ GetFluentExample
    table.close();
    connection.close();
    helper.close();
  }
}