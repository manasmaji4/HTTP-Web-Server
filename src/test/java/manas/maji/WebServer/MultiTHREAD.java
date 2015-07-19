package manas.maji.WebServer;

public class MultiTHREAD implements Runnable  {
	String fileName,baseDirLocation;
	
	//initialize the base directory location,location of the files for testing, the name of the file
	MultiTHREAD(String getFile,String baseDir)
	{
		fileName=getFile;
		baseDirLocation=baseDir;
	}
	
	//call the methods for testing the corresponding methods
	public void run()
	{
		TestMethods testing=new TestMethods(baseDirLocation);
		
		testing.testPost(fileName);
		
		testing.testGet(fileName);
		
    	testing.testHead(fileName);
		
     	testing.testDelete(fileName);			
	}		
}