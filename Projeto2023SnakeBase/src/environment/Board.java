package environment;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Observable;

import game.*;

public abstract class Board extends Observable implements Serializable{
	protected Cell[][] cells;
	private BoardPosition goalPosition;
	public static final long PLAYER_PLAY_INTERVAL = 200;//100
	public static final long REMOTE_REFRESH_INTERVAL = 300;//200
	public static final int NUM_COLUMNS = 30;
	public static final int NUM_ROWS = 30;
	protected LinkedList<Snake> snakes = new LinkedList<Snake>();
	protected LinkedList<Obstacle> obstacles= new LinkedList<Obstacle>();
	protected boolean isFinished = false;

	public Board() {
		cells = new Cell[NUM_COLUMNS][NUM_ROWS];
		for (int x = 0; x < NUM_COLUMNS; x++) {
			for (int y = 0; y < NUM_ROWS; y++) {
				cells[x][y] = new Cell(new BoardPosition(x, y));
			}
		}
	}

	//Define se o jogo chegou ao fim
	public void setFinished(boolean finished) {
		isFinished = finished;
	}

	public boolean getFinished() {
		return isFinished;
	}

	public Cell getCell(BoardPosition cellCoord) {
		return cells[cellCoord.x][cellCoord.y];
	}

	public BoardPosition getRandomPosition() {
		return new BoardPosition((int) (Math.random() *NUM_ROWS),(int) (Math.random() * NUM_ROWS));
	}

	public BoardPosition getGoalPosition() {
		return goalPosition;
	}

	public void setGoalPosition(BoardPosition goalPosition) {
		this.goalPosition = goalPosition;
	}
	
	public void addGameElement(GameElement gameElement) {
		boolean placed=false;
		while(!placed) {
			BoardPosition pos=getRandomPosition();
			if(!getCell(pos).isOcupied() && !getCell(pos).isOcupiedByGoal()) {
				getCell(pos).setGameElement(gameElement);
				if(gameElement instanceof Goal) {
					setGoalPosition(pos);
//					System.out.println("Goal placed at:"+pos);
				}else {
					//Apenas coloca a posição para os obstaculos
					gameElement.setPosition(pos);
				}
				placed=true;
			}
		}
	}

	public List<BoardPosition> getNeighboringPositions(Cell cell) {
		ArrayList<BoardPosition> possibleCells=new ArrayList<BoardPosition>();
		BoardPosition pos=cell.getPosition();
		if(pos.x>0)
			possibleCells.add(pos.getCellLeft());
		if(pos.x<NUM_COLUMNS-1)
			possibleCells.add(pos.getCellRight());
		if(pos.y>0)
			possibleCells.add(pos.getCellAbove());
		if(pos.y<NUM_ROWS-1)
			possibleCells.add(pos.getCellBelow());
		return possibleCells;

	}

	protected Goal addGoal() {
		Goal goal=new Goal(this);
		addGameElement( goal);
		return goal;
	}

	protected void addObstacles(int numberObstacles) {
		// clear obstacle list , necessary when resetting obstacles.
		getObstacles().clear();
		while(numberObstacles>0) {
			Obstacle obs=new Obstacle(this);
			addGameElement(obs);
			getObstacles().add(obs);
			numberObstacles--;
		}
	}
	
	public LinkedList<Snake> getSnakes() {
		return snakes;
	}

	@Override
	public void setChanged() {
		super.setChanged();
		notifyObservers();
	}

	public LinkedList<Obstacle> getObstacles() {
		return obstacles;
	}

	public Cell getCellByObstacle(Obstacle obs) {
		for(int i=0;i<NUM_COLUMNS;i++) {
			for(int j=0;j<NUM_ROWS;j++) {
				if(cells[i][j].getGameElement()==obs) {
					return cells[i][j];
				}
			}
		}
		return null;
	}

	public abstract void init(); 
	
	public abstract void handleKeyPress(int keyCode);

	public abstract void handleKeyRelease();

	public void addSnake(Snake snake) {
		snakes.add(snake);
	}

	//Usado para resetar a RemoteBoard
	public void clearBoard() {
		snakes.clear();
		for(int i=0;i<NUM_COLUMNS;i++) {
			for(int j=0;j<NUM_ROWS;j++) {
				if(cells[i][j].isOcupiedByGoal()) {
					cells[i][j].removeGoal();
				}
			}
		}
	}

	//Usado para colocar o goal e os obstaculos do RemoteBoard
	public void setGameElement(BoardPosition pos, GameElement gameElement) {
		cells[pos.x][pos.y].setGameElement(gameElement);
	}
}