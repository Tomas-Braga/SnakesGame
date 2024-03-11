package gui;

import java.io.IOException;
import environment.LocalBoard;
import game.Server;

public class Main {
	public static void main(String[] args) throws IOException {
		LocalBoard board=new LocalBoard();
		/*SnakeGui game = new SnakeGui(board,600,0);
		game.init();*/
		// Launch server
		// TODO
		Server server = new Server(board);
		//Server server2 = new Server();
	}
}
