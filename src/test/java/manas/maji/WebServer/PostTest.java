package manas.maji.WebServer;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import static org.junit.Assert.*;

public class PostTest{
	String testFileName;
	MyUtility util=new MyUtility();
	
	PostTest(String name)
	{
		testFileName=name;
	}

/////////////////////////////////////////////////////////////// Handle POST test ////////////////////////////////////////////////////////////////	
	public void testPost(String testFileLocation,String uploadDirLocation) throws Exception
    {
		HttpURLConnection connection = (HttpURLConnection) new URL(TestConstants.urlLocation+testFileName).openConnection();
		connection.setDoOutput(true); 
		connection.setDoInput(true);
		connection.setRequestMethod("POST");
		
		File fileOriginal = new File(testFileLocation+"/"+testFileName);
		//set the request properties for the Post request
		writeHeaders(StartupUtility.getMimeType(testFileName),connection,testFileName,(int)fileOriginal.length());
		//open the connection output stream
		OutputStream UrlOutputStream = connection.getOutputStream();
		//write the starting boundary & the file information
		writeInitialBody(StartupUtility.getMimeType(testFileName),UrlOutputStream,testFileName);
		//write the actual file content & the ending boundary
		writeBody(UrlOutputStream,fileOriginal);
		connection.getInputStream();
		int responseCode=connection.getResponseCode();
		//assert true if the uploaded file & the original file is identical byte wise
		if(responseCode==200)
			assertTrue(util.compareFiles(uploadDirLocation+"/"+testFileName,testFileLocation+"/"+testFileName));     
		//close the url connection
		connection.disconnect();
    }	

	//set the request headers
	void writeHeaders(String contentType,HttpURLConnection connection,String fileName,int fileLength){
		try{
			connection.setRequestProperty("Connection", "Keep-Alive");
			connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + TestConstants.boundary);											//set request property
		    connection.setRequestProperty("Content-Length", String.valueOf((fileLength+124+contentType.length()+fileName.length())));
		}catch(NullPointerException e){
			System.out.println("Null Pointer Exception");
		}
	}
	
	/////////////////////////////////////////////////////////// Write Header For the Request /////////////////////////////////////////////////////////
	
	//write the boundary & the file information to the output stream
	void writeInitialBody(String contentType,OutputStream UrlOutputStream,String fileName)
	{
	//	System.out.println(("--" + TestConstants.boundary+TestConstants.CRLF+"Content-Disposition: form-data; name=\"Browse\"; filename=\"" +"\""+ TestConstants.CRLF+"Content-Type: "+TestConstants.CRLF+TestConstants.CRLF+TestConstants.CRLF+"--" + TestConstants.boundary + "--"+TestConstants.CRLF).getBytes().length);
	   
		try{
	    	UrlOutputStream.write(("--" + TestConstants.boundary+TestConstants.CRLF).getBytes());																	//beginning boundary
	    	UrlOutputStream.write(("Content-Disposition: form-data; name=\"Browse\"; filename=\"" + fileName+"\""+ TestConstants.CRLF).getBytes());	//write body part
	    	UrlOutputStream.write(("Content-Type: "+contentType+TestConstants.CRLF).getBytes());
	    	UrlOutputStream.write(TestConstants.CRLF.getBytes());
	    }catch(IOException e){
	    	System.out.println("Error in writing File to connection output stream.");
	    }
	 }

	/////////////////////////////////////////////////////////// Write File to Output Stream of Connection/////////////////////////////////////////////
	//write the file body & the boundary to the connection output stream
	void writeBody(OutputStream UrlOutputStream,File file)
	{
		byte[] buffer = new byte[(int)file.length()];
		try {
			FileInputStream fileInputStream = new FileInputStream(file);		    
			fileInputStream.read(buffer);																		//write the file content
			
			UrlOutputStream.write(buffer, 0, (int)file.length());
			UrlOutputStream.write((TestConstants.CRLF+"--" + TestConstants.boundary + "--"+TestConstants.CRLF).getBytes());
			
			UrlOutputStream.flush();
			fileInputStream.close();
			UrlOutputStream.close();
		}catch (FileNotFoundException e) {
			System.out.println("File Not Found.");
		}catch(IOException e){
			System.out.println("Error in writing File to connection output stream.");
		}
	}
}
