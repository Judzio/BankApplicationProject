package bankserver;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.sql.ResultSet;
import java.sql.SQLException;

public class BankClientHandler implements Runnable {
    private Socket socket;
    private BankManager bankManager;
    private String login = null;


    public BankClientHandler(Socket socket) {
        this.socket = socket;
        this.bankManager = BankManager.getInstance();
    }

    @Override
    public void run() {

        //Deklaracje zmiennych strumieniowych
        BufferedReader brinp = null;
        DataOutputStream out = null;
        String line = null;
        String response = null;

        //Utworzenie strumieni
        try {
            brinp = new BufferedReader(
                    new InputStreamReader(
                            socket.getInputStream()
                    )
            );
            out = new DataOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            System.out.println("| Błąd przy tworzeniu strumieni " + e);
            return;
        }

        //Pętla główna klienta
        try {
            while (true) {
                line = brinp.readLine().toLowerCase();
                if ((line == null) || "quit".equals(line)) {
                    System.out.println("Zakończenie pracy z klientem: " + socket);
                    socket.close();
                    return;
                }
                switch (line) {
                    case "login":
                        while (true) {
                            if (!handleUserLoginAndAuthentication(brinp, out)) {
                                line = brinp.readLine();
                                if (line.equals("back")) {
                                    out.writeBytes("starterGUI\n");
                                    break;
                                }
                            } else {
                                String innerLine = brinp.readLine();
                                while(innerLine.equals("back")){
                                    out.writeBytes("Invalid input! Press ENTER to continue! \n");
                                    innerLine = brinp.readLine();
                                }
                                break;
                            }
                        }
                        if (line.equals("back")) {
                            break;
                        }
                        while (true) {
                            out.writeBytes("loggedInGUI\n");
                            out.flush();
                            line = brinp.readLine();
                            if(line.equals("logout")){
                                String querySwitchLogout = "UPDATE is_not_logged SET not_logged=1 WHERE login = '" + login + "'";
                                DataStorage.statement.executeUpdate(querySwitchLogout);
                                login = null;
                                break;
                            }
                            else{
                                response = bankManager.processCommand(line, login, brinp, out);
                                out.writeBytes(response);
                                out.flush();
                                brinp.readLine();
                            }
                        }
                        if(line.equals("logout")){
                            out.writeBytes("starterGUI\n");
                            break;
                        }
                    case "register":
                        bankManager.handleUserRegistration(brinp, out);
                        out.writeBytes("Registration completed! Press ENTER to continue: \n");
                        brinp.readLine();
                        out.writeBytes("starterGUI\n");
                        break;
                    default:
                        out.writeBytes("WRONG! Type in operation correctly: \n");
                        out.flush();
                }
            }
        }
        catch (IOException | SQLException e) {
            System.out.println("IO error in client handler: " + e.getMessage());
            if(login != null){
                String queryAutoLogout = "UPDATE is_not_logged SET not_logged=1 WHERE login = '" + login + "'";
                try {
                    DataStorage.statement.executeUpdate(queryAutoLogout);
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
            }
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                System.out.println("Error closing socket: " + e.getMessage());
            }
        }
    }

    private boolean handleUserLoginAndAuthentication(BufferedReader brinp, DataOutputStream out) throws IOException, SQLException {
        out.writeBytes("Enter your login: \n");
        out.flush();
        String username = brinp.readLine();
        out.writeBytes("Enter your password: \n");
        out.flush();
        String password = brinp.readLine();

        String queryCorrectPassword = "SELECT COUNT(*) FROM credential WHERE login = '" + username +
                "' AND password = '" + password + "'";
        ResultSet resultSet = DataStorage.statement.executeQuery(queryCorrectPassword);
        resultSet.next();
        int count = resultSet.getInt(1);
        if (count > 0) {
            String queryIsNotLogged = "SELECT not_logged FROM is_not_logged WHERE login = '" + username + "'";
            ResultSet resultSetIsNotLogged = DataStorage.statement.executeQuery(queryIsNotLogged);
            resultSetIsNotLogged.next();
            boolean isNotLogged = resultSetIsNotLogged.getBoolean("not_logged");
            if(!isNotLogged){
                out.writeBytes("This account is already in use! Press ENTER to try again or 'BACK' to return: \n");
                return false;
            }
            else{
                login = username;
                String querySwitchLogIn = "UPDATE is_not_logged SET not_logged=0 WHERE login = '" + login + "'";
                DataStorage.statement.executeUpdate(querySwitchLogIn);
                out.writeBytes("Login succeed! Press ENTER to continue! \n");
                return true;
            }
        } else {
            out.writeBytes("Wrong login/password! Press ENTER to try again or 'back' to return: \n");
            return false;
        }
    }

}