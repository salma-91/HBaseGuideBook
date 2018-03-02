package com.hbase.guide.chap03;
//Example inserting a faulty column family into HBase
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.util.Bytes;

import com.hbase.guide.utils.*;

public class PutListErrorExample1 {

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

    List<Put> puts = new ArrayList<Put>();
    Put put1 = new Put(Bytes.toBytes("row1"));
    put1.addColumn(Bytes.toBytes("colfam1"), Bytes.toBytes("qual1"),
      Bytes.toBytes("val1"));
    puts.add(put1);
    Put put2 = new Put(Bytes.toBytes("row2"));
    /*[*/put2.addColumn(Bytes.toBytes("BOGUS"),/*]*/ Bytes.toBytes("qual1"),
      Bytes.toBytes("val2")); // Add put with non existent family to list.
    puts.add(put2);
    Put put3 = new Put(Bytes.toBytes("row2"));
    put3.addColumn(Bytes.toBytes("colfam1"), Bytes.toBytes("qual2"),
      Bytes.toBytes("val3"));
    puts.add(put3);

    table.put(puts); //Store multiple rows with columns into HBase.
    table.close();
    connection.close();
    helper.close();
  }
}