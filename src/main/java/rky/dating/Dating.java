package rky.dating;

import rky.dating.io.IoManager;
import rky.dating.io.Message;
import rky.dating.player.Player;
import rky.dating.player.Player.Role;
import rky.dating.primitives.Candidate;
import rky.dating.primitives.Noise;
import rky.dating.primitives.Preferences;

public class Dating
{

	private static IoManager io;
	private static Player matchmaker;
	private static Player person;

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
		//initaliztion is done by IOManager
		//informPlayer( Player.Role.P, "P " + n ); 
		//informPlayer( Player.Role.M, "M " + n );


		//------------------------------------------------------
		startTimer( Role.P );
		Preferences p = getPlayerPreferences(n);
		pauseTimer( Role.P );

		if( !p.isValid() ) {
			informPlayerOfError( Role.P, p.getCachedMsg() );
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
			randomCandidatesMsg.append( " " + score + " " );
		}
		randomCandidatesMsg.setLength( randomCandidatesMsg.length()-1 );
		informPlayer( Role.M, randomCandidatesMsg.toString() );
		//------------------------------------------------------


		//------------------------------------------------------
		double maxScore = Double.NEGATIVE_INFINITY;
		int turnNumber = 0;
		for( ; turnNumber < 20; turnNumber++ )
		{
			if( maxScore == 1 )
				break;

			startTimer( Player.Role.M );
			Candidate c = getCandidateFromM( n );
			pauseTimer( Player.Role.M );
			if( c.isValid() )
				confirmPlayer( Player.Role.M, "accepted Candidate" );
			else
				informPlayerOfError( Player.Role.M, c.getCachedMsg() );



			startTimer( Player.Role.P );
			Noise noise = getNoiseFromP();
			pauseTimer( Player.Role.P );
			if( noise.isValid( p ) )
				confirmPlayer( Player.Role.P, "accepted Noise" );
			else
				informPlayerOfError( Player.Role.P, noise.getCachedMsg() );



			double score = c.getScore( p, noise );              // noisy score
			informPlayer( Player.Role.M, String.format("%.2f", score) );   // note that format() rounds the float
			maxScore = Math.max( maxScore, c.getScore( p ) );   // actual score

		}



		informPlayer( Player.Role.M, String.format("%.2f", maxScore) + " " + turnNumber );
		informPlayer( Player.Role.P, String.format("%.2f", maxScore) + " " + turnNumber );
	}

	private static Noise getNoiseFromP() {
//		Noise n = Noise.generateRandomNoise( p );
//		System.out.println( "P->S: " + n );
//		return n;
		
//		informPlayer( Role.P, "NOISE" );
//		Message noise = io.receive( io.getPlayers().person );
//		return new Noise( noise.toString() );
		
		Message response = io.requestForNoise(getPlayer(Player.Role.P));
		return new Noise(response.toString());
	}

	private static Candidate getCandidateFromM(int n) {
//		Candidate c = Candidate.generateRandomCandidate( n ); 
//		System.out.println( "M->S: " + c );
//		return c;
		
//		informPlayer( Role.M, "CANDIDATE" );
//		Message candidate = io.receive( io.getPlayers().matchmaker );
//		return new Candidate( candidate.toString() );
		
		Message response = io.requestForCandidate(getPlayer(Player.Role.M));
		return new Candidate(response.toString());
	}

	private static void pauseTimer(Role m) {
		// TODO Auto-generated method stub

	}

	private static void startTimer(Role m) {
		// TODO Auto-generated method stub

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

	private static Preferences getPlayerPreferences(int n)
	{
		// TODO Actually get from player
//		return Preferences.generateRandomPreferences(n);
		
//		informPlayer( Role.P, "WEIGHTS" );
//		Message prefs = io.receive( io.getPlayers().person );
//		return new Preferences( prefs.toString() );
		
		Message response = io.requestForPlayerPreferences(getPlayer(Player.Role.P));
		Preferences p = new Preferences(response.toString()); 
		return p;
	}

}
