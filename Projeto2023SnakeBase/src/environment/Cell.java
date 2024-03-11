package environment;

import java.io.Serializable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import game.GameElement;
import game.Goal;
import game.Obstacle;
import game.Snake;

/** Main class for game representation.
 * 
 * @author luismota
 *
 */
public class Cell implements Serializable{
	private BoardPosition position;
	private Snake ocuppyingSnake = null;
	private GameElement gameElement=null;

	private Lock lock = new ReentrantLock();
	private Condition recursoOcupado = lock.newCondition();

	private Lock lock2 = new ReentrantLock();
	private Condition recursoOcupado2 = lock2.newCondition();

	public GameElement getGameElement() {
		return gameElement;
	}

	public Cell(BoardPosition position) {
		super();
		this.position = position;
	}

	public BoardPosition getPosition() {
		return position;
	}

	public void request(Snake snake) throws InterruptedException {
		//TODO coordination and mutual exclusion
		lock.lock();
		try {
			while(isOcupied() && ocuppyingSnake!=snake) {
				recursoOcupado.await();
			}
			ocuppyingSnake=snake;
		} finally {
			lock.unlock();
		}
	}

	public void release() {
		//TODO
		lock.lock();
		try {
			ocuppyingSnake=null;
			recursoOcupado.signalAll();
		} finally {
			lock.unlock();
		}
		ocuppyingSnake=null;
	}

	public boolean isOcupiedBySnake() {
		return ocuppyingSnake!=null;
	}

	public void setGameElement(GameElement element) {
		// TODO coordination and mutual exclusion
		lock2.lock();
		try {
			while(isOcupied()) {
				recursoOcupado2.await();
			}
			gameElement=element;
			recursoOcupado2.signalAll();
		} catch (InterruptedException e) {
			e.printStackTrace();
        } finally {
			lock2.unlock();
		}
	}

	public boolean isOcupied() {
		return isOcupiedBySnake() || (gameElement!=null && gameElement instanceof Obstacle);
	}

	public Snake getOcuppyingSnake() {
		return ocuppyingSnake;
	}

	public void removeSnake() {
		ocuppyingSnake = null;
	}

	public Goal removeGoal() {
		// TODO
		if (gameElement instanceof Goal) {
			Goal goal = (Goal) gameElement;
			gameElement = null;
			return goal;
		}
		return null;
	}

	public void removeObstacle() {
		//TODO
		if (gameElement instanceof Obstacle) {
			gameElement = null;
		}
	}

	public Goal getGoal() {
		return (Goal)gameElement;
	}

	public boolean isOcupiedByGoal() {
		return (gameElement!=null && gameElement instanceof Goal);
	}

	public Snake getSnake() {
		return ocuppyingSnake;
	}
}
