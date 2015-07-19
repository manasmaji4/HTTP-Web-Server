package manas.maji.WebServer;

import org.apache.log4j.Logger;
import java.io.*;

/**
 * This class contains methods that send appropriate response to the client according to the outcome of the different operation requests. 
 * These methods are called from the the classes handling the individual method requests. Request are made as a html file containing the response,
 * & sent to be written to the output stream of the client connection.
 * 
 * @author maji
 */

public class Response{
	static Logger log=Logger.getLogger(Response.class);
	String htmlStart= "<html><head>"+ "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\">"	+ "</head><body background=\"/webserver.jpg\"><h2>"+Constants.serverName+"</h2><div>";
	String htmlEnd="</div></body></html>";
	String htmlLinksEnd="</div><br><hr><br><br><br><br><br><br><br>"
						+"<center><a href=\"home.html\" target=\"_self\"><h4>Goto Home</h4></a><br><br><br>"
						+"<a href=\"upload.html\" target=\"_self\">Goto <bold>Upload</bold> Page</a><br><br>"
						+"<a href=\"download.html\" target=\"_self\">Goto Download Page</a></center>"
						+"</body></html>";
		
////////////////////////////////////////////////////////////////Response for GET & HEAD /////////////////////////////////////////////////////////////////////
	
	// File NOT FOUND
	/**
	 * This methods send the File Not Found message to the user in case the file requested by the user is not a available in the server repository.
	 * 
	 * @param clientOutputStream socket output stream of the client
	 */
	
	public void fileNotFound(OutputStream clientOutputStream) 					
	{
		String stringContent = htmlStart+"404 - Not Found"+htmlEnd;
		String responseCode = "404 Not Found";
	    sendResponseToClient(stringContent.getBytes(),clientOutputStream,responseCode,Constants.htmlContentType); 
	}


	// BASE DIRECTORY NOT FOUND
	/**
	 * This methods send the Base Directory Not Found message to the user in case the server repository location cannot be determined.
	 * 
	 * @param clientOutputStream socket output stream of the client
	 */
	
	public void baseDirectoryNotFound(OutputStream clientOutputStream) 					
	{
		String stringContent = htmlStart+"500 Base Directory Not Found.Please set base directory location."+htmlEnd;
		String responseCode = "500 Base Directory Not Found";
	    sendResponseToClient(stringContent.getBytes(),clientOutputStream,responseCode,Constants.htmlContentType); 
	}

	
	//FILE TYPE NOT SUPPORTED
	/**
	 * This method tells the client that the content type for the requested file cannot be resolved. A client can add the MIME type of the required file to the property file & try again. 
	 *  
	 * @param clientOutputStream socket output stream of the client
	 */
	public void fileTypeNotSupported(OutputStream clientOutputStream) 					
	{
		String stringContent = htmlStart+"500 Could Not Resolve File Type."+htmlEnd;
		String responseCode = "500 File Type Not Supported";
	    sendResponseToClient(stringContent.getBytes(),clientOutputStream,responseCode,Constants.htmlContentType); 
	}

	// OK RESPONSE for GET
	/**
	 * Send OK response for GET operation. The body of the requested file is also sent as argument.
	 * 
	 * @param byteContent requested file as byte array
	 * @param clientOutputStream socket output stream of the client
	 * @param contentType content type of the requested file
	 */
	
	public void responseOkGET(byte[] byteContent,OutputStream clientOutputStream,String contentType)					
	{   
		sendResponseToClient(byteContent,clientOutputStream,"200 OK", contentType);
	}


	
	// OK RESPONSE for HEAD
	/**
	 * Send OK response for HEAD operation. The header information is sent as arguments.
	 * 
	 * @param file_length file size of the specified file
	 * @param clientOutputStream socket output stream of the client
	 * @param contentType content type of the specified file
	 */
	
	public void responseOkHEAD(int file_length,OutputStream clientOutputStream,String contentType)					
	{   
		sendResponseToClientHEAD(file_length,clientOutputStream,"200 OK", contentType);
	}
	
	
	
////////////////////////////////////////////////////////////// Response for OPTIONS ////////////////////////////////////////////////////////////////////////
	
	// OK RESPONSE for OPTIONS	
	/**
	 * Send OK response for OPTIONS operation.
	 * 
	 * @param content Allowed options on the specified file
	 * @param clientOutputStream socket output stream of the client
	 */

	public void responseOkOPTIONS(String content,OutputStream clientOutputStream)								
	{   
	    sendResponseToClientOPTIONS(content,clientOutputStream,"200 OK");
	}

	
	
/////////////////////////////////////////////////////////////// Response for DELETE ////////////////////////////////////////////////////////////////////////
	
	// File DELETED
	/**
	 * This method sends success response to the client corresponding to a successful delete operation on the specified file 
	 * 
	 * @param clientOutputStream socket output stream of the client
	 * @param fileName name of the file the user sent a delete request for
	 */

	public void fileDeleted(OutputStream clientOutputStream,String fileName) 					
	{
		String stringContent = htmlStart+"200 - "+fileName+" Deleted"+htmlEnd;
		String responseCode = "200 OK";
	    sendResponseToClient(stringContent.getBytes(),clientOutputStream,responseCode,Constants.htmlContentType); 
	}
	
	
	
///////////////////////////////////////////////////////////////Response for POST //////////////////////////////////////////////////////////////////////
	
	// File ALREADY EXISTS
	/**
	 * This method sends appropriate response to the client when a one tries to overwrite a file using POST method
	 * 
	 * @param clientOutputStream socket output stream of the client
	 * @param uploadedFileName the name of the file the client is uploading
	 * @throws IOException 
	 */

	public void fileAlreadyExists(OutputStream clientOutputStream,String uploadedFileName) throws IOException					
	{
		String stringContent =htmlStart+"409 A file with same name already exists. Cannot be overwritten.<br><br>"
							+"<a href=\""+uploadedFileName+"\" target=\"_self\">"+uploadedFileName+"</a><br>"+htmlLinksEnd;
		String responseCode = "409 Conflict";
	    sendResponseToClient(stringContent.getBytes(),clientOutputStream,responseCode,Constants.htmlContentType);
	    clientOutputStream.flush();
	}
	
	
	//File Size Exceeds
	/**
	 * This method tell the client that the uploaded file size exceeds the permissible limit.
	 * 
	 * @param clientOutputStream socket output stream of the client
	 * @param maxSize the maximum permissible file size
	 * @param uploadedFileName the name of the file the client is uploading
	 */
	public void fileSizeExceedsLimit(OutputStream clientOutputStream, int maxSize,String uploadedFileName)				
	{
		String stringContent = htmlStart+"409 - "+uploadedFileName+" size exceeds limit. Can only upload files upto "+maxSize +"bytes "+htmlLinksEnd;
		String responseCode = "409 Conflict";
	    sendResponseToClient(stringContent.getBytes(),clientOutputStream,responseCode,Constants.htmlContentType); 
	}

	
	
	// No File Attached
	/**
	 * This method tells the client to attach a file to the POST/PUT if he sends an empty POST/PUT request  
	 * 
	 * @param clientOutputStream socket output stream of the client
	 */
	
	public void noFileError(OutputStream clientOutputStream)
	{
		String stringContent = htmlStart+"400 NO FILE CHOSEN<br>Please choose a file and try again."+htmlLinksEnd;
		String responseCode = "400 Bad Request";
	    sendResponseToClient(stringContent.getBytes(),clientOutputStream,responseCode,Constants.htmlContentType); 		
	}
	
	
	
	// File UPLOADED CORRECTLY
	/**
	 * This method sends success response to the client corresponding to a POST/PUT operation of the uploaded file 
	 * 
	 * @param clientOutputStream socket output stream of the client
	 * @param uploadedFileName the name of the file the client is uploading
	 */

	public void fileUploaded(OutputStream clientOutputStream,String uploadedFileName) 					
	{
		String stringContent = htmlStart+"200 - "+uploadedFileName+" Successfully Uploaded.<br><br>"+"<a href=\""+uploadedFileName+"\" target=\"_self\">"+uploadedFileName+"</a><br>"+htmlLinksEnd;
		String responseCode = "201 OK";
	    sendResponseToClient(stringContent.getBytes(),clientOutputStream,responseCode,Constants.htmlContentType); 
	}

		
	// INTERNAL SERVER ERROR
	/**
	 * This method sends response to the client corresponding to a failed file upload attempt via PUT/POST operation 
	 * 
	 * @param clientOutputStream socket output stream of the client
	 */
	public void internalServerError(OutputStream clientOutputStream)									 					
	{
		String stringContent = htmlStart+"500 Internal Server Error"+htmlEnd;
		String responseCode = "500 Internal Server Error";
	    sendResponseToClient(stringContent.getBytes(),clientOutputStream,responseCode,Constants.htmlContentType); 
	}

	

////////////////////////////////////////////////////////////////////Response for Not Implemented///////////////////////////////////////////////////////////
	
	//METHOD Not implemented response
	/**
	 * This method informs the client if the requested HTTP operation is not supported in the server
	 * 
	 * @param clientOutputStream socket output stream of the client
	 */
	public void notImplemented(OutputStream clientOutputStream)				 
	{ 
		String stringContent=htmlStart+"501 Not Implemented"+htmlEnd;
	    String responseCode="501 Not Implemented"; 
	    sendResponseToClient(stringContent.getBytes(),clientOutputStream,responseCode,Constants.htmlContentType); 
	}
				
	

	//HTTP Version Not supported
	/**
	 * This method informs the client if the requested HTTP version is not supported by the server
	 * 
	 * @param clientOutputStream socket output stream of the client
	 */
	public void httpVersionNotSupported(OutputStream clientOutputStream) 
	{
		String stringContent=htmlStart+"505 Http Version Not Supported"+htmlEnd;
	    String responseCode="505 Http Version Not Supported"; 
	    sendResponseToClient(stringContent.getBytes(),clientOutputStream,responseCode,Constants.htmlContentType); 
	}
	
////////////////////////////////////////////////////////////////////Send Response To clients//////////////////////////////////////////////////////////////
	
	/**
	 * This method writes the response to the client socket, along with required header informations
	 * 
	 * @param byteContent The body of the response as byte array
	 * @param clientOutputStream socket output stream of the client
	 * @param responseCode The appropriate response code to be attached to the response sent to the client
	 * @param contentType The content type of the response body
	 * @exception IOException error in writing to client socket connection output stream
	 * @throws NullPointerException if client connection is terminated,output stream will be null
	 */
	
	private void sendResponseToClient(byte[] byteContent,OutputStream clientOutputStream,String responseCode, String contentType) throws NullPointerException {
		try {
			//call sendResponseToClient_HEAD() to write the header portion of the response
			sendResponseToClientHEAD(byteContent.length,clientOutputStream, responseCode, contentType);
			//write the actual body of the response
			clientOutputStream.write(byteContent);
		} catch (IOException e) {
			log.error("Could not send response to client: "+e+"\n");
		}
	}

	
	
///////////////////////////////////////////////////////////// FOR HEAD /////////////////////////////////////////////////////////////////////////////////////	
	
	/**
	 * This method writes the required header informations as response to the client socket for HEAD operation 
	 * 
	 * @param contentLength size of the specified file
	 * @param clientOutputStream socket output stream of the client
	 * @param responseCode The appropriate response code to be attached to the response sent to the client
	 * @param contentType The content type of the response body
	 * @exception IOException error in writing to client socket connection output stream
	 * @throws NullPointerException if client connection is terminated,output stream will be null
	 */

	private void sendResponseToClientHEAD(int contentLength,OutputStream clientOutputStream,String responseCode, String contentType) throws NullPointerException {
		try {
			//create the Header
			String stringHeader = "HTTP/1.1 " + responseCode + "\r\n"									
					+ "Server: "+Constants.serverName+"\r\n" 
					+ "Content-Length: " + contentLength + "\r\n"
					+ "Connection: close\r\n" 
					+ "Content-Type: " + contentType + "\r\n\r\n";
			//write the Header onto the Client Socket
			clientOutputStream.write(stringHeader.getBytes());								
		} catch (IOException e) {
			log.error("Could not send response to client: "+e+"\n");
		}
	}

	
	
///////////////////////////////////////////////////////////// For OPTIONS //////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * This method writes the required header informations as response to the client socket for OPTIONS operation
	 *  
	 * @param options_Available The HTTP operations allowed of the specified resource
	 * @param clientOutputStream socket output stream of the client
	 * @param responseCode The appropriate response code to be attached to the response sent to the client
	 * @exception IOException error in writing to client socket connection output stream
	 * @throws NullPointerException if client connection is terminated,output stream will be null
	 */

	private void sendResponseToClientOPTIONS(String options_Available,OutputStream clientOutputStream,String responseCode) throws NullPointerException {
		try {
			String stringHeader = "HTTP/1.1 " + responseCode + "\r\n"									
					+ "Server: "+Constants.serverName + "\r\n" 
					+ "Access-Control-Allow-Methods: "+options_Available + "\r\n"
					+ "Connection: close\r\n" ;
			//write the Header & the response onto the Client Socket
			clientOutputStream.write(stringHeader.getBytes());								
		} catch (IOException e) {
			log.error("Could not send response to client: "+e+"\n");
		}
	}
}