package bankserver;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class BankAdminServer implements Runnable {
    private int port;

    public BankAdminServer(int port) {
        this.port = port;
    }

    @Override
    public void run() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            while (true) {
                Socket socket = serverSocket.accept();
                new Thread(new BankAdminHandler(socket)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new Thread(new BankAdminServer(6666)).start();
    }
}
