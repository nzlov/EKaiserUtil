package ekaiser.nzlov.threadpool;

import java.util.ArrayList;

import ekaiser.nzlov.util.log.Log;

/**
 * 线程池
 * 
 * @author Nzlov
 * 
 */
public class EThreadPoolManage {
	/**
	 * 设置是否运行
	 */
	private boolean isRunning = false;
	/**
	 * 可用线程列表
	 */
	private ArrayList<EThread> eThreads = null;
	private ArrayList<Object> argumentList = null;
	/**
	 * 线程池最大线程数
	 */
	private int maxThreadNumber = 0;
	/**
	 * 最大任务数
	 */
	private int maxArgument = 0;
	/**
	 * 线程类
	 */
	private Class<EThread> cls = null;
	/**
	 * 线程池名字
	 */
	private String name;
	/**
	 * 任务分配线程
	 */
	private poolManageThread manageThread = null;

	/**
	 * 构造方法
	 * 
	 * @param maxTh
	 *            最大线程数
	 * @param maxArgu
	 *            线程类
	 * @param th
	 */
	public EThreadPoolManage(String name, int maxTh, int maxArgu, Class<?> th) {
		Log.addMessage(this, "创建 " + name + " 线程组！");
		this.name = name;
		this.maxThreadNumber = maxTh;
		this.maxArgument = maxArgu;
		this.cls = (Class<EThread>) th;
		this.eThreads = new ArrayList<EThread>(this.maxThreadNumber);
		this.argumentList = new ArrayList<Object>(this.maxArgument);
		manageThread = new poolManageThread();
		manageThread.setDaemon(true);
	}

	/**
	 * 申请线程处理
	 * 
	 * @param obj
	 * @return boolean 成功 true
	 */
	public boolean process(Object obj) {
		// Log.addMessage(this,this.name+" 线程池有信息添加, 任务队列大小："+argumentList.size());
		if (argumentList.size() + 1 > this.maxArgument)
			return false;
		argumentList.add(obj);
		return true;
	}

	/**
	 * 返回任务
	 * 
	 * @return
	 */
	private Object getArgument() {
		if (argumentList.size() > 0) {
			// Log.addMessage(this,this.name+" 提取:"+argumentList.get(0)+" , size:"+argumentList.size());
			return argumentList.remove(0);
		} else {
			return null;
		}
	}

	/**
	 * 设置是否运行
	 * 
	 * @param isRunning
	 */
	public void setRunning(boolean isRunning) {
		this.isRunning = isRunning;
		for (int i = 0; i < eThreads.size(); i++) {
			eThreads.get(i).setRunning(isRunning);
		}
		if (isRunning) {
			if (!manageThread.isAlive()) {
				manageThread = new poolManageThread();
				manageThread.setDaemon(true);
			}
			manageThread.start();
		}
	}

	private class poolManageThread extends Thread {
		public void run() {
			Object obj = null;
			int i = 0;
			EThread t = null;
			while (isRunning) {
				while ((obj = getArgument()) != null) {
					for (i = 0; i < eThreads.size(); i++) {
						t = eThreads.get(i);
						if (t.isWait()) {
							t.notifyProcess(obj);
							break;
						}
						t = null;
					}
					if (t == null && i < EThreadPoolManage.this.maxThreadNumber) {
						// Log.addMessage(this,EThreadPoolManage.this.name+" 线程池创建新的线程:"+i);
						try {
							t = EThreadPoolManage.this.cls.newInstance();
							eThreads.add(t);
							t.setName(i + "");
							// t.setManage(EThreadPoolManage.this);
							t.setDaemon(true);
							t.setRunning(isRunning);
							t.start();
							t.notifyProcess(obj);
						} catch (InstantiationException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (IllegalAccessException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

					}
					obj = null;
					t = null;
					try {
						Thread.sleep(10L);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}
	}
}
