package ekaiser.nzlov.util.log;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Calendar;

import ekaiser.nzlov.methodmap.EMethodMapManage;
import ekaiser.nzlov.methodmap.EMethodMessage;




public class Log {
	private static Log def = null;
	/**
	 * 日志存储路径
	 */
	private static final String LOGPATH = "log/";
	/**
	 * 当前时间
	 */
	private static String _date = null;
	/**
	 * 日志信息列表
	 */
	private static ArrayList<LogMessage> _messageList = null;
	/**
	 * 日志输出流
	 */
	private static BufferedOutputStream _bw = null;
	/**
	 * 处理日志的线程
	 */
	private static Thread _logThread = null;
	/**
	 * 是否有日志输出
	 */
	private static boolean _isOutLog = false;
	/**
	 * 初始化日志
	 */
	public static void Load(){
		if(def==null)
			def = new Log();
		EMethodMapManage.addMethodMap("Log", def, "addMessage");
		
		/**
		 * 显示时间格式
		 */
	    Calendar ca = Calendar.getInstance();
		_date = ca.get(Calendar.YEAR) +"年"+(ca.get(Calendar.MONTH)+1) +"月" +ca.get(Calendar.DATE) +"日" +
				ca.get(Calendar.HOUR_OF_DAY) +"时"+ca.get(Calendar.MINUTE) +"分" + ca.get(Calendar.SECOND) +"秒";
		/**
		 * 生成日志列表
		 */
		_messageList = new ArrayList<LogMessage>();
		
		File f = new File(LOGPATH);
		if(!f.exists()){
			f.mkdir();
		}
		
		
		/**
		 * 创建日志存储文件
		 */
		f = new File(LOGPATH + _date + ".log");
		FileOutputStream fos;
		try {
			fos = new FileOutputStream(f);
			_bw = new BufferedOutputStream(fos);
			System.setErr(new PrintStream(fos));
		} catch (FileNotFoundException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		/**
		 * 输出日志控制标识
		 */
		_isOutLog = true;
		/**启动日志线程*/
		_logThread = new Thread(new Runnable() {
			
			public void run() {
				// TODO Auto-generated method stub
				while(_isOutLog){
					outLog();
					try {
						Thread.sleep(10L);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		});
		_logThread.start();
	}
	/**
	 * 添加日志
	 * @param ob 当前对象
	 * @param str 信息
	 */
	public static synchronized void addMessage(Object ob,String str){
		if(!_isOutLog) return;
			LogMessage lmsg = new LogMessage(ob, str);
			_messageList.add(lmsg);
	}
	/**
	 * 添加日志
	 * @param message 日志信息
	 */
	public static synchronized void addMessage(EMethodMessage message){
		addMessage(message.getObject(),(String)message.getParameter());
	}
	/**
	 * 添加日志
	 * @param message 日志信息
	 */
	public static synchronized void addMessage(LogMessage message){
		if(!_isOutLog) return;
			_messageList.add(message);
	}

	/**
	 * 输出日志
	 */
	private static synchronized void outLog(){
		if(!_isOutLog) return;
		if(_messageList.size()<1) return;
		try {
			_bw.write((_messageList.remove(0).toString()+"\n").getBytes());
			_bw.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/**
	 * 关闭日志
	 */
	public static void close(){
		if(!_isOutLog) return;
		for(int i=0;i<=_messageList.size();i++){
			outLog();
		}
		try {
			_bw.flush();
			_bw.close();
			_bw = null;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		_isOutLog = false;
		EMethodMapManage.removeMethodMap("Log");
		def = null;
	}
}
