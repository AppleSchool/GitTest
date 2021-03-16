import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.awt.geom.Ellipse2D.Double;

import javax.swing.*;
import java.util.*;

public class Board extends JPanel implements Runnable
{
	
	private final int DELAY = 25;
	private Thread animator;
	
	private boolean menu = true;
	private boolean mouseHeld;
	
	private int currency = 0;
	
	private ArrayList<FloatingText> floatingText = new ArrayList<FloatingText>();
	
	private int game = 0;
	private int[] gamePanels = {0, 0, 0, 0};
	
	// Position Variables
	private int x = 0, y = 0; // The x and y positions of the mouse
	private double sw = 0, sh = 0; // The width and height of the screen
	private double sx = 0, sy= 0; // The x and y positions of the screen
	private double cX = 0, cY = 0; // The coordinates for the hyp endpoint
	// End of position variables
	
	// Start of calculated values
	double opposite = 0;
	double adjacent = 0;
	double angle = 0;
	// End of calculated values
	
	// JButtons
	JButton back = new JButton("Back");
	JButton next = new JButton("Next");
	
	JButton store = new JButton("Store");
	JButton gameOne = new JButton("Sine, Cosine, and the Circle");
	JButton gameTwo = new JButton("Inverse Trig and Angles");
	JButton gameThree = new JButton("Special Right Triangles");
	
	JButton answerButton = new JButton("Enter");
	
	JButton start = new JButton("Start!");
	
	// End of JButtons
	
	// JTextFields
	JTextField op = new JTextField(6);
	JTextField adj = new JTextField(6);
	JTextField ang = new JTextField(6);
	
	JTextField uCircleFields[] = {op, adj, ang};
	
	JTextField answerField = new JTextField(3);
	// End of JTextFields
	
	// Start of visualization ellipses
	
	ArrayList<Ellipse2D> sineVis = new ArrayList<Ellipse2D>();
	ArrayList<Ellipse2D> cosVis = new ArrayList<Ellipse2D>();
	ArrayList<Ellipse2D> tanVis = new ArrayList<Ellipse2D>();
	
	int visDegrees = 0;
	int visSpeed = 5;
	
	
	int displayAngle = 30;
	// End of visualization ellipses
	
	// Start of first game values
	
	int gameOneAngle = 0;
	int gameOneAnswer = 0;
	int gameOneGiven = 0;
	int gameOneGivenState = 0;
	int gameOneAnswerState = 0;
	
	int questionType = 0;
	int gameOneHyp = 0;
	int gameOnePlayerAnswer = 0;
	
	int pointMovementTimer = 2250;
	
	String gameOneQuestion = "";
	String gameOneQuestion2 = "";
	String gameOneQuestion3 = "";
	
	boolean gameOneActive = false;
	
	boolean questionAnswered = false;
	boolean pointAnimated = false;
	boolean correctAnswer = false;
	
	String[] givenStatementType =
		{
			"You see a drone overhead at an angle of ", "The angle between you and the top of a pole is "
		};

	String[] givenDrone =
		{
			" at a distance of approximately ", " at an altitude of ", " The horizontal distance between you and the drone is "
		};

	String[] givenWall =
		{
			" The distance between you and top of the pole is ", " The height of the pole is approximately ", " The distance between you and the base of the pole is "
		};

	
	String[] answerDrone =
		{
			" What is the minimum amount of rope needed to lasso the drone?", " How high would you have to jump to catch it?", " How far would you have to run to stand under the drone?"
		};
	
	String[] answerWall =
		{
			" What is the minimum length ladder needed to climb the pole?", " How tall is the pole?", " How far apart are you from the pole?"
		};
	
	Point personStart = new Point();
	Point anglePoint = new Point();
	
	Point personCurrent = new Point();
	
	GameTimer gameTimer = new GameTimer();
	boolean gameOver = false;
	int gameTime = 2400;
	
	int gameOneStreak = 0;
	int gameOneBest = 0;
	int previousAnswer = 0;
	
	// End of first game values
	
	// Start of second game values
	
	Point gameTwoSP = new Point();
	Point targetPoint = new Point();
	
	ArrayList<FishPoint> fishArray = new ArrayList<FishPoint>();
	
	boolean gameTwoActive = false;
	boolean gameTwoOver = false;
	
	int gameTwoTime = 4800;
	int selectedPoint = -1;
	int targetAngle = 45;
	
	String[] sideOneSection = {"The length from your line to the fish is ", "The northward distance between the fish and your boat is ", "The distance eastward between the fish and your boat is "};
	
	
	String[] sideTwoSection = {"You plan on casting a line at a length of approximately ", "Towards the north, the distance the fish is from your boat is ", "Eastward, your boat's distance to the fish is "};
	
	InputMap im = getInputMap(WHEN_IN_FOCUSED_WINDOW);
	ActionMap am = getActionMap();
	
	// End of second game values
	
	// Start of last game values
	
	String[] gameThreeChoices = {"30 or 45 degrees", "60 degrees", "30/60 hypotenuse", "45/45 hypotenuse", "x", "2x", "3x^.5", "2x^.5"};
	
	String[] gameThreeAnswers = {"x", "3x^.5", "2x", "2x^.5", "30 or 45 degrees", "30/60 hypotenuse", "60 degrees", "45/45 hypotenuse"};
	
	int [] randomPrompts = {0, 0, 0, 0};
	
	int gameThreeButton;
	int gameThreeCorrectButton;
	int gameThreePrompt;
	int waitTicks = 50;
	
	boolean gameThreeCorrect;
	boolean gameThreeAnswered = false;
	boolean gameThreeActive = false;
	
	// End of last game values
	
	public Board() 
	{
		initBoard();
	}
	
	private void initBoard()
	{
		setBackground(new Color(60, 60, 170));
		
		// Adds the buttons into the panel
		
		add(store);
		add(gameOne);
		add(gameTwo);
		add(gameThree);
		
		add(back);
		add(next);
		
		add(answerButton);
		add(start);
		
		// End of adding buttons
		
		// Start of adding text fields
		

		add(op);
		add(adj);
		add(ang);
		add(answerField);
		
		// End of adding text fields
		
		// Start of some misc. listeners
		
		addMouseListener(new TrigAdapter()); // Detects clicking on the screen
		
		addComponentListener(new ComponentAdapter()
				{
					public void componentResized(ComponentEvent componentEvent)
					{
						cX = 0;
						cY = 0;
						sineVis.clear();
						cosVis.clear();
						tanVis.clear();
					}
				});
		
		// End of the misc. listeners
		
		// Allows the buttons to have an action when pressed
		
		store.addActionListener(new ActionListener()
				{
					@Override
					public void actionPerformed(ActionEvent e)
					{
						menu = !menu;
					}
				});
		
		back.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				gameOneActive = false;
				gameTwoActive = false;
				gameThreeActive = false;
				gameTimer.stopTimer();
				fishArray.clear();
				
				if (gamePanels[game] > 0)
				{
					gamePanels[game] = gamePanels[game] - 1;
					System.out.println("Game = " + game);
					System.out.println("Game Panel = " + gamePanels[game]);
					if (gameOneStreak > 0)
					{
						int jackpotNumber = (int)(25 * Math.random());
						boolean jackpot = false;
						if (jackpotNumber == 1)
							jackpot = true;
						
						if (jackpot)
							rewardCoins((int)(gameOneStreak * (1000 * Math.random() + 1) + 1000), (int)(sw/2), (int)(sh/2), 120 , 100, true);
						else
							rewardCoins((int)(gameOneStreak * (500 * Math.random() + 1) + 500), (int)(sw/2), (int)(sh/2), 120 , 100, false);
						
						gameOneStreak = 0;
						
					}
				}
				else
				{
					menu = !menu;
					game = 0;
				}
				System.out.println("Game: " + game);
			}
		});
		
		gameOne.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				menu = !menu;
				game = 1;
			}
		});
		
		gameTwo.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				menu = !menu;
				game = 2;
			}
		});
		
		gameThree.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				menu = !menu;
				game = 3;
			}
		});
		
		next.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				gamePanels[game] = gamePanels[game] + 1;
				System.out.println("Game = " + game);
				System.out.println("Panel = " + gamePanels[game]);
			}
		});
		
		answerButton.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				if (!pointAnimated)
				{
					if (isNumeric(answerField.getText()) && !answerField.getText().isEmpty())
					{
						if (!questionAnswered)
						{
							gameOnePlayerAnswer = Integer.parseInt(answerField.getText());
						}
						questionAnswered = true;
						pointAnimated = true;
					
						if (Integer.parseInt(answerField.getText()) >= gameOneAnswer - 1 && Integer.parseInt(answerField.getText()) <= gameOneAnswer + 1 && gameOneActive)
						{
							System.out.println("Correct. Number = " + Integer.parseInt(answerField.getText()));
							correctAnswer = true;
						}
						else
						{
							System.out.println("Incorrect. Number = " + Integer.parseInt(answerField.getText()));
						}

					}
				}
			}
		});
		
		start.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				if (game == 1 && gamePanels[game] == 3)
				{
					newGameOne();
					gameOneActive = true;
					gameTime = 2400;
					gameTimer.startTimer(gameTime);
					if (gameOneStreak > 0)
					{
						int jackpotNumber = (int)(25 * Math.random());
						boolean jackpot = false;
						if (jackpotNumber == 1)
							jackpot = true;
						
						if (jackpot)
							rewardCoins((int)(gameOneStreak * (1000 * Math.random() + 1) + 1000), (int)(sw/2), (int)(sh/2), 120 , 100, true);
						else
							rewardCoins((int)(gameOneStreak * (500 * Math.random() + 1) + 500), (int)(sw/2), (int)(sh/2), 120 , 100, false);
						
					}
					gameOneStreak = 0;
				}
				if (game == 2 && gamePanels[game] == 2)
				{
					newGameTwo();
					selectedPoint = -1;
					gameTwoActive = true;
					gameTimer.startTimer(gameTwoTime);
				}
				if (game == 3 && gamePanels[game] == 1)
				{
					newGameThree();
					gameThreeActive = true;
					gameThreeAnswered = false;
				}
			}
		});

		im.put(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0), "left");
		im.put(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0), "right");
		im.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "enter");
		
		am.put("left", new aimAction(0));
		am.put("right", new aimAction(1));
		am.put("enter", new aimAction(2));
		
		// End of button actions
	}
	
	
	public void paintComponent(Graphics g)
	{
		super.paintComponent(g);
	
		testMethod(g);
		
	}
	
	private void testMethod(Graphics g)
	{
		Graphics2D g2d = (Graphics2D) g;
		
		RenderingHints rh = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		rh.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
		g2d.setRenderingHints(rh);
		
		Dimension size = getSize();
		sw = size.getWidth();
		sh = size.getHeight();
		
		int buttonX = (int)(sw*.25);
		int buttonY =(int)(sh*.1);
		
		personStart = new Point((int)(sw/2),(int)(sh/2));
		
		// Start of buttons and their visualization
		
		store.setBounds((int)(sw*.05), (int)((sh*.95) - (sh*.1)), buttonX, buttonY);
		store.setFocusPainted(false);
		store.setVisible(menu);
		
		gameOne.setVisible(menu);
		gameOne.setFocusPainted(false);
		gameOne.setBounds((int)((sw/2) - (buttonX/2)), (int)(sh*.25), buttonX, buttonY);
		//gameOne.setBounds((int)(sw*.05), (int)(sh*.25), buttonX, buttonY);
		
		gameTwo.setVisible(menu);
		gameTwo.setFocusPainted(false);
		gameTwo.setBounds((int)((sw/2) - (buttonX/2)), (int)(sh*.45), buttonX, buttonY);
		
		gameThree.setVisible(menu);
		gameThree.setFocusPainted(false);
		gameThree.setBounds((int)((sw/2) - (buttonX/2)), (int)(sh*.65), buttonX, buttonY);
		
		back.setVisible(!menu);
		back.setFocusPainted(false);
		back.setBounds((int)(sw*.05), (int)((sh*.95) - (sh*.1)), buttonX, buttonY);
		
		if (game > 0)
		{
			if ((game == 1 && gamePanels[game] == 3) || (game == 2 && gamePanels[game] == 2) || (game == 3 && gamePanels[game] == 1))
				next.setVisible(false);
			else
				next.setVisible(true);
		}
		else
			next.setVisible(false);
		next.setFocusPainted(false);
		next.setBounds((int)(sw*.70), (int)((sh*.95) - (sh*.1)), buttonX, buttonY);
		
		// End of button section
		
		if (game == 1 && gamePanels[game] == 3)
		{
			answerButton.setVisible(true);
			answerField.setVisible(true);
		}
		else
		{
			answerButton.setVisible(false);
			answerField.setVisible(false);			
		}
		answerButton.setFocusPainted(false);
		answerButton.setBounds((int)(sw/2), (int)(sh * .85), buttonX/2, buttonY);
		
		if ((game == 1 && gamePanels[game] == 3 && !gameOneActive) || (game == 2 && gamePanels[game] == 2) || (game == 3 && gamePanels[game] == 1))
			start.setVisible(true);
		else if (gameOver)
		{
			start.setVisible(true);
		}
		else
			start.setVisible(false);
		start.setFocusPainted(false);
		start.setBounds((int)(sw*.70), (int)((sh*.95) - (sh*.1)), buttonX, buttonY);
		
		// Start of TextField section
		
		Font bigFont = op.getFont().deriveFont(Font.PLAIN, 30);
		g2d.setFont(bigFont);
		if (game == 1 && gamePanels[game] == 0)
		{
			for(int i = 0; i < uCircleFields.length; i++)
			{
				uCircleFields[i].setVisible(true);
			}
		}
		else
		{
			for(int i = 0; i < uCircleFields.length; i++)
			{
				uCircleFields[i].setVisible(false);
			}
		}
		for(int i = 0; i < uCircleFields.length; i++)
		{
			uCircleFields[i].setFont(bigFont);
		}
		
		answerField.setBounds((int)((sw/2) - (buttonX/2)), (int)(sh * .85), buttonX/2, buttonY);
		answerField.setFont(bigFont);
		
		op.setLocation(g2d.getFontMetrics().stringWidth("Adjacent = "), (int)((sh*.8) - (g2d.getFontMetrics().getHeight() * 3)) - 10);
		adj.setLocation(g2d.getFontMetrics().stringWidth("Adjacent = "), (int)((sh*.8) - (g2d.getFontMetrics().getHeight() * 2)) - 10);
		ang.setLocation(g2d.getFontMetrics().stringWidth("Adjacent = "), (int)((sh*.8) - (g2d.getFontMetrics().getHeight())) - 10);
		
		// End of TextField section
		
		// Start of Main Menu
		
		g2d.setStroke(new BasicStroke(3));
		
		if (menu || game == 0)
		{
			
			g2d.setColor(new Color(240, 120, 0));
			g2d.fillRect(0,0, (int)(sw), (int)(sh*.2));
	
			g2d.setColor(new Color(150, 30, 0));
			g2d.drawLine(0, (int)(sh * .2), (int)sw, (int)(sh*.2));
			
			g2d.setColor(Color.BLACK);
			g2d.setFont(new Font("TimesRoman", Font.PLAIN, 25));
			
			if (menu)
			{
				drawCString(g2d, "Click on the buttons below to start a new game!", (int)(sh*.05));
				drawCString(g2d, "It's reccomended to keep the screen the default size!", (int)(sh*.1));
			}
		}
		else
		{
			
		}
		
		g2d.setColor(new Color(240, 120, 0));
		g2d.fillRect(0,(int)(sh*.8), (int)(sw), (int)(sh*.2));
		
		g2d.setColor(new Color(150, 30, 0));
		g2d.drawLine(0, (int)(sh * .8), (int)sw, (int)(sh*.8));
		
		if (game == 0)
		{
			g2d.setFont(new Font("TimesRoman", Font.PLAIN, 30));
			String currencyString = "" + currency;
			g2d.setColor(Color.BLACK);
			g2d.drawString(currencyString, 60 , 10 + g2d.getFontMetrics().getHeight());
			
			g2d.setColor(Color.YELLOW);
			g2d.fill(new Ellipse2D.Double(10,10,40,60));
			
			g2d.setColor(new Color(70,80,0));
			g2d.draw(new Ellipse2D.Double(10,10,40,60));
		}
		
		
		// End of Main Menu
		
		// Start of the first game's graphics
		
		if (game == 1)
		{
			if(gamePanels[game] == 0)
			{
				g2d.setColor(Color.WHITE);
				g2d.setStroke(new BasicStroke(5));
				g2d.draw(new Ellipse2D.Double((sw/2) - (sh/6), (sh/3) - (sh/6), sh/3, sh/3));
				g2d.fill((new Ellipse2D.Double((sw/2) - 3.5, (sh/3) - 3.5, 7, 7)));
			
			
				if(cX > 0 && cY > 0)
				{
					g2d.setColor(Color.RED);
					g2d.drawLine((int) ((sw/2)), (int)((sh/3)), (int)cX, (int)cY);
				
					g2d.setColor(Color.GREEN);
					g2d.drawLine((int)(sw/2), (int)(sh/3), (int)cX, (int)(sh/3));
				
					g2d.setColor(Color.CYAN);
					g2d.drawLine((int)cX, (int)(sh/3), (int)cX, (int)cY);
				}
				
				g2d.setFont(new Font("TimesRoman", Font.PLAIN, 25));
				g2d.setColor(Color.WHITE);
				drawCString(g2d, "Imagine that this is a unit circle, or a circle with a radius of 1.\nClick on edge of the circle to create the hypotenuse of a right triangle!", 0);
				
				g2d.setFont(new Font("TimesRoman", Font.PLAIN, 30));
				
				g2d.setColor(Color.CYAN);
				g2d.drawString("Opposite = ", 0, (int)((sh*.8) - (g2d.getFontMetrics().getHeight() * 2)) - 10);
				
				g2d.setColor(Color.GREEN);
				g2d.drawString("Adjacent = ", 0, (int)((sh*.8) - (g2d.getFontMetrics().getHeight())) - 10);

				g2d.setColor(Color.RED);
				g2d.drawString("Angle = ", 0, (int)((sh*.8) - 10 ));

				
			}
			if (gamePanels[game] == 1)
			{
				g2d.setColor(Color.WHITE);
				g2d.setFont(new Font("TimesRoman", Font.PLAIN, 23));
				drawCString(g2d, "Sin, Cosine, and Tangent are the ratios between the length of each of the triangle's lines.\nBelow are some visualizations of the graphs they create!\n The black line represents 0, the top is 1, and the bottom is -1.", 0);
				
				g2d.fillRect((int)(sw/2), (int)(sh/6), (int)(sw/2.5), (int)(sh/5.5));
				g2d.fillRect((int)(sw/2), (int)(sh/2.75), (int)(sw/2.5), (int)(sh/5.5));
				g2d.fillRect((int)(sw/2), (int)(sh/1.785), (int)(sw/2.5), (int)(sh/5.5));
				
				g2d.setColor(Color.BLACK);
				g2d.drawRect((int)(sw/2), (int)(sh/6), (int)(sw/2.5), (int)(sh/5.5));
				g2d.drawRect((int)(sw/2), (int)(sh/2.75), (int)(sw/2.5), (int)(sh/5.5));
				g2d.drawRect((int)(sw/2), (int)(sh/1.785), (int)(sw/2.5), (int)(sh/5.5));
				
				g2d.drawLine((int)(sw/2), (int)((sh/6) + (sh/11)), (int)((sw/2) + (sw/2.5)), (int)((sh/6) + (sh/11)));
				g2d.drawLine((int)(sw/2), (int)((sh/2.75) + (sh/11)), (int)((sw/2) + (sw/2.5)), (int)((sh/2.75) + (sh/11)));
				g2d.drawLine((int)(sw/2), (int)((sh/1.785) + (sh/11)), (int)((sw/2) + (sw/2.5)), (int)((sh/1.785) + (sh/11)));
				
				drawCString(g2d, "Angle:" + visDegrees, (int)(sh*.825));
				
				g2d.setFont(new Font("TimesRoman", Font.PLAIN, 30));
				g2d.setColor(Color.CYAN);
				g2d.drawString("Sine (Opposite / Hypotenuse): ", (int)(sh/2) - g2d.getFontMetrics().stringWidth("Sine (Opposite / Hypotenuse): "), (int)((sh/6) + (sh/11)));
				
				g2d.setColor(Color.GREEN);
				g2d.drawString("Cosine (Adjacent / Hypotenuse): ", (int)(sh/2) - g2d.getFontMetrics().stringWidth("Cosine (Adjacent / Hypotenuse): "), (int)((sh/2.75) + (sh/11)));
				
				g2d.setColor(Color.RED);
				g2d.drawString("Tangent (Opposite / Adjacent): ", (int)(sh/2) - g2d.getFontMetrics().stringWidth("Tangent (Opposite / Adjacent): "), (int)((sh/1.785) + (sh/11)));
				
				//g2d.fill(new Ellipse2D.Double(((sw/2) + (sw/2.5)) - 10, (sh/6), 10.0, 10.0));
				
				if(sineVis.size() > 0)
				{
					g2d.setColor(Color.CYAN);
					for (int i = 0; i < sineVis.size(); i++)
					{
						g2d.fill(sineVis.get(i));
					}
					
					g2d.setColor(Color.GREEN);
					for (int i = 0; i < cosVis.size(); i++)
					{
						g2d.fill(cosVis.get(i));
					}
					
					g2d.setColor(Color.RED);
					for (int i = 0; i < tanVis.size(); i++)
					{
						g2d.fill(tanVis.get(i));
					}
				}
				
			}
			if (gamePanels[game] == 2)
			{
				g2d.setColor(Color.WHITE);
				g2d.setFont(new Font("TimesRoman", Font.PLAIN, 25));
				drawCString(g2d, "Since these functions are the ratios of two sides from an angle,\nwe can use algebra to find a missing length!", 0);
				
				Font spacingFont = new Font("TimesRoman", Font.PLAIN, 25);
				
				g2d.setColor(Color.GRAY);
				g2d.setFont(new Font("TimesRoman", Font.PLAIN, 20));
				drawCString(g2d, "The symbol ^(1/3) represents 'To the power of 1/3', or the cube root of the number", g2d.getFontMetrics(spacingFont).getHeight() * 2);
				
				g2d.setColor(Color.BLACK);
				g2d.fillRect((int)((sw * 0.90) - (sh * .3) - (sh * .005)), (int)((sh * 0.20) - (sh * .005)), (int)(sh * .31), (int)(sh * .31));
				
				g2d.setColor(Color.WHITE);
				g2d.fillRect((int)((sw * 0.90) - (sh * .3)), (int)((sh * 0.20)), (int)(sh * .3), (int)(sh * .3));
				
				g2d.setStroke(new BasicStroke(5));				
				g2d.setColor(Color.RED);
				g2d.drawLine( (int) ((sw * .9) - (sh * .3) + 50), (int) ((sh * .2) + (sh * .3) - 50), (int) ( ((sw * .9) - (sh * .3) + 50) + ( Math.sin(Math.toRadians(30)) * (sh * .3) ) ) , (int) ( ((sh * .2) + (sh * .3) - 50) - ( Math.sin(Math.toRadians(30)) * (sh * .3) ) ) );
				
				g2d.setColor(Color.CYAN);
				g2d.drawLine( (int) ( ((sw * .9) - (sh * .3) + 50) + ( Math.sin(Math.toRadians(30)) * (sh * .3) ) ), (int) ((sh * .2) + (sh * .3) - 50), (int) ( ((sw * .9) - (sh * .3) + 50) + ( Math.sin(Math.toRadians(30)) * (sh * .3) ) ) , (int) ( ((sh * .2) + (sh * .3) - 50) - ( Math.sin(Math.toRadians(30)) * (sh * .3) ) ) );				
				
				g2d.setColor(Color.GREEN);
				g2d.drawLine( (int) ((sw * .9) - (sh * .3) + 50), (int) ((sh * .2) + (sh * .3) - 50), (int) ( ((sw * .9) - (sh * .3) + 50) + ( Math.sin(Math.toRadians(30)) * (sh * .3) ) ) , (int) ((sh * .2) + (sh * .3) - 50) );
				
				g2d.setColor(Color.BLUE);
				g2d.drawArc((int) ( ((sw * .9) - (sh * .3) + 50) - (sh * .05) ), (int) ( ((sh * .2) + (sh * .3) - 50) - (sh * .05)), (int)(sh * .1), (int)(sh * .1), 3, (int)36);
				
				g2d.setStroke(new BasicStroke(3));
				
				g2d.setFont(new Font("TimesRoman", Font.PLAIN, 25));
				g2d.setColor(Color.WHITE);
				g2d.fillRect(0, (int)(sh * .25) - 50, (int)(sw/3), (int)((sh * .2) + (g2d.getFontMetrics().getHeight() * 4)));
				
				g2d.setColor(Color.BLACK);
				g2d.drawRect(0, (int)(sh * .25) - 50, (int)(sw/3), (int)((sh * .2) + (g2d.getFontMetrics().getHeight() * 4)));
				
				g2d.setColor(Color.BLACK);
				g2d.drawString("Assuming that: ", 10, (int)(sh * .25));
				
				g2d.setColor(Color.BLUE);
				g2d.drawString("Angle = 30 Degrees", 10, (int)((sh * .25) + g2d.getFontMetrics().getHeight()));
				
				g2d.setColor(Color.RED);
				g2d.drawString("Hypotenuse = 100 m", 10, (int)((sh * .25) + g2d.getFontMetrics().getHeight() * 3));
				
				g2d.setColor(Color.CYAN);
				g2d.drawString("Opposite = 50", 10, (int)((sh * .25) + g2d.getFontMetrics().getHeight() * 4));
				
				g2d.setColor(Color.GREEN);
				g2d.drawString("Adjacent = 50 * 3^(1/2)", 10, (int)((sh * .25) + g2d.getFontMetrics().getHeight() * 5));
				
				g2d.setColor(Color.WHITE);
				drawCString(g2d, "Examples:", (int)(sh/1.8));
				
				g2d.setColor(Color.LIGHT_GRAY);
				g2d.fillRect(0, (int)(sh/1.6), (int)sw, (int)(sh/6));
				
				g2d.setColor(Color.BLACK);
				g2d.setFont(new Font("TimesRoman", Font.PLAIN, 18));
				drawMString(g2d, "Sin(30) = 50/Hypotenuse\n\nCos(30) = Adjacent/100\n\nTan(30) = Opposite/50*3^(1/3)", 0, (int)(sh/1.6));
				drawCString(g2d, "Hypotenuse * Sin(30) = 50\n\n 100*Cos(30) = Adjacent\n\n(50*3^(1/3))*Tan(30) = Opposite", (int)(sh/1.6));
				drawMString(g2d, "Hypotenuse = 50/sin(30) = 100\n\nAdjacent = 50 * 3^(1/2)\n\nOpposite = 50", (int)(sw) - g2d.getFontMetrics().stringWidth("Hypotenuse = 50/sin(30) = 100") ,(int)(sh/1.6));
				
				g2d.setColor(Color.DARK_GRAY);
				g2d.drawRect(0, (int)(sh/1.6), (int)sw, (int)(sh/6));
				g2d.drawLine(0,(int)(sh/1.6) + g2d.getFontMetrics().getHeight() * 2, (int)sw, (int)(sh/1.6) + g2d.getFontMetrics().getHeight() * 2);
				g2d.drawLine(0,(int)(sh/1.6) + g2d.getFontMetrics().getHeight() * 4, (int)sw, (int)(sh/1.6) + g2d.getFontMetrics().getHeight() * 4);
			}
			if (gamePanels[game] == 3)
			{
				
				g2d.setColor(Color.WHITE);
				g2d.fillRect(50, 125, (int)(sw/3), (int)(sh/7));
				
				g2d.setStroke(new BasicStroke(3));
				g2d.setColor(Color.BLACK);
				g2d.drawRect(50, 125, (int)(sw/3), (int)(sh/7));
				
				g2d.setFont(new Font("TimesRoman", Font.PLAIN, 20));
				drawMString(g2d, "Current Streak:\nBest Streak:\nPrevious Answer:", 65, 140);
				
				g2d.drawString("" + gameOneStreak, 70 + g2d.getFontMetrics().stringWidth("Current Streak:"), 168);
				g2d.drawString("" + gameOneBest, 70 + g2d.getFontMetrics().stringWidth("Best Streak:"), 195);
				g2d.drawString("" + previousAnswer, 70 + g2d.getFontMetrics().stringWidth("Previous Answer:"), 222);
				
				final int TIMER_SIZE = (int)(sw/2);
				g2d.setStroke(new BasicStroke(10));
				
				gameTimer.drawTimer((int)((sw/2) - (TIMER_SIZE/2)), (int)((sh/1.75) + (sh/6) + 25), TIMER_SIZE, g2d);
				
				g2d.setStroke(new BasicStroke(3));
					
				g2d.setColor(Color.WHITE);
				g2d.fillRect(20, (int)(sh/1.75), (int)(sw - 40), (int)(sh/6));
				
				g2d.setFont(new Font("TimesRoman", Font.PLAIN, 30));
				drawCString(g2d, "Practice getting side lengths and gain some coins!", 0);
				
				g2d.setFont(new Font("TimesRoman", Font.PLAIN, 20));
				drawCString(g2d, "Round your answer to the nearest whole number. Triangle not to scale.", 40);
				
				g2d.setColor(Color.BLACK);
				g2d.drawRect(20, (int)(sh/1.75), (int)(sw - 40), (int)(sh/6));
				
				if (gameOneActive)
				{
					g2d.setFont(new Font("TimesRoman", Font.PLAIN, 20));
					drawCString(g2d, gameOneQuestion + "\n" + gameOneQuestion2 + "\n" + gameOneQuestion3, (int)((sh/1.75) + (sh/16) - g2d.getFontMetrics().getHeight()));
				}
				else
				{
					g2d.setFont(new Font("TimesRoman", Font.PLAIN, 20));
					drawCString(g2d, "Press start in order to recieve the prompt here!\nThere will be a timer below. Don't worry, you can still answer if you run out of time!\n However, your streak will end upon the timer stopping.", (int)((sh/1.75) + (sh/16) - g2d.getFontMetrics().getHeight()));
				}
				
				g2d.setColor(Color.BLACK);
				g2d.fill(new Ellipse2D.Double(personStart.getX(), personStart.getY() - (sh/12), 10.0, 10.0));
				if (gameOneActive)
				{
					
					double dX = ( gameOneHyp * ( Math.cos(Math.toRadians(gameOneAngle)) )/200);
					double dY = ( gameOneHyp * ( Math.sin(Math.toRadians(gameOneAngle)) )/200);
					
					if (dX > 1)
						dX = 1;
					if (dY > 1)
						dY = 1;
					
					int minX = (int)personStart.getX() + 100;
					int maxX = (int)(sw - 100);
					int minY = (int)((personStart.getY() - (sh/12)) - 100);
					int maxY = 100;
					
					int disX = (int)Math.sqrt(Math.pow((maxX - minX), 2));
					int disY = (int)Math.sqrt(Math.pow((minY - maxY), 2));
					
					anglePoint = new Point((int)(personStart.getX() + ((dX*disX) + 100)), (int)((personStart.getY() - (sh/12)) - (dY*disY) - 100));
					
					if(questionType == 0)
					{
						g2d.setColor(Color.WHITE);
						g2d.fill(new Ellipse2D.Double(anglePoint.getX() - 5, anglePoint.getY(), 20, 10));
						g2d.setColor(Color.gray);
						g2d.fill(new Ellipse2D.Double(anglePoint.getX(), anglePoint.getY() , 10.0, 10.0));
					}
					else
					{
						g2d.setColor(Color.DARK_GRAY);
						g2d.setStroke(new BasicStroke(6));
						g2d.drawLine((int)anglePoint.getX() + 5 , (int)(personStart.getY() - (sh/12) + 5), (int)anglePoint.getX() + 5, (int)anglePoint.getY() + 5);
						
						g2d.setColor(Color.GRAY);
						g2d.setStroke(new BasicStroke(5));
						g2d.drawLine((int)anglePoint.getX() + 5 , (int)(personStart.getY() - (sh/12) + 5), (int)anglePoint.getX() + 5, (int)anglePoint.getY() + 5);
						
						g2d.setColor(Color.LIGHT_GRAY);
						g2d.fill(new Ellipse2D.Double(anglePoint.getX(), anglePoint.getY(), 10.0, 10.0));
					}
					
					if(pointAnimated)
					{
						g2d.setColor(Color.RED);
						g2d.setStroke(new BasicStroke(5));
						int dotSize = 12;
						g2d.fill(new Ellipse2D.Double(personCurrent.getX(), personCurrent.getY(), dotSize, dotSize));
						if(gameOneAnswerState == 0)
						{
							g2d.drawLine((int)(personStart.getX() + (dotSize/2)), (int)(personStart.getY() - (sh/12) + (dotSize/2)) , (int)(personCurrent.getX() + (dotSize/2)), (int)(personCurrent.getY() + (dotSize/2) ));
						}
						else if (gameOneAnswerState == 1)
						{
							g2d.drawLine((int)(personStart.getX() + (dotSize/2)), (int)(personStart.getY() - (sh/12) + (dotSize/2)) , (int)(personStart.getX() + (dotSize/2)), (int)(personCurrent.getY() + (dotSize/2) ));
						}
						else
						{
							g2d.drawLine((int)(personStart.getX() + (dotSize/2)), (int)(personStart.getY() - (sh/12) + (dotSize/2)) , (int)(personCurrent.getX() + (dotSize/2)), (int)(personStart.getY() - (sh/12) + (dotSize/2)));
						}
					}
					
				}
			}
		}
		
		// End of the first game's graphics
		
		if (game == 2)
		{
			if(gamePanels[game] == 0)
			{
				g2d.setColor(Color.WHITE);
				g2d.setFont(new Font("TimesRoman", Font.PLAIN, 25));
				drawCString(g2d, "Sine, Cosine, and Tangent can be used to find side lengths, but what about angles?\nThis is where inverse trig functions come in, obtaining the suffix 'Arc'.", 0);	
				
				g2d.setFont(new Font("TimesRoman", Font.PLAIN, 30));
				g2d.setColor(Color.CYAN);
				g2d.drawString("Sine -> ArcSine", (int)((sw/2) - g2d.getFontMetrics().stringWidth("Sine -> ArcSine")/2), (int)(sh/4));
				
				g2d.setColor(Color.GREEN);
				g2d.drawString("Cosine -> ArcCosine ", (int)((sw/2) - g2d.getFontMetrics().stringWidth("Cosine -> ArcCosine ")/2), (int)(sh/4) + g2d.getFontMetrics().getHeight());
				
				g2d.setColor(Color.RED);
				g2d.drawString("Tangent -> ArcTangent ", (int)((sw/2) - g2d.getFontMetrics().stringWidth("Tangent -> ArcTangent ")/2), (int)(sh/4) + g2d.getFontMetrics().getHeight()*2);
				
				g2d.setColor(Color.LIGHT_GRAY);
				drawCString(g2d, "These functions are essentially the opposite of the trig functions!\nPut in the ratio of two sides, and you recieve the angle.\nThe next page has some examples.", (int)(sh/2.2));
				
			}
			
			if (gamePanels[game] == 1)
			{
				g2d.setColor(Color.WHITE);
				g2d.setFont(new Font("TimesRoman", Font.PLAIN, 25));
				drawCString(g2d, "Below are some examples of these inverse functions in action.", 0);
				
				Font spacingFont = new Font("TimesRoman", Font.PLAIN, 25);
				
				g2d.setColor(Color.GRAY);
				g2d.setFont(new Font("TimesRoman", Font.PLAIN, 20));
				drawCString(g2d, "The symbol ^(1/3) represents 'To the power of 1/3', or the cube root of the number", g2d.getFontMetrics(spacingFont).getHeight() * 2);
				
				g2d.setColor(Color.BLACK);
				g2d.fillRect((int)((sw * 0.90) - (sh * .3) - (sh * .005)), (int)((sh * 0.20) - (sh * .005)), (int)(sh * .31), (int)(sh * .31));
				
				g2d.setColor(Color.WHITE);
				g2d.fillRect((int)((sw * 0.90) - (sh * .3)), (int)((sh * 0.20)), (int)(sh * .3), (int)(sh * .3));
				
				g2d.setStroke(new BasicStroke(5));				
				g2d.setColor(Color.RED);
				g2d.drawLine( (int) ((sw * .9) - (sh * .3) + 50), (int) ((sh * .2) + (sh * .3) - 50), (int) ( ((sw * .9) - (sh * .3) + 50) + ( Math.sin(Math.toRadians(30)) * (sh * .3) ) ) , (int) ( ((sh * .2) + (sh * .3) - 50) - ( Math.sin(Math.toRadians(30)) * (sh * .3) ) ) );
				
				g2d.setColor(Color.CYAN);
				g2d.drawLine( (int) ( ((sw * .9) - (sh * .3) + 50) + ( Math.sin(Math.toRadians(30)) * (sh * .3) ) ), (int) ((sh * .2) + (sh * .3) - 50), (int) ( ((sw * .9) - (sh * .3) + 50) + ( Math.sin(Math.toRadians(30)) * (sh * .3) ) ) , (int) ( ((sh * .2) + (sh * .3) - 50) - ( Math.sin(Math.toRadians(30)) * (sh * .3) ) ) );				
				
				g2d.setColor(Color.GREEN);
				g2d.drawLine( (int) ((sw * .9) - (sh * .3) + 50), (int) ((sh * .2) + (sh * .3) - 50), (int) ( ((sw * .9) - (sh * .3) + 50) + ( Math.sin(Math.toRadians(30)) * (sh * .3) ) ) , (int) ((sh * .2) + (sh * .3) - 50) );
				
				g2d.setColor(Color.BLUE);
				g2d.drawArc((int) ( ((sw * .9) - (sh * .3) + 50) - (sh * .05) ), (int) ( ((sh * .2) + (sh * .3) - 50) - (sh * .05)), (int)(sh * .1), (int)(sh * .1), 3, (int)36);
				
				g2d.setStroke(new BasicStroke(3));
				
				g2d.setFont(new Font("TimesRoman", Font.PLAIN, 25));
				g2d.setColor(Color.WHITE);
				g2d.fillRect(0, (int)(sh * .25) - 50, (int)(sw/3), (int)((sh * .2) + (g2d.getFontMetrics().getHeight() * 4)));
				
				g2d.setColor(Color.BLACK);
				g2d.drawRect(0, (int)(sh * .25) - 50, (int)(sw/3), (int)((sh * .2) + (g2d.getFontMetrics().getHeight() * 4)));
				
				g2d.setColor(Color.BLACK);
				g2d.drawString("Assuming that: ", 10, (int)(sh * .25));
				
				g2d.setColor(Color.BLUE);
				g2d.drawString("Angle = 30 Degrees", 10, (int)((sh * .25) + g2d.getFontMetrics().getHeight()));
				
				g2d.setColor(Color.RED);
				g2d.drawString("Hypotenuse = 100 m", 10, (int)((sh * .25) + g2d.getFontMetrics().getHeight() * 3));
				
				g2d.setColor(Color.CYAN);
				g2d.drawString("Opposite = 50", 10, (int)((sh * .25) + g2d.getFontMetrics().getHeight() * 4));
				
				g2d.setColor(Color.GREEN);
				g2d.drawString("Adjacent = 50 * 3^(1/2)", 10, (int)((sh * .25) + g2d.getFontMetrics().getHeight() * 5));
				
				g2d.setColor(Color.WHITE);
				drawCString(g2d, "Examples:", (int)(sh/1.8));
				
				g2d.setColor(Color.LIGHT_GRAY);
				g2d.fillRect(0, (int)(sh/1.6), (int)sw, (int)(sh/6));
				
				g2d.setColor(Color.BLACK);
				g2d.setFont(new Font("TimesRoman", Font.PLAIN, 18));
				drawMString(g2d, "ArcSine(50/100) = x Degrees\n\nArcCos(50 * 3^(1/2)/100) = x Degrees\n\nArcTan(50/50 * 3(1/3)) = x Degrees", 0, (int)(sh/1.6));
				drawCString(g2d, "30 Degrees = x\n\n30 Degrees = x\n\n30 Degrees = x", (int)(sh/1.6));
				
				g2d.setColor(Color.DARK_GRAY);
				g2d.drawRect(0, (int)(sh/1.6), (int)sw, (int)(sh/6));
				g2d.drawLine(0,(int)(sh/1.6) + g2d.getFontMetrics().getHeight() * 2, (int)sw, (int)(sh/1.6) + g2d.getFontMetrics().getHeight() * 2);
				g2d.drawLine(0,(int)(sh/1.6) + g2d.getFontMetrics().getHeight() * 4, (int)sw, (int)(sh/1.6) + g2d.getFontMetrics().getHeight() * 4);
			}
			if (gamePanels[game] == 2)
			{
				
				g2d.setColor(Color.WHITE);
				g2d.fillRect(20, (int)(sh/1.75), (int)(sw - 40), (int)(sh/6));
				
				g2d.setColor(Color.BLACK);
				g2d.drawRect(20, (int)(sh/1.75), (int)(sw - 40), (int)(sh/6));
				
				if (!gameTwoActive)
				{
					g2d.setFont(new Font("TimesRoman", Font.PLAIN, 20));
					drawCString(g2d, "Press start in order to begin the game!\nBelow is a timer. When the game starts, click on one of the circles to get its measurements.\nCollect all before the time limit is over, and recieve a large prize!", (int)((sh/1.75) + (sh/16) - g2d.getFontMetrics().getHeight()));
				}
				
				if (gameTwoActive && selectedPoint == -1)
				{
					g2d.setFont(new Font("TimesRoman", Font.PLAIN, 20));
					drawCString(g2d, "Click on one of the grey circles to recieve a new prompt!", (int)((sh/1.75) + (sh/16) - g2d.getFontMetrics().getHeight()));
				}
				
				if (gameTwoActive && selectedPoint > -1 && !gameTwoOver)
				{
					g2d.setFont(new Font("TimesRoman", Font.PLAIN, 20));
					drawCString(g2d, sideOneSection[fishArray.get(selectedPoint).getSideOneType()] + fishArray.get(selectedPoint).getSideOne() + " Feet.\n" 
					+ sideTwoSection[fishArray.get(selectedPoint).getSideTwoType()] + fishArray.get(selectedPoint).getSideTwo() + " Feet.\n"
					+ "Use left, right, and enter to aim and hit the target!" , (int)((sh/1.75) + (sh/16) - g2d.getFontMetrics().getHeight()));
				}
				
				if (gameTwoOver)
				{
					g2d.setFont(new Font("TimesRoman", Font.PLAIN, 20));
					drawCString(g2d, "Click start to begin a new game!" , (int)((sh/1.75) + (sh/16) - g2d.getFontMetrics().getHeight()));					
				}
				
				g2d.setColor(Color.WHITE);
				if (!gameTwoActive)
				{
					g2d.setFont(new Font("TimesRoman", Font.PLAIN, 30));
					drawCString(g2d, "It's fishing time! Find the angle for coins!", 0);
				}
				
				gameTwoSP = new Point((int)(sw/2.5), (int)(sh/2.5));
				
				g2d.setStroke(new BasicStroke(5));
				g2d.fill(new Ellipse2D.Double(gameTwoSP.getX() - 25, gameTwoSP.getY() - 12, 50, 25));
				
				g2d.setColor(Color.LIGHT_GRAY);
				g2d.draw(new Ellipse2D.Double(gameTwoSP.getX() - 25, gameTwoSP.getY() - 12, 50, 25));
				
				if (!fishArray.isEmpty());
				{
					for (int i = 0; i < fishArray.size(); i++)
					{
						if(fishArray.get(i).isActive())
							g2d.setColor(Color.GREEN);
						else
							g2d.setColor(Color.LIGHT_GRAY);
						
						fishArray.get(i).drawPoint(g2d);
					}
				}
				
				g2d.setColor(Color.BLACK);
				g2d.setFont(new Font("TimesRoman", Font.PLAIN, 30));
				drawCString(g2d, "Angle = " + targetAngle, (int)(sh*.85));
				
				g2d.setColor(Color.DARK_GRAY);
				g2d.setStroke(new BasicStroke(5));
				g2d.drawLine((int)(gameTwoSP.getX()),(int) (gameTwoSP.getY()), (int)(gameTwoSP.getX() + (75 * Math.cos(Math.toRadians(targetAngle)))), (int)(gameTwoSP.getY() - (75 * Math.sin(Math.toRadians(targetAngle)))));
			
				g2d.setStroke(new BasicStroke(3));
				g2d.setColor(Color.RED);
				g2d.draw(new Ellipse2D.Double( gameTwoSP.getX() - 5 + (75 * Math.cos(Math.toRadians(targetAngle))), gameTwoSP.getY() - 5 - (75 * Math.sin(Math.toRadians(targetAngle))), 10 , 10 ));
				
				int TIMER_SIZE = (int)(sw/2);
				g2d.setStroke(new BasicStroke(10));
				gameTimer.drawTimer((int)((sw/2) - (TIMER_SIZE/2)), (int)((sh/1.75) + (sh/6) + 25), TIMER_SIZE, g2d);
				g2d.setStroke(new BasicStroke(3));
			}

		}
		if (game == 3)
		{
			if (gamePanels[game] == 0)
			{
				g2d.setStroke(new BasicStroke(5));
				drawTriangle(g2d, (int)((sw/2) - (300 * Math.cos(Math.toRadians(30)))) - 50, (int)(sh/2), 30, 300);
				
				drawTriangle(g2d, (int)(sw/2) + 50, (int)(sh/2), 45, 300);
				
				g2d.setColor(Color.WHITE);
				g2d.setFont(new Font("TimesRoman", Font.PLAIN, 30));
				drawCString(g2d, "These are the special right triangles! They have special properties!\nTo the left is a 30/60/90 triangle, and to the right a 45/45/90 triangle.\nTheir names are based on their angles. They maintain these ratios.", 0);
				
				g2d.setColor(Color.GREEN);
				g2d.drawString("x", (int)((sw/2) - (150 * Math.cos(Math.toRadians(30)))) - 50, (int)(sh/2) + g2d.getFontMetrics().getHeight());
				g2d.drawString("x", (int)(sw/2) + 50 + (int)(150 * Math.cos(Math.toRadians(45))), (int)(sh/2) + g2d.getFontMetrics().getHeight());
				
				g2d.setColor(Color.CYAN);
				g2d.drawString("x", (int)(sw/2) + 75 + (int)(300 * Math.cos(Math.toRadians(45))), (int)(sh/2) + (int)(g2d.getFontMetrics().getHeight()/2.0) - (int)(150 * Math.sin(Math.toRadians(45))));
				g2d.drawString("3x", (int)(sw/2) - 25, (int)(sh/2) + (int)(g2d.getFontMetrics().getHeight()/2.0) - (int)(150 * Math.sin(Math.toRadians(30))));
				g2d.setFont(new Font("TimesRoman", Font.PLAIN, 10));
				g2d.drawString("1/2", (int)(sw/2) - 25 + g2d.getFontMetrics(new Font("TimesRoman", Font.PLAIN, 30)).stringWidth("3x"), (int)(sh/2) + (int)(g2d.getFontMetrics().getHeight()/2.0) - (int)(150 * Math.sin(Math.toRadians(30))));
				
				g2d.setFont(new Font("TimesRoman", Font.PLAIN, 30));
				g2d.setColor(Color.RED);
				g2d.drawString("2x", (int)((sw/2) - (150 * Math.cos(Math.toRadians(30)))) - 50, (int)(sh/2) - 25 - (int)(150 * Math.sin(Math.toRadians(30))));
				g2d.drawString("2x", (int)(sw/2) + 30 + (int)(150 * Math.cos(Math.toRadians(45))), (int)(sh/2) - 25 - (int)(150 * Math.sin(Math.toRadians(45))));
				g2d.setFont(new Font("TimesRoman", Font.PLAIN, 10));
				g2d.drawString("1/2", (int)(sw/2) + 30 + (int)(150 * Math.cos(Math.toRadians(45))) + g2d.getFontMetrics(new Font("TimesRoman", Font.PLAIN, 30)).stringWidth("2x"),(int)(sh/2) - (g2d.getFontMetrics(new Font("TimesRoman", Font.PLAIN, 30)).getHeight()) - (int)(150 * Math.sin(Math.toRadians(45))));

				g2d.setFont(new Font("TimesRoman", Font.PLAIN, 30));
				g2d.setColor(Color.WHITE);
				g2d.drawString("30", (int)((sw/2) - (300 * Math.cos(Math.toRadians(30)))), (int)(sh/2) - 5);
				g2d.drawString("45", (int)(sw/2) + 75, (int)(sh/2) - 5);
				
				g2d.drawString("60", (int)(sw/2) - 90, (int)(sh/2) - 100);
				g2d.drawString("45", (int)(sw/2) + 10 + (int)(300 * Math.cos(Math.toRadians(45))), (int)(sh/2) - 150);
				
			}
			if (gamePanels[game] == 1)
			{
				g2d.setColor(Color.WHITE);
				g2d.setFont(new Font("TimesRoman", Font.PLAIN, 30));
				drawCString(g2d, "Click the correct value below to gain coins!", 0);
				
				final int BUTTON_SIZE = 175;
				
				g2d.setColor(Color.WHITE);
				g2d.fillRect((int)((sw/2) - (BUTTON_SIZE/2.0)),(int)((sh/2) - (BUTTON_SIZE/2.0) - (sh/4)), BUTTON_SIZE, BUTTON_SIZE);
				g2d.setColor(Color.BLACK);
				g2d.drawRect((int)((sw/2) - (BUTTON_SIZE/2.0)),(int)((sh/2) - (BUTTON_SIZE/2.0) - (sh/4)), BUTTON_SIZE, BUTTON_SIZE);
				
				g2d.setColor(Color.WHITE);
				if (gameThreeAnswered && gameThreeCorrectButton == 0)
					g2d.setColor(Color.GREEN);
				if (gameThreeAnswered && !gameThreeCorrect && gameThreeButton == 0)
					g2d.setColor(Color.RED);
				g2d.fillRect(25,(int)((sh * .8) - BUTTON_SIZE - 25), BUTTON_SIZE, BUTTON_SIZE);
				g2d.setColor(Color.BLACK);
				g2d.drawRect(25,(int)((sh * .8) - BUTTON_SIZE - 25), BUTTON_SIZE, BUTTON_SIZE);
				
				g2d.setColor(Color.WHITE);
				if (gameThreeAnswered && gameThreeCorrectButton == 1)
					g2d.setColor(Color.GREEN);
				if (gameThreeAnswered && !gameThreeCorrect && gameThreeButton == 1)
					g2d.setColor(Color.RED);
				g2d.fillRect((25 * 2) + (BUTTON_SIZE * 1),(int)((sh * .8) - BUTTON_SIZE - 25), BUTTON_SIZE, BUTTON_SIZE);
				g2d.setColor(Color.BLACK);
				g2d.drawRect((25 * 2) + (BUTTON_SIZE * 1),(int)((sh * .8) - BUTTON_SIZE - 25), BUTTON_SIZE, BUTTON_SIZE);
				
				g2d.setColor(Color.WHITE);
				if (gameThreeAnswered && gameThreeCorrectButton == 2)
					g2d.setColor(Color.GREEN);
				if (gameThreeAnswered && !gameThreeCorrect && gameThreeButton == 2)
					g2d.setColor(Color.RED);
				g2d.fillRect((25 * 3) + (BUTTON_SIZE * 2),(int)((sh * .8) - BUTTON_SIZE - 25), BUTTON_SIZE, BUTTON_SIZE);
				g2d.setColor(Color.BLACK);
				g2d.drawRect((25 * 3) + (BUTTON_SIZE * 2),(int)((sh * .8) - BUTTON_SIZE - 25), BUTTON_SIZE, BUTTON_SIZE);
				
				g2d.setColor(Color.WHITE);
				if (gameThreeAnswered && gameThreeCorrectButton == 3)
					g2d.setColor(Color.GREEN);
				if (gameThreeAnswered && !gameThreeCorrect && gameThreeButton == 3)
					g2d.setColor(Color.RED);
				g2d.fillRect((25 * 4) + (BUTTON_SIZE * 3),(int)((sh * .8) - BUTTON_SIZE - 25), BUTTON_SIZE, BUTTON_SIZE);
				g2d.setColor(Color.BLACK);
				g2d.drawRect((25 * 4) + (BUTTON_SIZE * 3),(int)((sh * .8) - BUTTON_SIZE - 25), BUTTON_SIZE, BUTTON_SIZE);
				
				g2d.setColor(Color.black);
				g2d.setFont(new Font("TimesRoman", Font.PLAIN, 20));
				
				if (gameThreeActive)
					g2d.drawString(gameThreeChoices[gameThreePrompt], (int)((sw/2) - (g2d.getFontMetrics().stringWidth(gameThreeChoices[gameThreePrompt])/2.0)),(int)((sh/2) - (BUTTON_SIZE) - 20));
				
				if (gameThreeActive)
				{
					for (int i = 0; i < 4; i++)
					{
						if (i == gameThreeCorrectButton)
							g2d.drawString(gameThreeAnswers[gameThreePrompt], (int)((BUTTON_SIZE * 1.5) - 150 + (200 * i) - (g2d.getFontMetrics().stringWidth(gameThreeAnswers[gameThreePrompt])/2.0)), (int)((sh * .8) - BUTTON_SIZE + 75));
						else
						{
							g2d.drawString(gameThreeAnswers[randomPrompts[i]], (int)((BUTTON_SIZE * 1.5) - 150 + (200 * i) - (g2d.getFontMetrics().stringWidth(gameThreeAnswers[randomPrompts[i]])/2.0)), (int)((sh * .8) - BUTTON_SIZE + 75));
						}
					}
				}
			}
		}
		
		g2d.setColor(Color.GREEN);
		if (game == 3 && gamePanels[game] == 1)
			g2d.setColor(Color.GRAY);
		g2d.setFont(new Font("TimesRoman", Font.PLAIN, 30));
		if (!floatingText.isEmpty())
		{
			for(int i = 0; i < floatingText.size(); i++)
			{
				floatingText.get(i).drawText(g2d);
			}
		}
	}
		
	private void cycle() // Controls what happens on each game tick.
	{
		
		if (!floatingText.isEmpty())
			for(int i = 0; i < floatingText.size(); i++ )
			{
				floatingText.get(i).incrementString();
				if (!floatingText.get(i).isActive())
				{
					floatingText.remove(i);
					i -= 1;
				}
			}
		
		if (gameThreeAnswered)
		{
			waitTicks -= 1;
			if (waitTicks <= 0)
			{
				newGameThree();
				waitTicks = 50;
			}
		}
		
		if (gameOneActive && !pointAnimated)
		{
			gameTimer.incrementTime();
			gameOver = !gameTimer.active();
		}
		
		if (gameTwoActive)
		{
			gameTimer.incrementTime();
			gameTwoOver = !gameTimer.active();
		}
		
		
		if (questionAnswered && !pointAnimated)
		{
			if (correctAnswer)
			{
				if (!gameOver)
				{
					previousAnswer = gameOneAnswer;
					newGameOne();
					gameTime = gameTime - 40;
					if (gameTime < 400)
						gameTime = 400;
					gameTimer.startTimer(2400);
					gameOneStreak += 1;
					if (gameOneStreak > gameOneBest)
						gameOneBest = gameOneStreak;
				}
			}
		
			for(int i = 0; i < 750; i++)
				gameTimer.incrementTime();
			
			questionAnswered = false;
			correctAnswer = false;
		}
		
		if (isShowing())
		{
			Point s = getLocationOnScreen();
			sx = s.getX();
			sy = s.getY();
			
			PointerInfo mi = MouseInfo.getPointerInfo();
			Point mp = mi.getLocation();
			
			x = (int)(mp.getX() - sx - 1.5);
			y = (int)(mp.getY() - sy - 1.5);
			
		}
		
		if (mouseHeld)
		{
			calcHyp();
		}
		
		if (game == 1 && gamePanels[game] == 1)
		{
			incrementVisualization();
		}
		
		if (pointAnimated)
		{
			
			if (gameOneAnswerState == 0)
			{
				int disX = (int)Math.sqrt(Math.pow((anglePoint.getX() - personStart.getX()), 2));
				int disY = (int)Math.sqrt(Math.pow(((personStart.getY() - (sh/12)) - anglePoint.getY()), 2));
				
				int cPX = (int)(personStart.getX() +  ((Math.cos(Math.toRadians(90.0 * (pointMovementTimer/2250.0))))* ((double)gameOnePlayerAnswer / (double)gameOneAnswer) *disX)  );
				int cPY = (int)((personStart.getY() - (sh/12)) - ((Math.cos(Math.toRadians(90.0 * (pointMovementTimer/2250.0))))* ((double)gameOnePlayerAnswer / (double)gameOneAnswer) * disY)  );
				
				personCurrent = new Point(cPX, cPY);
			}
			else if (gameOneAnswerState == 1)
			{
				int disY = (int)Math.sqrt(Math.pow(((personStart.getY() - (sh/12)) - anglePoint.getY()), 2));
				
				int cPX = (int)(personStart.getX());
				int cPY = (int)((personStart.getY() - (sh/12)) - ((Math.cos(Math.toRadians(90.0 * (pointMovementTimer/2250.0))))* ((double)gameOnePlayerAnswer / (double)gameOneAnswer) * disY)  );
				
				personCurrent = new Point(cPX, cPY);
			}
			else
			{
				int disX = (int)Math.sqrt(Math.pow((anglePoint.getX() - personStart.getX()), 2));
				
				int cPX = (int)(personStart.getX() +  ((Math.cos(Math.toRadians(90.0 * (pointMovementTimer/2250.0))))* ((double)gameOnePlayerAnswer / (double)gameOneAnswer) *disX)  );
				int cPY = (int)((personStart.getY() - (sh/12)));
				
				personCurrent = new Point(cPX, cPY);
			}
			
			pointMovementTimer = pointMovementTimer - 25;
		}
			
		if (pointMovementTimer <= 0)
		{
			pointAnimated = false;
			pointMovementTimer = 2250;
		}
		
	}
	
	@Override
	public void addNotify()
	{
		super.addNotify();
		
		animator = new Thread(this);
		animator.start();
	}
	
	public void run() // Starts the thread, in which gameticks are made.
	{
		long beforeTime, timeDiff, sleep;
		
		beforeTime = System.currentTimeMillis();
		
		while (true)
		{
			cycle();
			repaint();
			
			timeDiff = System.currentTimeMillis() - beforeTime;
			sleep = DELAY - timeDiff;
			
			if (sleep < 0)
			{
				sleep = 2;
			}
			
			try
			{
				Thread.sleep(sleep);
			}
			catch (InterruptedException e)
			{
				String msg = String.format("Thread interrupted: %s", e.getMessage());
				
				JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
			}
			
			beforeTime = System.currentTimeMillis();
			
		}	
		
	}
	
	private class TrigAdapter extends MouseAdapter
	{
		@Override
		public void mousePressed(MouseEvent e)
		{
			mouseHeld = true;	
			
			if (game == 2 && gamePanels[game] == 2 && gameTwoActive)
			{
				for(int i = 0; i < fishArray.size(); i++)
				{
					if (x > fishArray.get(i).getX() - 10 && x < fishArray.get(i).getX() + 30 && y > fishArray.get(i).getY() - 10 && y < fishArray.get(i).getY() + 30)
					{
						selectedPoint = i;
					}
					else
					{
						fishArray.get(i).setDeactive();
					}
				
				}
			
				if (selectedPoint > -1)
				{
					fishArray.get(selectedPoint).setActive();
				}
			}
			
			if (game == 3 && gamePanels[game] == 1 && !gameThreeAnswered && gameThreeActive)
			{
				if ( x < (201) && x > 24 && y < (int)(sh * .8) - 24 && y > (int)(sh * .8) - 201)
				{
					gameThreeAnswered = true;
					gameThreeButton = 0;
					if (gameThreeButton == gameThreeCorrectButton)
					{
						gameThreeCorrect = true;
						rewardCoins((int)(250 *Math.random()) + 250, x, y, 100, 120, false);
					}
				}
				if (x < (401) && x > 224 && y < (int)(sh * .8) - 24 && y > (int)(sh * .8) - 201)
				{
					gameThreeAnswered = true;
					gameThreeButton = 1;
					if (gameThreeButton == gameThreeCorrectButton)
					{
						gameThreeCorrect = true;
						rewardCoins((int)(250 *Math.random()) + 250, x, y, 100, 120, false);
					}
				}
				if (x < (601) && x > 424 && y < (int)(sh * .8) - 24 && y > (int)(sh * .8) - 201)
				{
					gameThreeAnswered = true;
					gameThreeButton = 2;
					if (gameThreeButton == gameThreeCorrectButton)
					{
						gameThreeCorrect = true;
						rewardCoins((int)(250 *Math.random()) + 250, x, y, 100, 120, false);
					}
				}
				if (x < (801) && x > 624 && y < (int)(sh * .8) - 24 && y > (int)(sh * .8) - 201)
				{
					gameThreeAnswered = true;
					gameThreeButton = 3;
					if (gameThreeButton == gameThreeCorrectButton)
					{
						gameThreeCorrect = true;
						rewardCoins((int)(250 *Math.random()) + 250, x, y, 100, 120, false);
					}
				}
			}

		}
		
		public void mouseReleased(MouseEvent e)
		{
			mouseHeld = false;
		}
		
	}
	
	private class aimAction extends AbstractAction
	{
		int actionType;
		
		aimAction(int at)
		{
			actionType = at;
		}
		
		@Override
		public void actionPerformed(ActionEvent e)
		{
			
			if (game == 1 && gamePanels[game] == 3 && actionType == 2)
			{
				if (!pointAnimated)
				{
					if (isNumeric(answerField.getText()) && !answerField.getText().isEmpty())
					{
						if (!questionAnswered)
						{
							gameOnePlayerAnswer = Integer.parseInt(answerField.getText());
						}
						questionAnswered = true;
						pointAnimated = true;
					
						if (Integer.parseInt(answerField.getText()) >= gameOneAnswer - 1 && Integer.parseInt(answerField.getText()) <= gameOneAnswer + 1 && gameOneActive)
						{
							System.out.println("Correct. Number = " + Integer.parseInt(answerField.getText()));
							correctAnswer = true;
						}
						else
						{
							System.out.println("Incorrect. Number = " + Integer.parseInt(answerField.getText()));
						}

					}
				}

			}
			
			if (game == 2 && gamePanels[game] == 2)
			{
				if (actionType == 0)
				{
					targetAngle += 1;
					if (targetAngle > 90)
						targetAngle = 90;
				}
				if (actionType == 1)
				{
					targetAngle -= 1;
					if (targetAngle < 0)
						targetAngle = 0;
				}
				if (actionType == 2 && !gameTwoOver)
				{
					answerGameTwo();
				}
			}
			
		}
	}
	
	private void calcHyp()
	{
		if (game == 1 && gamePanels[game] == 0 && ((sw/2) - (sh/6) - 50) < x && x < ((sw/2) + (sh/6) + 50) && ((sh/3) - (sh/6) - 50) < y && y < ((sh/3) + (sh/6) + 50))
		{
			
			angle = Math.atan2(y - ((sh/3)), x - ((sw/2)));
			
			cX = (Math.round(((sw/2)) + ((sh/6) * Math.cos(angle))));
			cY = (Math.round(((sh/3)) + ((sh/6) * Math.sin(angle))));
		}
		
		if (cX > 0 & cY > 0)
		{
			double angleDeg = (angle * (-180/Math.PI));
			if (angleDeg < 0)
			angleDeg = 360 - Math.abs(angleDeg);
			String stringDeg = ("" + angleDeg);
			ang.setText(stringDeg.substring(0, Math.min(stringDeg.length(), 8)));
		
			opposite = (Math.sqrt(Math.pow((cX - cX), 2) + Math.pow((cY - (sh/3)), 2)) / (sh/6));
			String stringOp = "" + opposite;
			op.setText(stringOp.substring(0, Math.min(stringOp.length(), 8)));
		
		
			adjacent = (Math.sqrt(Math.pow((cX - sw/2), 2) + Math.pow(((sh/3) - (sh/3)), 2)) / (sh/6));
			String stringAdj = "" + adjacent;
			adj.setText(stringAdj.substring(0, Math.min(stringAdj.length(), 8)));
			
		}
		
	}
	
	private void drawCString(Graphics g, String text, int y)
	{
		for (String line : text.split("\n"))
			g.drawString(line, (int)(sw/2) - (g.getFontMetrics().stringWidth(line)/2) , y += g.getFontMetrics().getHeight());
		
	}
	
	private void drawMString(Graphics g, String text, int x, int y)
	{
		for (String line : text.split("\n"))
			g.drawString(line, x, y += g.getFontMetrics().getHeight());
		
	}
	
	private void incrementVisualization()
	{
		visDegrees += visSpeed;
		if (visDegrees > 360)
			visDegrees = visDegrees - 360;
		
		final int ELLIPSE_SIZE = 10;
		int maxSize = (int)((sw/2.5)/ELLIPSE_SIZE);
		double mag = (sh/11);
		
		// Sine Section
		// Removes the ellipse at the end in order to prevent the ellipses from building up on the left
		if (sineVis.size() >= maxSize)
		{
			sineVis.remove(maxSize - 1);
		}
		
		// X = the edge of the rect - the size of the ellipse, Y = distance down to the rect + the % down based on sine.
		sineVis.add(0, (new Ellipse2D.Double((((sw/2) + (sw/2.5)) - ELLIPSE_SIZE), (sh/6) + (sh/11) + ((mag * Math.sin(Math.toRadians(visDegrees))) - (ELLIPSE_SIZE/2)), ELLIPSE_SIZE, ELLIPSE_SIZE)));
		
		// After a new ellipse is created, move the next one to the left of it at a distance of the size of the ellipse
		for (int i = 0; i < sineVis.size(); i++)
		{
			sineVis.set(i, new Ellipse2D.Double((((sw/2) + (sw/2.5)) - (ELLIPSE_SIZE * (i + 1))), sineVis.get(i).getY(), ELLIPSE_SIZE, ELLIPSE_SIZE));
		}
		
		// Cosine Section
		
		if (cosVis.size() >= maxSize)
		{
			cosVis.remove(maxSize - 1);
		}
		
		cosVis.add(0, (new Ellipse2D.Double((((sw/2) + (sw/2.5)) - ELLIPSE_SIZE), (sh/2.75) + (sh/11) + ((mag * Math.cos(Math.toRadians(visDegrees))) - (ELLIPSE_SIZE/2)), ELLIPSE_SIZE, ELLIPSE_SIZE)));
		
		for (int i = 0; i < sineVis.size(); i++)
		{
			cosVis.set(i, new Ellipse2D.Double((((sw/2) + (sw/2.5)) - (ELLIPSE_SIZE * (i + 1))), cosVis.get(i).getY(), ELLIPSE_SIZE, ELLIPSE_SIZE));
		}
		
		// Tan Section
		if (tanVis.size() >= maxSize)
		{
			tanVis.remove(maxSize - 1);
		}
		
		if (Math.tan(Math.toRadians(visDegrees)) >= -1 && Math.tan(Math.toRadians(visDegrees)) <= 1)
			tanVis.add(0, (new Ellipse2D.Double((((sw/2) + (sw/2.5)) - ELLIPSE_SIZE), (sh/1.785) + (sh/11) + ((mag * Math.tan(Math.toRadians(visDegrees))) - (ELLIPSE_SIZE/2)), ELLIPSE_SIZE, ELLIPSE_SIZE)));
		else
			tanVis.add(0, (new Ellipse2D.Double((((sw/2) + (sw/2.5)) - ELLIPSE_SIZE), (sh/1.785) + (sh/11) + (mag * 0 - (ELLIPSE_SIZE/2)), 0, 0)));
		
		for (int i = 0; i < sineVis.size(); i++)
		{
			tanVis.set(i, new Ellipse2D.Double((((sw/2) + (sw/2.5)) - (ELLIPSE_SIZE * (i + 1))), tanVis.get(i).getY(), tanVis.get(i).getWidth(), tanVis.get(i).getHeight()));
		}
		
	}
	
	public static boolean isNumeric(String str)
	{
		for (char c : str.toCharArray())
		{
			if (!Character.isDigit(c)) return false;
		}
		return true;
	}
	
	public void newGameOne()
	{
		gameOneAngle = (int)(89 * Math.random()) + 1;
		gameOneAnswer= (int)(10 + (90 * Math.random() + 1));
		
		gameOneAnswerState = (int)(Math.random() * 3);
		gameOneGivenState = (int)(Math.random() * 3);
		while(gameOneGivenState == gameOneAnswerState)
		{
			gameOneGivenState = (int)(Math.random() * 3);
		}
		
		// State 0 = hypotenuse , state 1 = opposite, state 2 = adjacent
		
		if(gameOneAnswerState == 0)
		{
			gameOneHyp = gameOneAnswer;
			
			if (gameOneGivenState == 1)
			{
				gameOneGiven = (int)Math.round(gameOneAnswer * Math.sin(Math.toRadians(gameOneAngle)));
			}
			if (gameOneGivenState == 2)
			{
				gameOneGiven = (int)Math.round(gameOneAnswer * Math.cos(Math.toRadians(gameOneAngle)));
			}
		}
		else if (gameOneAnswerState == 1)
		{
			if (gameOneGivenState == 0)
			{
				gameOneGiven = (int)Math.round(gameOneAnswer / Math.sin(Math.toRadians(gameOneAngle)));
				
				gameOneHyp = (int)(gameOneAnswer / Math.sin(Math.toRadians(gameOneAngle)));
			}
			if (gameOneGivenState == 2)
			{
				gameOneGiven = (int)Math.round(gameOneAnswer / Math.tan(Math.toRadians(gameOneAngle)));
				
				gameOneHyp = (int)(gameOneAnswer / Math.sin(Math.toRadians(gameOneAngle)));
			}
		}
		else
		{
			if (gameOneGivenState == 0)
			{
				gameOneGiven = (int)Math.round(gameOneAnswer / Math.cos(Math.toRadians(gameOneAngle)));
				
				gameOneHyp = (int)(gameOneAnswer / Math.cos(Math.toRadians(gameOneAngle)));
			}
			if (gameOneGivenState == 1)
			{
				gameOneGiven = (int)Math.round(gameOneAnswer * Math.tan(Math.toRadians(gameOneAngle)));
				
				gameOneHyp = (int)(gameOneAnswer / Math.cos(Math.toRadians(gameOneAngle)));
			}
		}
		
		questionType = (int)(Math.random() * givenStatementType.length);
		String gAng = ""; //Given Angle String 
		String as = ""; // Part of the string for what the user will solve for
				
		if (questionType == 0)
		{
			gAng = givenDrone[gameOneGivenState];
			as = answerDrone[gameOneAnswerState];
		}
		else
		{
			gAng = givenWall[gameOneGivenState];
			as = answerWall[gameOneAnswerState];
		}
		
		gameOneQuestion = givenStatementType[questionType] + gameOneAngle + " Degrees";
		gameOneQuestion2 = gAng + gameOneGiven + " meters";
		gameOneQuestion3 = as;
		
		System.out.println("Angle = " + gameOneAngle + ", Answer = " + gameOneAnswer + " Given = " + gameOneGiven + ", Answer State = " + gameOneAnswerState + ", Given State = "+ gameOneGivenState);
	}
	
	
	public void answerGameTwo()
	{
		if (selectedPoint != -1 && !fishArray.isEmpty())
		{
			if (targetAngle == fishArray.get(selectedPoint).getAngle() || targetAngle == fishArray.get(selectedPoint).getAngle() + 1 || targetAngle == fishArray.get(selectedPoint).getAngle() - 1)
			{
				int jackpotNumber = (int)(25 * Math.random());
				boolean jackpot = false;
				if (jackpotNumber == 1)
					jackpot = true;
				
				if (jackpot)
					rewardCoins((int)(1000 * Math.random() + 1) + 1000, fishArray.get(selectedPoint).getX(), fishArray.get(selectedPoint).getY(), 120 , 100, true);
				else
					rewardCoins((int)(400 * Math.random() + 1) + 100, fishArray.get(selectedPoint).getX(), fishArray.get(selectedPoint).getY(), 120 , 100, false);
				
				fishArray.remove(selectedPoint);
				selectedPoint = -1;
				
				if (fishArray.isEmpty())
				{
					rewardCoins((int)(1000 * Math.random() + 1) + 5000, (int)(sw/2), (int)(sh/3), 120 , 100, true);
					gameTimer.stopTimer();
					gameTwoActive = false;
				}
				
			}
			else
			{
				for(int i = 0; i < 1000; i++)
					gameTimer.incrementTime();
			}
		}
	}
	
	public void newGameTwo()
	{
		fishArray.clear();
		for(int i = 0; i < 5; i++)
		{
			fishArray.add(new FishPoint(gameTwoSP));
			for (int b = 0; b < i; b++)
			{
				while (fishArray.get(i).getX() < fishArray.get(b).getX() + 50 && fishArray.get(i).getX() > fishArray.get(b).getX() - 50 && fishArray.get(i).getY() < fishArray.get(b).getY() + 50 && fishArray.get(i).getY() > fishArray.get(b).getY() - 50)
				{
					fishArray.set(i, new FishPoint(gameTwoSP));
				}
				
			}
		}

	}
	
	public void newGameThree()
	{
		gameThreeAnswered = false;
		gameThreeCorrect = false;
		gameThreeCorrectButton = (int)(4 * Math.random());
		gameThreePrompt = (int)(gameThreeChoices.length * Math.random());
		
		boolean repeats = true;
		
		while(repeats)
		{
			repeats = false;
			for (int i = 0; i < randomPrompts.length; i++)
			{
				randomPrompts[i] = (int)(gameThreeAnswers.length * Math.random());
				if(randomPrompts[i] == gameThreePrompt)
					repeats = true;
				for(int b = 0; b < i; b++)
				{
					if (randomPrompts[i] == randomPrompts[b])
						repeats = true;
				}
			}
		}
	}
	
	public void rewardCoins(int amount, int x, int y, int ti, int d, boolean jp)
	{
		if (jp)
			floatingText.add(new FloatingText("Jackpot! Coins +" + amount, ti, x, y, d));
		else
			floatingText.add(new FloatingText("Coins +" + amount, ti, x, y, d));
		currency += amount;
	}
	
	public void drawTriangle(Graphics2D g, int x, int y, int angle, int hyp)
	{
		g.setColor(Color.RED);
		g.drawLine(x, y, x + (int)(hyp * Math.cos(Math.toRadians(angle))), y - (int)(hyp *Math.sin(Math.toRadians(angle))));
		
		g.setColor(Color.CYAN);
		g.drawLine(x + (int)(hyp *Math.cos(Math.toRadians(angle))), y, x + (int)(hyp * Math.cos(Math.toRadians(angle))), y - (int)(hyp * Math.sin(Math.toRadians(angle))));
		
		g.setColor(Color.GREEN);
		g.drawLine(x, y, x + (int)(hyp * Math.cos(Math.toRadians(angle))), y);
	}
	
}
