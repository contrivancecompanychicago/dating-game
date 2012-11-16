package rky.dating.primitives;

import java.util.ArrayList;
import java.util.Collections;

import vecs.Veci;

public class Noise extends Veci
{
	private static final double MAX_PROPORTION_NOISE = 0.05;
	private static final double MAX_NOISE_MAGNITUDE = 0.2;
	
	private String msgCache_ = null;

	public Noise(int degree)
	{
		super(degree);
	}
	
	// validates the noise against given preferences 
	// if noise is not valid, the reason may be retrieved via the getCachedMsg() method
	public boolean isValid( Preferences prefs )
	{
		msgCache_ = validate( prefs );
		return msgCache_ == null;
	}
	
	// validates the noise vector against the preferences argument
	// returns null if valid, an error message otherwise
	public String validate( Preferences prefs )
	{
		if( prefs.getDegree() != getDegree() )
			return "noise doesn't match prefs in degree: " + prefs.getDegree() + " vs. " + getDegree();

		int valuesChanged = 0;
		
		for( int i = 0; i < getDegree(); i++ )
		{
			if( get(i) != 0 )
			{
				valuesChanged++;
				
				if( Math.abs( get(i) ) > Math.abs( prefs.get(i) * MAX_NOISE_MAGNITUDE ) )
					return "magnitude of noise in position " + i + " is too large: " + get(i);
			}
		}
		
		if( valuesChanged > getDegree() * MAX_PROPORTION_NOISE )
			return "too many noisy values: " + valuesChanged;
		
		return null;
	}
	
	public static Noise generateRandomNoise( Preferences prefs )
	{
		ArrayList<Integer> indices = new ArrayList<Integer>(prefs.getDegree());
		
		for( int i = 0; i < prefs.getDegree(); i++ )
			indices.add( i );
		
		Collections.shuffle( indices );
		
		int maxNoisyIndices = (int)(prefs.getDegree() * MAX_PROPORTION_NOISE);
		
		Noise noise = new Noise( prefs.getDegree() );
		for( int i = 0; i < maxNoisyIndices; i++ )
		{
			int unaltered = prefs.get( indices.get(i) );
			int alteration = (int)(unaltered * MAX_NOISE_MAGNITUDE * Math.random());
			if( Math.random() > 0.5 ) alteration = -alteration;
			noise.set( indices.get(i), alteration );
		}
		
		return noise;
	}
	
	public String getCachedMsg()
	{
		return msgCache_;
	}
}
