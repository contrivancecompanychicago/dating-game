package rky.dating.io;

import rky.dating.Dating;
import rky.dating.player.Player.Players;

public class Main {

	private static IoManager io;

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

			//initiate players
			Players players = io.getPlayers();

			Dating.setMatchmaker(players.matchmaker);
			Dating.setPerson(players.person);

			Dating.runGame(N);

		
	}catch(Exception e){
		e.printStackTrace();
	}

}
}
