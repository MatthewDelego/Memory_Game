import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class GameGUI extends JFrame {
    private static final long serialVersionUID = 1L;
    private JComboBox<String> levelComboBox;
    private JLabel playerNameLabel;
    private JLabel triesLabel;
    private JLabel gamesPlayedLabel;
    private JLabel gamesWonLabel;
    private JLabel gamesLostLabel;
    private JPanel gridPanel;
    private JPanel infoPanel;
    private JButton startButton;
    private JButton nameButton;
    private boolean allMatchesFound = false;
    private Set<JButton> matchedTiles = new HashSet<>();
    private int requiredMatches; // The number of matches required to win
    private int gridSize; // The size of the grid
    private List<JButton> gameButtons = new ArrayList<>();
    private int tries;
    
    private int gamesPlayed = 0;
    private int gamesWon = 0;
    private int gamesLost = 0;
    
    private boolean debugMode = true; // custom debug mode to check matching tiles

	public GameGUI() {
	    setTitle("Memory Game");
	    setSize(800, 800);
	    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    setLayout(new BorderLayout());

	    // Top panel with name button, level selection, and start button
	    JPanel topPanel = new JPanel(new BorderLayout(10, 0));
	    topPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.BLACK));
	    
	    // Name button that triggers the popup
	    nameButton = new JButton("NAME");
	    styleButton(nameButton);
	    nameButton.setBorder(new RoundedBorder(10));
	    nameButton.addActionListener(new NameButtonListener());
	    nameButton.addMouseListener(new java.awt.event.MouseAdapter() {
	        public void mouseEntered(java.awt.event.MouseEvent evt) {
	            nameButton.setBackground(new Color(180, 180, 180)); // Darker on hover
	        }

	        public void mouseExited(java.awt.event.MouseEvent evt) {
	            styleButton(nameButton); // Reset to original style
	        }
	    });


	    // Level selection panel containing the label and combo box
	    JPanel levelPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
	    JLabel levelLabel = new JLabel("LEVEL:");
	    String[] levels = { "", "Easy: 3x3", "Medium: 5x5", "Hard: 10x10" };
	    levelComboBox = new JComboBox<>(levels);
	    levelPanel.add(levelLabel);
	    levelPanel.add(levelComboBox);

	    // Panel for organizing name button and level panels vertically
	    JPanel nameLevelPanel = new JPanel(new GridLayout(2, 1));
	    nameLevelPanel.add(nameButton);
	    nameLevelPanel.add(levelPanel);

	    // Start button on the right
	    startButton = new JButton("Start");
	    styleButton(startButton);
	    startButton.setBorder(new RoundedBorder(10));
	    startButton.addActionListener(new StartButtonListener());
	    startButton.addMouseListener(new java.awt.event.MouseAdapter() {
	        public void mouseEntered(java.awt.event.MouseEvent evt) {
	            startButton.setBackground(new Color(180, 180, 180)); // Darker on hover
	        }

	        public void mouseExited(java.awt.event.MouseEvent evt) {
	            styleButton(startButton); // Reset to original style
	        }
	    });


	    // Add the name/level panel and the start button to the top panel
	    topPanel.add(nameLevelPanel, BorderLayout.CENTER);
	    topPanel.add(startButton, BorderLayout.EAST);

	    // Set the top panel as the north component of the main border layout
	    add(topPanel, BorderLayout.NORTH);

	    // Initialize the infoPanel with BorderLayout
	    infoPanel = new JPanel(new BorderLayout());
	    JLabel levelInfoLabel = new JLabel("LEVEL: EASY"); 
	    triesLabel = new JLabel("TRIES: 00");
	    infoPanel.add(levelInfoLabel, BorderLayout.WEST);
	    infoPanel.add(triesLabel, BorderLayout.EAST);

	    // Center panel with grid and infoPanel
	    gridPanel = new JPanel(new BorderLayout());
	    gridPanel.add(infoPanel, BorderLayout.NORTH);

	    // The actual game grid will be added into a sub-panel within gridPanel
	    JPanel gameGridPanel = new JPanel();
	    gameGridPanel.setBackground(new Color(240, 240, 240)); // Light gray
	    gridPanel.add(gameGridPanel, BorderLayout.CENTER);

	    // Add gridPanel to the center of the main frame
	    add(gridPanel, BorderLayout.CENTER);

	    // Bottom panel with game stats and players results
	    JPanel bottomPanel = new JPanel(new BorderLayout());
	    Border padding = BorderFactory.createEmptyBorder(10, 10, 10, 10);
	    Border topBorder = BorderFactory.createMatteBorder(1, 0, 0, 0, Color.BLACK);
	    Border compound = BorderFactory.createCompoundBorder(topBorder, padding);
	    bottomPanel.setBorder(compound);

	    // Stats panel on the left with padding
	    JPanel statsPanel = new JPanel(new GridLayout(3, 1));
	    statsPanel.setBorder(padding);
	    gamesPlayedLabel = new JLabel("Game Played: 0");
	    gamesWonLabel = new JLabel("Game Won: 0");
	    gamesLostLabel = new JLabel("Game Lost: 0");
	    statsPanel.add(gamesPlayedLabel);
	    statsPanel.add(gamesWonLabel);
	    statsPanel.add(gamesLostLabel);

	    // Player panel on the right with padding
	    JPanel playerPanel = new JPanel(new BorderLayout());
	    playerPanel.setBorder(padding);
	    playerNameLabel = new JLabel("Player Name");
	    playerNameLabel.setHorizontalAlignment(JLabel.CENTER);
	    JLabel winLoseLabel = new JLabel("WIN / LOSE");
	    winLoseLabel.setHorizontalAlignment(JLabel.CENTER);
	    winLoseLabel.setForeground(Color.RED);
	    playerPanel.add(playerNameLabel, BorderLayout.NORTH);
	    playerPanel.add(winLoseLabel, BorderLayout.CENTER);

	    bottomPanel.add(statsPanel, BorderLayout.WEST);
	    bottomPanel.add(playerPanel, BorderLayout.EAST);

	    // Add the bottom panel to the JFrame
	    add(bottomPanel, BorderLayout.SOUTH);

	    pack(); // This sizes the frame 
	    setLocationRelativeTo(null); // Center the JFrame on the screen
	    setVisible(true);
	}

	private void styleButton(JButton button) {
	    button.setFont(new Font("Arial", Font.PLAIN, 16));
	    button.setBackground(new Color(200, 200, 200));
	    button.setFocusPainted(false);
	}

	class RoundedBorder implements Border {
	    private int radius;

	    RoundedBorder(int radius) {
	        this.radius = radius;
	    }

	    public Insets getBorderInsets(Component c) {
	        return new Insets(this.radius+1, this.radius+1, this.radius+2, this.radius);
	    }

	    public boolean isBorderOpaque() {
	        return true;
	    }

	    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
	        g.drawRoundRect(x, y, width-1, height-1, radius, radius);
	    }
	}

	
	private class NameButtonListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			JDialog nameDialog = new JDialog(GameGUI.this, "Enter Your Name", true);
			nameDialog.setSize(300, 150);
			nameDialog.setLayout(new FlowLayout());

			JLabel nameLabel = new JLabel("Please enter Your Name");
			JTextField nameTextField = new JTextField(10);
			JButton okButton = new JButton("OK");
			JButton cancelButton = new JButton("CANCEL");

			okButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					String playerName = nameTextField.getText();
					if (isValidName(playerName)) {
						// playerNameLabel is a class member and is initialized
						playerNameLabel.setText(playerName);
						nameDialog.dispose();
					} else {
						JOptionPane.showMessageDialog(nameDialog,
								"Please enter a valid name (not null and contains no numbers).", "Invalid Input",
								JOptionPane.ERROR_MESSAGE);
					}
				}
			});

			cancelButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					nameDialog.dispose();
				}
			});

			nameDialog.add(nameLabel);
			nameDialog.add(nameTextField);
			nameDialog.add(okButton);
			nameDialog.add(cancelButton);

			nameDialog.setLocationRelativeTo(GameGUI.this);
			nameDialog.setVisible(true);
		}

		private boolean isValidName(String name) {
			return name != null && !name.isEmpty() && !name.matches(".*\\d.*");
		}
	}

	private class StartButtonListener implements ActionListener {
	    @Override
	    public void actionPerformed(ActionEvent e) {
	        // Check if the playerNameLabel text is a valid name
	    	if (!isValidName(playerNameLabel.getText())) {
	    	    JOptionPane.showMessageDialog(GameGUI.this,
	    	        "Please press the NAME button to enter your name before starting the game.",
	    	        "Name Required",
	    	        JOptionPane.WARNING_MESSAGE);
	    	} else if (levelComboBox.getSelectedIndex() == -1 || "".equals(levelComboBox.getSelectedItem().toString())) {
	    	    JOptionPane.showMessageDialog(GameGUI.this,
	    	        "Please select a level from the dropdown.",
	    	        "Level Required",
	    	        JOptionPane.WARNING_MESSAGE);
	    	 } else {
	             String selectedLevel = levelComboBox.getSelectedItem().toString();
	             setupGameGrid(selectedLevel); // Call setupGameGrid with the selected level
	         }
	     }
	    
	    private void setupGameGrid(String level) {
	        // Determine grid size and required matches based on level selected
	        switch (level) {
	            case "Easy: 3x3":
	                gridSize = 3;
	                requiredMatches = 3;
	                break;
	            case "Medium: 5x5":
	                gridSize = 5;
	                requiredMatches = 5;
	                break;
	            case "Hard: 10x10":
	                gridSize = 10;
	                requiredMatches = 10;
	                break;
	            default:
	                return; // Invalid level, return without setting up the grid
	        }
	        tries = requiredMatches; // Set the initial tries based on required matches
	        triesLabel.setText("TRIES: " + tries);

	        // Increment and update the games played counter
	        gamesPlayed++;
	        gamesPlayedLabel.setText("Games Played: " + gamesPlayed);

	        matchedTiles.clear(); // Clear matched tiles
	        gameButtons.clear(); // Clear the previous game buttons

	        JPanel gameGridPanel = (JPanel) gridPanel.getComponent(1);
	        gameGridPanel.setLayout(new GridLayout(gridSize, gridSize, 10, 10));
	        gameGridPanel.removeAll(); // Remove previous buttons if any

	        // Populate the grid with buttons and add action listeners
	        for (int i = 0; i < gridSize * gridSize; i++) {
	            JButton button = new JButton();
	            
	            button.addActionListener(new ActionListener() {
	                @Override
	                public void actionPerformed(ActionEvent e) {
	                    handleButtonClick(e); // Call a method to handle button click
	                }
	            });
	            gameGridPanel.add(button);
	            gameButtons.add(button); // Add the button to the list of game buttons
	        }

	        // Set the command for the buttons that need to be matched
	        setMatchCommands(requiredMatches);

	        gameGridPanel.revalidate();
	        gameGridPanel.repaint();

	        // Disable top panel components
	        levelComboBox.setEnabled(false);
	        startButton.setEnabled(false);
	        nameButton.setEnabled(false);
	        
	        // Resize the frame to fit the content
	        pack(); // This will resize the window to accommodate the game grid
	        setSize(800, 800);
	        setLocationRelativeTo(null); // Keep the window centered
	    }

	    
	    private void setMatchCommands(int requiredMatches) {
	        Collections.shuffle(gameButtons); // Shuffle the list of buttons
	        for (int i = 0; i < requiredMatches; i++) {
	            // Set a special command on the buttons that are matches
	            gameButtons.get(i).setActionCommand("match");

	            if (debugMode) {
	                // Set background color to a light shade of blue for debugging purposes
	                // This allows the programmer to see which tiles are meant to match when the game starts
	                gameButtons.get(i).setBackground(new Color(173, 216, 230)); // Light blue color
	                gameButtons.get(i).setOpaque(true);
	                gameButtons.get(i).setBorderPainted(false);
	            }
	        }
	    }

	    private boolean isValidName(String name) {
	        // Consider a name valid if it's not null, not empty, not the default "Player Name", and contains no numbers
	        return name != null && !name.isEmpty() && !name.equals("Player Name") && !name.matches(".*\\d.*");
	    }
	}
	
	private void handleButtonClick(ActionEvent e) {
	    JButton clickedButton = (JButton) e.getSource();

	    if (matchedTiles.contains(clickedButton)) {
	        return; // Clicking on an already matched tile does nothing
	    }

	    if (isMatch(clickedButton)) {
	        matchedTiles.add(clickedButton);
	        clickedButton.setEnabled(false); // Optionally we can disable the button to prevent further clicks

	        // Set background color to indicate a match
	        clickedButton.setBackground(Color.GREEN); 

	        // Check if all matches have been found
	        allMatchesFound = matchedTiles.size() == requiredMatches;

	        if (allMatchesFound) {
	            showGameOverDialog(true); // Invoke game over dialog indicating a win
	        }
	    } else {
	        tries--;
	        triesLabel.setText("TRIES: " + tries);

	        if (tries <= 0) {
	            showGameOverDialog(false); // Invoke game over dialog indicating a loss
	        }
	    }
	}


	private void showGameOverDialog(boolean hasWon) {
	    // Prepare the message based on win or loss
	    String message = hasWon ? "You win!" : "No more tries left. You lost.";
	    message += "\nDo you want to start over?";
	    
	    // Custom button texts
	    Object[] options = {"Yes", "No"};
	    
	    // Show the option dialog
	    int choice = JOptionPane.showOptionDialog(
	            this,
	            message,
	            "Game Over",
	            JOptionPane.YES_NO_OPTION,
	            JOptionPane.QUESTION_MESSAGE,
	            null,
	            options,
	            options[0]
	    );
	    
	    if (hasWon) {
	        // Increment and update the games won counter
	        gamesWon++;
	        gamesWonLabel.setText("Games Won: " + gamesWon);
	    } else {
	        // Increment and update the games lost counter
	        gamesLost++;
	        gamesLostLabel.setText("Games Lost: " + gamesLost);
	    }
	    
	    // If the user chooses "Yes" to start over
	    if (choice == JOptionPane.YES_OPTION) {
	        resetGame();
	    } else {
	        // If the user chooses "No", reset for new entry
	        resetForNewEntry();
	    }
	}

	
	private boolean isMatch(JButton button) {
	    // Check if the button's action command is "match"
	    return "match".equals(button.getActionCommand());
	}
	
	private void resetGame() {
	    // Enable components for level selection
	    levelComboBox.setEnabled(true);
	    startButton.setEnabled(true);
	    nameButton.setEnabled(true);
	    
	}

	private void resetForNewEntry() {
	    // Close the current window
	    this.dispose();

	    // Reopen the game by creating a new instance of GameGUI
	    SwingUtilities.invokeLater(new Runnable() {
	        public void run() {
	            new GameGUI().setVisible(true);
	        }
	    });
	}


	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				new GameGUI();
			}
		});
	}
}
