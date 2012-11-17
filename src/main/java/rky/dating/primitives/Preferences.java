package rky.dating.primitives;

import java.util.ArrayList;
import java.util.Collections;

public class Preferences extends HidePrecision2Vec
{
	private String msgCache_ = null;
	
	public Preferences(int degree)
	{
		super(degree);
	}
	
	public Preferences(String vecString)
	{
		super(vecString);
	}
	
	
	
	public Preferences(ArrayList<Integer> prefsList)
	{
		super( prefsList );
	}



	// if preference is not valid, the reason may be retrieved via the getCachedMsg() method
	public boolean isValid()
	{
		msgCache_ = validate();
		return msgCache_ == null;
	}
	
	// returns null if valid, error message otherwise
	public String validate()
	{
		int positive = 0;
		int negative = 0;
		
		for( int i = 0; i < getDegree(); i++ )
		{
			int val = get(i);
			if( val > 0 )
				positive += val;
			else
				negative += val;
		}
		
		if( positive !=  100 )  return "positive preferences add up to " + positive;
		if( negative != -100 )  return "negative preferences add up to " + negative;
		
		return null;
	}
	
	public static Preferences generateRandomPreferences( int degree )
	{
		ArrayList<Integer> prefsList = new ArrayList<Integer>(degree);
		for( int i = 0; i < degree; i ++ )
			prefsList.add(0);
		
		// some of these will be 0's
		int howManyPositives = 1 + (int)(Math.random() * (degree-1)) % (degree-1);  // must always be at least 1
		int howManyNegatives = degree - howManyPositives;

		for( int i = 0; i < 100; i++ ) {
			int posIndex = (int)( Math.random() * howManyPositives ) % howManyPositives;
			int negIndex = (int)(( Math.random() * howManyNegatives ) % howManyNegatives) + howManyPositives; 
			prefsList.set( posIndex, prefsList.get(posIndex) + 1 );
			prefsList.set( negIndex, prefsList.get(negIndex) - 1 );
		}
		
		Collections.shuffle(prefsList);
		
		return new Preferences( prefsList );
	}
	
	public String getCachedMsg()
	{
		return msgCache_;
	}
}
