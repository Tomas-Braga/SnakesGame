package game;

import java.io.Serializable;
import java.util.LinkedList;

import environment.Board;
import environment.BoardPosition;
import environment.Cell;
/** Base class for representing Snakes.
 * Will be extended by HumanSnake and AutomaticSnake.
 * Common methods will be defined here.
 * @author luismota
 *
 */
public abstract class Snake extends Thread implements Serializable{
	protected LinkedList<Cell> cells = new LinkedList<Cell>();
	protected int size = 5;
	private int id;
	private Board board;

	public Snake(int id,Board board) {
		this.id = id;
		this.board=board;
	}

	public int getSize() {
		return size;
	}

	public int getIdentification() {
		return id;
	}

	public int getLength() {
		return cells.size();
	}
	
	public LinkedList<Cell> getCells() {
		return cells;
	}

	public void move(Cell cell) throws InterruptedException {
		// TODO
		if(cell.isOcupiedByGoal() && cell.getGoal().captureGoal() == 1) {
			board.setFinished(true);
			for(Snake s : board.getSnakes()) {
				s.interrupt();
			}
			System.out.println("Snake "+getIdentification()+" venceu o jogo!");
		}

		if(cell.isOcupiedByGoal() && cell.getGoal().captureGoal() == -1) {
			Goal goal = cell.removeGoal();
			size+=goal.getValue();
			goal.incrementValue();
			board.addGameElement(goal);

		}

		if(size > 5) {
			cells.addFirst(cell);
			size--;
		}

		cell.request(this);
		cells.addFirst(cell);
		cells.getLast().release();
		cells.removeLast();
		board.setChanged();
	}

	public LinkedList<BoardPosition> getPath() {
		LinkedList<BoardPosition> coordinates = new LinkedList<BoardPosition>();
		for (Cell cell : cells) {
			coordinates.add(cell.getPosition());
		}
		return coordinates;
	}

	protected void doInitialPositioning() {
		// Random position on the first column. 
		// At startup, snake occupies a single cell
		int posX = 0;
		int posY = (int) (Math.random() * Board.NUM_ROWS);
		BoardPosition at = new BoardPosition(posX, posY);
		try {
			board.getCell(at).request(this);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		cells.add(board.getCell(at));
		System.err.println("Snake "+getIdentification()+" starting at:"+getCells().getLast());
		/*try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}*/
	}
	
	public Board getBoard() {
		return board;
	}

	public void setCells(LinkedList<Cell> cells) {
		this.cells.clear();
		this.cells = cells;
	}
}
