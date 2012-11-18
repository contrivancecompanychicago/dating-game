package rky.gui.gamePlatform;

import java.awt.Event;

// A RECTANGLE SHAPED THING

public class RectPiece extends Piece implements Comparable<RectPiece>
{
	public Piece setBounds(double x, double y, double width, double height) {
		this.x = x + width / 2;
		this.y = y + height / 2;
		X[0] = x;
		Y[0] = y;
		X[1] = x + width;
		Y[1] = y;
		X[2] = x + width;
		Y[2] = y + height;
		X[3] = x;
		Y[3] = y + height;
		setShape(X, Y, 4);
		return this;
	}

	public int compareTo(RectPiece o) {
		
		return super.getId() - o.getId();
	}
	
	boolean mouseDown(int x, int y) {
	 
		 return true;
	 }

	 boolean mouseDrag(int x, int y) {
		 
		 return true;
	 }

	 boolean mouseUp(Event e,int x, int y) {
		
		 return true;
	 }
}

