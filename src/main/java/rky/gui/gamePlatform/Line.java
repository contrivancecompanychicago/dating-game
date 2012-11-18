package rky.gui.gamePlatform;

public class Line {
	
	Point start;
	Point end;
	
	int length;
	
	
	public Line(int x1,int y1,int x2, int y2){
		start = new Point(x1,y1);
		end = new Point(x2,y2);
		
		if(y1 == y2){
			this.length = Math.abs(x2 -x1);
		}else {
			throw new IllegalArgumentException("Line must be Horizontal");
		}
	}
	
	public Point getStart() {
		return start;
	}	
	public Point getEnd() {
		return end;
	}

	public int getLength() {
		return length;
	}

	public static class Point{
		public int x;
		public int y;
		
		public Point(int x , int y){
			this.x = x;
			this.y = y;
		}
	}

}
