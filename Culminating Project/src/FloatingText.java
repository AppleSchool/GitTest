import java.awt.Graphics2D;

public class FloatingText {
	
	private String text;
	private int maxTicks;
	private int ticks;
	private int x;
	private int y;
	private int distance;
	
	FloatingText(String te, int ti, int eX, int eY, int d)
	{
		text = te;
		maxTicks = ti;
		ticks = ti;
		x = eX;
		y = eY;
		if (y < 50)
			y = 50;
		distance = d;
	}
	
	public void drawText(Graphics2D g)
	{
		g.drawString(text, x - (int)(g.getFontMetrics().stringWidth("text") / 2.0), y - (int)(distance * Math.cos(Math.toRadians(90.0 * ((double)ticks/(double)maxTicks)))));
	}

	public void drawTextDefault(Graphics2D g)
	{
		g.drawString(text, x, y - (int)(distance * Math.cos(Math.toRadians(90.0 * ((double)ticks/(double)maxTicks)))));
	}
	
	public boolean isActive()
	{
		if (ticks <= 0)
			return false;
		return true;
	}
	
	public void incrementString()
	{
		ticks = ticks -1;
	}

}
