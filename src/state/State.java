package state;
/*
 * Class: State
 * Stores the position of every queen in a 2d array
 * Calculates the conflict of a specific state or,
 * tests if there are any conflicts of some sort
 * Has the ability to move the queens to the place of least conflicts
 */
public class State {
	private boolean[][] state;	//what the board looks like
	private int parentState;	//index of the parent
	private int q;				//size of board
	
	//constructor
	public State(int q) {
		this.q = q;
		state = new boolean[q][q];
		for(int i = 0; i < q; i++) {
			for(int j = 0; j < q; j++) {
				state[i][j] = false;
			}
		}
	}
	
	
	//getter functions
	public boolean[][] getState() { return this.state; }
	public int getParentState() { return this.parentState; }
	
	//setter functions
	public void setState(boolean[][]state) {
		this.state = state;
	}
	public void setParentState(int parentState) {
		this.parentState = parentState;
	}
	
	public void changeState(int x, int y, boolean b) {
		this.state[x][y] = b;	//add or take away a queen from position(x,y)
	}
		//clears the chosen column from all queens
	public void clearCol(int col) {
		for(int i = 0; i < this.q; i++) {
			this.state[i][col] = false;
		}
	}
	
	//calculation functions
	//if state has conflicts return true else return false
	public boolean hasConflicts() {
		for(int i = 0; i < this.q; i++){
			if(horizontalConflicts(i)){
				return true;
			} 
		}
		if(diagonalConflicts() > 0){
			return true;
		}
		return false;
	}
	
	//return the number of conflicts for a specific place
	public int conflictsForSpace(int row, int col) {
		int total = 0;
		total = lateralConflicts(row,col);
		total += getRightLeftConflicts(row,col);
		total += getLeftRightConflicts(row,col);
		return total;
	}
	
	//return true as soon as there is one conflict
	private int diagonalConflicts() {
		for(int i = 0; i < this.state.length; i++) {
			for(int j = 0; j < this.state.length; j++) {
				if(state[i][j] == true) {
					if(getRightLeftConflicts(i,j) > 0) return 1;
					if(getLeftRightConflicts(i,j) > 0) return 1;
				}
			}
		}
		return 0;
	}
	
	private int getRightLeftConflicts(int row, int col) {
		int yRow, xCol;
		//get the edge piece that we start from and then go diagonal from there
		if(row < col) {
			xCol = col-row;
			yRow = 0;
		} else {
			yRow = row-col;
			xCol = 0;
		}
		
		int count = 0;
		boolean left = false;
		boolean right = false;
		
		//ladder down
		for(int i = 0; i < this.q; i++) {
			if(yRow + i < this.q && xCol + i < this.q){	//keep going across until we have met the other edge piece
				if(this.state[yRow+i][xCol+i] == true && (xCol+i) != col) {	//ignore the column with the queen in, 
																			//and only count when there is a queen
					if(xCol+i < col && !left){ count++; left = true;  }		//only count if on the left or right hand side 
					if(xCol+i > col && !right) { count++; right = true; }	//of the space has not been counted
				}															//so maximum is 2 conflicts diagonally
			}
		}
		return count;
	}
	//same as previous one BUT go diagonally in the other direction
	private int getLeftRightConflicts(int row, int col) {
		int yRow, xCol;
		//get the edge piece that we start from and then go diagonal from there
		if((row + col) < this.q) {
			xCol = row + col;
			yRow = 0;
		} else {
			xCol = this.q - 1;
			yRow = col + row - (this.q - 1);
		}
		int count = 0;
		boolean left = false;
		boolean right = false;
		//ladder down
		for(int i = 0; i < this.q; i++) {
			if((yRow + i) < this.q && (xCol - i) >= 0) {
				if(state[yRow + i][xCol - i] == true && (xCol-i) != col) {
					if((xCol-i) < col && !left){ count++; left = true;}
					if((xCol-i) > col && !right) { count++; right = true;}
				}
			} else {
				break;
			}
		}
		return count;
	}
	
	//return true as soon there is more than one queen in a row
	private boolean horizontalConflicts(int y) {
		int count = 0;
		for(int i = 0; i < this.state.length; i++) {
				if(state[y][i] == true){
					count++;
				}
			}
		if(count > 1)return true;
		else return false;
	}
	
	//return the number of conflicts in said row
	private int lateralConflicts(int row, int col) {
		int count = 0;
		boolean left = false;
		boolean right = false;
		for(int i = 0; i < this.q; i++) {
			if(state[row][i] == true && i != col) {	//ignore the col that we are checkign for
				if(i < col && !left){ count++; left = true;}	//same as previously only count once for 
				if(i > col && !right){ count++; right = true;}	//left or right hand side of the queen
			}													//so maximum is 2 conflicts horizontally
		}
		return count;
	}
	
}
