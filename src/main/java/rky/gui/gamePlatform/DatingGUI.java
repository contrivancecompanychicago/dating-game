package rky.gui.gamePlatform;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import rky.gui.gamePlatform.Line.Point;
import rky.gui.gamePlatform.Line;


@SuppressWarnings("serial")
public class DatingGUI extends GamePlatform {

	Font bigFont = new Font("Helvetica", Font.ITALIC, 23);
	Font smallFont = new Font("Helvetica", Font.BOLD, 18);

	Color color[] = {Color.red,new Color(255,128,0),Color.yellow,Color.green,
			Color.cyan,new Color(140,140,255),Color.magenta,Color.red};

	String team1 = "????";
	String team2 = "????";


	List<RectPiece>  bars = new ArrayList<RectPiece>();
	Set<RectPiece> setOfBars = new TreeSet<RectPiece>();
	Line upper,lower;

	public void setup(){
		addNewBar(new Line(50,250,700,250),0.4);
		addNewBar(new Line(50,250,700,250),1);
		addNewBar(new Line(50,250,700,250),-.6);

	}

	public void update() {

		for(RectPiece p :bars){
			if(!setOfBars.contains(p)){
				addPiece(p);
				setOfBars.add(p);
			}
		}
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

		if(round == 1){
			upper = new Line(50,250+fromOffset,700,250+fromOffset);

		}else{
			lower = new Line(50,250+fromOffset,700,250+fromOffset);
		}

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

	private void addNewBar(Line l,double score){

		Point start = l.getStart();

		int barX = 0;
		int barY = l.start.y - 60;

		score += 1; //convert score from 0-2

		double place = score *l.getLength()/2;

		barX =(int)( place - 5) + 50;

		System.out.println("x="+barX+"y="+barY);
		RectPiece bar = new RectPiece();
		bar.setBounds(barX, barY, 10, 60);
		bar.setColor(Color.blue);
		addPiece(bar);

		bars.add(bar);
	}

}
