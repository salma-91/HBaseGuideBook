package com.hbase.tutorialpoint;
import java.io.IOException;

import org.apache.hadoop.conf.Configuration;

import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.util.Bytes;

public class InsertData{

   public static void main(String[] args) throws IOException {

      // Instantiating Configuration class
      Configuration config = HBaseConfiguration.create();

      /*if not exist===> ERROR [main] (ConnectionManager.java:900) - 
       * The node /hbase is not in ZooKeeper. It should have been written by the master. 
       * Check the value configured in 'zookeeper.znode.parent'.
       *  There could be a mismatch with the one configured in the master.
       */
       config.set("zookeeper.znode.parent", "/hbase-unsecure");
       
      // Instantiating HTable class
      HTable hTable = new HTable(config, "emp");

      // Instantiating Put class
      // accepts a row name.
      Put p = new Put(Bytes.toBytes("row1")); 

      // adding values using add() method
      // accepts column family name, qualifier/row name ,value
      p.add(Bytes.toBytes("personal"),
      Bytes.toBytes("name"),Bytes.toBytes("raju"));

      p.add(Bytes.toBytes("personal"),
      Bytes.toBytes("city"),Bytes.toBytes("hyderabad"));

      p.add(Bytes.toBytes("professional"),Bytes.toBytes("designation"),
      Bytes.toBytes("manager"));

      p.add(Bytes.toBytes("professional"),Bytes.toBytes("salary"),
      Bytes.toBytes("50000"));
      
      // Saving the put Instance to the HTable.
      hTable.put(p);
      System.out.println("data inserted");
      
      // closing HTable
      hTable.close();
   }
}