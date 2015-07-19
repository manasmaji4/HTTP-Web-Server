package manas.maji.WebServer;

import java.io.*;
import java.net.Socket;

/**
 * This class handles the HEAD request from the client. For any file,it returns the file size, the file type. The file body is not sent in the response(as is the case in GET)
 * 
 * @author maji
 *
 */
public class HEAD extends GETHEADBaseHandler{
	
	/**
	 * Class Constructor initializing the base directory location & the file path extracted from the HTTP request. 
	 * 
	 * @param fileLocation- the file path as requested by the client
	 * @param dirLocation- the base directory location as read from the property file
	 */
	HEAD(String fileLocation,String dirLocation) {	
		fileNameString = fileLocation;	
		baseDirLocation=dirLocation;
	}		  

	//////////////////////////////////////////////// Handle the HEAD request////////////////////////////////////////////////
	/**
	 * This method handles the HEAD request,sending the info about the requested file(if found) to be sent to the client as response.
	 * Sent header includes the MIME type,the file size.
	 * <p>The MIME type for the requested file is derived from the property file. If content type is not found for a file, File Not Supported message is sent to the client.
	 * <p>It fetches the requested file(if found),converts it to byte array to be sent to the client along with response.
	 * If the file is not found, File Not Found Message is sent to the client.
	 * 
	 * @param client the socket connection with the client
	 * @throws IOException if exception arises in calling other class methods
	 * @throws NullPointerException if client connection is terminated,output stream will be null
	 */
	public void headHandle(Socket client) throws IOException, NullPointerException
	{
		log.info("Inside HEAD\n");
		fileNameOperation();
		
		String contentType=StartupUtility.getMimeType(fileName);
		File requestedFile=getFile(fileName);
		if(requestedFile==null)
		{
			responseToClient.fileNotFound(client.getOutputStream());
		}
		else if(contentType==null)
		{
			responseToClient.fileTypeNotSupported(client.getOutputStream());
		}
		//if file exists
		else
		{
			log.info("HEAD filename="+fileName+" type= "+contentType+" size="+requestedFile.length()+"\n");
			// send OK response along with the file
			responseToClient.responseOkHEAD((int)requestedFile.length(),client.getOutputStream(), contentType);		
		}
		log.info("Exiting HEAD");
	}
}