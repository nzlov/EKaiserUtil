package ekaiser.nzlov.net;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;


public class ENetServer {

	private String name;

	private ServerSocket server;

	private Thread serverListener;

	private ENetListener listener;

	private boolean running, daemon;

	private ArrayList<ENetClient> clients;

	public ENetServer(String name, int port) throws IOException {
		this.running = true;
		this.daemon = false;
		this.clients = new ArrayList<ENetClient>();
		this.name = name;
		this.server = new ServerSocket(port);
	}

	public void start() {
		if (serverListener == null) {
			serverListener = new Thread(new Runnable() {
				public void run() {
					for (; isRunning();) {
						try {
							Socket s = server.accept();
							addClient(s);
						} catch (IOException ex) {
							ex.printStackTrace();
						}
					}
				}
			});
			serverListener.setDaemon(daemon);
			serverListener.start();
		}
	}

	public void stop() {
		for (int i = 0; i < countClients(); i++) {
			getClient(i).close();
		}
		running = false;
		try {
			server.close();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	public synchronized void addClient(String host, int port)
			throws UnknownHostException, IOException {
		addClient(new ENetClient(host, port));
	}

	public synchronized void addClient(ENetClient client) {
		clients.add(client);
		if (listener != null) {
			listener.connected(client);
		}
	}

	public synchronized void addClient(Socket s) {
		addClient(new ENetClient(s, this));
	}

	public String getName() {
		return name;
	}

	public boolean isRunning() {
		return running;
	}



	public void sendMessage(ENetMessage message) {
		for (int i = 0; i < clients.size(); i++) {
			((ENetClient) clients.get(i)).sendMessage(message);
		}
	}

	public void sendMessage(String name, String message) {
		ENetMessage tmpmsg = new ENetMessage();
		tmpmsg.name = name;
		tmpmsg.message = message;
		sendMessage(tmpmsg);
	}

	public synchronized int countClients() {
		return clients.size();
	}

	public synchronized ENetClient getClient(int number) {
		return (ENetClient) clients.get(number);
	}

	public synchronized void removeClient(int number) {
		clients.remove(number);
	}

	public synchronized void removeClient(ENetClient client) {
		clients.remove(client);
	}

	public ServerSocket getServerSocket() {
		return server;
	}

	public ENetListener getListener() {
		return listener;
	}

	public void setListener(ENetListener listener) {
		this.listener = listener;
	}

	public boolean isDaemon() {
		return daemon;
	}

	public void setDaemon(boolean daemon) {
		this.daemon = daemon;
	}

	public void dispose() {
		stop();
	}

}
