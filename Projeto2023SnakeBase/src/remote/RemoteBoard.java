package remote;

import java.awt.event.KeyEvent;
import java.io.Serializable;
import java.rmi.server.RMIClassLoader;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import environment.Board;
import environment.BoardPosition;
import environment.Cell;
import environment.LocalBoard;
import game.*;

import javax.swing.*;

/** Remote representation of the game, no local threads involved.
 * Game state will be changed when updated info is received from Srver.
 * Only for part II of the project.
 * @author luismota
 *
 */
public class RemoteBoard extends Board {

	private HumanSnake humanSnake;
	private boolean aux = true;
	private BoardPosition nextPositionToMove = new BoardPosition(0,0);

    public RemoteBoard() {
		super();
    }

	public void updateBoard(Board receivedBoard) {
		//System.out.println("Remote: Inside updateBoard()");
		//clearBoard();
		//System.out.println("Remote: After resetBoard()");

		for (Snake receivedSnake : receivedBoard.getSnakes()) {
			if (receivedSnake instanceof HumanSnake){
				if (!getSnakes().contains(receivedSnake)) {
					humanSnake = new HumanSnake(receivedSnake.getIdentification(), this);
					humanSnake.setCells(receivedSnake.getCells());
					addSnake(humanSnake);
					nextPositionToMove = humanSnake.getCells().getFirst().getPosition();
				}
			}
			if (!getSnakes().contains(receivedSnake)) {
				AutomaticSnake localSnake = new AutomaticSnake(receivedSnake.getIdentification(), this);
				localSnake.setCells(receivedSnake.getCells());
				addSnake(localSnake);
			}
		}

		if (getGoalPosition() != receivedBoard.getGoalPosition()) {
			BoardPosition goalPosition = receivedBoard.getGoalPosition();
			setGameElement(goalPosition, receivedBoard.getCell(goalPosition).getGameElement());
		}

		//atualiza os obstaculos da RemoteBoard com os obstaculos da receivedBoard
		if (aux) {
			for (Obstacle receivedObstacle : receivedBoard.getObstacles()) {
				if (!getObstacles().contains(receivedObstacle)) {
					Obstacle localObstacle = new Obstacle(this);
					localObstacle.setPosition(receivedObstacle.getPosition());
					setGameElement(receivedObstacle.getPosition(), receivedObstacle);
				}
			}
			aux = false;
		}
		//setChanged();
	}

		@Override
		public void handleKeyPress (int keyCode){
			//TODO
			BoardPosition currentPosition = humanSnake.getCells().getFirst().getPosition();
			BoardPosition newPosition = null;

			switch (keyCode) {
				case KeyEvent.VK_UP:
					newPosition = currentPosition.getCellAbove();
					break;
				case KeyEvent.VK_DOWN:
					newPosition = currentPosition.getCellBelow();
					break;
				case KeyEvent.VK_LEFT:
					newPosition = currentPosition.getCellLeft();
					break;
				case KeyEvent.VK_RIGHT:
					newPosition = currentPosition.getCellRight();
					break;
			}

			if (newPosition != null && !getCell(newPosition).isOcupied()) {
					nextPositionToMove = newPosition;
			} else {
				//newPosition = currentPosition;
				nextPositionToMove = currentPosition;
			}
		}


	@Override
	public void handleKeyRelease() {
		// TODO
	}

	@Override
	public void init() {
		// TODO
	}

	public Board getBoard() {
		return this;
	}

	public BoardPosition getNextPositionToMove() {
		return nextPositionToMove;
	}
}