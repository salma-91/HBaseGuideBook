package com.hbase.guide.chap03;

//Example 3-4. Example using the client-side write buffer
import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.BufferedMutator;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.util.Bytes;

import com.hbase.guide.utils.*;

public class PutWriteBufferExample1 {

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
		TableName name = TableName.valueOf("testtable");
		Connection connection = ConnectionFactory.createConnection(conf);
		Table table = connection.getTable(name);
		BufferedMutator mutator = connection.getBufferedMutator(name); //Get a mutator instance for the
																		// table.

		Put put1 = new Put(Bytes.toBytes("row1"));
		put1.addColumn(Bytes.toBytes("colfam1"), Bytes.toBytes("qual1"), Bytes.toBytes("val1"));
		mutator.mutate(put1); // Store some rows with columns into HBase.

		Put put2 = new Put(Bytes.toBytes("row2"));
		put2.addColumn(Bytes.toBytes("colfam1"), Bytes.toBytes("qual1"), Bytes.toBytes("val2"));
		mutator.mutate(put2);

		Put put3 = new Put(Bytes.toBytes("row3"));
		put3.addColumn(Bytes.toBytes("colfam1"), Bytes.toBytes("qual1"), Bytes.toBytes("val3"));
		mutator.mutate(put3);

		Get get = new Get(Bytes.toBytes("row1"));
		Result res1 = table.get(get);
		System.out.println("Result: " + res1); // Try to load previously stored row, this will print "Result:
												// keyvalues=NONE".

		mutator.flush(); // Force a flush, this causes an RPC to occur.

		Result res2 = table.get(get);
		System.out.println("Result: " + res2); // Now the row is persisted and can be loaded.

		mutator.close();
		table.close();
		connection.close();
		helper.close();
	}
}