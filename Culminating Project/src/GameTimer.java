
import java.awt.*;

public class GameTimer {

	private int gameTicks = 0;
	private int maxTicks = 0;
	private boolean timerActive = false;
	
	
	public GameTimer()
	{
		
	}
	
	public void drawTimer(int x, int y, int size, Graphics2D g)
	{
		if (!timerActive)
		{
			g.setColor(Color.RED);
			g.drawLine(x, y, x + size, y);
		}
		
		if(timerActive)
		{
			g.setColor(Color.BLACK);
			g.drawLine(x, y, x + size, y);
			
			if (maxTicks > 0 && gameTicks > 0)
			{
				double tlSize = ( ((double)gameTicks/(double)maxTicks) * size );
				g.setColor(Color.GREEN);
				g.drawLine((int)(x + ((size - tlSize)/2.0)),y, (int)((x + (size/2.0)) + (tlSize / 2.0)) ,y);
			}
		}
	}
	
	public void incrementTime()
	{
		gameTicks = gameTicks - 1;
		if (gameTicks < 0)
		{
			gameTicks = 0;
			timerActive = false;
		}
	}
	
	public void startTimer(int ticks)
	{
		maxTicks = ticks;
		gameTicks = ticks;
		timerActive = true;
	}
	
	public void stopTimer()
	{
		maxTicks = 0;
		gameTicks = 0;
		timerActive = false;
	}
	
	
	public boolean active()
	{
		return timerActive;
	}
	
}
