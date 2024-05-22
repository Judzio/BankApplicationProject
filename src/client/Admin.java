package client;


import java.net.*;
import java.io.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.ExecutionException;

public class Admin {

    public static void main(String args[]) {
        String host = "localhost";
        int port = 6666;
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
                
                Bank Admin Manager Application
                Choose your operation:
                1. LOGIN
                2. QUIT
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
                if(line.contains("starterGUI") || line.contains("loggedInGUI") || line.contains("clientListGUI")){
                    printGUI(line);
                }
                if(!line.contains("starterGUI") && !line.contains("loggedInGUI") && !line.contains("clientListGUI")) {
                    System.out.print(line + '\n');
                }
            } catch (IOException | SQLException e) {
                System.out.println("Błąd wejścia-wyjścia: " + e);
                System.exit(-1);
            }
        }
    }
    private static void printGUI (String command) throws SQLException {
        switch(command){
            case "starterGUI":
                String textBlockStart = """
                
                Bank Admin Manager Application
                Choose your operation:
                1. LOGIN
                2. QUIT
                """;
                System.out.println(textBlockStart);
                break;
            case "loggedInGUI":
                String textBlockLogged = """
                        
                        Choose your operations:
                         1. ADD CLIENT
                         2. CLIENT LIST
                         3. MODIFY CLIENT
                         4. DELETE CLIENT
                         5. LOGOUT
                        """;
                System.out.println(textBlockLogged);
                break;
            case "clientListGUI":
                String query = "SELECT * FROM account";
                ResultSet resultSet = DataStorage.statement.executeQuery(query);
                while (resultSet.next()) {
                    String login = resultSet.getString("login");
                    String name = resultSet.getString("name");
                    String surname =resultSet.getString("surname");
                    String pesel = resultSet.getString("pesel");
                    String transfer_number = resultSet.getString("transfer_number");
                    double balance = resultSet.getDouble("balance");

                    System.out.println("Login: " + login + ", Name: " + name + ", Surname: "
                            + surname + ", PESEL: " + pesel + ", Transfer Number: " + transfer_number
                            + ", Balance: " + balance);
                }
                System.out.println();
                System.out.println("Press ENTER to continue: ");
                break;
        }
    }
}
