package rky.gui.gamePlatform;

// GENERIC THING

import java.awt.*;
import java.awt.image.ImageObserver;



public class Piece
{
	
	/**
      The default color for a game piece.
	 */

	public final static Color defaultColor = Color.gray;
	

	/**
	 * Background image
	 */
	Image image;
	
	//Using delegation 
	//need a better way of doing this
	public GamePlatform delegate;
	
	//identifier for gaming logic
	public int tag;


	public Piece() {
		setId(idCount++);
	}

	Piece setGamePlatform(GamePlatform platform) {
		this.platform = platform;
		return this;
	}

	/**
      Returns the game platform containing this piece.
	 */

	public GamePlatform getGamePlatform() {
		return platform;
	}

	Piece setId(int id) {
		this.id = id;
		return this;
	}

	/**
     Assign a label to this piece.
	 */

	public Piece setLabel(String label) {
		this.label = label;
		return this;
	}

	/**
      Get the unique id of this this piece.
	 */

	public int getId() {
		return id;
	}

	/**
      Set a rectangular bounding area for this piece.
	 */

	public Piece setBounds(double x, double y, double width, double height) {
		return this;
	}

	void update(Graphics g) {
		updateShape();

		if (polygon != null) {
			g.setColor(color);
			g.fillPolygon(polygon);
			g.setColor(Color.black);
			g.drawPolygon(polygon);

		}
		if (label != null) {
			g.setColor(Color.black);
			int ascent = g.getFontMetrics(g.getFont()).getAscent();
			g.drawString(label, (int)(centerX - platform.stringWidth(g, label) / 2),
					(int)(centerY + ascent / 2));
		}
	}

	/**
      Returns true iff this piece contains pixel (x,y).
	 */

	 public boolean contains(int x, int y) {
		 updateShape();
		 return polygon != null && polygon.contains(x, y);
	 }

	 boolean mouseDown(int x, int y) {
		 mx = x;
		 my = y;
		 
		 if(delegate != null)
			 delegate.pieceClicked(this);
		 
		 return false;
	 }

	 boolean mouseDrag(int x, int y) {
		 setX(this.x + x - mx);
		 setY(this.y + y - my);
		 mx = x;
		 my = y;
		 
		 if(delegate != null)
			 delegate.pieceDragged(this);
		 
		 return false;
	 }

	 boolean mouseUp(Event e,int x, int y) {
		 
		 
		 if(delegate != null)
			 delegate.didFinishedDrag(e,this,x,y);
		 
		 return false;
	 }

	 boolean keyUp(int key) {
		 return false;
	 }

	 boolean keyDown(int key) {
		 return false;
	 }

	 /**
      Set the color of this piece.
	  */

	 public Piece setColor(Color color) {
		 this.color = color;
		 return this;
	 }

	 /**
      Get the x position of this piece.
	  */

	 public double getX() { return x; }

	 /**
      Get the y position of this piece.
	  */

	 public double getY() { return y; }

	 /**
      Set the x position of this piece.
	  */

	 public Piece setX(double x) {
		 moveX += x - this.x;
		 this.x = x;
		 needToUpdateShape = true;
		 return this;
	 }

	 /**
      Set the y position of this piece.
	  */

	 public Piece setY(double y) {
		 moveY += y - this.y;
		 this.y = y;
		 needToUpdateShape = true;
		 return this;
	 }

	 /**
      Set the shape of this piece, given arrays of X coords and Y coords.
	  */

	 public Piece setShape(double X[], double Y[]) {
		 return setShape(X, Y, X.length);
	 }

	 /**
      Set the shape of this piece, given arrays of X coords and Y coords, and number of points.
	  */

	 public Piece setShape(double X[], double Y[], int n) {
		 this.n = n;
		 for (int i = 0 ; i < n ; i++) {
			 this.X[i] = X[i];
			 this.Y[i] = Y[i];
		 }
		 needToUpdateShape = true;
		 return this;
	 }

	 void updateShape() {
		 if (needToUpdateShape) {
			 centerX = 0;
			 centerY = 0;

			 for (int i = 0 ; i < n ; i++) {
				 X[i] += moveX;
				 Y[i] += moveY;
				 IX[i] = (int)X[i];
				 IY[i] = (int)Y[i];

				 centerX += X[i] / n;
			 }

			 moveX = moveY = 0;
			 polygon = new Polygon(IX, IY, n);
			 needToUpdateShape = false;
		 }
	 }

	 Color color = defaultColor;
	 double X[] = new double[100];
	 double Y[] = new double[100];
	 int n = 0;

	 int IX[] = new int[100];
	 int IY[] = new int[100];
	 double x = 0, y = 0, mx = 0, my = 0;
	 double moveX = 0, moveY = 0;

	 String label = "";
	 Polygon polygon = null;
	 boolean needToUpdateShape = false;
	 GamePlatform platform;
	 int id = 0;
	 double centerX, centerY;
	 static int idCount = 0;
}

