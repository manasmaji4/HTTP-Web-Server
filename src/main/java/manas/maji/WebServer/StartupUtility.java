package manas.maji.WebServer;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.BindException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.UnknownHostException;
import java.util.Properties;

import org.apache.log4j.Logger;
/**
 * This class contains the methods that help in starting the server. It does the following jobs-
 * <p>-Getting the configuration values for the server socket connection setup & the thread pool creation.
 * <p>-create the server socket connection at the specified address & port.
 * <p>-make the base directory for storing the client uploaded files.
 * <p>-methods return the configuration values as loaded from the property file
 *  
 * @author maji
 *
 */
public class StartupUtility{
	static Logger log=Logger.getLogger(StartupUtility.class);
	static int port,backLog,corePoolSize,maxPoolSize,keepAliveTime,waitingQueueSize,timeOutTime,maxFileSize;
	static InetAddress bindAddress;
	static String baseDirAddress;
	static Properties prop = new Properties();
	/**
	 * This method loads the property file from he class path, reads the values of the parameters needed to open the socket connection & create the thread pool.
	 * <p>The values read from the property file are displayed to the user in the terminal.
	 * <p>If the property file cannot be found due to some reason it logs the error & exits the program. 
	 * 
	 * @exception IOException if exceptions arises in loading the configuration file from the class path
	 * @exception UnknownHostException IP address of a host cannot be determined
	 */
	public static void setConfiguration()
	{
		try {
			InputStream configFile=StartupUtility.class.getClassLoader().getResourceAsStream(Constants.configFileLocation);
			prop.load(configFile);
		} catch (IOException e) {
			log.error("Error in loading the configuration file from the class path"+e);
			System.exit(1);			
		}
		baseDirAddress=(prop.getProperty("BaseDirectoryLocation"));
		makeBaseDirectory(baseDirAddress);
		log.info("Connection Configuration Information:");
		System.out.println("\n************************************************************************");
		System.out.println("Connection Information:");
		port=Integer.parseInt(prop.getProperty("PortNo"));
		log.info("Port No.="+port);
		System.out.println("\tPort No.="+port);
		backLog=Integer.parseInt(prop.getProperty("BackLog"));
		log.info("Backlog="+backLog);
		System.out.println("\tBacklog="+backLog);
		try {
			String address=	prop.getProperty("BindingAddress");
			bindAddress=InetAddress.getByName(address);
			log.info("Binding Address="+address);
			System.out.println("\tBinding Address="+address);
		} catch (UnknownHostException e) {
			log.error("IP address of a host cannot be determined");
			System.exit(1);
		}
		System.out.println("\n************************************************************************");
		log.info("Thread Pool Configuration Information:");
		System.out.println("Thread Pool Information:");
		corePoolSize=Integer.parseInt(prop.getProperty("CorePoolSize"));
		log.info("Core Pool Size="+corePoolSize);
		System.out.println("\tCore Pool Size="+corePoolSize);
		maxPoolSize=Integer.parseInt(prop.getProperty("MaxPoolSize"));
		log.info("Max pool Size="+maxPoolSize);
		System.out.println("\tMax pool Size="+maxPoolSize);
		keepAliveTime=Integer.parseInt(prop.getProperty("KeepAliveTime"));
		log.info("Keep Alive Time="+keepAliveTime);
		System.out.println("\tKeep Alive Time="+keepAliveTime);
		waitingQueueSize=Integer.parseInt(prop.getProperty("WaitingQueueSize"));
		log.info("Waiting Queue Size="+waitingQueueSize);
		System.out.println("\tWaiting Queue Size="+waitingQueueSize);
		timeOutTime=Integer.parseInt(prop.getProperty("TimeOutTime"));
		log.info("Thread Timeout Time="+timeOutTime);
		System.out.println("\tThread Timeout Time="+timeOutTime);
		maxFileSize=Integer.parseInt(prop.getProperty("MaxFileSize"));
	}

	
	/**
	 * This method creates the base directory from the location specified in the property file if the directory is not already present.
	 * <p>If it is not able to create the base directory then the error is logged & the server is terminated.
 	 * <p>If the base directory location is not found then the server exits, the user can set the base directory location from the configuration file & restart the server.
 	 * 
	 * @param baseLocation the location of the base directory as specified in the property file
	 */
	public static void makeBaseDirectory(String baseLocation)
	{
		File baseDir = null;
		if(baseLocation!=null)
		{
			baseDir=new File(baseLocation);
		}
		if(baseLocation==null)
		{
			log.info("Could not find base directory location.");
			System.exit(1);
		}
		if(!baseDir.exists())
		{
			if(baseDir.mkdir())
			{
				log.info("Base Directory created");
			}
			else {
				log.info("Failed to create Base Directory");
				System.exit(1);
			}	
		}
	}

	/**
	 * Return the base directory location where user uploaded files will be stored
	 * 
	 * @return baseDirAddress return its value after reading it from the configuration file
	 */
	public static String getBaseDirLocation()
	{
		return baseDirAddress;
	}

	/**
	 * Return the maximum file size to be allowed for user to upload  
	 *  
	 * @return maxFileSize return its value after reading it from the configuration file
	 */
	public static int getMaxFileSize()
	{
		return maxFileSize;
	}
	
	/**
	 * Return the core pool size required for thread pool creation
	 * 
	 * @return corePoolSize return its value after reading it from the configuration file
	 */
	public static int getCorePoolSize()
	{
		return corePoolSize;
	}

	/**
	 * This method returns the max pool size required for thread pool creation
	 * 
	 * @return maxPoolSize return its value after reading it from the configuration file
	 */
	public static int getMaxPoolSize()
	{
		return maxPoolSize;
	}
	
	
	/**
	 * This method returns the keep alive time of threads required for thread pool creation
	 * 
	 * @return keepAliveTime return its value after reading it from the configuration file
	 */
	public static int getKeepAliveTime()
	{
		return keepAliveTime;
	}
	
	
	/**
	 * This method returns the waiting size queue required for thread pool creation
	 * 
	 * @return waitingQueueSize return its value after reading it from the configuration file
	 */
	public static int getWaitingQueueSize()
	{
		return waitingQueueSize;
	}
	
	
	
	/**
	 * This method returns the time for which the server will wait for a running threads to finish execution before shutting them down.
	 * 
	 * @return timeOutTime return its value after reading it from the configuration file
	 */
	public static int getTimeOutTime()
	{
		return timeOutTime;
	}
	
	/** 
	* This method extracts the extension from the file name given,and searches for the corresponding MYME type & returns it. Returns null is MIME type for a file is not found.
	* 
	* @param fileName the name of the file uploaded by the client
	* @return the MIME type corresponding to the extension of the file 
	*/
	public static String getMimeType(String fileName)
	{
		String extension=fileName.substring(fileName.lastIndexOf(".")+1);									
		extension=extension.toLowerCase();
		//find out the content type of the requested file from configuration file
		return prop.getProperty(extension);
	}

	/**
	 * This method creates a server socket connection on the specified port and IP address with the backlog size as read from the property file. 
	 * Returns the newly created server socket.
	 * <p>If server socket cannot be opened properly the error is logged & the program is terminated.
	 * 
	 * @return a server socket
	 */

	public static ServerSocket openSocketConnection()
	{ 
		log.info("Starting Web Server.\n");
		ServerSocket serverSocketConnection = null;
		try {
			serverSocketConnection=new ServerSocket(port,backLog,bindAddress);
			log.info("Listening on port: "+port+"\n");
			System.out.println("Server is running.\n\n");
		} catch(BindException e) {
			log.error("Could not bind to local address & port. Port may be already in use.");
			System.exit(1);
		}catch (IOException e) {
			log.error("Could Not listen on port: "+port);
			System.exit(1);
		}		
		return serverSocketConnection;
	}
}
