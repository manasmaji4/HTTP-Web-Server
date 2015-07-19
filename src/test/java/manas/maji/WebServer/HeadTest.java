package manas.maji.WebServer;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

import static org.junit.Assert.*;

public class HeadTest{
	String testFile;
	
	HeadTest(String fileName)
	{
		testFile=fileName;
	}
	
//////////////////////////////////////////////////////////////////////// Handle GET test //////////////////////////////////////////////////////////////
	public void testHead(String baseDirLocation) throws Exception																//Test for GET
    {       
		//open the url connection
		HttpURLConnection connection = (HttpURLConnection) new URL(TestConstants.urlLocation+testFile).openConnection(); 
		connection.setRequestMethod("HEAD");
        connection.setDoOutput(false);
        //if file is found,check if body is null
        if(connection.getResponseCode()==200)
	 	{
	 		InputStream UrlInputStream=connection.getInputStream();
	 		//if null assert true
	 		if(UrlInputStream==null)
	 			assertTrue(true);	
	 	}
	 	//if file is not found,check if file exists
	 	if(connection.getResponseCode()==404)
	 	{
	 		File fileOriginal=new File(baseDirLocation+"/"+testFile);
	 		//if not exists assert true
		 	assertTrue(fileOriginal.exists()==false);
	 	}
	 	connection.disconnect();
    }
}