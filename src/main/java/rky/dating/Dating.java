package rky.dating;

import rky.dating.io.IoManager;
import rky.dating.io.Message;
import rky.dating.player.Player;
import rky.dating.player.Player.Role;
import rky.dating.primitives.Candidate;
import rky.dating.primitives.Noise;
import rky.dating.primitives.Preferences;
import rky.gui.gamePlatform.DatingGUI;

public class Dating
{

	private static IoManager io;
	private static Player matchmaker;
	private static Player person;
	
	public static DatingGUI applet;

	public static void main(String[] args)
	{
		// TODO: N should be a command line argument
		int N = 40;

		runGame(N);
	}

	public static void setIO(IoManager io_manager){
		io = io_manager;
	}

	public static Player getMatchmaker() {
		return matchmaker;
	}

	public static void setMatchmaker(Player M) {
		matchmaker = M;
	}

	public static Player getPerson() {
		return person;
	}

	public static void setPerson(Player p) {
		person = p;
	}

	public static void runGame(int n)
	{
		String finalScore = "if this is showing...";
		String turnsUsed = "something went horribly wrong";
		
		int turnNumber = 0;
		double maxScore = Double.NEGATIVE_INFINITY;
		
		try {
		//------------------------------------------------------
			startTimer( Role.P );
			Preferences p = getPlayerPreferences(n);
			checkTimeLeft(Role.P, pauseTimer( Role.P ));
			
			if( !p.isValid()) {
//				informPlayerOfError( Role.P, p.getCachedMsg() );
				throw new PlayerErrorException( getPlayer(Role.P), p.getCachedMsg() );
			} else {
				confirmPlayer( Role.P, "accepted P");
			}
			//------------------------------------------------------
	
			//------------------------------------------------------
			StringBuilder randomCandidatesMsg = new StringBuilder();
			for( int i = 0; i < 20; i++ )
			{
				Candidate c = Candidate.generateRandomCandidate( n );
				if( !c.isValid() )
					throw new RuntimeException( "generated an illegal candidate: " + c.toString() + " -> " + c.getCachedMsg() );
				randomCandidatesMsg.append( c.toString() );
				double score = c.getScore( p );
				randomCandidatesMsg.append( " " + String.format("%.2f", score) + " " );
			}
			randomCandidatesMsg.setLength( randomCandidatesMsg.length()-1 );
			informPlayer( Role.M, randomCandidatesMsg.toString() );
			//------------------------------------------------------
	
	
			//------------------------------------------------------
			for( ; turnNumber < 20; turnNumber++ )
			{
				if( maxScore == 1 )
					break;
	
				startTimer( Role.M );
				Candidate c = getCandidateFromM( n );
				checkTimeLeft(Role.M, pauseTimer( Role.M ));
	            
				if( c.isValid() )
					confirmPlayer( Player.Role.M, "accepted Candidate" );
				else {
//					informPlayerOfError( Player.Role.M, c.getCachedMsg() );
					throw new PlayerErrorException( getPlayer( Role.M ), c.getCachedMsg() );
				}
	
				startTimer( Role.P );
				Noise noise = getNoiseFromP();
				checkTimeLeft(Role.P, pauseTimer( Role.P ));
				
				if( noise.isValid( p ) )
					confirmPlayer( Player.Role.P, "accepted Noise" );
				else {
//					informPlayerOfError( Player.Role.P, noise.getCachedMsg() );
					throw new PlayerErrorException( getPlayer(Role.P), noise.getCachedMsg() );
				}
	
				double score = c.getScore( p, noise );              // noisy score
				informPlayer( Player.Role.M, String.format("%.2f", score) );   // note that format() rounds the float
				maxScore = Math.max( maxScore, c.getScore( p ) );   // actual score
				
				
				//update applet
				if(applet != null){
					applet.updateScore(maxScore);
				}
			}
	

			finalScore = String.format("%.2f", maxScore);
			turnsUsed = "" + turnNumber;
		}
		catch ( PlayerErrorException e )
		{
			
			if( e.getResponsiblePlayer() == getPlayer(Role.M) )
			{
				finalScore = "-1.0";
				turnsUsed  = "20";
			}
			else  // if P is responsible
			{
				finalScore = "1.0";
				turnsUsed  = "0";
			}
			
			informPlayerOfError( getRoleOf(e.getResponsiblePlayer()), e.toString() );
		}
		
			informPlayer( Player.Role.M, "GAMEOVER " + finalScore + " " + turnsUsed );
			informPlayer( Player.Role.P, "GAMEOVER " + finalScore + " " + turnsUsed );
		
	}

	private static Noise getNoiseFromP() {
		Message response = io.requestForNoise(getPlayer(Player.Role.P));
		return new Noise(response.toString());
	}

	private static Candidate getCandidateFromM(int n) {
		Message response = io.requestForCandidate(getPlayer(Player.Role.M));
		return new Candidate(response.toString());
	}

	/**
	 * Returns true if time limit was exceeded by last action.
	 */
	private static boolean pauseTimer(Role r) {
		return getPlayer(r).pauseTimer();
	}
	
	private static void startTimer(Role r) {
        getPlayer(r).startTimer();
    }
	
	private static void checkTimeLeft(Role r, boolean hasTimeLeft) throws PlayerErrorException {
        if (!hasTimeLeft) {
            throw new PlayerErrorException(getPlayer(r), "Ran out of time");
        }
    }

	private static void informPlayer( Role role, String msg) {

		if(io != null){
			io.informPlayer(getPlayer(role), msg);
		}else{
			//TODO once done with testing need to throw exception here
			System.out.println( "S->" + (role == Role.M ? "M" : "P") + ": " + msg );
		}
	}

	private static void confirmPlayer( Role role, String clarification) {

		if(io != null){
			io.confirmPlayer(getPlayer(role));
		}else{
			//TODO once done with testing need to throw exception here
			System.out.println( "S->" + (role == Player.Role.M ? "M" : "P") + ": OK    # " + clarification );
		}
	}

	private static void informPlayerOfError( Player.Role role, String errMsg) {
		if(io != null){
			io.informPlayerOfError(getPlayer(role),errMsg);
		}else{
			//TODO once done with testing need to throw exception here
			System.out.println( "S->" + (role == Player.Role.M ? "M" : "P") + ": ERROR " + errMsg );
		}
	}
	
	private static Player getPlayer(Player.Role role){

		if(role == Player.Role.M){
			return matchmaker;
		}else{
			return person;
		}
	}
	
	private static Role getRoleOf( Player player ) {
		if( player == matchmaker )
			return Role.M;
		else
			return Role.P;
	}

	private static Preferences getPlayerPreferences(int n)
	{
		Message response = io.requestForPlayerPreferences(getPlayer(Player.Role.P));
		Preferences p = new Preferences(response.toString()); 
		return p;
	}

	
	
	private static class PlayerErrorException extends Exception
	{
		private final Player responsiblePlayer;
		
		public PlayerErrorException( Player responsiblePlayer, String errorMessage )
		{
			super( errorMessage );
			this.responsiblePlayer = responsiblePlayer;
		}
		
		public Player getResponsiblePlayer()
		{
			return responsiblePlayer;
		}
		
		public String toString()
		{
			return responsiblePlayer.toString() + ": " + super.toString();
		}
	}
}
