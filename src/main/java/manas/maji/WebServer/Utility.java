package manas.maji.WebServer;

import org.apache.log4j.Logger;
import java.io.*;

/**
 * This class contains few methods that are utilized for processing the client requests.
 * <p>1.reading from the input stream- One line is read from the input stream of the client connection,& returned as string. 
 * <p>2.altering the download page- Checks the repository for the files uploaded by the clients,& creates a listing from where they can be down loaded.
 * 
 * @author maji
 */
public class Utility{
	static Logger log=Logger.getLogger(Utility.class);
	//get the path of Uploaded Files repository
	String baseDirLocation=StartupUtility.getBaseDirLocation();
	File folder = new File(baseDirLocation);
	private InputStream inputStream;
	public InputStream getInputstream() {return inputStream;}
	public void setInputstream(InputStream inputstream) {this.inputStream = inputstream;}
	
	
///////////////////////////////////////////////////////// read one line the input stream & return it as string ////////////////////////////////////////////////////////////////
	
/**
 *  This method reads one line from the Input Byte Stream of the Socket Connection,convert it to string & return it. The input stream is read one character at a time & is checked for occurrence of end of line character "\r\n"
 *  <if the line is a newline,then "\r\n" is returned
 *  
 * @return one line of String
 * @exception IOException error in reading from client socket connection input stream
 * @throws NullPointerException if client connection is terminated,input stream will be null
 */
	public String readLine() throws NullPointerException															
	{
		String readLine="";
		try{
			int index=inputStream.read();
			//Convert input stream into string one line at a time(until a newline character is encountered or input stream is available)
			while((char)index!='\r' && index != -1)
			{	
				readLine=readLine+(char)index;
				index=inputStream.read();
			}
			index=inputStream.read();
			if(readLine.equals(""))
			{
				return "\r\n";
			}
		}catch(IOException e){
			log.info("Cannot read from request.");
			return null;
		}		
		return readLine;
	}
	
	
//////////////////////////////////////////////////////////////Altering the Download Page /////////////////////////////////////////////////////////////////////

/**
 *	This method reads the server repository location from the class path, & creates a directory listing of the files uploaded by the client that are currently in repository.
 *  Listing is shown in a html page as links to the files from where they can be viewed if desired.
 *  
 *  @exception IOException error in writing the html page
 */
	public void generateDownloadPage()
	{	
		try{
			log.info("Changing Download Page");
			String page1stPart="<html><head><title>Download Page</title><h2>Web Server</h2></head><body background=\"webserver.jpg\"><center><h3>Click to download.</h3><hr><br><br>";
			String pageLastPart="</center></body></html>";
			String page=page1stPart;
			
			File[] listOfFiles = folder.listFiles();
			log.info("Files found in directory:\n"+listOfFiles.length);
			if(listOfFiles.length==0)
			{
				page=page+"<br><br><br><br>There are no files avaiable for download.<br><br><br><br><br><br><br><br><br><br>";
			}
			else{
				page=page+"<table border=\"1\"><tr><th>File Name</th><th>Size (in bytes)</th></tr>";
				for (int i = 0; i < listOfFiles.length; i++) 
				{	
					if (listOfFiles[i].isFile())
					{	
						page=page+"<tr><td><a href=\""+listOfFiles[i].getName()+"\" target=\"_self\">"+listOfFiles[i].getName()+"</a> </td><td> "+listOfFiles[i].length()+" </td></tr>";
						log.info(listOfFiles[i].getName()+"\n");
					}
				}
			}
			page=page+"</table><br><br><br><br><br><br><br><a href=\""+Constants.uploadPage+"\" target=\"_self\">Goto Upload Page</a><br>";
			page=page+"<br><a href=\""+Constants.homePage+"\" target=\"_self\">Goto Home</a><br><br>";
			page=page+pageLastPart;
			FileWriter fw=new FileWriter(new File(Constants.serverFilesFolder+"/"+Constants.downloadPage),false);
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(page);
			bw.close();
		} catch (IOException e) {
			log.info("Could not alter Download Page");
			return;
		}
		log.info("Download Page Altered");
	}
}