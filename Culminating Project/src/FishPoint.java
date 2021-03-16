import java.awt.*;
import java.awt.geom.Ellipse2D;

public class FishPoint {

	private int x = 0;
	private int y = 0;
	private int hypLength = 0;
	private int angle = 0;
	
	private int sideOne = 0;
	private int sideTwo = 0;
	
	private int sideOneType = 0;
	private int sideTwoType = 0;
	
	private Point initial = null;
	private Point thisPoint = null;
	
	private boolean active = false;
	
	public FishPoint(Point initialPoint)
	{

		initial = initialPoint;
		
		createConditions();
	}
	
	public int getX()
	{
		return x;
	}
	
	public int getY()
	{
		return y;
	}
	
	public int getAngle()
	{
		return angle;
	}
	
	public int getHyp()
	{
		return hypLength;
	}
	
	public Point getPoint()
	{
		return thisPoint;
	}
	
	public int getSideOne()
	{
		return sideOne;
	}
	
	public int getSideTwo()
	{
		return sideTwo;
	}
	
	public int getSideOneType()
	{
		return sideOneType;
	}
	
	public int getSideTwoType()
	{
		return sideTwoType;
	}
	
	public void drawPoint(Graphics2D g)
	{
		g.fill(new Ellipse2D.Double(x, y, 20, 20));
	}
	
	public boolean isActive()
	{
		return active;
	}
	
	public void setActive()
	{
		active = true;
	}
	
	public void setDeactive()
	{
		active = false;
	}
	
	public void debug()
	{
		System.out.println("Hyp length: " + hypLength + " Side One Type: " + sideOneType + " Side One Length: " + sideOne + " Side Two Type: " + sideTwoType + " Side Two Length: " + sideTwo + "Angle = " + angle);
	}
	
	private void createConditions()
	{
		sideOneType = (int)(Math.random() * 3);
		sideTwoType = (int)(Math.random() * 3);
		
		while (sideOneType == sideTwoType)
		{
			sideTwoType = (int)(Math.random() * 3);
		}
		
		hypLength = (int)(100 + (200 * Math.random() + 1));
		angle = (int)(90 * Math.random());
		
		double dX = ( hypLength * ( Math.cos(Math.toRadians(angle)) )/350);
		double dY = ( hypLength * ( Math.sin(Math.toRadians(angle)) )/350);
		
		if (dX > 1)
			dX = 1;
		if (dY > 1)
			dY = 1;
		
		int minX = (int)(initial.getX() + 200);
		int maxX = (int)(initial.getX() + 550);
		int minY = (int)(initial.getY() - 200);
		int maxY = (int)(initial.getY() - 550);
		
		double disX = (int)Math.sqrt(Math.pow((maxX - minX), 2));
		double disY = (int)Math.sqrt(Math.pow((minY - maxY), 2));
		
		x = (int)(initial.getX() + (dX * disX)) - 10;
		y = (int)(initial.getY() - (dY * disY)) - 10;
		
		thisPoint = new Point(x,y);
		
		if (sideOneType == 0)
		{
			sideOne = hypLength;
		}
		if (sideOneType == 1)
		{
			sideOne = (int) (hypLength *Math.sin(Math.toRadians(angle)));
		}
		if (sideOneType == 2)
		{
			sideOne = (int) (hypLength *Math.cos(Math.toRadians(angle)));
		}
		
		if (sideTwoType == 0)
		{
			sideTwo = hypLength;
		}
		if (sideTwoType == 1)
		{
			sideTwo = (int) (hypLength *Math.sin(Math.toRadians(angle)));
		}
		if (sideTwoType == 2)
		{
			sideTwo = (int) (hypLength *Math.cos(Math.toRadians(angle)));
		}
		
	}
	
}
