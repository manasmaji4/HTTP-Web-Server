package manas.maji.WebServer;

import java.io.*;
import java.net.HttpURLConnection;
import static org.junit.Assert.*;

import java.net.URL;

public class OptionsTest{

		
//////////////////////////////////////////////////////////////////////// Handle GET test //////////////////////////////////////////////////////////////
	public void testOptions() throws Exception
    {
		//open the url connection
		HttpURLConnection connection = (HttpURLConnection) new URL(TestConstants.urlLocation).openConnection(); 
		connection.setDoOutput(false);
	    connection.setRequestMethod("OPTIONS");
	    String allowedOptions = null;
	    
		int responseCode=0;
		try{
			allowedOptions=connection.getHeaderField("Access-Control-Allow-Methods");
			//get the response code from the response
			responseCode=connection.getResponseCode();
		}catch(IOException e){
			System.out.println("Connection Error");
		}	 	
	 	if(responseCode==200)
	 	{
	 		//assert true if the server returns the correct allowed options
	 		assertEquals(TestConstants.serverOptions,allowedOptions); 
	 	}
	 	//close the url connection
	 	connection.disconnect();
    }
}