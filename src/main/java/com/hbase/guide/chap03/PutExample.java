package com.hbase.guide.chap03;
//Example 3-2. Example application inserting data into HBase
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.util.Bytes;

import java.io.IOException;

public class PutExample {

	public static void main(String[] args) throws IOException {
		Configuration conf = HBaseConfiguration.create();
		/*
		 * if not exist===> ERROR [main] (ConnectionManager.java:900) - The node /hbase
		 * is not in ZooKeeper. It should have been written by the master. Check the
		 * value configured in 'zookeeper.znode.parent'. There could be a mismatch with
		 * the one configured in the master.
		 */
		conf.set("zookeeper.znode.parent", "/hbase-unsecure");

		Connection connection = ConnectionFactory.createConnection(conf);

		Table table = connection.getTable(TableName.valueOf("testtable"));
		Put put = new Put(Bytes.toBytes("row1"));
		put.addColumn(Bytes.toBytes("colfam1"), Bytes.toBytes("qual1"), Bytes.toBytes("val1"));
		put.addColumn(Bytes.toBytes("colfam1"), Bytes.toBytes("qual2"), Bytes.toBytes("val2"));

		table.put(put);
		table.close();
		connection.close();

	}
}