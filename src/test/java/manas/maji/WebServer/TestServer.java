package manas.maji.WebServer;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class TestServer implements Runnable{

	
	 public void run(){
	 	//load the startup configuration from the configuration file
		StartupUtility.setConfiguration();
		
		// Create the ThreadPoolExecutor
		ThreadPoolExecutor executor = null;
		ArrayBlockingQueue<Runnable> waitingQueue = new ArrayBlockingQueue<Runnable>(StartupUtility.getWaitingQueueSize());
		try{
			executor = new ThreadPoolExecutor(StartupUtility.getCorePoolSize(), StartupUtility.getMaxPoolSize(), StartupUtility.getKeepAliveTime(),TimeUnit.SECONDS,waitingQueue);
		}catch(IllegalArgumentException e){
			System.exit(1);
		}catch(NullPointerException e){
			System.exit(1);
		}
		executor.allowCoreThreadTimeOut(true);
		
		//open a server socket connection
		/**
		 * Server Socket connection that is opened
		 */
		ServerSocket serverSocketConnection=StartupUtility.openSocketConnection();

		//incoming client socket connection
		/**
		 * Incoming client socket connection.
		 */
		Socket clientSocket = null;		
		while (!Thread.interrupted()) {
			// Accept any incoming client connection
			try {
				clientSocket = serverSocketConnection.accept();
			} catch (IOException e) {
				System.out.println("Client request accept failed.");
				System.exit(1);
			}
			//assign the task to the thread
			Runnable cThread=new ClientThread(clientSocket);
			//start the thread
			executor.execute(cThread);
	    }
		try {
			executor.awaitTermination(StartupUtility.getTimeOutTime(), TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			System.out.println("Server Forcefully Shut Down.");
		}
		//shut down the server when it is interrupted
		try {
			serverSocketConnection.close();
		} catch (IOException e) {
			System.out.println("Server socket closing error");
		}

	}
}
