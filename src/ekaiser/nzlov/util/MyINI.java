package ekaiser.nzlov.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;



public class MyINI{
	private Properties pro;
	private String filename;
	public MyINI(String str){
		filename = str;
		pro = new Properties();
	}
	public void add(String str,String str1,String str2){
		pro.setProperty(str+"@@"+str1, str2);
	}
	public String getFileName(){
		return filename;
	}
	public Properties getProperties(){
		return pro;
	}
	public void writer(){
		try{
			File file = new File(filename);
			file.delete();
			//直接写入文件
			pro.storeToXML(new FileOutputStream(filename), null);
		}catch(IOException e){
			e.printStackTrace();
		}
	}
	public void writerIni(){
		try{
			File file = new File(filename);
			file.delete();
			pro.store(new FileOutputStream(filename), null);
		}catch(IOException e){
			e.printStackTrace();
		}
	}
	public void Load(){
				try {
					pro.loadFromXML(new FileInputStream(filename));
				}  catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	}
	public void LoadIni(boolean isPNG){
		try{
			pro.load(new FileInputStream(filename));
		}catch(IOException e){
			e.printStackTrace();
		}
	}
	public String get(String str,String str1,String def){
		return pro.getProperty(str+"@@"+str1,def);
	}
}