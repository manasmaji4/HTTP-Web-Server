package manas.maji.WebServer;

import org.apache.log4j.Logger;
import java.io.*;
import java.net.Socket;

/**
 * This class handles the DELETE request for a file from the client. The delete operation is applicable for files in repository only. 
 * For delete request of server files, file not found error will be sent to the client. For any error while deleting the file,internal server error will be reported to the client.
 *  
 * @author maji
 */
public class DELETE{
	static Logger log=Logger.getLogger(DELETE.class);
	String fileNameString;
	Response responseToClient=new Response();
	Utility util=new Utility();
	String baseDirLocation;
	
	/**
	 * Class Constructor initializing the base directory location & the file path extracted from the HTTP request. 
	 * <p>If file is not found in the repository the client is appropriately informed.
	 * <p>Success or Failure of the delete operation is reported to the client.
	 * 
	 * @param fileLocation- the file path as requested by the client
	 * @param dirLocation- the base directory location as read from the property file
	 */
	DELETE(String fileLocation,String dirLocation) {
		fileNameString = fileLocation;	
		baseDirLocation=dirLocation;
	}		  

	//////////////////////////////////////////////// Handle the DELETE request////////////////////////////////////////////////
	/**
	 * This method handles the DELETE request,returning the success/failure status of the delete operation as response to the client. 
	 * <p>In case the file is not found,the client is sent a response denoting so.
	 * 
	 * @param client the socket connection with the client
	 * @throws IOException if exception arises in calling other class methods
	 * @throws NullPointerException if client connection is terminated,output stream will be null
	 */
	public void deleteHandle(Socket client) throws IOException, NullPointerException
	{	
		log.info("Inside DELETE\n");
		File fileToDelete = new File(baseDirLocation+fileNameString);
		//check if file exists
		if(!fileToDelete.exists())																			
		{
			responseToClient.fileNotFound(client.getOutputStream());
			return;
		}
		//delete file & print success response 
		if(fileToDelete.delete())																					
		{
			responseToClient.fileDeleted(client.getOutputStream(),fileNameString.substring(fileNameString.indexOf("/")));
			util.generateDownloadPage();																		
		}
		//if delete was unsuccessful
		else
			responseToClient.internalServerError(client.getOutputStream());
		//alter download page to reflect necessary change
		log.info("Exiting DELETE");
	}
}