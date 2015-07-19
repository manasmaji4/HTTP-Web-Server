package manas.maji.WebServer;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import static org.junit.Assert.*;


public class DeleteTest{
	String testFile;
	MyUtility util=new MyUtility();
	
	DeleteTest(String fileName)
	{
		testFile=fileName;
	}
	
//////////////////////////////////////////////////////////////////////// Handle GET test //////////////////////////////////////////////////////////////
	public void testDelete(String filesLocation) throws Exception
    {
		//open the url connection
		HttpURLConnection connection = (HttpURLConnection) new URL(TestConstants.urlLocation+testFile).openConnection(); 
		connection.setDoOutput(true);
		connection.setRequestProperty("Content-Type", StartupUtility.getMimeType(testFile));
		connection.setRequestMethod("DELETE");
		connection.connect();		
		
		int responseCode = 0;
		try {
			//get the response code from the response
			connection.getInputStream();
			responseCode=connection.getResponseCode();
		} catch (IOException e) {
			System.out.println("Delete Connection error. "+testFile);
		}
		//if response is of successful delete operation,check if file was actually deleted
        if(responseCode==200)
	 	{
        	//assert true if file does'nt exist
	 		File fileOriginal=new File(filesLocation+"/"+testFile);
	 		assertTrue(fileOriginal.exists()==false);        	
	 	}
	 	//close the url connection
	 	connection.disconnect();
    }
}