package remote;

import environment.Board;
import environment.BoardPosition;
import environment.Cell;
import game.AutomaticSnake;
import gui.SnakeGui;

import javax.swing.*;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

/** Remore client, only for part II
 * 
 * @author luismota
 *
 */

public class Client {
	private ObjectInputStream in;
	private ObjectOutputStream out;
	private Socket socket;
	private RemoteBoard remoteBoard;

	public Client() {
		remoteBoard = new RemoteBoard();
	}

	public static void main(String[] args) {
		// TODO
		String endereco = args[0];
		int porto = Integer.parseInt(args[1]);

		new Client().runClient(endereco, porto);

	}

	private void runClient(String endereco, int porto) {
		try {
			connectToServer(endereco, porto);

			Thread receberAtualizacoes = new Thread(this::receiveGameInfo);
			receberAtualizacoes.start();

			Thread enviarComandos = new Thread(this::sendCommands);
			enviarComandos.start();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private void receiveGameInfo() {
		SnakeGui game = new SnakeGui(remoteBoard, 600, 0);
		game.init();
		try {
			Object objetoRecebido;
			while ((objetoRecebido = in.readObject()) != null) {
					remoteBoard.clearBoard();

					//System.out.println("Recebeu: " + objetoRecebido);

					remoteBoard.updateBoard((Board) objetoRecebido);
					//System.out.println("Feito update do board");

					remoteBoard.setChanged();
			}
		} catch(IOException | ClassNotFoundException e){
			e.printStackTrace();
		}
	}

	private void sendCommands () {
		try {
			BoardPosition nextPosition;
			while ((nextPosition = remoteBoard.getNextPositionToMove()) != null) {
			//while (true) {
				//BoardPosition nextPosition = remoteBoard.getNextPositionToMove();
				System.out.println("Sending: " + nextPosition);
				out.writeObject(nextPosition);
				out.flush();
				//out.reset();
				Thread.sleep(remoteBoard.REMOTE_REFRESH_INTERVAL);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void connectToServer (String end,int porto){
		try {
			InetAddress endereco = InetAddress.getByName(end);
			socket = new Socket(endereco, porto);
			in = new ObjectInputStream(socket.getInputStream());
			out = new ObjectOutputStream(socket.getOutputStream());
			System.out.println("Connection established with " + socket.getInetAddress());
		} catch (IOException e) {
			e.printStackTrace();
			System.err.println("Connection refused");
		}
	}
}
