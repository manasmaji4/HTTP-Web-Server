package manas.maji.WebServer;

import java.io.*;

public class MyUtility {
	
///////////////////////////////////////////////////////////Compare Original & Down Loaded File /////////////////////////////////////////////////////////////
	//compare if 2 files are identical byte wise & return true/false accordingly
	boolean compareFiles(String copyFile,String originalFile)
	{
		boolean result=true;
	
		File fileOriginal=new File(originalFile);												//open the original file
		File fileCopy= new File(copyFile);										//open the down loaded file
		byte[] bufferOriginal = new byte[(int) fileOriginal.length()];
		byte[] bufferCopy=new byte[(int) fileCopy.length()];
		try {
			FileInputStream fileInputStream_original = new FileInputStream(fileOriginal);
			fileInputStream_original.read(bufferOriginal);
			FileInputStream fileInputStream_downloaded = new FileInputStream(fileCopy);
			fileInputStream_downloaded.read(bufferCopy);
			if(fileOriginal.length()==fileCopy.length())								//compare file size
			{
				for(int index=0;index<bufferOriginal.length;index++)							//compare the two files byte wise
				if(bufferOriginal[index]!=bufferCopy[index])
					result=false;
			}
			else result=false;
			fileInputStream_original.close();
			fileInputStream_downloaded.close();
		} catch (FileNotFoundException e) {
			System.out.println("File Not Found.");
			return false;
		}
		catch (IOException e1) {
			System.out.println("Error Reading The File.");
			return false;
		}
		return result;
	}
}