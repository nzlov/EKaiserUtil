package ekaiser.nzlov.httpdownload;
import java.io.*;

public class FileAccess implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	RandomAccessFile oSavedFile;

	long nPos;
	
	public FileAccess() throws IOException{
		this("",0);
	}
	
	public FileAccess(String sName,long nPos) throws IOException{
		oSavedFile = new RandomAccessFile(sName,"rw");
		this.nPos = nPos;
		oSavedFile.seek(nPos);
	}
	
	public synchronized int write(byte[] b,int nStart,int nLen){
		int n = -1;
		try{
			oSavedFile.write(b,nStart,nLen);
			n = nLen;
		}catch(IOException e){	
			e.printStackTrace ();
		}
		return n;
	
	}
	
}
	
