package bankserver;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class BankClientServer implements Runnable {
    private int port;

    public BankClientServer(int port) {
        this.port = port;
    }

    @Override
    public void run() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            while (true) {
                Socket socket = serverSocket.accept();
                new Thread(new BankClientHandler(socket)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new Thread(new BankClientServer(5555)).start();
    }
}
