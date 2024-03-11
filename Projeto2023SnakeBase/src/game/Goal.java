package game;

import environment.Board;
import environment.LocalBoard;

import java.io.Serializable;

public class Goal extends GameElement {
	private int value=1;
	private Board board;
	public static final int MAX_VALUE=9;

	public Goal( Board board2) {
		this.board = board2;
	}
	
	public int getValue() {
		return value;
	}
	public void incrementValue() throws InterruptedException {
		//TODO
		if(value<MAX_VALUE) {
			value++;
		}
	}

	public int captureGoal() {
		//TODO
		if (value == MAX_VALUE) {
			return 1;
		}
		return -1;
	}
}
