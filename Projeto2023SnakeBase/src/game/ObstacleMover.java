package game;

import environment.Board;
import environment.LocalBoard;

public class ObstacleMover extends Thread {
	private Obstacle obstacle;
	private Board board;
	
	public ObstacleMover(Obstacle obstacle, Board board) {
		super();
		this.obstacle = obstacle;
		this.board = board;
	}

	@Override
	public void run() {
		// TODO
		while(obstacle.getRemainingMoves()>0) {
			try {
				Thread.sleep(obstacle.OBSTACLE_MOVE_INTERVAL);
				board.getCellByObstacle(obstacle).removeObstacle();
				obstacle.decrementRemainingMoves();
				board.addGameElement(obstacle);
				board.setChanged();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
