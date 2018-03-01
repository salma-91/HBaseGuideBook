package com.hbase.tutorialspoint;
import java.io.IOException;

import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.client.HBaseAdmin;

public class DeleteTable {

   public static void main(String[] args) throws IOException {

      // Instantiating configuration class
      Configuration conf = HBaseConfiguration.create();
      
      /*if not exist===> ERROR [main] (ConnectionManager.java:900) - 
       * The node /hbase is not in ZooKeeper. It should have been written by the master. 
       * Check the value configured in 'zookeeper.znode.parent'.
       *  There could be a mismatch with the one configured in the master.
       */
       conf.set("zookeeper.znode.parent", "/hbase-unsecure");

      // Instantiating HBaseAdmin class
      HBaseAdmin admin = new HBaseAdmin(conf);

      // disabling table named employe
      admin.disableTable("employe");

      // Deleting employe
      admin.deleteTable("employe");
      System.out.println("Table deleted");
   }
}