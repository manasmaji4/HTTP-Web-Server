package manas.maji.WebServer;
/**
 * This interface lists the commonly used constant values throughout the different classes. 
 * 
 * @author maji
 *
 */
public interface Constants {
	
		int bufferSize=1024;
			
		String serverFilesFolder="../bin/ServerFiles";
		
		String javaDocs="../bin/javaDocs";
		
		String configFileLocation="config.properties";
		
		String allowedMethods="GET,HEAD,POST,DELETE,OPTIONS";
		
		String htmlContentType = "text/html";
		
		String serverName="Web Server";
		
		String homePage="home.html";
		
		String downloadPage="download.html";
		
		String uploadPage="upload.html";
}
