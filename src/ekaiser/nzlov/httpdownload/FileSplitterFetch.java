package ekaiser.nzlov.httpdownload;
import java.io.*;

import java.net.*;

public class FileSplitterFetch extends Thread {

	String sURL; //File URL
	
	long nStartPos; //File Snippet Start Position
	
	long nEndPos; //File Snippet End Position
	
	int nThreadID; //Thread's ID
	
	boolean bDownOver = false; //Downing is over
	
	boolean bStop = false; //Stop identical
	
	FileAccess fileAccessI = null; //File Access interface
	
	public FileSplitterFetch(String sURL,String sName,long nStart,long nEnd,int id)throws IOException{
		this.sURL = sURL;
		this.nStartPos = nStart;
		this.nEndPos = nEnd;
		nThreadID = id;
		fileAccessI = new FileAccess(sName,nStartPos);
	}
	
	public void run(){
		while(nStartPos < nEndPos && !bStop){
			try{
				URL url = new URL(sURL);
				HttpURLConnection httpConnection = (HttpURLConnection)url.openConnection ();
				httpConnection.setRequestProperty("User-Agent","NetFox");
				String sProperty = "bytes="+nStartPos+"-";
				httpConnection.setRequestProperty("RANGE",sProperty);
				Utility.log(sProperty);
				InputStream input = httpConnection.getInputStream();
				//logResponseHead(httpConnection);
				byte[] b = new byte[1024];
				int nRead;
				while((nRead=input.read(b,0,1024)) > 0 && nStartPos < nEndPos && !bStop){
					System.out.println(new String(b));
					nStartPos += fileAccessI.write(b,0,nRead);
					//if(nThreadID == 1)
					// Utility.log("nStartPos = " + nStartPos + ", nEndPos = " + nEndPos);
				}				
				Utility.log("Thread " + nThreadID + " is over!");
				bDownOver = true;
				//nPos = fileAccessI.write (b,0,nRead);
			}catch(Exception e){
				e.printStackTrace ();
			}
		}
	}
	// 打印回应的头信息
	public void logResponseHead(HttpURLConnection con){
		for(int i=1;;i++){
			String header=con.getHeaderFieldKey(i);
			if(header!=null)
				//responseHeaders.put(header,httpConnection.getHeaderField(header));
				Utility.log(header+" : "+con.getHeaderField(header));
			else
				break;
		}
	}
	
	public void splitterStop(){
		bStop = true;
	}
}

