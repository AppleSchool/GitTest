import java.awt.*;
import javax.swing.*;

public class MainGame extends JFrame {
	
	public MainGame()
	{
		initUI();
	}
	
	private void initUI()
	{
		add(new Board());
		
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		int side = (int) (screenSize.getWidth() / 2.25);
		setSize(side, side);
		setTitle("Work in Progress");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLocationRelativeTo(null);
		
	}
	
	public static void main(String[] args) 
	{
		EventQueue.invokeLater(() ->
		{
			MainGame ex = new MainGame();
			ex.setVisible(true);
		});
	}

}
