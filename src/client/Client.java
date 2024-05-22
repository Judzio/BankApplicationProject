package client;


import java.net.*;
import java.io.*;

public class Client {

    public static void main(String args[]) {
        String host = "localhost";
        int port = 5555;
        //Inicjalizacja gniazda klienckiego
        Socket clientSocket = null;
        try {
            clientSocket = new Socket(host, port);
        } catch (UnknownHostException e) {
            System.out.println("Nieznany host.");
            System.exit(-1);
        } catch (ConnectException e) {
            System.out.println("Połączenie odrzucone.");
            System.exit(-1);
        } catch (IOException e) {
            System.out.println("Błąd wejścia-wyjścia: " + e);
            System.exit(-1);
        }
        System.out.println("Połączono z " + clientSocket);

        //Deklaracje zmiennych strumieniowych
        String response = null;
        String line = null;
        BufferedReader brSockInp = null;
        BufferedReader brLocalInp = null;
        DataOutputStream out = null;

        //Utworzenie strumieni
        try {
            out = new DataOutputStream(clientSocket.getOutputStream());
            brSockInp = new BufferedReader(
                    new InputStreamReader(
                            clientSocket.getInputStream()));
            brLocalInp = new BufferedReader(
                    new InputStreamReader(System.in));
        } catch (IOException e) {
            System.out.println("Błąd przy tworzeniu strumieni: " + e);
            System.exit(-1);
        }
        String textBlockStart = """
                
                Welcome to your Bank Application!
                Choose your operation:
                1. LOGIN
                2. REGISTER
                3. QUIT
                """;
        System.out.println(textBlockStart);
        //Pętla główna klienta
        while (true) {
            try {
                response = brLocalInp.readLine();
                if (response != null) {
                    out.writeBytes(response + '\n');
                    out.flush();
                }
                if (response == null || "quit".equalsIgnoreCase(response)) {
                    System.out.println("Closing...");
                    clientSocket.close();
                    System.exit(0);
                }
                line = brSockInp.readLine();
                if(line.contains("starterGUI") || line.contains("loggedInGUI")){
                    printGUI(line);
                }
                if(!line.contains("starterGUI") && !line.contains("loggedInGUI")) {
                    System.out.print(line + '\n');
                }
            } catch (IOException e) {
                System.out.println("Błąd wejścia-wyjścia: " + e);
                System.exit(-1);
            }
        }
    }
    private static void printGUI (String command){
        switch(command){
            case "starterGUI":
                String textBlockStart = """
                
                Welcome to your Bank Application!
                Choose your operation:
                1. LOGIN
                2. REGISTER
                3. QUIT
                """;
                System.out.println(textBlockStart);
                break;
            case "loggedInGUI":
                String textBlockLogged = """
                        
                        Choose your operations:
                         1. BALANCE
                         2. ADD MONEY
                         3. WITHDRAW MONEY
                         4. TRANSFER MONEY
                         5. LOGOUT
                        """;
                System.out.println(textBlockLogged);
                break;
        }
    }
}
