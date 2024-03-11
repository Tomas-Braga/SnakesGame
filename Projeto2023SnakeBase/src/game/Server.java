package game;

import environment.Board;
import environment.BoardPosition;
import environment.LocalBoard;
import gui.SnakeGui;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server {
	// TODO
    private static final int PORTO = 8080;
    private static final String HOST = "localhost";
    private Board remoteBoard;

    private List<ClientHandler> clientHandlers;

    public Server(Board board) throws IOException {
        clientHandlers = new ArrayList<>();
        remoteBoard = board;

        startServing();
    }

    public void startServing() throws IOException {
        ServerSocket serverSocket = new ServerSocket(PORTO);
        System.out.println("Server is running. Waiting for clients...");

        SnakeGui game = new SnakeGui(remoteBoard,600,0);
        game.init();

        try {
            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("New client connected: " + clientSocket);
                ClientHandler clientHandler = new ClientHandler(clientSocket);
                clientHandlers.add(clientHandler);
                new Thread(clientHandler).start();
            }
        } finally {
            serverSocket.close();
        }
    }

    private class ClientHandler implements Runnable {
        private ObjectOutputStream out;
        private ObjectInputStream in;
        private HumanSnake snake;

        public ClientHandler(Socket clientSocket) throws IOException {
            doConnection(clientSocket);
            System.out.println("Connection established with " + clientSocket);
            snake = new HumanSnake(remoteBoard.getSnakes().size(), remoteBoard);
            remoteBoard.addSnake(snake);
            snake.start();
        }

        @Override
        public void run() {
            while (true) {
                try {
                    serve();
                } catch (Exception e) {
                    e.printStackTrace();
                    //break;
                }
            }
        }

        private void serve() {
            try {
                while (true) {
                    out.writeObject(remoteBoard);
                    out.flush();
                    out.reset();
                    //System.out.println("Sent: " + remoteBoard);


                    Object nextPosition = in.readObject();
                    System.out.println("Received next move: " + (BoardPosition)nextPosition);
                    //snake.move(remoteBoard.getCell((BoardPosition) nextPosition));
                    Thread.sleep(remoteBoard.REMOTE_REFRESH_INTERVAL);
                }
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        private void doConnection (Socket clientSocket) throws IOException {
            out = new ObjectOutputStream(clientSocket.getOutputStream());
            in = new ObjectInputStream(clientSocket.getInputStream());
        }
    }
}
