package manas.maji.WebServer;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class AppTest extends TestCase
{
	String filesLocation,baseDir;
	public AppTest( String testName )
    {
        super( testName );
    }
    public static Test suite()
    {
        return new TestSuite( AppTest.class );
    }
    
	@org.junit.Test
    public void testServer()
    {
		//create the server thread that will start the Test server & keep it running
		Thread serverThread=new Thread(new TestServer());
		serverThread.start();
				
    	//OPTIONS testing
    	OptionsTest optionsObject=new OptionsTest();						
		try {
			optionsObject.testOptions();
		} catch (Exception e) {
			System.out.println("Error in OPTIONS "+e);
		}

    	//files to be tested
    	String[] filename={"1.txt","1.JPG","1.aac","1.xlsx","1.pdf"};
    	
		//testing the concurrency by creating few threads that will sent request to the server simultaneously
    	List<Thread> threads=new ArrayList<Thread>();
   
    	for(int i=0;i<5;i++)
    	{
    		Thread testThread=new Thread(new MultiTHREAD(filename[i],baseDir));//filesLocation,
    		testThread.start();
    		threads.add(testThread);
    	}
    	//joining the threads
    	for(int i=0;i<5;i++)
    	{
    		try {
				threads.get(i).join();
			} catch (InterruptedException e) {
				System.out.println(e.getMessage());
			}
    	}
    	//interrupt the server thread,so that the server is shutdown
    	serverThread.interrupt();  	
    }


	//load the location of test files from class path & make the directories for testing
	public void setUp(){
		File downloadDir=new File(TestConstants.downloadLocation);
		//create the required downloadFiles directory for testing
		if(!downloadDir.exists())
		{
			downloadDir.mkdir();
		}
		baseDir=TestConstants.uploadLocation;
	}
	
	//remove the directories used in testing
	public void tearDown(){
		File fileDownload=new File(TestConstants.downloadLocation);
		fileDownload.delete();
		File fileUpload=new File(TestConstants.uploadLocation);
		fileUpload.delete();
	}
}