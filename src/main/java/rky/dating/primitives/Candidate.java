package rky.dating.primitives;

import vecs.Opi;
import vecs.Veci;



public class Candidate extends HidePrecision2Vec
{
	private String msgCache_ = null;
	
	public Candidate(int degree)
	{
		super(degree);
	}

	// if candidate is not valid, the reason may be retrieved via the getCachedMsg() method
	public boolean isValid()
	{
		msgCache_ = validate();
		return msgCache_ == null;
	}
	
	// returns null if valid, an error message otherwise
	public String validate()
	{
		for( int i = 0; i < getDegree(); i++ )
			if( get(i) < 0 || get(i) > 100 )
				return "Candidate value at " + i + " is out of range: " + get(i);
		return null;
	}
	
	public int getScore( Preferences prefs, Noise noise )
	{
		Veci p = new Veci(prefs);
		Veci n = new Veci(noise);
		Veci c = new Veci(this);
		
		return Opi.DOT.performOp( Opi.PLUS.performOp(p, n), c );
	}
	
	public int getScore( Preferences prefs )
	{
		Veci p = new Veci(prefs);
		Veci c = new Veci(this);
		
		return Opi.DOT.performOp( p, c );
	}
	
	public static Candidate generateRandomCandidate( int degree )
	{
		Candidate candidate = new Candidate( degree );
		
		for( int i = 0; i < degree; i++ )
			candidate.set(i, (int)Math.round((Math.random() * 100)));
		
		return candidate;
	}
	
	public String getCachedMsg()
	{
		return msgCache_;
	}
	
	
	
	public static void main( String[] args )
	{
		final int DEGREE = 50;
		
		Preferences prefs;
		Candidate cand;
		Noise noise;

		for( int i = 0; i < 1000; i++ )
		{
			prefs = Preferences.generateRandomPreferences(DEGREE);
			if( !prefs.isValid() )
				System.out.println( "PREFS: " + prefs + "\n" + prefs.getCachedMsg() );
			
			cand = Candidate.generateRandomCandidate(DEGREE);
			if( !cand.isValid() )
				System.out.println( "CAND: " + cand + "\n" + cand.getCachedMsg() );
			
			noise = Noise.generateRandomNoise( prefs );
			if( !noise.isValid( prefs ) )
				System.out.println( "NOISE: " + noise + "\n" + noise.getCachedMsg() );
			
			System.out.println( "scores: " + cand.getScore(prefs, noise) + ", " + cand.getScore(prefs, new Noise(DEGREE)));		
		}
	}
}
