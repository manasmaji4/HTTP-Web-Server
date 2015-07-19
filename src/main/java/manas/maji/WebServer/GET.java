package manas.maji.WebServer;

import java.io.*;
import java.net.Socket;

/**
 * This class handles the GET request from the client,fetches the file from among the server file(if file is present there) or from the repository of uploaded files.
 * If the file is not found the user is sent a response denoting so.
 * <p>If not file name is provided the the home page is displayed by default. Checking is done for separating query(?) from the get request & the query part is ignored.
 * <p>For those files whose MIME types cannot be resolved by the server appropriate sesponse is sent to the client.
 * 
 * @author maji
 *
 */
public class GET extends GETHEADBaseHandler{
	
	/**
	 * Class Constructor initializing the base directory location & the file path extracted from the HTTP request. 
	 * 
	 * @param fileLocation the file path as requested by the client
	 * @param dirLocation the base directory location as read from the property file
	 */
	GET(String fileLocation,String dirLocation) {	
		fileNameString = fileLocation;	
		baseDirLocation=dirLocation;
	}		  
	  
	
	///////////////////////////////////////////////// Handle the GET request //////////////////////////////////////////////////////////////
	/**
	 * This method handles the GET request.If no file name is found,it GETs the home page of the server by default. Queries (?****) are ignored & the original file is server as it is. 
	 * <p>The MIME type for the requested file is derived from the property file. If content type is not found for a file, File Not Supported message is sent to the client.
	 * <p>It fetches the requested file(if found),converts it to byte array to be sent to the client along with response.
	 * If the file is not found, File Not Found Message is sent to the client.
	 * 
	 * @param client the socket connection with the client
	 * @throws IOException if exception arises in calling other class methods
	 * @throws NullPointerException if client connection is terminated,output stream will be null
	 */
	public void getHandle(Socket client) throws IOException, NullPointerException
	{
		log.info("Inside GET\n");
		
		fileNameOperation();
		String contentType=StartupUtility.getMimeType(fileName);
		File requestedFile=getFile(fileName);
		if(requestedFile==null)
		{
			responseToClient.fileNotFound(client.getOutputStream());
		}
		if(requestedFile!=null && contentType==null)
		{
			responseToClient.fileTypeNotSupported(client.getOutputStream());		
		}
		//if file exists
		else
		{
			InputStream fileStream=new FileInputStream(requestedFile);
			//convert buffer to byte array
			byte[] fileInBytes = getFileAsByteArray(fileStream);			
			fileStream.close();
			log.info("GET filename="+fileName+" type= "+contentType+" size="+fileInBytes.length+"\n");
			// send OK response along with the file
			responseToClient.responseOkGET(fileInBytes,client.getOutputStream(), contentType);									
		}	
		log.info("Exiting GET. Served file "+fileName);
	}
	
	
//////////////////////////////////////////////////////Get file as Byte[] ///////////////////////////////////////////////////////////////

	/**
	* In this method file requested by the client is read from the file stream into a byte buffer, the buffer is written into a output stream which 
	* is returned as a byte array.This file will then be sent over the client socket connection as response to the client query.
	* 
	* @param fileStream the requested file as Input Stream 
	* @return requested file as byte array
	* @exception IOException for error while reading from file
	*/
	public byte[] getFileAsByteArray(InputStream fileStream)
	{
		int count=-1;
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		byte[] buffer = new byte[Constants.bufferSize];																
		//read file into a buffer from stream
		try {
			while ((count=fileStream.read(buffer)) != -1)
			{
			outStream.write(buffer,0,count);
			}
		} catch (IOException e) {
			log.info("GET:File Reading Error.");
		}//convert buffer to byte array
		return(outStream.toByteArray());																
	}
}
	