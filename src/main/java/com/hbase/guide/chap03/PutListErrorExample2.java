package com.hbase.guide.chap03;

//Special error handling with lists of puts
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.RetriesExhaustedWithDetailsException;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.util.Bytes;

import com.hbase.guide.utils.Helper;

public class PutListErrorExample2 {

	public static void main(String[] args) throws IOException {
		Configuration conf = HBaseConfiguration.create();
		/*
		 * if not exist===> ERROR [main] (ConnectionManager.java:900) - The node /hbase
		 * is not in ZooKeeper. It should have been written by the master. Check the
		 * value configured in 'zookeeper.znode.parent'. There could be a mismatch with
		 * the one configured in the master.
		 */
		conf.set("zookeeper.znode.parent", "/hbase-unsecure");
		try (Helper helper = Helper.getHelper(conf);
				Connection connection = ConnectionFactory.createConnection(conf);
				Table table = connection.getTable(TableName.valueOf("testtable"));) {
			helper.dropTable("testtable");
			helper.createTable("testtable", "colfam1");
			List<Put> puts = new ArrayList<Put>();

			Put put1 = new Put(Bytes.toBytes("row1"));
			put1.addColumn(Bytes.toBytes("colfam1"), Bytes.toBytes("qual1"), Bytes.toBytes("val1"));
			puts.add(put1);
			Put put2 = new Put(Bytes.toBytes("row2"));
			put2.addColumn(Bytes.toBytes("BOGUS"), Bytes.toBytes("qual1"), Bytes.toBytes("val2"));
			puts.add(put2);
			Put put3 = new Put(Bytes.toBytes("row2"));
			put3.addColumn(Bytes.toBytes("colfam1"), Bytes.toBytes("qual2"), Bytes.toBytes("val3"));
			puts.add(put3);

			try {
				table.put(puts); // Store multiple rows with columns into HBase.
			} catch (RetriesExhaustedWithDetailsException e) {
				int numErrors = e.getNumExceptions(); // Error Handle failed operations.
				System.out.println("Number of exceptions: " + numErrors);
				for (int n = 0; n < numErrors; n++) {
					System.out.println("Cause[" + n + "]: " + e.getCause(n));
					System.out.println("Hostname[" + n + "]: " + e.getHostnamePort(n));
					System.out.println("Row[" + n + "]: " + e.getRow(n)); // Gain access to the failed operation.
				}
				System.out.println("Cluster issues: " + e.mayHaveClusterIssues());
				System.out.println("Description: " + e.getExhaustiveDescription());
			}

		}
	}
}