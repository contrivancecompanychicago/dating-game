package rky.dating;

import rky.dating.player.Player;
import rky.dating.player.Player.Role;
import rky.dating.primitives.Candidate;
import rky.dating.primitives.Noise;
import rky.dating.primitives.Preferences;

public class Dating
{

    public static void main(String[] args)
    {
    	// TODO: N should be a command line argument
        int N = 40;
        
        runGame(N);
    }

	private static void runGame(int n)
	{
		informPlayer( Player.Role.P, "P " + n );
		informPlayer( Player.Role.M, "M " + n );
		

		//------------------------------------------------------
		startTimer( Player.Role.P );
		informPlayer( Player.Role.P, "WEIGHTS" );
		
		Preferences p = getPlayerPreferences(n);
		pauseTimer( Player.Role.P );
		
		if( !p.isValid() ) {
			informPlayerOfError( Player.Role.P, p.getCachedMsg() );
		} else {
			confirmPlayer( Player.Role.P, "accepted P");
		}
		//------------------------------------------------------
		
		
		//------------------------------------------------------
		StringBuilder randomCandidatesMsg = new StringBuilder();
		for( int i = 0; i < 20; i++ )
		{
			Candidate c = new Candidate( Candidate.generateRandomCandidate( n ).toString() );
			randomCandidatesMsg.append( c.toString() );
			double score = c.getScore( p );
			randomCandidatesMsg.append( " " + score + " " );
		}
		randomCandidatesMsg.setLength( randomCandidatesMsg.length()-1 );
		informPlayer( Player.Role.M, randomCandidatesMsg.toString() );
		//------------------------------------------------------
		
		
		//------------------------------------------------------
		double maxScore = Double.NEGATIVE_INFINITY;
		int turnNumber = 0;
		for( ; turnNumber < 20; turnNumber++ )
		{
			if( maxScore == 1 )
				break;

			startTimer( Player.Role.M );
			informPlayer( Player.Role.M, "CANDIDATE" );
			// TODO receive candidate from user
			Candidate c = getCandidateFromM( n );
			pauseTimer( Player.Role.M );
			if( c.isValid() )
				confirmPlayer( Player.Role.M, "accepted Candidate" );
			else
				informPlayerOfError( Player.Role.M, c.getCachedMsg() );
			
			
			
			startTimer( Player.Role.P );
			informPlayer( Player.Role.P, "NOISE" );
			Noise noise = getNoiseFromP( p );  //TODO shouldn't need p, should already have it
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

	private static Noise getNoiseFromP(Preferences p) {
		Noise n = Noise.generateRandomNoise( p );
		System.out.println( "P->S: " + n );
		return n;
	}

	private static Candidate getCandidateFromM(int n) {
		Candidate c = Candidate.generateRandomCandidate( n ); 
		System.out.println( "M->S: " + c );
		return c;
	}

	private static void pauseTimer(Role m) {
		// TODO Auto-generated method stub
		
	}

	private static void startTimer(Role m) {
		// TODO Auto-generated method stub
		
	}

	private static void informPlayer( Player.Role role, String msg) {
		// TODO Auto-generated method stub
		System.out.println( "S->" + (role == Player.Role.M ? "M" : "P") + ": " + msg );
	}

	private static void confirmPlayer( Player.Role role, String clarification) {
		// TODO Auto-generated method stub
		System.out.println( "S->" + (role == Player.Role.M ? "M" : "P") + ": OK    # " + clarification );
	}

	private static void informPlayerOfError( Player.Role role, String errMsg) {
		// TODO Actually send to players
		System.out.println( "S->" + (role == Player.Role.M ? "M" : "P") + ": ERROR " + errMsg );
	}

	private static Preferences getPlayerPreferences(int n)
	{
		// TODO Actually get from player
		return Preferences.generateRandomPreferences(n);
	}

}
