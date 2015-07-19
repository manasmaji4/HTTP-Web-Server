package manas.maji.WebServer;

import org.apache.log4j.Logger;
import java.io.*;
import java.net.Socket;
import java.net.URLDecoder;

/**
 * This class handles a client thread(signifying a socket connection with a client), processes the client request,checks for its validity,
 * tries to handles it according to the method requested.
 * <p>If the request sent is an invalid one,the client is made aware of it.
 * <p>After handling each request the client connection is closed.
 * 
 * @author maji
 *
 */
class ClientThread implements Runnable {
	static Logger log=Logger.getLogger(ClientThread.class);	
	Socket client;
	InputStream inputStream;
	String requestString = "";
	Response responseToClient=new Response();
	Utility util=new Utility();
	
	/**
	 * Class Constructor initializing the client socket connection,initializes the input stream of the Utility class with the client socket connection input stream.
	 * <p>It also reads the first line from the client request which will then be used for validity checking & resolving the request.	
	 * 
	 * @param clientSocket the socket connection with the client
	 */
	ClientThread(Socket clientSocket) 
	{	
		this.client = clientSocket;	
		try {
			inputStream = client.getInputStream();
			util.setInputstream(inputStream);
			requestString=util.readLine();
			log.info("First Line of request: "+requestString+"\n");
		}catch(NullPointerException e){
			log.info("Thread Stopped. "+e);
			closeClientConnection();									
		}catch (IOException e) {
			log.info("Could not read from client socket input stream.");
			closeClientConnection();									
		}
	}		  
   	
	/**
	 * This method parses the request to identify the HTTP method,the HTTP version & get the file path from the first line of the request.
	 * <p>The HTTP method & the HTTP protocol is checked for their validity,if found invalid the client is reported about it & the client connection terminated.
	 * 
	 * @exception IOException 1.If exception arises in calling functions in other classes. 
	 * 						   2.If there is error in closing client socket connection
	 */
	public void run() {
   		log.info("Inside Run.\n");
   		try {
   			String baseDirLocation=StartupUtility.getBaseDirLocation();
   			// parsing the request headers
   			if(requestString==null || requestString.equals("") || requestString.equals("\r\n"))
			{
				closeClientConnection();	
				return;
			}
			System.out.println(requestString);
   			String[] requestParamaters=requestString.split(" ");
   			byte[] buffer=new byte[Constants.bufferSize*2];
   			//check for BAD request
   			if(requestParamaters.length==3 && checkForValidHttpProtocol(requestParamaters[2]))
   			{
   				//decode the URL encoding if any (done by the browser)
   				requestParamaters[1]=URLDecoder.decode(requestParamaters[1], "UTF-8");
   				
///////////////////////////////////////////////////////////////////////// Processing GET request //////////////////////////////////////////////////////////////////////////
   			
	   			if(requestParamaters[0].equals("GET"))
	   			{	
	   				GET get=new GET(requestParamaters[1],baseDirLocation);
	   				log.info("GET");
	   				get.getHandle(client);
	   				inputStream.read(buffer, 0, buffer.length);
	   			}
	   			
/////////////////////////////////////////////////////////////////////////// Processing POST request /////////////////////////////////////////////////////////////////////////////   			
   			
	   			else if(requestParamaters[0].equals("POST"))								
	   			{
	   				POST post=new POST();
	   				log.info("POST");
	   				post.setInputOutputStream(client);
	   				post.postHandle(baseDirLocation);
	   				while(inputStream.read(buffer, 0, buffer.length)!=-1);
	   			}
	   			
/////////////////////////////////////////////////////////////////////////// Processing DELETE request /////////////////////////////////////////////////////////////////////////////   			

				else if(requestParamaters[0].equals("DELETE"))								
				{
					DELETE delete=new DELETE(requestParamaters[1],baseDirLocation);
					log.info("DELETE");
					delete.deleteHandle(client);
	   				inputStream.read(buffer, 0, buffer.length);
				}
	   			
//////////////////////////////////////////////////////////////////////////// Processing HEAD request ////////////////////////////////////////////////////////////////////////////
   			
				else if(requestParamaters[0].equals("HEAD"))
				{
					HEAD head=new HEAD(requestParamaters[1],baseDirLocation);
					log.info("HEAD");
					head.headHandle(client);
	   				inputStream.read(buffer, 0, buffer.length);
				}
   			
////////////////////////////////////////////////////////////////////////////Processing OPTIONS request ////////////////////////////////////////////////////////////////////////////
   			
				else if(requestParamaters[0].equals("OPTIONS"))
				{
					log.info("OPTIONS");
					OPTIONS options=new OPTIONS();
					options.optionsHandle(client);
	   				inputStream.read(buffer, 0, buffer.length);
				}

/////////////////////////////////////////////////////////////////////////// Processing Not Implemented requests /////////////////////////////////////////////////////////////////////////////   			
			
				else{
					responseToClient.notImplemented(client.getOutputStream());
				}
   			}
   		}catch(NullPointerException e){
			log.info("Thread Stopped. "+e);
		} catch (IOException e) {
 			log.error("Client Thread Failed"+client.getPort()+"\n");
		}catch(Exception e){
 			log.error("Client Thread Error"+e);			
		}
		//close client socket connection
   		closeClientConnection();
   	}
	
	
	/**
	 * This method checks if the HTTP protocol mentioned in the request is a valid one and sends the true/false value accordingly.
	 * 
	 * @param httpProtocol the specified HTTP protocol
	 * @return boolean value denoting the validity of the specified HTTP protocol version
	 * @throws IOException 
	 */
	boolean checkForValidHttpProtocol(String httpProtocol) throws IOException
	{
		if(httpProtocol.equals("HTTP/1.1") || httpProtocol.equals("HTTP/1.0"))
			return true;
		else{
			log.info("Bad Request. Http version error.");
			responseToClient.httpVersionNotSupported(client.getOutputStream());
			return false;
		}
	}
	
	/**
	 * This method closes the socket connection with the client. For any new request sent by the client a new connection with the client will be setup.
	 */
	void closeClientConnection()
	{
   		log.info("Closing the Socket Connection");
		try {
			client.close();
		} catch (IOException e) {
			log.info("Socket Closing Error.");
		}
	}
}