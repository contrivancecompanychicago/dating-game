package rky.gui.gamePlatform;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;


@SuppressWarnings("serial")
public class DatingGUI extends GamePlatform {

	Font bigFont = new Font("Helvetica", Font.ITALIC, 23);
	Font smallFont = new Font("Helvetica", Font.BOLD, 18);

	Color color[] = {Color.red,new Color(255,128,0),Color.yellow,Color.green,
			Color.cyan,new Color(140,140,255),Color.magenta,Color.red};

	String team1 = "????";
	String team2 = "????";


	Piece  chaser;

	public void setup(){

		int w = getWidth(), h = getHeight();


		chaser = new RectPiece();
		chaser.setBounds(150, 200, 60, 100);
		chaser.setColor(Color.blue);
		addPiece(chaser);

	}

	public void update() {

	}

	public void overlay(Graphics g) {

		Graphics2D g2 = (Graphics2D) g;
		createGraph(1,g2, 0);
		createGraph(2,g2, 300);


	}

	private void createGraph(int round,Graphics2D g2, int fromOffset){

		g2.setFont(bigFont);
		g2.setColor(Color.RED);

		// Render a string using the derived font
		g2.drawString("Bad Match", 20, 100+fromOffset);
		g2.setColor(Color.BLACK);
		g2.drawString("-1",20, 260+fromOffset);

		g2.setColor(Color.BLUE);
		g2.drawString("Ideal Match", 600, 100+fromOffset);
		g2.setColor(Color.BLACK);
		g2.drawString("+1",720, 260+fromOffset);

		g2.drawLine(400,240+fromOffset,400,260+fromOffset);
		g2.drawString("0",410, 275+fromOffset);

		g2.setStroke(new BasicStroke(3));
		g2.drawLine(50,250+fromOffset,700,250+fromOffset);

		g2.setFont(smallFont);
		g2.drawString("MatchMaker :",30,300+fromOffset);
		if(round == 1){
			g2.drawString(team1,140,300+fromOffset);
		}else{
			g2.drawString(team2,140,300+fromOffset);
		}

		g2.drawString("Person :",600,300+fromOffset);
		if(round == 1){
			g2.drawString(team2,680,300+fromOffset);
		}else{
			g2.drawString(team1,680,300+fromOffset);
		}

	}

}
