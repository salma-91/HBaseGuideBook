package com.hbase.tutorialpoint;
import java.io.IOException;

import org.apache.hadoop.conf.Configuration;

import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.MasterNotRunningException;
import org.apache.hadoop.hbase.client.HBaseAdmin;

public class ListTables {

   public static void main(String args[])throws MasterNotRunningException, IOException{

      // Instantiating a configuration class
      Configuration conf = HBaseConfiguration.create();
      
      /*if not exist===> ERROR [main] (ConnectionManager.java:900) - 
       * The node /hbase is not in ZooKeeper. It should have been written by the master. 
       * Check the value configured in 'zookeeper.znode.parent'.
       *  There could be a mismatch with the one configured in the master.
       */
       conf.set("zookeeper.znode.parent", "/hbase-unsecure");

      // Instantiating HBaseAdmin class
      HBaseAdmin admin = new HBaseAdmin(conf);

      // Getting all the list of tables using HBaseAdmin object
      HTableDescriptor[] tableDescriptor = admin.listTables();

      // printing all the table names.
      for (int i=0; i<tableDescriptor.length;i++ ){
         System.out.println(tableDescriptor[i].getNameAsString());
      }
   
   }
}