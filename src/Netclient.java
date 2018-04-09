package mainP;

import java.io.*;
import java.net.*;
import java.util.*;
import javax.swing.*;

public class Netclient { //The network client class, containing the voids of a client needs
	
	String incoming = ""; //local variables used in the Netclient class
	BufferedReader reader;
	PrintWriter writer;
	Socket sock;
	InetAddress toip;
	int toport;
	
	public boolean connect(InetAddress ip, int port) { //Void of connecting to a server. Returns true if succeeded or false if not
		toip = ip;
		toport = port;
		boolean worked;
		worked = setUpNetworking();
		
		if (worked==true) {
			Thread readerThread = new Thread (new IncomingReader());
			readerThread.start();
			return true;
		} else return false;
	}
		
	private boolean setUpNetworking() { //Void that sets up the Networking. Returns true if succeeded
		try{
			sock = new Socket(toip, toport);
			InputStreamReader streamReader = new InputStreamReader (sock. getInputStream ( ) ) ;
			reader = new BufferedReader(streamReader);
			writer = new PrintWriter(sock.getOutputStream());
			return true;
		}
		catch(IOException ex) {
			ex.printStackTrace();
			return false;
		}
	}
		
	public void SendM(String mess) { //Void called to send a message
		try{
			writer.println(mess);
			writer.flush();
		}
		catch(Exception ex) {ex.printStackTrace();}
	}
	
	public synchronized String GetM() { //Void called to get the last message if it exists, else returns null
		if (incoming.equals("")) {
			return null;
		} else {
			String message = "";
			message = incoming;
			incoming = "";
			return message;
		}
		
	}
		
	public class IncomingReader implements Runnable { //Class that handles the newly incoming messages
		public void run() { //It's runnable void, the thread itself
			String message;
			try {
				while ((message = reader.readLine()) != null) { 
					incoming = message;
				}
			} catch (Exception ex) { ex.printStackTrace();}	
		}
	}	
}