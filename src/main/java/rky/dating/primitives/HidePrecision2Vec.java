package rky.dating.primitives;

import java.util.List;

import vecs.Veci;

public class HidePrecision2Vec extends Veci
{
	public HidePrecision2Vec( int degree )
	{
		super( degree );
	}
	
	public HidePrecision2Vec( List<Integer> vec )
	{
		super( vec );
	}
	
	public HidePrecision2Vec( String vecString )
	{
		super( vecString.split(", ").length );
		if( vecString.charAt(0) != '[' || vecString.charAt( vecString.length()-1) != ']' )
			throw new IllegalArgumentException("brackets '[' and ']' are expected to start and terminate the vector: " + vecString);
		vecString = vecString.substring(1, vecString.length()-1);
		
		String[] stringNums = vecString.split(", ");
		
		for( int i = 0; i < stringNums.length; i++ )
		{
			String[] split = stringNums[i].split("\\.");  // before point and after point
			int val = Math.abs(Integer.parseInt(split[0])) * 100;
			if( split.length == 2 ) {
				///////  this may need to be replaced
				if( split[1].length() == 1 )
					val += Integer.parseInt(split[1]) * 10;
				else if( split[1].length() == 2 )
					val += Integer.parseInt(split[1]);
				else
					throw new IllegalArgumentException("invalid precision on string vector");
				//////////////////////////////////////
				//////   ... replaced with this:    
				/*
				if( split[1].length() > 0 )
					val += Integer.parseInt("" + split[1].charAt(0)) * 10;
				if( split[1].length() > 1 )
					val += Integer.parseInt("" + split[1].charAt(1));
				 *///////   ... end of replace
			}
			if( split[0].charAt(0) == '-' )
				val = -val;
			
			set( i, val );
			
		}
	}
	
	
	
	public String toString()
	{
		StringBuilder sb = new StringBuilder("[");
		for( int i = 0; i < getDegree(); i++ )
		{
			int v = get(i);
			sb.append(String.format("%.2f, ", ((double)v / 100)));
		}
		sb.setLength( sb.length() - 2 );
		sb.append("]");
		return sb.toString();
	}
}
