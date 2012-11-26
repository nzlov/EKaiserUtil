package ekaiser.nzlov.util.log;

import java.util.Calendar;
import java.util.Date;
/**
 * 日志信息
 * @author qpaber
 *
 */
public class LogMessage {
	/**
	 * 时间
	 */
	private String _date = null;
	/**
	 * 操作类
	 */
	private String _class = null;
	/**
	 * 信息
	 */
	private String _message = null;
	
	public LogMessage(Object ob,String message){
		/**日志创建时间*/
		_date = creatDate();
		/**获取当前对象的类名*/
		_class = ob.getClass().getName();
		/**日志信息*/
		_message = message;
	}
	/**
	 * 日志创建时间
	 * @return
	 */
	private String creatDate(){
		return Calendar.getInstance().getTime().toString();
	}
	/**
	 * 重写tostring返回：时间+操作类名+日志信息
	 */
	public String toString(){
		return new String("[ "+_date + " - " + _class + " ]  " + _message +"\r\n");
	}
}
