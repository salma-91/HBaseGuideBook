package com.hbase.guide.chap03;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.BufferedMutator;
import org.apache.hadoop.hbase.client.BufferedMutatorParams;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.RetriesExhaustedWithDetailsException;
import org.apache.hadoop.hbase.util.Bytes;

public class BufferedMutatorExample {

	private static final int POOL_SIZE = 10;
	private static final int TASK_COUNT = 100;
	private static final TableName TABLE = TableName.valueOf("testtable");
	private static final byte[] FAMILY = Bytes.toBytes("colfam1");

	public static void main(String[] args) throws Exception {
		Configuration configuration = HBaseConfiguration.create();
		/*
		 * if not exist===> ERROR [main] (ConnectionManager.java:900) - The node /hbase
		 * is not in ZooKeeper. It should have been written by the master. Check the
		 * value configured in 'zookeeper.znode.parent'. There could be a mismatch with
		 * the one configured in the master.
		 */
		configuration.set("zookeeper.znode.parent", "/hbase-unsecure");

		// Create a custom listener instance.
		BufferedMutator.ExceptionListener listener = new BufferedMutator.ExceptionListener() {
			@Override
			public void onException(RetriesExhaustedWithDetailsException e, BufferedMutator mutator) {
				// Handle callback in case of an exception.

				for (int i = 0; i < e.getNumExceptions(); i++) {
					// Generically retrieve the mutation that failed, using the common superclass.
					org.mortbay.log.Log.info("Failed to sent put: " + e.getRow(i));
				}
			}
		};
		// Create a parameter instance, set the table name and custom listener
		// reference.
		BufferedMutatorParams params = new BufferedMutatorParams(TABLE).listener(listener);

		try (
				// Allocate the shared resources using the Java 7 try-with-resource pattern.

				Connection conn = ConnectionFactory.createConnection(configuration);
				BufferedMutator mutator = conn.getBufferedMutator(params)) {
			// Create a worker pool to update the shared mutator in parallel.
			ExecutorService workerPool = Executors.newFixedThreadPool(POOL_SIZE);
			List<Future<Void>> futures = new ArrayList<>(TASK_COUNT);
			// Start all the workers up.
			for (int i = 0; i < TASK_COUNT; i++) {
				futures.add(workerPool.submit(new Callable<Void>() {
					@Override
					public Void call() throws Exception {
						Put p = new Put(Bytes.toBytes("row1"));
						// Each worker uses the shared mutator instance, sharing the same backing
						// buffer, callback listener, and RPC executor pool.
						p.addColumn(FAMILY, Bytes.toBytes("qual1"), Bytes.toBytes("val1"));
						mutator.mutate(p);
						// [...]
						// Do work... Maybe call mutator.flush() after many edits to ensure
						// any of this worker's edits are sent before exiting the Callable
						return null;
					}
				}));
			}

			for (Future<Void> f : futures) {
				// Wait for workers and shut down the pool.
				f.get(5, TimeUnit.MINUTES);
			}
			workerPool.shutdown();
		} catch (IOException e) {
			// The try-with-resource construct ensures that first the mutator,and then the
			// connection are closed.This could trigger exceptions and call the custom
			// listener.

			org.mortbay.log.Log.info("Exception while creating or freeing resources", e);
		}
	}
}
