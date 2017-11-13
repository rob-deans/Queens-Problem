package ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextPane;
import javax.swing.border.EmptyBorder;

import state.Queen;
import state.State;

@SuppressWarnings("serial")
public class QueenUI extends JPanel{
	
	//set variables that can be accessed by every function
	private JPanel boardPanel = new JPanel();
	private JLabel[][] labels;	//checker board
	private int q;				//board size/number of queens
	private State state;		//init state
	private boolean found = false;	
	private int index;			// number of moves/where it is in the list
	double end;					//to get the length of time taken
	private ArrayList<State>path;	//path from start to goal state
	private int showing;			//which element is showing in the path array
	
	private JButton prev;			//to navigate through the loop
	private JButton next;
	JLabel tierNumber;
	
	//constructor
	public QueenUI(){
		this.q = 8; //set the starting board size/ number of queens
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		//draw the UI
		drawOptions();
		this.add(boardPanel);
		drawBoard(q);
		drawTrace();
	}
	
	private void drawOptions() {
		//just the positioning of elements on the options pane
		GridBagConstraints gbc = new GridBagConstraints();
		Insets i = new Insets(5,5,5,5);
		gbc.insets = i;
		gbc.anchor = GridBagConstraints.NORTHWEST;
		gbc.weighty = 0.1;
		gbc.weightx = 0.5;
		gbc.gridx = 0;
		gbc.gridy = 0;
		
		JPanel jp = new JPanel(new GridBagLayout());	//adds the panel
		jp.setMaximumSize(new Dimension(500, 100));
		
		JLabel numQueens= new JLabel("Number of queens: ");
		JTextPane numQueensIn = new JTextPane();
		numQueensIn.setText(String.valueOf(this.q));
		numQueensIn.setFont(new Font("Verdana",Font.BOLD,12));
		
		JButton update = new JButton("Update");
		JButton random = new JButton("Randomise");
		JButton calc = new JButton("Calculate");
		
		JLabel currentTier = new JLabel("Current tier: ");
		tierNumber = new JLabel("N/A");
		JLabel numOfMoves = new JLabel("Number of moves: ");
		JLabel moves = new JLabel("N/A");
		JLabel timeLabel = new JLabel("Time taken:");
		JLabel timeTaken =  new JLabel("N/A");
		
		random.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				random();
				//reset the optoins panel
				calc.setEnabled(true);
				prev.setEnabled(false);
				next.setEnabled(false);
				tierNumber.setText("N/A");
				moves.setText("N/A");
				timeTaken.setText("N/A");
			}
		});
		
		update.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				resetPanel();
				try{
					//has to be a value between 4 and 10 otherwise show error
					if(numQueensIn.getText().length() > 0){
						if(Integer.parseInt(numQueensIn.getText()) > 3 && Integer.parseInt(numQueensIn.getText()) < 11) {
							drawBoard(Integer.parseInt(numQueensIn.getText()));
						} else {
							numQueensIn.setText("");
							JOptionPane.showMessageDialog(null, "Value must be between 4 and 10!", "Invalid!", JOptionPane.INFORMATION_MESSAGE);
						}
						//reset options panel and dont allow to press calculate on empty board
						calc.setEnabled(false);
						prev.setEnabled(false);
						next.setEnabled(false);
						tierNumber.setText("N/A");
						moves.setText("N/A");
						timeTaken.setText("N/A");
						
					}
				} catch(Exception e) {
					numQueensIn.setText("");
					JOptionPane.showMessageDialog(null, "Must be an integer!", "Invalid!", JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		
		calc.setEnabled(false);
		calc.addActionListener(new ActionListener(){
			//call the method to calculate the problem
			@Override
			public void actionPerformed(ActionEvent arg0) {
				try{
					if(numQueensIn.getText().length() > 0){
						if(Integer.parseInt(numQueensIn.getText()) > 3 && Integer.parseInt(numQueensIn.getText()) < 11) {
							startBFS();
							//displays the information about how long it took to solve etc.
							moves.setText(String.valueOf(index));
							tierNumber.setText(String.valueOf(path.size() - 1));
							timeTaken.setText(String.valueOf(end));
							//allows the user to navigate through the path now
							prev.setEnabled(true);
							next.setEnabled(true);
						} else {
							numQueensIn.setText("");
							JOptionPane.showMessageDialog(null, "Value must be between 4 and 10!", "Invalid!", JOptionPane.INFORMATION_MESSAGE);
						}
					}
				} catch(Exception e) {
					e.printStackTrace();
					numQueensIn.setText("");
					JOptionPane.showMessageDialog(null, "Must be an integer!", "Invalid!", JOptionPane.ERROR_MESSAGE);
				}
			}
		});

		//adding all the components
		jp.add(numQueens, gbc);
		
		gbc.gridx = 1;
		numQueensIn.setPreferredSize(new Dimension(35,25));
		
		jp.add(numQueensIn, gbc);		
		
		gbc.gridx = 2;
		gbc.gridwidth = 2;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		jp.add(update,gbc);
		
		gbc.gridy = 1;
		gbc.gridx = 0;
		jp.add(random,gbc);	
		
		gbc.gridx = 2;
		gbc.gridwidth = 2;
		jp.add(calc, gbc);
		
		gbc.gridwidth =1 ;
		gbc.fill = GridBagConstraints.NONE;
		gbc.gridx = 0;
		gbc.gridy = 2;
		jp.add(currentTier, gbc);
		
		gbc.gridx = 1;
		jp.add(tierNumber, gbc);
		
		gbc.gridx = 2;
		jp.add(numOfMoves, gbc);
		
		gbc.gridx = 3;
		jp.add(moves,gbc);
		
		gbc.gridx = 4;
		jp.add(timeLabel,gbc);
		
		gbc.gridx = 5;
		jp.add(timeTaken,gbc);
		
		this.add(jp);
	}

	//display the queens in a random pattern
	private void random() {
		removeQueens();
		for(int i = 0; i < this.q; i++) {
			Queen queen = new Queen(this.q);
			Random yPos = new Random();
			int y = yPos.nextInt(this.q);
			labels[y][i].setIcon(queen.getImage());
		}
		
		validate();
	}
	
	//remove all the queens on the board
	private void removeQueens() {
		for(int i = 0; i < this.q; i++) {
			for(int j = 0; j < this.q; j++) {
				labels[i][j].setIcon(null);
			}
		}
	}

	//reset the panel so we can change the size of the checker board
	private void resetPanel() {
		boardPanel.removeAll();
	}

	//draws the checker board
	private void drawBoard(int q) {
		this.q = q;
		boardPanel.setLayout(new GridLayout(this.q,this.q));
		boardPanel.setPreferredSize(new Dimension(600, 600));
		boardPanel.setBorder(BorderFactory.createLineBorder(Color.black));
		boardPanel.setBorder(new EmptyBorder(40, 40, 10, 40));
		
		labels = new JLabel[this.q][this.q];
	
		//this creates the black and white checkboard pattern
		for(int i = 0; i < this.q; i++){	
			for(int j = 0; j < this.q; j++){
				JLabel l = new JLabel();
				l.setOpaque(true);
				if(i % 2 == 0){
					if(j % 2 == 0){
						l.setBackground(Color.white);
					} else {
						l.setBackground(Color.black);
					}
				} else {
					if(j % 2 != 0){
						l.setBackground(Color.white);
					} else {
						l.setBackground(Color.black);
					}
				}
				labels[i][j] = l;
				boardPanel.add(l);
			}
		}
		validate();
		
	}
	
	//draws the previous and next buttons to explore the trace
	private void drawTrace(){
		JPanel tracePanel = new JPanel(new GridBagLayout());
		tracePanel.setMaximumSize(new Dimension(300,100));
		tracePanel.setBorder(new EmptyBorder(0, 0, 20, 0));
		
		
		prev = new JButton("Previous");
		prev.setPreferredSize(new Dimension(150, 30));
		prev.setEnabled(false);
		next = new JButton("Next");
		next.setPreferredSize(new Dimension(150, 30));
		next.setEnabled(false);
		
		tracePanel.add(prev);
		tracePanel.add(next);
		
		//allows you to go towards the beginning of the inital state
		prev.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if(showing > 0){
					showing--;
					updateBoard(path.get(showing));
					//update the current tier number
					if(showing != 0){
						tierNumber.setText(String.valueOf(showing));
					} else {
						tierNumber.setText("Initial State");
					}
				}
			}
			
		});
		
		//allows you to go towards the end of the state
		next.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				if(showing < path.size() - 1){
					showing++;
					updateBoard(path.get(showing));
					//update the current tier number
					tierNumber.setText(String.valueOf(showing));
				}
			}
			
		});
		
		this.add(tracePanel);
	}
	
	private void startBFS() {
		index = 0;
		found = false;
		//set the initial state to the random one from the board
		state = new State(this.q);
		state.setParentState(-1);
		getInitState();
		
		ArrayList<State> s = new ArrayList<>();
		
		s.add(state);	//add the initial state to the 
		
		int pState = 0;	//parent state initial value
		
		double start = System.currentTimeMillis();	//starts the timer
		
		while(!found){	//coninue until the GOAL STATE
			for(int i = 0; i < this.q; i++) {	//loop through the cols
				s.add(getBestPos(i, s.get(pState), pState));	
				if(found) { break; }	//if found break out of this loop
			}
			pState++;	//increment which parent state it is
		}

		end = (System.currentTimeMillis() - start)/1000;	//end the timer
		updateBoard(s.get(index));	//update the ui
		int pos = index;
		
		//this creates the path for the trace
		path = new ArrayList<>();
		while(pos != -1) {
			path.add(s.get(pos));
			pos = s.get(pos).getParentState();
		}
		showing = path.size() - 1;
		Collections.reverse(path);
	}

	private State getBestPos(int col, State prevState, int pState) {
		int low = 100;
		
		//deep copy the parent state
		State s = new State(this.q);
		s = copyState(s, prevState);
		s.setParentState(pState);
		
		int yPos = 0;
		
		for(int i = 0; i < this.q; i++) {	//goes down the row
			int temp = s.conflictsForSpace(i,col);	//gets the value of the conflict for that row and col
			if(temp < low) { low = temp; yPos = i;}	//move it to the first place with the lowest conflict
		}
		
		s.clearCol(col);
		s.changeState(yPos, col, true);
		
		found = !s.hasConflicts();	
		index++;
		
		return s;
	}	
	
	//copies the parent state pattern and moves it into the new state
	private State copyState(State newState, State oldState) {
		for(int i = 0; i < this.q; i++) {
			for(int j = 0; j < this.q; j++) {
				if(oldState.getState()[i][j]){
					newState.changeState(i, j, true);
				}
			}
		}
		return newState;
	}

	
	//gets the initial state of the boards
	private void getInitState() {
		for(int i = 0; i < this.q; i++) {
			for(int j = 0; j < this.q; j++) {
				if(labels[i][j].getIcon() != null) {
					state.changeState(i, j, true);
				}
			}
		}
	}
	
	//updates the board at the end showing the positions of the queens
	private void updateBoard(State s) {
		removeQueens();
		for(int i = 0; i < this.q; i++) {
			for(int j = 0; j < this.q; j++) {
				if(s.getState()[i][j]) {
					Queen queen = new Queen(this.q);
					labels[i][j].setIcon(queen.getImage());
				}
			}
		}
	}
}
