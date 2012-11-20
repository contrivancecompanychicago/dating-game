package rky.dating.io;

import java.applet.Applet;
import java.awt.Frame;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import rky.dating.Dating;
import rky.dating.player.Player.Players;
import rky.gui.gamePlatform.DatingGUI;

public class Main {

	private static IoManager io;
	private static DatingGUI applet;
	
	public  final static int IDEAL_WIDTH = 800;
	public  final static int IDEAL_HEIGHT = 400;
	
	/**
	 * Assuming input as N, port number
	 * @param args
	 */
	public static void main(String args[]){

		try{

			int N = Integer.parseInt(args[0]);
			int PORT = Integer.parseInt(args[1]);

			io = new IoManager(PORT);

			System.out.println("TEST: About to start server...");
			System.out.println("Listening to port :"+PORT);
			
			io.start(N);

			Dating.setIO(io);
			
			//initiate applet
			startGUI();
			
			//initiate players
			Players players = io.getPlayers();
			applet.startGame(players.matchmaker.name, players.person.name);
			Dating.applet = applet;

			Dating.setMatchmaker(players.matchmaker);
			Dating.setPerson(players.person);

			Dating.runGame(N);

		
	}catch(Exception e){
		e.printStackTrace();
	}

}

	private static void startGUI() {
		
		applet = new DatingGUI();
		applet.init();
		applet.start();

		Frame frame = new Frame("RKY-Dating-Game Applet");
		frame.addWindowListener(new WindowAdapter()
		{
			public void windowClosing(WindowEvent e)
			{
				System.exit(0);
			}
		});

		frame.add(applet);
		frame.setSize(IDEAL_WIDTH,IDEAL_HEIGHT);
		frame.setVisible(true);		
	}
}
