package mainP;

import javax.swing.*; //import the needed packages
import java.awt.*;
import java.awt.event.*;
import java.net.*;

public class mainC extends JFrame implements ActionListener { //beginning of the main class
	public int SIZE = 19; //Size of the map
	public JButton[][] Gb = new JButton[SIZE][SIZE]; //Grid of the map
	public byte[][] Gbv = new byte[SIZE][SIZE]; //A variable containing information of the map
	public String myname = "player"; //The player's name
	public boolean myserver = false; //Am I running a server?
	public boolean myturn = true; //Is this my turn?
	public boolean ongame = false; //Is this an online game?
	
	public JMenu m1 = new JMenu("Menu"); //J variables to the Menus
	JMenuItem mi1 = new JMenuItem("New Game 2P1C");
	JMenuItem mi2 = new JMenuItem("Connect to a server");
	JMenuItem mi3 = new JMenuItem("Request new game");
	JMenuItem mi4 = new JMenuItem("Exit");
	public JMenu m2 = new JMenu("Options");
	JMenuItem moi1 = new JMenuItem("Start game server");
	JMenuItem moi2 = new JMenuItem("Change my name");
	public JMenu m3 = new JMenu("Help");
	JMenuItem mhi1 = new JMenuItem("What's my IP?");
	
	public JTextField Asor = new JTextField("Program started.. Player 1 turn!"); //Status text field.
	public ImageIcon iconx = new ImageIcon("x.jpg"); //Icon of X
	public ImageIcon icono = new ImageIcon("o.jpg"); //Icon of Y
	public int lepes = 0; //Number of steps made by players in a game
	public InetAddress Toip; //Connecting to which IP
	public int Sport = 6731; //Hard coded network port used by the program.
	
	Netserver nets = new Netserver(); //Import of Class Netserver
	Netclient netc = new Netclient(); //Import of Class Netclient
	
	public mainC() { //Code of the main window
		//Standard options
		setTitle("Gomoku");
		setSize(570, 640);
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		//Declaring Menus
		m1.add(mi1);
		mi1.addActionListener(this);
		m1.addSeparator();
		m1.add(mi2);
		mi2.addActionListener(this);
		m1.add(mi3);
		mi3.setEnabled(false);
		mi3.addActionListener(this);
		m1.addSeparator();
		m1.add(mi4);
		mi4.addActionListener(this);
		m2.add(moi1);
		moi1.setEnabled(true);
		moi1.addActionListener(this);
		m2.addSeparator();
		m2.add(moi2);
		moi2.addActionListener(this);
		m3.add(mhi1);
		mhi1.addActionListener(this);
		JMenuBar jmb = new JMenuBar();
		jmb.add(m1);
		jmb.add(m2);
		jmb.add(m3);
		
		//Declaring the grid of the map
		BorderLayout mainLO = new BorderLayout();
		setLayout(mainLO);
		
		GridLayout gridLO = new GridLayout(SIZE,SIZE);
		JPanel Pgrid = new JPanel(gridLO);
		for (int i=0; i<SIZE; i++)
			for (int j=0; j<SIZE; j++) {
				Gb[i][j] = new JButton();
				Pgrid.add(Gb[i][j]);
				Gb[i][j].addActionListener(this);
				Gb[i][j].putClientProperty("key", i);
			}
		
		//Declaring the status text Field
		GridLayout LOAsor = new GridLayout(1,1);
		JPanel PAsor = new JPanel(LOAsor);
		PAsor.add(Asor);
		Asor.setEditable(false);
		
		//Adding in the declared window elements
		setJMenuBar(jmb);
		add(Pgrid, BorderLayout.CENTER);
		add(PAsor, BorderLayout.SOUTH);		
		
		setVisible(true);
	}
	
	//void called if something happened in the main window.
	public void actionPerformed(ActionEvent evt) {
		Object source = evt.getSource(); //getting the source of this event
		
		//menu events
		if (source == mi1) {
			myturn = true;
			if (ongame==true) {
				netc.SendM("disc");
			} else {
				ongame=false;
				newgame();
				lepes++;
			}
		}
		
		if (source == mi2)
			if (myname==null || myname=="" || myname=="player") {
				String Toadr = JOptionPane.showInputDialog("Enter his Address: ");
				try {
					Toip = InetAddress.getByName(Toadr);
				} catch(Exception e) {
					e.printStackTrace();
					JOptionPane.showMessageDialog(null,"Erron in address!");
				}
				boolean worked;
				worked = netc.connect(Toip, Sport);
				if (worked == true) {
					StartMessageWatcher();
					JOptionPane.showMessageDialog(null,"Connected!");
					netc.SendM("cconn");
				} else {
					JOptionPane.showMessageDialog(null,"Connected failed!");
				}
			}
		
		if (source == mi3) {
			if (myserver==true)
				netc.SendM("srng");
			else 
				netc.SendM("crng");
		}
		
		if (source == mi4)
			System.exit(1);
		
		if (source == moi1) {
			nets.start(Sport);
			netc.connect(InetAddress.getLoopbackAddress(), Sport);
			myserver = true;
			StartMessageWatcher();
		}
		
		if (source == moi2) {
			getmyname();
		}		
		
		//Get my IP in the help menu
		if (source == mhi1) {
			try {
				InetAddress myip = InetAddress.getLocalHost();
				Asor.setText("Your IP: " + myip.getHostAddress());
				JOptionPane.showMessageDialog(null,myip.getHostAddress());
			}
			catch(Exception e){e.printStackTrace();}
		}
		
		//handling of map click event
		if (ongame == false) {
			for (int i=0;i<SIZE;i++)
				for(int j=0;j<SIZE;j++)
					if (source == Gb[i][j] && Gbv[i][j] == 0) {
						if (lepes % 2 == 0) {
							Gb[i][j].setIcon(icono);
							Gbv[i][j]=1;
							Asor.setText("Player 2 turn!");
						}
						else {
							Gb[i][j].setIcon(iconx);
							Gbv[i][j]=2;
							Asor.setText("Player 1 turn!");
						}
						statuscheck(i,j);
						lepes++;		
					}
		} else {
			if (myturn==true)
				for (int i=0;i<SIZE;i++)
					for(int j=0;j<SIZE;j++)
						if (source == Gb[i][j] && Gbv[i][j] == 0) {
							if (myserver==true) {
								Gb[i][j].setIcon(icono);
								Gbv[i][j]=1;
								netc.SendM("s" + i + "," + j);
								Asor.setText("Enemy turn! Waiting..");
								myturn=false;
							} else {
								Gb[i][j].setIcon(iconx);
								Gbv[i][j]=2;
								netc.SendM("c" + i + "," + j);
								Asor.setText("Enemy turn! Waiting..");
								myturn=false;
							}
							lepes++;
							statuscheck(i,j);
						}			
		}
					
	}
	
	//Starting void of Message Watcher (network message listener)
	public void StartMessageWatcher() {
		moi1.setEnabled(false);
		mi2.setEnabled(false);
		mi3.setEnabled(true);
		
		Thread mwthread = new Thread(new MessageWatcher());
		mwthread.start();
	}
	
	//The new thread that listens to network messages and handles them
	public class MessageWatcher implements Runnable {
		public void run() {
			while(true) {
				String incM = "";
				incM = netc.GetM();
				
				if (incM != null) {
					if (incM.equals("srng") && myserver == false)
						if (oke()==true)
							netc.SendM("snog");
						else netc.SendM("rnog");
				
					if (incM.equals("crng") && myserver == true)
						if (oke()==true)
							netc.SendM("snog");
						else netc.SendM("rnog");
					
					if (incM.equals("snog"))
						startongame();
					
					if (incM.equals("rnog"))
						Asor.setText("New on game refused!");
					
					if (incM.equals("disc")) {
						JOptionPane.showMessageDialog(null,"New offline game started by someone! On game closed.");
						ongame=false;
						newgame();
					}
						
					
					if (myserver == true)
						if (incM.equals("cconn")) {
							Asor.setText("A client has connected!");
							JOptionPane.showMessageDialog(null,"A client has connected!");
						}
							
					
					if (myserver == false) {
						for (int i=0;i<SIZE;i++)
							for(int j=0;j<SIZE;j++)
								if (incM.equals("s" + i + "," + j)) {
									Gb[i][j].setIcon(icono);
									Gbv[i][j]=1;
									Asor.setText("Enemy moved, it's your turn!");
									myturn=true;
									lepes++;
									statuscheck(i,j);
								}
					} else {
						for (int i=0;i<SIZE;i++)
							for(int j=0;j<SIZE;j++)
								if (incM.equals("c" + i + "," + j)) {
									Gb[i][j].setIcon(iconx);
									Gbv[i][j]=2;
									Asor.setText("Enemy moved, it's your turn!");
									myturn=true;
									lepes++;
									statuscheck(i,j);
								}
					}
					
				
				}
					
				try {
					Thread.sleep(100);
				} catch (Exception ex) {ex.printStackTrace();}
			}
		}
	}
	
	//void of asking the player to start a new network game
	public boolean oke() {
		int ok = JOptionPane.showConfirmDialog(this, "Wanna start new on game?");
		if (ok==0) return true; else return false;	
	}
	
	//void called if new network game started
	public void startongame() {
		if (myserver==true) {
			myturn = true;
			lepes = 1;
			Asor.setText("The on game has started! It's your turn!");
		} else  {
			myturn = false;
			lepes = 1;
			Asor.setText("The on game has started! It's enemy turn! Waiting...");
		}
		ongame = true;
		newgame();
	}
	
	//Supervision of the last move to see if anybody won
	public void statuscheck(int x, int y) {
		int a=0, b=0, c=0, d=0;	
		
		for (int i=-4;i<=4;i++) {
			if (x+i>=0 && x+i<SIZE)
				if (Gbv[x+i][y] == lepes % 2 + 1) a++; else a=0;
			if (y+i>=0 && y+i<SIZE)
				if (Gbv[x][y+i] == lepes % 2 + 1) b++; else b=0;
			if (x+i>=0 && x+i<SIZE && y+i>=0 && y+i<SIZE)
				if (Gbv[x+i][y+i] == lepes % 2 + 1) c++; else c=0;
			if (x+i>=0 && x+i<SIZE && y-i>=0 && y-i<SIZE)
				if (Gbv[x+i][y-i] == lepes % 2 + 1) d++; else d=0;
			
			if (a>=5 || b>=5 || c>=5 || d>=5)
				if (lepes % 2 == 0)
					gameover(true);
				else
					gameover(false);
		}
	}
	
	//Void called if New simple (non network) game is starting
	public void newgame() {
		for (int i=0;i<SIZE;i++)
			for(int j=0;j<SIZE;j++) {
				Gb[i][j].setIcon(null);
				Gbv[i][j]=0;
			}
		lepes=lepes % 2;
		if (ongame == false) {
			if (lepes % 2 == 0)
				Asor.setText("New game started.. Player 2 turn!");
			else
				Asor.setText("New game started.. Player 1 turn!");
		}
	}
	
	//Void called by the Supervisor void if the game is over
	public void gameover(boolean p1won) {
		if (ongame==false) {
			if (p1won==true) {
				Asor.setText("Player 1 won!");
				JOptionPane.showMessageDialog(null,"Player 1 won!");
			}
			else {
				Asor.setText("Player 2 won!");
				JOptionPane.showMessageDialog(null,"Player 2 won!");
			}
			newgame();
		} else {
			if (myturn==true) {
				Asor.setText("You lost!");
				JOptionPane.showMessageDialog(null,"You lost!");
			} else {
				Asor.setText("You won!");
				JOptionPane.showMessageDialog(null,"You won!");
			}
			startongame();
		}		
	}
	
	//Simple void to get the player's name
	public void getmyname() {
		do {
			myname=JOptionPane.showInputDialog("Enter your name:");
		}
		while (myname=="" || myname=="player");
		if (myname==null) myname="player";
		Asor.setText("Your name is " + myname + " now.");
	}
	
	//The main void of the class MainC, which generates the main window
	public static void main(String[] args) {
		mainC ablak = new mainC();
	}
}

//Ádám Kunák //the name of the writer of this code :)