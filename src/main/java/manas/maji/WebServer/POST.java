package manas.maji.WebServer;

import java.io.*;
import java.net.Socket;

import org.apache.log4j.Logger;

/**
 * This class handles the POST request from the client,creates the file(if not existing) sends a suitable response to the client. 
 * The file size is read from the request header & the file name is read from the request body.
 * <p>The actual file content is read from the request body by omitting the file information(beginning boundary, file name, file type, end boundary).
 * The content if then written to the newly created file in the repository.
 * 
 * @author maji
 *
 */
public class POST{
	Logger log=Logger.getLogger(POST.class);
	
	Response responseToClient=new Response();
	Utility util=new Utility();	
	/**
	 * This variable stores the content length of the request sent by the POST method 
	 */
	int contentLength;
	/**
	 * This input stream from the client request 
	 */
	InputStream inputStr;
	OutputStream outStr;
	int maxFileSize=StartupUtility.getMaxFileSize();
	
	/**
	 * Class Constructor initializing the input stream & output stream from the client request
	 * 
	 * @param client the client socket connection
	 * @throws IOException error in getting the input/output stream of the client connection
	 */
	public void setInputOutputStream(Socket client) throws IOException , NullPointerException
	{
		this.outStr=client.getOutputStream();
		this.inputStr =client.getInputStream();
	}


////////////////////////////////////////////////////////Handle The POST request ///////////////////////////////////////////////////////////////////////
	/**
	 * This method handles the POST request,gets the content length of the request body from the request header,checks if the file size exceeds the allowed file size,extracts the boundary length and initializes the cutoff as its value.
	 * <p>The file name is extracted from the request,it checks if the file is already existing & sends a link to the file is found to be existing,else the file is created from the file from the content sent in the request body. 
	 * It sends back an appropriate response to the client based on the status of file creation.
	 * 
	 * @param baseDirLocation the location of the server repository,where the user uploaded files will be stored
	 * @throws IOException if exception arises in calling other class methods.
	 * @throws NullPointerException if client connection is terminated,input stream will be null
	 */
	
	public void postHandle(String baseDirLocation) throws IOException, NullPointerException
	{
		log.info("Inside POST\n");		
		util.setInputstream(inputStr);
		
		int cutOff=getContentLengthCutOff();
		String uploadedFileName=getFileNameAdjustContentLength(cutOff);
		if(contentLength>maxFileSize)
		{
			log.info("Uploaded File Size exceeds set limit");
			//tell the client that the uploaded file size exceeded limit
			responseToClient.fileSizeExceedsLimit(outStr,maxFileSize,uploadedFileName);
		}	
		else
		{
			if(uploadedFileName.equals(""))
			{
				responseToClient.noFileError(outStr);
			}
			else{
				log.info("POST FileName="+uploadedFileName+" File Size="+(contentLength-cutOff)+"\n");
		
				File file = new File(baseDirLocation+"/"+uploadedFileName);
				//if file already exists in repository, show required message to user
				if(file.exists())																		
				{
					responseToClient.fileAlreadyExists(outStr,uploadedFileName);
				}	
				//create the file if file doesn't exist
				else{
					//error in creating file
					if(!createFile(file,contentLength))			
					{
						responseToClient.internalServerError(outStr);
					}
					else
					{
						log.info("Successfully Created file "+uploadedFileName+"\n");
						//change the download page
						util.generateDownloadPage();															
						//tell client that file upload was successful
						responseToClient.fileUploaded(outStr,uploadedFileName);													
					}
				}
			}
		}
		log.info("Exiting POST\n");
	}
	
	///////////////////////////////////////// Get Content Length From Header & set the CutOFF that needs to be adjusted/////////////////////////////////

	/**
	* This method parses the POST request header to get the content-length of the file uploaded. 
	* It returns the cutoff(i.e. the size of the boundary that need to be adjusted while reading the request body).
	* 
	* @return cutOff the boundary size that need to be adjusted
	* @throws NullPointerException if client connection is terminated,input stream will be null
	*/
	public int getContentLengthCutOff() throws NullPointerException
	{
		String content = null;
		//the cutoff is initialized to 14 as while calculating the length of the extra bytes in the request body the length function doesnt take the \r\n 
		int index,cutOff=14;
		content=util.readLine();
		while(!content.trim().isEmpty())
		{
			if(content.contains("Content-Length"))														
			{
				index=content.indexOf("Content");
				//extracting the content length from the request
				contentLength=Integer.parseInt(content.substring(index+16));			 
			}
			if(content.contains("--"))														
			{
				index=content.indexOf("--");
				//calculating the cutOff
				cutOff=cutOff+content.substring(index).length();								
			}
			content=util.readLine();
		}
		return cutOff;
	}
	

	///////////////////////////////////////// Get the Name Of the Uploaded file & adjust the content length
	
	/**
	* This method extracts the name of the uploaded file from the request & returns it.
	* It also adjusts the content-length to exclude the length of the info about the uploaded file (i.e. the file name,file type & other such data)
	* 
	* @param cutOff the value by which the content length will be adjusted to get the actual content of the uploaded file
	* @return upoadedFileName the name of the uploaded file
	* @throws NullPointerException if client connection is terminated,input stream will be null
	*/
	
	public String getFileNameAdjustContentLength(int cutOff) throws NullPointerException
	{
		int countSize=0;
		String uploadedFileName = null;
		String 	content;
		
		for(int count=0;count<4;count++)
		{
			content=util.readLine();
			countSize=countSize+content.getBytes().length;
			if(content.contains("filename"))														
			{
				int index=content.indexOf("filename");
				//extracting the filename from the request 
				uploadedFileName=content.substring(index+10, content.lastIndexOf("\""));			
				if(uploadedFileName.contains("\\"))
				{
					uploadedFileName=uploadedFileName.substring(uploadedFileName.lastIndexOf("\\")+1);
				}
			}
		}
		contentLength=contentLength-countSize-cutOff;
		return uploadedFileName;
	}


/////////////////////////////////////////////// Create the File uploaded into the server repository ///////////////////////////////////

	/**
	* This method reads from the request body to get the content of the file uploaded by the user, then create a new file of the same name as the uploaded file in the server repository.
	* The file content is then written to the newly created file. Success or failure message is returned as result.
	* 
	* @param fileToBeCreated file instance of the file that is to be created from the user uploaded file content
	* @param fileSize size of the file uploaded by the user
	* @return boolean value denoting success/failure in file creation
	* @exception IOException error while reading & writing to and from file
    * @throws NullPointerException if client connection is terminated,input stream will be null
	*/
	public boolean createFile(File fileToBeCreated,int fileSize) throws  NullPointerException
	{
		try{
			FileOutputStream fileOutStream = new FileOutputStream(fileToBeCreated);	
			
			//create file  
			byte[] buffer = new byte[Constants.bufferSize];
			int count = 0;
			while(fileSize>1024)
			{
				count=inputStr.read(buffer, 0, Constants.bufferSize);
				fileOutStream.write(buffer, 0, count);
				fileSize-=count;
			}
			count=inputStr.read(buffer, 0, fileSize);
			fileOutStream.write(buffer, 0, fileSize);
			//close the file output stream
			fileOutStream.close();
			//to read the remaining part of the request
		}catch(IOException e){
			log.info("Error in Creating File. "+e);
			return false;
		}	
		return true;		
	}
}

