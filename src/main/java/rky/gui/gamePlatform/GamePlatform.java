package rky.gui.gamePlatform;

// GENERIC PLATFORM, WITH SUPPORT FOR DISPLAY, PICKING AND AUDIO.

import java.awt.*;
import java.text.*;
import java.util.*;
import java.awt.event.*;
import java.lang.reflect.*;
import javax.sound.sampled.*;
import java.io.*;
import java.net.*;

public class GamePlatform extends BufferedApplet
{
	int w = 0, h = 0;
	Color bgColor = Color.white;
	Piece selectedPiece = null;
	ArrayList collisionTrackedPieces = new ArrayList();
	ArrayList pieces = new ArrayList();
	ArrayList trackedPieces = new ArrayList();
	ArrayList shadowOfTrackedPieces = new ArrayList();
	boolean isColliding[][];
	int verbosity = 0;
	boolean isMouseOverStatus = false;
	StringBuffer buffer = new StringBuffer();
	StringBuffer eventBuffer = new StringBuffer();
	TextIO textIO;
	String logFileName = null;
	String projectName = "project";
	boolean copyingLogToStdOut = false;
	boolean isLogging = false;

	/**
      For data logging purposes, set a name for this project.
	 */

	public void setProjectName(String name) {
		projectName = name;
	}

	/**
      Toggle whether there will be data logging.
	 */

	public void setLogging(boolean state) {
		isLogging = state;
	}

	/**
      Returns which piece is currently selected.
	 */

	public Piece selectedPiece() {
		return selectedPiece;
	}

	/**
      Callback function, invoked at the start of a collision between two pieces.
	 */

	public void startCollision(Piece one, Piece two) { }

	/**
      Callback function, invoked at the end of a collision between two pieces.
	 */

	public void endCollision(Piece one, Piece two) { }

	/**
	 * Callback when pieces is dropped
	 */
	public void didFinishedDrag(Event e,Piece droppedPiece , int x, int y){}

	/**
	 * Callback when pieces is dropped
	 */
	public void pieceClicked(Piece p){}

	/**
	 * Callback when pieces is dragged
	 */
	public void pieceDragged(Piece p){}

	/**
      Set the level of verbosity of reporting game events.
	 */

	public void setVerbosity(int level) {
		verbosity = level;
		eventBuffer.append(" VERBOSITY " + verbosity);
	}

	/**
      Returns a piece in this game, indexed by its order of creation.
	 */

	public Piece piece(int i) { return ((Piece)pieces.get(i)); }

	/**
      Returns the width of the game window.
	 */

	public int getWidth() { return w; }

	/**
      Returns the height of the game window.
	 */

	public int getHeight() { return h; }

	/**
      Indicate that collisions for this piece should be tracked.
	 */

	public void setCollisionTracked(Piece piece) {
		addToSet(collisionTrackedPieces, piece);
	}

	/**
      Called by the system when the user moves the mouse.
	 */

	public boolean mouseMove(Event e, int x, int y) {
		isMouseOverStatus = y >= getHeight() - 20;
		if (verbosity >= 3)
			eventBuffer.append(" MOUSE_MOVE { " + x + " " + y + " }");
		return true;
	}

	/**
      Called by the system when the user presses the mouse.
	 */

	public boolean mouseDown(Event e, int x, int y) {
		damage = true;
		if (isMouseOverStatus) {
			setVerbosity((verbosity + 1) % 4);
			return true;
		}

		selectedPiece = null;
		for (int i = pieces.size() - 1 ; i >= 0 ; i--)
			if (piece(i).contains(x, y)) {
				selectedPiece = piece(i);
				if (verbosity >= 1)
					eventBuffer.append(" DOWN_ON " + selectedPiece.getId());
				return piece(i).mouseDown(x, y);
			}
		if (verbosity >= 3)
			eventBuffer.append(" MOUSE_DOWN { " + x + " " + y + " }");
		return false;
	}

	/**
      Called by the system when the user drags the mouse.
	 */

	public boolean mouseDrag(Event e, int x, int y) {
		damage = true;
		if (selectedPiece != null) {
			if (verbosity >= 2)
				eventBuffer.append(" DRAG_ON " + selectedPiece.getId());
			selectedPiece.mouseDrag(x, y);
		}
		if (verbosity >= 3)
			eventBuffer.append(" MOUSE_DRAG { " + x + " " + y + " }");
		return false;
	}

	/**
      Called by the system when the user releases the mouse.
	 */

	public boolean mouseUp(Event e, int x, int y) {
		damage = true;
		if (selectedPiece != null) {
			if (verbosity >= 1)
				eventBuffer.append(" UP_ON " + selectedPiece.getId());
			selectedPiece.mouseUp(e,x, y);
		}
		if (verbosity >= 3)
			eventBuffer.append(" MOUSE_UP { " + x + " " + y + " }");
		return false;
	}

	/**
      Called by the system when the user presses a key on the keyboard.
	 */

	public boolean keyDown(Event e, int key) {
		damage = true;
		for (int i = pieces.size() - 1 ; i >= 0 ; i--)
			if (piece(i).contains(e.x, e.y))
				return piece(i).keyDown(key);
		return false;
	}

	/**
      Called by the system when the user releases a key on the keyboard.
	 */

	public boolean keyUp(Event e, int key) {
		if (key == 'V' - '@')
			copyingLogToStdOut = ! copyingLogToStdOut;

		damage = true;
		for (int i = pieces.size() - 1 ; i >= 0 ; i--)
			if (piece(i).contains(e.x, e.y))
				return piece(i).keyUp(key);
		return false;
	}

	/**
      Subclasses can override this method to set up the game board.
	 */

	public void setup() {               // WHERE TO DECLARE THINGS
	}

	/**
      Subclasses can override this method to define a 30fps update behavior.
	 */

	public void update() {              // BEHAVIOR PER ANIMATION FRAME
	}

	/**
      Subclasses can override this method to draw graphics over the scene.
	 */

	public void overlay(Graphics g) {   // DRAW GRAPHICS ON TOP OF THE SCENE PER ANIMATION FRAME
	}

	// THE SUPERVISORY RENDERING LOOP, WHICH CALLS APPLICATION PROGRAMMER'S CALLBACKS

	long time0;

	/**
      Called by the system -- the rendering loop.
	 */

	public void render(Graphics g) {
		if (w == 0) {
			w = bounds().width;
			h = bounds().height;
			setup();
			isColliding = new boolean[collisionTrackedPieces.size()][pieces.size()];
			time0 = System.currentTimeMillis();
			Format format = new SimpleDateFormat("yy-MM-dd-HH:mm");
			String uniqueStamp = getClass().getName() + "-" + format.format(new Date());
			if (logFileName == null)
				logFileName = uniqueStamp;
			outputToLog(projectName + " report version 0.0 " + uniqueStamp + "\n");
		}

		long time = System.currentTimeMillis() - time0;

		for (int i = 0 ; i < collisionTrackedPieces.size() ; i++) {
			Piece piece1 = (Piece)collisionTrackedPieces.get(i);
			for (int j = 0 ; j < pieces.size() ; j++) {
				Piece piece2 = (Piece)pieces.get(j);
				if (piece1 != piece2) {

					boolean collision = isColliding(piece1, piece2);

					if (collision && ! isColliding[i][j]) {
						startCollision(piece1, piece2);
						if (verbosity >= 1)
							eventBuffer.append(" START_COLLISION { " + piece1.getId() + " " + piece2.getId() + " }");
					}

					else if (! collision && isColliding[i][j]) {
						endCollision(piece1, piece2);
						if (verbosity >= 1)
							eventBuffer.append(" END_COLLISION { " + piece1.getId() + " " + piece2.getId() + " }");
					}
					isColliding[i][j] = collision;
				}
			}
		}

		buffer.setLength(0);
		buffer.append((time / 10));
		int bufferLength0 = buffer.length();
		buffer.append(eventBuffer);
		eventBuffer.setLength(0);
		update();
		reportChangesToTrackedObjects();
		if (buffer.length() > bufferLength0)
			outputToLog(buffer + "\n");

		g.setColor(bgColor);
		g.fillRect(0, 0, w, h);

		for (int i = 0 ; i < pieces.size() ; i++)
			piece(i).update(g);

		overlay(g);

		if (isMouseOverStatus) {
			g.setColor(Color.black);
			Font saveFont = g.getFont();
			g.setFont(messageFont);
			g.drawString("verbose = " + verbosity, 5, h - 5);
			g.setFont(saveFont);
		}

		animating = true;
	}

	/**
      Allocate a new piece and add it to the scene.
	 */

	public Piece addPiece() {
		return addPiece(new Piece());
	}

	/**
      Allocate a new piece of the given object class, and add it to the scene.
	 */

	public Piece addPiece(String className) {
		Piece piece = null;
		try {
			piece = (Piece)Class.forName(className).newInstance();
		}
		catch (Throwable e) {
			System.err.println("nonexisting piece class name " + className);
		}
		addPiece(piece);
		return piece;
	}

	/**
      Add an already existing piece to the scene.
	 */


	public Piece addPiece(Piece piece) {
		//piece.setId(pieces.size());
		pieces.add(piece);
		piece.setGamePlatform(this);
		return piece;
	}

	/**
   		remove an already existing piece to the scene.
	 */


	public Piece removePiece(Piece piece) {
		//piece.setId(pieces.size());
		pieces.remove(piece);
		piece.setGamePlatform(this);
		return piece;
	}

	/**
      Play an audio clip, given the name of the file containing the audio clip.
	 */

	public Clip playClip(String fileName) {
		Clip clip = null;
		try {
			clip = AudioSystem.getClip();
			clip.open(fileName.substring(0, 4).equals("http") ? AudioSystem.getAudioInputStream(new URL(fileName))
					: AudioSystem.getAudioInputStream(new File(fileName)));
			clip.start();
		}
		catch (Exception e) { e.printStackTrace(); }
		return clip;
	}

	/**
      Return the width, in pixels, of a text string.
	 */

	public int stringWidth(Graphics g, String s) {
		return g.getFontMetrics(g.getFont()).stringWidth(s);
	}

	void addToSet(ArrayList set, Object object) {
		for (int i = 0 ; i < set.size() ; i++)
			if (object == set.get(i))
				return;
		set.add(object);  
	}

	boolean isColliding(Piece piece1, Piece piece2) {
		return CollisionDetector.isCollision(piece1.X, piece1.Y, piece1.n, piece2.X, piece2.Y, piece2.n);
	}

	void outputToLog(String s) {
		if (isLogging) {
			if (textIO == null) {
				textIO = new TextIO();
				textIO.openOutput(logFileName + ".txt");
			}
			textIO.writeOutput(s);
		}
		if (copyingLogToStdOut)
			System.out.print(s);
	}

	/***************************************************************************************************
   Strategy for tracking objects:
   -- instantiate a shadow fields: an array of field valuess corresponding to the ones in the original object.
   -- at each update:
      -- compare object's field values with corresponding shadow field values.
      -- if any field values have changed:
         -- write out name/value pairs of all changed fields of the object.
         -- for all changed fields: set shadow field values to the object's field values.
	 */

	ArrayList trackedName = new ArrayList();
	ArrayList trackedVars = new ArrayList();
	ArrayList tracked = new ArrayList();
	ArrayList shadows = new ArrayList();

	/**
      Track the values of an object, for logging purposes.
	 */

	public void track(Object object, String name) {
		track(object, name, "");
	}

	/**
      Track specific variables of an object, for logging purposes.
	 */

	public void track(Object object, String name, String varNames) {
		ArrayList vars = new ArrayList();
		for (StringTokenizer st = new StringTokenizer(varNames) ; st.hasMoreTokens() ; )
			vars.add(st.nextToken());

		trackedName.add(name);
		trackedVars.add(vars);
		tracked.add(object);
		shadows.add(newShadowObject(object, vars));
	}

	ArrayList newShadowObject(Object object, ArrayList vars) {
		ArrayList shadow = new ArrayList();
		try {
			Field field[] = object.getClass().getDeclaredFields();
			for (int i = 0 ; i < field.length ; i++) {
				field[i].setAccessible(true);
				shadow.add(field[i].get(object));
			}
			for (int i = 0 ; i < vars.size() ; i++)
				shadow.add(fieldValue(object, (String)vars.get(i)));
		}
		catch (Throwable e) { System.err.println(e); }
		return shadow;
	}

	Object fieldValue(Object object, String fieldName) {
		try {
			Class c = object.getClass();
			for ( ; c != null ; c = c.getSuperclass()) {
				Field field[] = c.getDeclaredFields();
				for (int i = 0 ; i < field.length ; i++) {
					field[i].setAccessible(true);
					if (field[i].getName().equals(fieldName))
						return field[i].get(object);
				}
			}
		}
		catch (Throwable e) { System.err.println(e); }
		return null;
	}

	void reportChangesToTrackedObjects() {
		for (int i = 0 ; i < tracked.size() ; i++) {

			Object    object = tracked.get(i);
			ArrayList shadow = (ArrayList)shadows.get(i);
			ArrayList vars   = (ArrayList)trackedVars.get(i);

			if (! hasEqualValues(shadow, object, vars)) {
				buffer.append(" " + trackedName.get(i));
				reportChanges(shadow, object, vars);
			}
		}
	}

	boolean hasEqualValues(ArrayList shadow, Object object, ArrayList vars) {
		try {
			Field field[] = object.getClass().getDeclaredFields();
			for (int i = 0 ; i < field.length ; i++) {
				field[i].setAccessible(true);
				if (! shadow.get(i).equals(field[i].get(object)) )
					return false;
			}
			for (int i = 0 ; i < vars.size() ; i++)
				if ( ! shadow.get(field.length + i).equals( fieldValue(object, (String)vars.get(i)) ) )
					return false;
		}
		catch (Throwable e) { System.err.println(e); }
		return true;
	}

	void reportChanges(ArrayList shadow, Object object, ArrayList vars) {
		try {
			buffer.append(" { ");

			Field field[] = object.getClass().getDeclaredFields();
			for (int i = 0 ; i < field.length ; i++) {
				field[i].setAccessible(true);
				if (! shadow.get(i).equals(field[i].get(object)) ) {
					buffer.append(field[i].getName() + " " + field[i].get(object) + " ");
					shadow.set(i, field[i].get(object) );
				}
			}
			for (int i = 0 ; i < vars.size() ; i++) {
				Object value = fieldValue(object, (String)vars.get(i));
				if (! shadow.get(field.length + i).equals(value)) {
					buffer.append(vars.get(i) + " " + value + " ");
					shadow.set(field.length + i, value);
				}
			}

			buffer.append("}");
		}
		catch (Throwable e) { System.err.println(e); }
	}

	Font messageFont = new Font("Helvetica", Font.PLAIN, 12);
}

