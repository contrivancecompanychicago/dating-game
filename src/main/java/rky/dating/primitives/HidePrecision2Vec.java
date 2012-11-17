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
