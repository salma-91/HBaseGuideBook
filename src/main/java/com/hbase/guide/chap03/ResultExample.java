package com.hbase.guide.chap03;
// cc ResultExample Retrieve results from server and dump content
import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.util.Bytes;

import com.hbase.guide.utils.Helper;




public class ResultExample {

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

    Put put = new Put(Bytes.toBytes("row1"));
    put.addColumn(Bytes.toBytes("colfam1"), Bytes.toBytes("qual1"),
      Bytes.toBytes("val1"));
    put.addColumn(Bytes.toBytes("colfam1"), Bytes.toBytes("qual2"),
      Bytes.toBytes("val2"));
    table.put(put);

    // vv ResultExample
    Get get = new Get(Bytes.toBytes("row1"));
    Result result1 = table.get(get);
    System.out.println(result1);

    Result result2 = Result.EMPTY_RESULT;
    System.out.println(result2);

    result2.copyFrom(result1);
    System.out.println(result2);

    // ^^ ResultExample
    table.close();
    connection.close();
    helper.close();
  }
}