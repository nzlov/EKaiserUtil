package ekaiser.nzlov.net;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;


public class ENetClient {

	private static String delimiter = "&";

	private static boolean hiPerformance = false;

	private ArrayList<ENetMessage> messageIn;

	private ArrayList<ENetMessage> messageOut;

	private Socket client;

	private ENetServer server;

	private Thread threadListen;

	private Thread threadSender;

	private ENetListener listener;

	private boolean isRunning, daemon;

	private int pingtime;

	public ENetClient(String host, int port) throws UnknownHostException,
			IOException {
		this.isRunning = true;
		this.daemon = false;
		this.messageIn = new ArrayList<ENetMessage>();
		this.messageOut = new ArrayList<ENetMessage>();
		this.pingtime = -1;
		this.client = new Socket(host, port);
		this.startClient();
	}

	public ENetClient(Socket s, ENetServer server) {
		this.isRunning = true;
		this.daemon = true;
		this.messageIn = new ArrayList<ENetMessage>();
		this.messageOut = new ArrayList<ENetMessage>();
		this.pingtime = -1;
		this.client = s;
		this.server = server;
		this.startClient();
	}

	private void startClient() {
		if (threadListen == null) {

			threadListen = new Thread(new Runnable() {

				BufferedReader in;

				String line;

				ENetMessage tmpmsg;

				public void run() {
					try {
						in = new BufferedReader(new InputStreamReader(client
								.getInputStream()));
					} catch (IOException ex) {
						ex.printStackTrace();
					}
					while (isRunning) {
						line = null;
						try {
							line = in.readLine();
							String splits[] = line.split(ENetClient
									.getDelimiter());
							tmpmsg = new ENetMessage();
							tmpmsg.name = splits[0];
							tmpmsg.message = splits[1];
							if (tmpmsg.name.startsWith(ENetListener.FLAG)) {
								if (tmpmsg.name.equals(ENetListener.PING)) {
									tmpmsg.name = ENetListener.REPING;
									sendMessage(tmpmsg);
								}
								if (tmpmsg.name.equals(ENetListener.REPING)) {
									pingtime = (int) (System.nanoTime() - Long
											.parseLong(tmpmsg.message) / 1000000);
								}
							} else {
								addInMessage(tmpmsg);
							}
						} catch (Exception ex) {
							close();
						}
						if (!ENetClient.hiPerformance) {
							try {
								Thread.sleep(10L);
							} catch (InterruptedException ex) {
							}
						} else {
							int i = 0;
							while (i < 10) {
								Thread.yield();
								i++;
							}
						}
					}
				}

			});
			threadListen.setDaemon(daemon);
			threadListen.start();
		}
		if (threadSender == null) {
			threadSender = new Thread(new Runnable() {

				BufferedWriter out;

				ENetMessage tmpmsg;

				public void run() {
					try {
						out = new BufferedWriter(new OutputStreamWriter(client
								.getOutputStream()));
					} catch (IOException ex) {
						ex.printStackTrace();
					}
					while (isRunning) {
						if (messageOut.size() > 0) {
							tmpmsg = getNextOutMessage();
							try {
								out.write((new StringBuilder()).append(
										tmpmsg.name).append(
										ENetClient.getDelimiter()).append(
										tmpmsg.message).toString());
								out.newLine();
								out.flush();
							} catch (IOException ex) {
								close();
							}
						}
						if (!ENetClient.hiPerformance) {
							try {
								Thread.sleep(10L);
							} catch (InterruptedException ex) {
							}
						} else {
							int i = 0;
							while (i < 10) {
								Thread.yield();
								i++;
							}
						}
					}
				}

			});
			threadSender.setDaemon(daemon);
			threadSender.start();
		}
	}

	private synchronized void addInMessage(ENetMessage message) {
		messageIn.add(message);
		if (server == null) {
			if (listener != null) {
				listener.getMessage(this);
			}
		} else if (server.getListener() != null) {
			server.getListener().getMessage(this);
		}
	}

	private synchronized void addOutMessage(ENetMessage message) {
		messageOut.add(message);
	}

	private synchronized ENetMessage getNextOutMessage() {
		if (messageOut.size() > 0) {
			return (ENetMessage) messageOut.remove(0);
		} else {
			return null;
		}
	}

	private synchronized ENetMessage getNextInMessage() {
		if (messageIn.size() > 0) {
			return (ENetMessage) messageIn.remove(0);
		} else {
			return null;
		}
	}


	public synchronized ArrayList<ENetMessage> findMessage(String name) {
		if (name == null) {
			return null;
		}
		if (messageIn != null) {
			ArrayList<ENetMessage> messages = new ArrayList<ENetMessage>(
					10);
			for (ENetMessage mes : messageIn) {
				if (mes.name.equals(name)) {
					messages.add(mes);
				}
			}
			return messages;
		} else {
			return null;
		}
	}



	public void sendMessage(ENetMessage message) {
		addOutMessage(message);
	}

	public void sendMessage(String name, String message) {
		ENetMessage tmpmsg = new ENetMessage();
		tmpmsg.name = name;
		tmpmsg.message = message;
		sendMessage(tmpmsg);
	}

	public ENetMessage getNextMessage() {
		return getNextInMessage();
	}

	public Socket getSocket() {
		return client;
	}

	public void close() {
		if (!client.isClosed())
			try {
				if (server == null) {
					if (listener != null)
						listener.discconnected(this);
				} else {
					server.removeClient(this);
					if (server.getListener() != null) {
						server.getListener().discconnected(this);
					}
				}
				client.close();
				isRunning = false;
			} catch (IOException ex) {
			}
	}

	public static String getDelimiter() {
		return delimiter;
	}

	public static void setDelimiter(String aDelimiter) {
		delimiter = aDelimiter;
	}

	public ENetListener getListener() {
		return listener;
	}

	public void setListener(ENetListener listener) {
		this.listener = listener;
	}

	public void ping() {
		sendMessage(ENetListener.PING, Long.toString(System.nanoTime()));
	}

	public boolean isDaemon() {
		return daemon;
	}

	public void setDaemon(boolean daemon) {
		this.daemon = daemon;
	}

	public int getPingtime() {
		return pingtime;
	}

	public static void useHiPerformance(boolean b) {
		hiPerformance = b;
	}

	public void dispose() {
		close();
	}

}
