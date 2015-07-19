package manas.maji.WebServer;

import org.apache.log4j.Logger;

import java.io.*;
import java.net.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * This is the Http Server class,it loads the initial configuration from the properties file, creates the thread pool using ThreadPoolExecutor services the configuration.
 * <p>It starts the Web Server,accepts all incoming client connection and assigns thread from the thread pool to handle the client requests. 
 * <p>The Server will continue to run until it is interrupted,when the threads will be joined & the thread pool closed.
 * <p>When a Shut Down request is received, the shut down hook services is is used to meet the request.  
 * 
 * @author maji
 */
public class HttpServer{
		
	/**
	 * This method loads the startup configuration from the property file,opens the server socket. It also creates a thread pool of the size specified in the property file.
	 * <p>It keeps the server running,accepts any incoming client connection requests & handles it by assigning a new thread from the thread pool to handle the request.
	 * 
	 * @param args
	 */	
	public static void main(String[] args){
		
		Logger log=Logger.getLogger(HttpServer.class);

		//load the startup configuration from the configuration file
		StartupUtility.setConfiguration();
		
		// Create the ThreadPoolExecutor
		ThreadPoolExecutor executor = null;
		ArrayBlockingQueue<Runnable> waitingQueue = new ArrayBlockingQueue<Runnable>(StartupUtility.getWaitingQueueSize());
		try{
			executor = new ThreadPoolExecutor(StartupUtility.getCorePoolSize(), StartupUtility.getMaxPoolSize(), StartupUtility.getKeepAliveTime(),TimeUnit.SECONDS,waitingQueue);
		}catch(IllegalArgumentException e){
			log.info("Invalid cofiguration for thread pool creation.");
			System.exit(1);
		}catch(NullPointerException e){
			log.info("Working Queue for thread pool creation is null.");
			System.exit(1);
		}
		executor.allowCoreThreadTimeOut(true);
		
		//open a server socket connection
		/**
		 * Server Socket connection that is opened
		 */
		ServerSocket serverSocketConnection=StartupUtility.openSocketConnection();

		//Refresh Download Page
		Utility util=new Utility();
		util.generateDownloadPage();					
		
		//incoming client socket connection
		/**
		 * Incoming client socket connection.
		 */
		Socket clientSocket = null;		
		Runtime.getRuntime().addShutdownHook(new ShutDownHook(serverSocketConnection,executor,log));
		while (!Thread.interrupted()){
			// Accept any incoming client connection
			try {
				clientSocket = serverSocketConnection.accept();
				log.info("New connection made");
			} catch (IOException e) {
				log.info("Client request accept failed.");
				System.exit(1);
			}
			//assign the task to the thread
			Runnable cThread=new ClientThread(clientSocket);
			//start the thread
			executor.execute(cThread);
		}
		//shut down the server when it is interrupted
		log.info("Server Shut down");
	}
	
	
	/**
	 * This class shuts down the server in a graceful manner. It waits for a certain amount of time for any currently running threads to finish execution (the time is read from the property file), 
	 * then shuts down the thread pool,& closes server socket connection.
	 */
	static class ShutDownHook extends Thread{
		ThreadPoolExecutor executor;
		ServerSocket serverSocketConnection;
		Logger log;
		/**
		 * This constructor the executor instance & the server socket connection to be closed
		 * 
		 * @param serverSocket the server to be shut down
		 * @param exec the executor instance
		 * @param Logger the logger object,which creates the log file
		 */
		ShutDownHook(ServerSocket serverSocket,ThreadPoolExecutor exec,Logger logReference){
			executor=exec;
			serverSocketConnection=serverSocket;
			log=logReference;
		}
		
		/**
		 * This method waits for running threads to finish execution, then closes the thread pool executor services & terminates the server socket connection.
		 * 
		 *@exception InterruptedException Error in awaitTermination() method 
		 *			 IOException Error in closing the server socket
		 */
		public void run() {
			System.out.println("Waiting for Client threads to finish...");
    		log.info("Waiting for threads to finish execution");
    		try {
                 if (executor.awaitTermination(StartupUtility.timeOutTime,TimeUnit.SECONDS)) 
                 {
                	System.out.print("The executor has timed out. Shutting down all threads.");
                 }
                 log.info("Shutting down executor");
                 executor.shutdownNow();
                 log.info("Closing Server Socket connection");
             	 serverSocketConnection.close();
         		System.out.println("SERVER SHUT DOWN.");
         	} catch (InterruptedException e) {
                 System.out.println("Error in shutdown hook.");
            } catch (IOException e) {
					System.out.println("Error in closing socket connection "+e);
			}
         }
	}
}