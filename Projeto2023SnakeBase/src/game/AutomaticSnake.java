package game;

import environment.Board;
import environment.Cell;
import environment.LocalBoard;
import environment.BoardPosition;

import java.util.List;


public class AutomaticSnake extends Snake {
	public AutomaticSnake(int id, Board board) {
		super(id,board);
	}

	@Override
	public void run() {
		doInitialPositioning();

        System.err.println("initial size:" + cells.size());
		try {
			cells.getLast().request(this);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//TODO: automatic movement
		while (true) {
			try {
				Cell nexPos = getNearPositionForGoal();
				while (!isPathBlocked(nexPos.getPosition())) {
					move(nexPos);
				}

				Thread.sleep(getBoard().PLAYER_PLAY_INTERVAL);

			} catch (InterruptedException e) {
				if (!getBoard().getFinished()) {
					Cell next = changeDirection();
					try {
						move(next);
					} catch (InterruptedException ex) {
					}
				}
				//se receber um interrupt da classe snake, interrompe o while e sai do método run
				if (getBoard().getFinished()) {
					break;
				}
			}
		}
	}

	//Escolhe a posição mais próxima do objetivo
	public Cell getNearPositionForGoal() {
		Cell head = cells.getFirst();
		List<BoardPosition> neighbors = getBoard().getNeighboringPositions(head);
		BoardPosition goal = getBoard().getGoalPosition();
		BoardPosition near = neighbors.get(0);
		double distance = near.distanceTo(goal);
		for (BoardPosition neighbor : neighbors) {
			if (!getBoard().getCell(neighbor).isOcupiedBySnake() && getBoard().getCell(neighbor).getOcuppyingSnake() == null && neighbor.distanceTo(goal) < distance) {
				near = neighbor;
				distance = neighbor.distanceTo(goal);
			}
		}
		return getBoard().getCell(near);
	}

	public Cell changeDirection() {
		Cell head = cells.getFirst();
		List<BoardPosition> neighbors = getBoard().getNeighboringPositions(head);
		BoardPosition goal = getBoard().getGoalPosition();
		BoardPosition near = neighbors.get(0);
		double distance = near.distanceTo(goal);
		for (BoardPosition neighbor : neighbors) {
			if (!isPathBlocked(neighbor) && !getBoard().getCell(neighbor).isOcupiedBySnake() && neighbor.distanceTo(goal) < distance) {
				near = neighbor;
				distance = neighbor.distanceTo(goal);
			}
		}
		return getBoard().getCell(near);
	}

	public boolean isPathBlocked(BoardPosition pos) {
		if(getBoard().getCell(pos).isOcupied()) {
			return true;
		}
		return false;
	}
}
