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
			set( i, (int)(Double.parseDouble( stringNums[i] ) * 100) );
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
