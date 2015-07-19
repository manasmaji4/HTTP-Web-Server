package manas.maji.WebServer;

import static org.junit.Assert.*;

public class TestMethods {
	String filesLocation,baseDirLocation;
	
	//constructor to initialize the base directory location
	TestMethods(String baseDir)
	{
		//filesLocation=location;
		baseDirLocation=baseDir;
	}

	//test The Get method
    void testGet(String fileName)
    {
    	GetTest getObject=new GetTest(fileName);									//GET
		try {
			getObject.testGet(TestConstants.downloadLocation,baseDirLocation);
		} catch (Exception e) {
			assertTrue(false);
			System.out.println("Thread error GET "+e);
		}
    }
    
    //Test the Head method
    void testHead(String fileName)
    {
    	HeadTest headObject=new HeadTest(fileName);								//HEAD
		try {
			headObject.testHead(baseDirLocation);
		} catch (Exception e) {
			assertTrue(false);
			System.out.println("Thread error HEAD "+e);
		}
    }

	//test the Delete method
    void testDelete(String fileName)
    {
    	DeleteTest deleteObject=new DeleteTest(fileName);						//DELETE
		try {
			deleteObject.testDelete(baseDirLocation);
		} catch (Exception e) {
			assertTrue(false);
			System.out.println("Thread error DELETE "+e);
		}
    }
    
    //test the Post method
    void testPost(String fileName)
    {
		PostTest postObject=new PostTest(fileName);							//POST
		try {
			postObject.testPost(TestConstants.testFileLocation,baseDirLocation);
		} catch (Exception e) {
			assertTrue(false);
			System.out.println("Thread error POST "+e);
		}
    }    
}
