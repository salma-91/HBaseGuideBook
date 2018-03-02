package com.hbase.guide.chap03;
// cc CheckAndPutExample Example application using the atomic compare-and-set operations
import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.util.Bytes;

import com.hbase.guide.utils.Helper;


public class CheckAndPutExample {

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
    helper.createTable("testtable", "colfam1");

    Connection connection = ConnectionFactory.createConnection(conf);
    Table table = connection.getTable(TableName.valueOf("testtable"));
    Put put1 = new Put(Bytes.toBytes("row1"));
    put1.addColumn(Bytes.toBytes("colfam1"), Bytes.toBytes("qual1"),
      Bytes.toBytes("val1")); //Create a new Put instance.

    boolean res1 = table.checkAndPut(Bytes.toBytes("row1"),
      Bytes.toBytes("colfam1"), Bytes.toBytes("qual1"), null, put1); //Check if column does not exist and perform optional put operation.
    System.out.println("Put 1a applied: " + res1); //Print out the result, should be "Put 1a applied: true".

    boolean res2 = table.checkAndPut(Bytes.toBytes("row1"),
      Bytes.toBytes("colfam1"), Bytes.toBytes("qual1"), null, put1); //Attempt to store same cell again.
    System.out.println("Put 1b applied: " + res2); //Print out the result, should be "Put 1b applied: false" as the column now already exists.

    Put put2 = new Put(Bytes.toBytes("row1"));
    put2.addColumn(Bytes.toBytes("colfam1"), Bytes.toBytes("qual2"),
      Bytes.toBytes("val2")); //Create another Put instance, but using a different column qualifier.

    boolean res3 = table.checkAndPut(Bytes.toBytes("row1"),
      Bytes.toBytes("colfam1"), Bytes.toBytes("qual1"), //Store new data only if the previous data has been saved.
      Bytes.toBytes("val1"), put2);
    System.out.println("Put 2 applied: " + res3); //Print out the result, should be "Put 2 applied: true" as the checked column exists.

    Put put3 = new Put(Bytes.toBytes("row2"));
    put3.addColumn(Bytes.toBytes("colfam1"), Bytes.toBytes("qual1"),
      Bytes.toBytes("val3")); //Create yet another Put instance, but using a different row.

    boolean res4 = table.checkAndPut(Bytes.toBytes("row1"),
      Bytes.toBytes("colfam1"), Bytes.toBytes("qual1"), //Store new data while checking a different row.
      Bytes.toBytes("val1"), put3);
    System.out.println("Put 3 applied: " + res4); // We will not get here as an exception is thrown beforehand!
   
    table.close();
    connection.close();
    helper.close();
  }
}