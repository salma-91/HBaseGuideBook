package com.hbase.tutorialspoint;


import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.HBaseAdmin;

public class CreateTable {

	  public static void main(String[] args) throws IOException {

	      // Instantiating configuration class
	      Configuration con = HBaseConfiguration.create();
	      
	      /*if not exist===> ERROR [main] (ConnectionManager.java:900) - 
	       * The node /hbase is not in ZooKeeper. It should have been written by the master. 
	       * Check the value configured in 'zookeeper.znode.parent'.
	       *  There could be a mismatch with the one configured in the master.
	       */
	       con.set("zookeeper.znode.parent", "/hbase-unsecure");

	      // Instantiating HbaseAdmin class
	      HBaseAdmin admin = new HBaseAdmin(con);

	      // Instantiating table descriptor class
	      HTableDescriptor tableDescriptor = new
	      HTableDescriptor(TableName.valueOf("employe"));

	      // Adding column families to table descriptor
	      tableDescriptor.addFamily(new HColumnDescriptor("personal"));
	      tableDescriptor.addFamily(new HColumnDescriptor("professional"));

	      // Execute the table through admin
	      admin.createTable(tableDescriptor);
	      System.out.println(" Table created ");
	   }	
}