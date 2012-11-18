package rky.dating.io;

import rky.dating.player.Player;
import rky.dating.player.Player.Players;
import rky.dating.player.Player.Role;

public class IoManager
{
	private final Server server;
	private Players players;

	public IoManager(int port)
	{
		server = new Server(port);
	}

	public Players start(int n)
	{
		players = server.start();
		server.send(players.matchmaker, "M " + n );
		server.send(players.person, "P " + n );
		return players;
	}

	/**
	 * Will shutdown the game, sending the message to both players
	 */
	public void shutdown(Message m)
	{
		server.send(players.matchmaker, m.toString());
		server.send(players.person, m.toString() );
	}

	public void send(Player p, Message m)
	{
		System.err.println("S->" + p + ": " + m);
		server.send(p, m.toString());
	}

	public Message receive(Player p)
	{
		String msg = server.receive(p);
		System.err.println("S<-" + p + ": " + msg);
		return new Message(msg);
	}

	/**
	 * A convenience method for a user prompt and reply
	 * 
	 * @param p The player to which the message is to be sent
	 * @param m The prompt to the player ex. WEIGHTS
	 * @return The reply from the player.
	 */
	public Message prompt(Player p, Message m)
	{
		server.send(p, m.toString());
		return new Message(server.receive(p));
	}

	public Players getPlayers()
	{
		return players;
	}

	public void informPlayer(Player player, String msg)
	{
		send(player,new Message(msg)); 
	}

	public void confirmPlayer(Player player) 
	{
		send(player,Message.ACK);
	}

	public void informPlayerOfError(Player player, String errMsg)
	{
		send(player, Message.createError(errMsg));
	}

	public Message requestForNoise(Player player){

		if(player.role == Player.Role.P ){

			informPlayer(player, "NOISE" );
			return receive(player);

		}else{
			throw new IllegalArgumentException("Request for NOISE can only be sent to Person");
		}

	}

	public Message requestForCandidate(Player player){

		if(player.role == Player.Role.M ){

			informPlayer(player, "CANDIDATE" );
			return receive(player);

		}else{
			throw new IllegalArgumentException("Request for CANDIDATE can only be sent to Matchmaker");
		}
	}

	public Message requestForPlayerPreferences(Player player){

		if(player.role == Player.Role.P ){

			informPlayer(player, "WEIGHTS" );
			return receive(player);

		}else{
			throw new IllegalArgumentException("Request for WEIGHTS can only be sent to Person");
		}
	}

}
