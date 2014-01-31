// Interface for prototype ShipFrame entity
// implemented by Ship  class in ClientThread in server

package com.noobgrinder.haxathalon.spacebattles;

interface ShipFrame {
	private Map<String, int[2]> subsystems; // {componentName: [health, optional]}
											// example: {"wp1": [health, weaponDamagePerShot]}
											// example: {"wp1cap": [health, capacitorArrayCharge]}
											// example: {"reactor": [health, baseChargeRate]}
	private Map<String, int> hullsections;  // example: {"h001":99}

	public int getHealth (); // overall = (hullHealth + subsystemHealth) / (hullHealth + subsystemHealth)
	public int getShields ();
	public Map<String, int> getHealth (String name); /*
		if (name=="subsystems") {
			Map<String, int> subs = new HashMap ();
			subs.put("sensors", sensorHealth);
			subs.put("shieldCapArray", shieldCapArrayHealth);
		}
	*/

	public Map<String, int> getHullStatus ();				// returns, e.g. {"h001": 99, "h002": 85,...} for hullsections
															// subsystem status can be inferred by hull section id
	public Map<String, int> getSubsystemStatuses ();		// returns {"wp1":99, "wp1cap": 42,...} for subsystems
	public Map<String, String> getShipMap ();				// maps subsystems to hull sections {"wp1":"h021","wp1cap":"h022",...}

	private ArrayList<Shot> firedProjectiles;
	public boolean fireShot (String weaponId, String targetId, int xCoord, int yCoord); /*
		int damage = getSubsystem(weaponId)[1];
		int values[] = getSubsystem(weaponId+"cap");
		if (values[1]>=2*damage-1) {
			values[1] -= (2*damage-1);
			setSubsystem(weaponId+"cap", values);
			mainDataThread.queueShot(targetId, damage, xCoord, yCoord);
			firedProjectiles.add( new Shot (targetId, damage, xCoord, yCoord) );
		}
	*/

	public boolean assignDamage (String hullSectionId, int damageValue);

	public Collision nextImpact (); /*{
		Collision c = null;
		for (Shot x: firedProjectiles) {
			if (x.intersect<=mainDataThread.getCurrentTimestamp()) {
				c = new Collision (x);
				break;
			}
		}
		return c;
	}*/
	
}

class Collision {
	public Shot s;
}
class Shot {
	public String targetId;
	public int damage;
	public int xCoord;
	public int yCoord;
	public int timeStamp;
	Shot (String tid, int dmg, int xc, int yc) {
		targetId = tid; damage = dmg;
		xCoord = xc; yCoord = yc;
		timeStamp = MainDataThread.getTime();
	}
}


class ClientThread implements Runnable {
	private Socket socket;
	private Thread thisThread;
	private BufferedInputStream inStream;
	private OutputStream outStream;
	public Player player;
	
	ClientThread (Socket clientSocket) {
		socket = clientSocket;
		synchronized (this) {
			this.thisThread = Thread.currentThread();
		}
	}
	private synchronized int getCurrentTimestamp () {
		return MainDataThread.getTime();
	}
	public void run () {
		try {
			inStream = socket.getInputStream();
			outStream = socket.getOutputStream();
		} catch (IOException e) { e.printStackTrace(); }
		while (GameServer.isRunning()) {
			proccessInput
		}
		try {
			inStream.close(); outStream.close(); socket.close();
		} catch (IOException e) { e.printStackTrace(); }
		return;
	}
}

class SocketAccepter extends ServerSocket {
	private Socket socket;
}

class GameServer implements Runnable {
	private int port = 7331;
	private int maxplayers = 16;
	private int delayBeforeStart = 30;
	private SocketAccepter socketHandler;
	private Map<String, ClientThread> clientListMap;
	private ArrayList<ClientThread> clientList;
	private boolean hasStopped = false;
	
	GameServer () {
		socketHandler = new SocketAccepter (port);
	}
	public void run () {
		int keepTrack = 0;
		Socket sock;
		ClientThread ct;
		while (keepTrack<delayBeforeStart) {
			sock = null; ct = null;
			try {
				sock = socketHandler.accept();
				ct = new ClientThread (sock);
				ct.start(); clientList.add(ct);
			} catch (IOException e) { e.printStackTrace(); }
		}
		for (ClientThread x: clientList) {
			if (!clientListMap.containsKey(x.player.playerId)) {
				clientListMap.put(x.player.playerId, x);
			} else {
				x.player.fixName(); // add _ and 3 random digits
				clientListMap.put(x.player.playerId, x);
			}
		}
		while (isRunning()) {
			
		}
	}
	public static synchronized boolean isRunning () {
		return (!hasStopped);
	}
	public static synchronized void stop () {
		hasStopped = true;
	}
	
}



// http://www.javamex.com/tutorials/wait_notify_how_to.shtml
