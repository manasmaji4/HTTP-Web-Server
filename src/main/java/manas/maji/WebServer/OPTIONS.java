package manas.maji.WebServer;

import org.apache.log4j.Logger;
import java.io.*;
import java.net.Socket;


/**
 * This class handles the OPTIONS request from the client. Its specifies the HTTP methods allowed by the server on resources.
 * This web server implements OPTIONS,GET,HEAD,POST & DELETE methods.
 * 
 * @author maji
 */
public class OPTIONS{
	static Logger log=Logger.getLogger(OPTIONS.class);
	Response responseToClient=new Response();
	
	//////////////////////////////////////////////// Handle the OPTIONS request ////////////////////////////////////////////////////////////

	/**
	 * This method handles the GET request,returning the HTTP methods allowed by the server response to the client.
	 * The server allows GET,HEAD,POST,PUT,DELETE,OPTIONS methods.
	 * 
	 * @param client the socket connection with the client
	 * @throws IOException if exception arises in calling response methods
	 * @throws NullPointerException if client connection is terminated,output stream will be null
	 */
	public void optionsHandle(Socket client) throws IOException, NullPointerException
	{
		responseToClient.responseOkOPTIONS(Constants.allowedMethods,client.getOutputStream());
		log.info("Exiting OPTIONS");
	}
}