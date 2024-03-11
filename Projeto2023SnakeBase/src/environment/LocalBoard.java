package environment;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;


import game.*;

/** Class representing the state of a game running locally
 * 
 * @author luismota
 *
 */
public class LocalBoard extends Board implements Serializable{
	
	private static final int NUM_SNAKES = 2;
	private static final int NUM_OBSTACLES = 5;//25
	private static final int NUM_SIMULTANEOUS_MOVING_OBSTACLES = 3;

	//private ExecutorService pool = Executors.newFixedThreadPool(NUM_SIMULTANEOUS_MOVING_OBSTACLES);

	public LocalBoard() {
		for (int i = 0; i < NUM_SNAKES; i++) {
			AutomaticSnake snake = new AutomaticSnake(i, this);
			snakes.add(snake);
		}

		addObstacles(NUM_OBSTACLES);

		Goal goal=addGoal();
//		System.err.println("All elements placed");
	}

	//Cria a pool de threads para os obstaculos
	/*private void createObstacleThreadPool() {
		List<Obstacle> availableObstacles = getAvailableObstacles();

		for (Obstacle o : availableObstacles) {
			ObstacleMover obstacleMover = new ObstacleMover(o, this);
			pool.submit(obstacleMover);
		}
		pool.shutdown();
	}*/

	//Adiciona os obstaculos que ainda têm movimentos disponiveis
	private List<Obstacle> getAvailableObstacles() {
		List<Obstacle> availableObstacles = new ArrayList<>();
		for (Obstacle o : obstacles) {
			if (o.getRemainingMoves() > 0) {
				availableObstacles.add(o);
			}
		}
		return availableObstacles;
	}

	public void init() {
		for(Snake s : snakes)
			s.start();
		// TODO: launch other threads
		//createObstacleThreadPool();
        setChanged();
	}

	@Override
	public void handleKeyPress(int keyCode) {
		// do nothing... No keys relevant in local game
	}

	@Override
	public void handleKeyRelease() {
		// do nothing... No keys relevant in local game
	}
}
