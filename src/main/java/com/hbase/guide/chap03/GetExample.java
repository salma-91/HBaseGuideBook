package com.hbase.guide.chap03;
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



public class GetExample {

  public static void main(String[] args) throws IOException {
    Configuration conf = HBaseConfiguration.create(); // co GetExample-1-CreateConf Create the configuration.
    /*
   	 * if not exist===> ERROR [main] (ConnectionManager.java:900) - The node /hbase
   	 * is not in ZooKeeper. It should have been written by the master. Check the
   	 * value configured in 'zookeeper.znode.parent'. There could be a mismatch with
   	 * the one configured in the master.
   	 */
   	conf.set("zookeeper.znode.parent", "/hbase-unsecure");

    Helper helper = Helper.getHelper(conf);
    if (!helper.existsTable("testtable")) {
      helper.createTable("testtable", "colfam1");
    }
    // vv GetExample
    Connection connection = ConnectionFactory.createConnection(conf);
    Table table = connection.getTable(TableName.valueOf("testtable")); // co GetExample-2-NewTable Instantiate a new table reference.

    Get get = new Get(Bytes.toBytes("row1")); // co GetExample-3-NewGet Create get with specific row.

    get.addColumn(Bytes.toBytes("colfam1"), Bytes.toBytes("qual1")); // co GetExample-4-AddCol Add a column to the get.

    Result result = table.get(get); // co GetExample-5-DoGet Retrieve row with selected columns from HBase.

    byte[] val = result.getValue(Bytes.toBytes("colfam1"),
      Bytes.toBytes("qual1")); // co GetExample-6-GetValue Get a specific value for the given column.

    System.out.println("Value: " + Bytes.toString(val)); // co GetExample-7-Print Print out the value while converting it back.

    table.close(); // co GetExample-8-Close Close the table and connection instances to free resources.
    connection.close();
    // ^^ GetExample
    helper.close();
  }
}