package bankserver;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.sql.ResultSet;
import java.sql.SQLException;

public class BankAdminHandler implements Runnable {
    private Socket socket;
    private BankManager bankManager;

    private String login = null;

    public BankAdminHandler(Socket socket) {
        this.socket = socket;
        this.bankManager = BankManager.getInstance();

    }

  public void run() {
      //Deklaracje zmiennych
      BufferedReader brinp = null;
      DataOutputStream out = null;
      String line = null;
      String response = null;
      String threadName = Thread.currentThread().getName();

      //inicjalizacja strumieni
      try {
          brinp = new BufferedReader(
                  new InputStreamReader(
                          socket.getInputStream()
                  )
          );
          out = new DataOutputStream(socket.getOutputStream());
      } catch (IOException e) {
          System.out.println(threadName + "| Błąd przy tworzeniu strumieni " + e);
          return;
      }


      //pętla główna
      try {
          while (true) {
              line = brinp.readLine();
              if ((line == null) || "quit".equals(line)) {
                  System.out.println(threadName + "| Zakończenie pracy z klientem: " + socket);
                  socket.close();
                  return;
              }
              switch (line.toLowerCase()) {
                  case "login":
                      while (true) {
                          if (!handleUserLoginAndAuthenticationAdmin(brinp, out)) {
                              line = brinp.readLine();
                              if (line.equals("back")) {
                                  out.writeBytes("starterGUI\n");
                                  break;
                              }
                          } else {
                              String innerLine = brinp.readLine();
                              while (innerLine.equals("back")) {
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
                          if (line.equals("logout")) {
                              String querySwitchLogoutAdmin = "UPDATE is_not_logged_admin SET not_logged_admin=1 WHERE login = '" + login + "'";
                              DataStorage.statement.executeUpdate(querySwitchLogoutAdmin);
                              login = null;
                              break;
                          } else {
                              response = bankManager.processCommandAdmin(line, brinp, out);
                              out.writeBytes(response);
                              out.flush();
                              brinp.readLine();
                          }
                      }
                      if (line.equals("logout")) {
                          out.writeBytes("starterGUI\n");
                          break;
                      }
                  default:
                      out.writeBytes("WRONG! Type in operation correctly: \n");
                      out.flush();
              }
          }
      }
      catch (IOException | SQLException e) {
          System.out.println("IO error in client handler: " + e.getMessage());
          if(login != null){
              String queryAutoLogoutAdmin = "UPDATE is_not_logged_admin SET not_logged_admin=1 WHERE login = '" + login + "'";
              try {
                  DataStorage.statement.executeUpdate(queryAutoLogoutAdmin);
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
     public boolean handleUserLoginAndAuthenticationAdmin(BufferedReader brinp, DataOutputStream out) throws IOException, SQLException {
        out.writeBytes("Enter your login: \n");
        out.flush();
        String username = brinp.readLine();
        out.writeBytes("Enter your password: \n");
        out.flush();
        String password = brinp.readLine();

        String queryCorrectPassword = "SELECT COUNT(*) FROM admin WHERE login = '" + username +
                "' AND password = '" + password + "'";
        ResultSet resultSet = DataStorage.statement.executeQuery(queryCorrectPassword);
        resultSet.next();
        int count = resultSet.getInt(1);
        if (count > 0) {
            String queryIsNotLoggedAdmin = "SELECT not_logged_admin FROM is_not_logged_admin WHERE login = '" + username + "'";
            ResultSet resultSetIsNotLoggedAdmin = DataStorage.statement.executeQuery(queryIsNotLoggedAdmin);
            resultSetIsNotLoggedAdmin.next();
            boolean isNotLoggedAdmin = resultSetIsNotLoggedAdmin.getBoolean("not_logged_admin");
            if(!isNotLoggedAdmin){
                out.writeBytes("This account is already in use! Press ENTER to try again or 'back' to return: \n");
                return false;
            }
            else{
                login = username;
                String querySwitchLogInAdmin = "UPDATE is_not_logged_admin SET not_logged_admin=0 WHERE login = '" + login + "'";
                DataStorage.statement.executeUpdate(querySwitchLogInAdmin);
                out.writeBytes("Login succeed! Press ENTER to continue! \n");
                return true;
            }
        } else {
            out.writeBytes("Wrong login/password! Press ENTER to try again or 'back' to return: \n");
            return false;
        }
    }

}

