package rky.gui.gamePlatform;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeSet;
import rky.gui.gamePlatform.Line;


@SuppressWarnings("serial")
public class DatingGUI extends GamePlatform {

	private Font bigFont = new Font("Helvetica", Font.ITALIC, 23);
	private Font smallFont = new Font("Helvetica", Font.BOLD, 18);

	private String team1 = "????";
	private String team2 = "????";

	private double bestScoreTeam1 = -1;

	private int no_of_guess_Team1 = 0;


	private List<RectPiece>  bars = new ArrayList<RectPiece>();
	private Set<RectPiece> setOfBars = new TreeSet<RectPiece>();
	private Line topScale,bottomScale;


	public static final int IDEAL_WIDTH = 800;
	public static final int IDEAL_HEIGHT = 400;


	public void setup(){
		//setSize(IDEAL_WIDTH,IDEAL_HEIGHT);
		setBackground(Color.CYAN);
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

		g.setColor(Color.BLACK);
		Graphics2D g2 = (Graphics2D) g;
		createGraph(1,g2, 0);
		//createGraph(2,g2, 300);

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
			topScale = new Line(50,250+fromOffset,700,250+fromOffset);

		}else{
			bottomScale = new Line(50,250+fromOffset,700,250+fromOffset);
		}


		g2.drawLine(50,250+fromOffset,700,250+fromOffset);

		g2.setFont(smallFont);
		g2.drawString("MatchMaker :",30,300+fromOffset);
		if(round == 1){
			g2.drawString(team1,140,300+fromOffset);
		}else{
			g2.drawString(team2,140,300+fromOffset);
		}

		g2.drawString("Score : ",30,330 + fromOffset);
		g2.drawString(""+bestScoreTeam1,90,330 + fromOffset);
		g2.drawString(""+no_of_guess_Team1,130,330 + fromOffset);




		g2.drawString("Person :",600,300+fromOffset);
		if(round == 1){
			g2.drawString(team2,680,300+fromOffset);
		}else{
			g2.drawString(team1,680,300+fromOffset);
		}

	}

	private void addNewBar(Line l,double score){

		int barX = 0;
		int barY = l.start.y - 60;

		score += 1; //convert score from 0-2

		double place = score *l.getLength()/2;

		barX =(int)( place - 5) + 50;

		//System.out.println("x="+barX+"y="+barY);
		RectPiece bar = new RectPiece();
		bar.setBounds(barX, barY, 10, 60);
		bar.setColor(Color.blue);
		addPiece(bar);

		bars.add(bar);
	}

	public void startGame(String team1,String team2){

		this.team1 = team1;
		this.team2 = team2;


	}

	public void updateScore(double score){
		
		double value = score;
		score = Math.round( value * 100.0 ) / 100.0;

		if(topScale != null || bottomScale != null){



			if(bestScoreTeam1 < score){
				bestScoreTeam1 = score;
			}

			addNewBar(topScale, score);
			no_of_guess_Team1++;
		}
	}


	/**
	 * Test applet
	 * @param args
	 */
	private final static Timer timer = new Timer();

	public static void main(String args[]){

		final DatingGUI applet = new DatingGUI();
		applet.init();
		applet.start();

		Frame frame = new Frame("RKY-Dating-Game Applet");
		frame.addWindowListener(new WindowAdapter()
		{
			public void windowClosing(WindowEvent e)
			{
				System.exit(0);
			}
		});

		frame.add(applet);
		frame.setSize(IDEAL_WIDTH,IDEAL_HEIGHT);
		frame.setVisible(true);

		timer.schedule(new TimerTask() {
			public void run() {
				playSound();
				timer.cancel();
			}
			private void playSound() {
				// Start a new thread to play a sound...
				applet.startGame("rky", "arx");
				for(int i = 0 ; i < 15; i++){
					
					double value = Math.pow(-1,i)*Math.random();
					double finalValue = Math.round( value * 100.0 ) / 100.0;

					applet.updateScore(finalValue);

					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}, (long) (2000));

	}

}
