package com.noobgrinder.jonathan.clientsockethandler;

import java.io.*;
import java.net.*;

class ClientHandler implements Runnable {
	private ClientSocketHandler csh;
	private Thread thread;
	private CyclicBarrier barrier;
	private ArrayList<String> inStrings;
	private ArrayList<String> outStrings;
	private Object synchForData;
	private Object synchForNotifyServer;
	private keepRunning = true;
	
	public ClientHandler (Object synchForData, Object synchForNotifyServer, Socket cs, String threadName) {
		barrier = b;
		inStrings = new AraryList<String>;
		outStrings = new AraryList<String>;
		csh = new ClientSocketHandler (cs, threadName + ".ClientSocketHandler");
		thread = new Thread(this, true);
		thread.start();
	}
	
	public void run () {
		int i;
		while (keepRunning) {
			barrier.await();
			if (outStrings.size()>0) {
				for (int i=0; i<outStrings.size(); i++) {
					csh.send(outStrings.get(i));
				}
				outStrings.clear();
			}
		}
	}
	
	public void updateSynchObjects (Object synchOne, Object sunchTwo) {
	}
}

class ClientSocketHandler implements Runnable {
	private Socket clientSocket;
	private BufferedReader netin;
	private PrintWriter netout;
	private Thread thread;
	
	public ClientSocketHandler (Socket cs, String threadName) {
		clientSocket = cs;
		thread = new Thread(this, threadName);
		netin = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
		netout = new PrintWriter(clientSocket.getOutputStream(), true);
		thread.start();
	}
	
	public void run () {
		try {
			String instr;
			while ((instr = netin.readLine()) != null) {
				// program logic
				inStrings.add(instr);
			}
		} catch (Exception e) {
			System.err.println("Exception: " + e.toString());
		}
	}
}