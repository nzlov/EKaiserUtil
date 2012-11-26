package ekaiser.nzlov.threadpool;

import ekaiser.nzlov.util.log.Log;

/**
 * 线程池管理的线程的父类
 * @author Nzlov
 *
 */
public abstract class EThread extends Thread{
	/**
	 * 是否正在运行
	 */
	private boolean isRunning = false;
	/**
	 * 是否正在等待唤醒
	 */
	private Boolean isWait = false;
	/**
	 * 此线程所在的线程池
	 */
//	private EThreadPoolManage manage = null;
	/**
	 * 线程将处理的对象
	 */
	private Object obj = null;
	
	
//	private String sign = "";
	/**
	 * 构造方法
	 * @param manage
	 */
	public EThread(){
		this.isRunning = false;
		this.isWait = false;
	}
//	/**
//	 * 设置此线程所在的线程池
//	 * @param manage
//	 */
//	public void setManage(EThreadPoolManage manage){
//		this.manage = manage;
//	}
	/**
	 * 子类必须实现的方法
	 * 线程执行体
	 */
	abstract public void process(Object obj);
	/**
	 * 线程执行
	 */
	public void run(){
		while(isRunning){
			if(obj!=null)
			{
				process(obj);
			}
			obj = null;
			synchronized (this) {
				try {
					isWait = true;
					Log.addMessage(this,getName()+"Thread is Wating");
					this.wait();
					isWait = false;
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
	/**
	 *  唤醒线程执行子类实现方法
	 * @param obj 
	 */
	public void notifyProcess(Object obj){
		synchronized (this) {
			this.obj = obj;
			if(isWait){
				this.notifyAll();
			}
		}
	}
	/**
	 * 是否正在运行
	 * @return
	 */
	public boolean isRunning() {
		return isRunning;
	}
	/**
	 * 设置是否运行
	 * @param isRunning
	 */
	public void setRunning(boolean isRunning) {
		this.isRunning = isRunning;
	}

	/**
	 * 是否等待唤醒
	 * @return
	 */
	public boolean isWait() {
		synchronized (this) {
			return isWait;
		}
	}
	
}
