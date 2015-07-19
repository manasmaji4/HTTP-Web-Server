package manas.maji.WebServer;

import java.io.File;

import org.apache.log4j.Logger;

/**
 * This class handles the operations common to both GET & HEAD method such as getting the file instance from the file path supplied by the user, 
 * getting the MIME type corresponding to the file extension
 * 
 * @author maji
 *
 */
public class GETHEADBaseHandler {
	Logger log=Logger.getLogger(GETHEADBaseHandler.class);
	String fileNameString,fileName;
	Response responseToClient=new Response();
	String baseDirLocation;
	
	public void fileNameOperation()
	{
		fileName=fileNameString.substring(fileNameString.lastIndexOf("/")+1);	
		if(fileNameString.equals("/"))
		{
			fileNameString="/"+Constants.homePage;
			fileName=Constants.homePage;
		}
		if(fileName.contains("?"))
		{
			fileNameString=fileNameString.substring(0,fileName.indexOf("?")+1);
			fileName=fileName.substring(0,fileName.indexOf("?"));
		}
	}
	
//////////////////////////////////////////////////Get the file instance from class path //////////////////////////////////////////////////////
	
	/**
	* This method get the file instance of the requested by the user,returns the requested file instance 
	* If file in not found then null is returned.
	*  
	* @param fileName name of the file requested by the client (or null if file is not found)
	* @return the requested file
	*/
	public File getFile(String fileName)
	{
		//look for file in base directory
		File file=new File(baseDirLocation+fileNameString);
		if(!file.exists())
		{
			//if file is a server file
			file = new File(Constants.serverFilesFolder+fileNameString);
			if(!file.exists())
			{
				file = new File(Constants.javaDocs+fileNameString);
				if(!file.exists())
					return null;
			}
		}
		return file;
	}

}
