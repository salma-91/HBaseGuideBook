package com.hbase.tutorialpoint;
import java.io.IOException;

import org.apache.hadoop.conf.Configuration;

import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.MasterNotRunningException;
import org.apache.hadoop.hbase.client.HBaseAdmin;

public class AddColoumn{

   public static void main(String args[]) throws MasterNotRunningException, IOException{

      // Instantiating configuration class.
      Configuration conf = HBaseConfiguration.create();
      /*if not exist===> ERROR [main] (ConnectionManager.java:900) - 
       * The node /hbase is not in ZooKeeper. It should have been written by the master. 
       * Check the value configured in 'zookeeper.znode.parent'.
       *  There could be a mismatch with the one configured in the master.
       */
       conf.set("zookeeper.znode.parent", "/hbase-unsecure");

      // Instantiating HBaseAdmin class.
      HBaseAdmin admin = new HBaseAdmin(conf);

      // Instantiating columnDescriptor class
      HColumnDescriptor columnDescriptor = new HColumnDescriptor("contactDetails");
      
      // Adding column family
      admin.addColumn("emp", columnDescriptor);
      System.out.println("coloumn added");
   }
}