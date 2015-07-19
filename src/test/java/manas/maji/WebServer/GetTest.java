package manas.maji.WebServer;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import static org.junit.Assert.*;

public class GetTest{
	String testFile;
	MyUtility util=new MyUtility();
	
	GetTest(String fileName)
	{
		testFile=fileName;
	}
		
//////////////////////////////////////////////////////////////////////// Handle GET test //////////////////////////////////////////////////////////////
	public void testGet(String downloadFolderLocation,String baseDirLocation) throws Exception														//Test for GET
    {
		//open the url connection
		HttpURLConnection connection = (HttpURLConnection) new URL(TestConstants.urlLocation+testFile).openConnection(); 
		connection.setDoOutput(false);
		byte[] buffer= new byte[Constants.bufferSize];
		//get the input stream from the connection
        InputStream UrlInputStream=connection.getInputStream();
		OutputStream fileOutStream=new FileOutputStream(downloadFolderLocation+"/"+testFile);
		int count;
		//write the content returned in the response to file 
		while((count=UrlInputStream.read(buffer, 0, Constants.bufferSize))!=-1)											//read response from GET
        {   
        	if(count>0)
        		fileOutStream.write(buffer,0,count);													//write onto file
        }
        fileOutStream.close();
        //assert true if the newly created file is identical to the the original file in the base directory
        assertTrue(util.compareFiles(baseDirLocation+"/"+testFile,downloadFolderLocation+"/"+testFile));
	 	connection.disconnect();
    }
}