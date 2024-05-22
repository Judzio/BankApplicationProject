package bankserver;


import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Random;

public class BankManager {
    private static BankManager instance;

    private BankManager() {
    }

    public static synchronized BankManager getInstance() {
        if (instance == null) {
            instance = new BankManager();
        }
        return instance;
    }

    public synchronized String addClient(String login, String password, String name, String surname, String pesel, String transferNumber) throws SQLException {
        String queryCount = "SELECT COUNT(*) AS count FROM account WHERE pesel = '" + pesel + "'";
        ResultSet resultSet = DataStorage.statement.executeQuery(queryCount);
        resultSet.next();
        int count = resultSet.getInt(1);
        if (count > 0) {
            return "Client already exists. Press ENTER to continue: \n";
        }
        else{
            String queryAccount = "INSERT INTO Account VALUES ('" + login + "','"
                    + name + "','" + surname + "','"
                    + pesel + "','" + transferNumber
                    + "'," + 0.0 + ");";
            String queryCredential = "INSERT INTO Credential VALUES ('" + login + "','"
                    + password + "');";
            String queryNotLogged = "INSERT INTO is_not_logged VALUES ('" + login + "','1');";
            DataStorage.statement.executeUpdate(queryAccount);
            DataStorage.statement.executeUpdate(queryCredential);
            DataStorage.statement.executeUpdate(queryNotLogged);
            return "Client added successfully. Press ENTER to continue: \n";
        }
    }
    public synchronized String modifyClient(String loginToModify, String name, String surname, String pesel) throws SQLException  {
            String queryCurrentTransferNumber = "SELECT transfer_number FROM account WHERE login = '" + loginToModify + "'";
            ResultSet resultSetTransferNumber = DataStorage.statement.executeQuery(queryCurrentTransferNumber);
            resultSetTransferNumber.next();
            String currentTransferNumber = resultSetTransferNumber.getString("transfer_number");

            String queryCurrentBalance = "SELECT balance FROM account WHERE login = '" + loginToModify + "'";
            ResultSet resultSetBalance = DataStorage.statement.executeQuery(queryCurrentBalance);
            resultSetBalance.next();
            double currentBalance = resultSetBalance.getDouble("balance");

            String queryModify = "UPDATE account SET login='"+loginToModify+"',name='"+name+"',surname='"
                    +surname+"',pesel='"+pesel+"',transfer_number='"+currentTransferNumber+"',balance="+currentBalance
                    +" WHERE login='"+loginToModify+"'";
            DataStorage.statement.executeUpdate(queryModify);

            return "Client modified successfully. Press ENTER to continue: \n";
    }
    public synchronized String deleteClient(String login) throws SQLException {
        String queryCount = "SELECT COUNT(*) AS count FROM account WHERE login = '" + login + "'";
        ResultSet resultSet = DataStorage.statement.executeQuery(queryCount);
        resultSet.next();
        int count = resultSet.getInt(1);
        if (count <= 0) {
            return "Client doesn't exists. Press ENTER to continue: \n";
        }
        else{
            String queryDelete = "DELETE FROM account WHERE login = '" + login + "'";
            DataStorage.statement.executeUpdate(queryDelete);
            return "Client has been deleted successfully. Press ENTER to continue: \n";
        }
    }
    public synchronized String queryBalance(String login) throws SQLException {

        String queryGetBalance = "SELECT balance FROM account WHERE login = '" + login + "'";
        ResultSet resultSetBalance = DataStorage.statement.executeQuery(queryGetBalance);
        resultSetBalance.next();
        double currentBalance = resultSetBalance.getDouble(1);

        return String.format("Your balance is: %.2f Press ENTER to continue: \n", currentBalance);
    }
    public synchronized String addMoney(String login, double amount) throws SQLException {
        String queryAddMoney = "UPDATE account SET balance=balance+" + amount
                + " WHERE login = '" + login + "'";
        DataStorage.statement.executeUpdate(queryAddMoney);
        String queryNewBalance = "SELECT balance FROM account WHERE login = '" + login + "'";
        ResultSet resultGetBalance = DataStorage.statement.executeQuery(queryNewBalance);
        resultGetBalance.next();
        double currentBalance = resultGetBalance.getDouble(1);

        return "Money added successfully. " + currentBalance + " is your new balance. Press ENTER to continue: \n";
    }
    public synchronized String withdrawMoney(String login, double amount) throws SQLException {
        String queryWithdrawMoney = "UPDATE account SET balance=balance-" + amount
                + " WHERE login = '" + login + "'";
        DataStorage.statement.executeUpdate(queryWithdrawMoney);
        String queryNewBalance = "SELECT balance FROM account WHERE login = '" + login + "'";
        ResultSet resultGetBalance = DataStorage.statement.executeQuery(queryNewBalance);
        resultGetBalance.next();
        double currentBalance = resultGetBalance.getDouble(1);

        return "Money withdrew successfully. " + currentBalance + " is your new balance. Press ENTER to continue: \n";
    }
    public synchronized String transferMoney(String fromTransferLogin, String toTransferNumber, double amount) throws SQLException {
        String queryTransferMoney = "UPDATE account SET balance=balance-" + amount
                + " WHERE login = '" + fromTransferLogin + "'";
        DataStorage.statement.executeUpdate(queryTransferMoney);
        String queryReceiveMoney = "UPDATE account SET balance=balance+" + amount
                + " WHERE transfer_number = '" + toTransferNumber + "'";
        DataStorage.statement.executeUpdate(queryReceiveMoney);

        String queryNewBalance = "SELECT balance FROM account WHERE login = '" + fromTransferLogin + "'";
        ResultSet resultGetBalance = DataStorage.statement.executeQuery(queryNewBalance);
        resultGetBalance.next();
        double currentBalance = resultGetBalance.getDouble(1);

        return "Transfer successful. " + currentBalance + " is your new balance. Press ENTER to continue: \n";
    }
    private boolean isEnoughMoney(double moneyToWithdraw, String login) throws SQLException {
        String queryCurrentMoney = "SELECT balance FROM account WHERE login = '" + login + "'";
        ResultSet resultSetMoney = DataStorage.statement.executeQuery(queryCurrentMoney);
        resultSetMoney.next();
        double currentBalance = resultSetMoney.getDouble(1);
        if ((currentBalance - moneyToWithdraw) < 0) {
            return true;
        } else {
            return false;
        }
    }
    private boolean doesTransferNumberExists(String targetTransferNumber) throws SQLException {
        String queryTransferNumber = "SELECT COUNT(*) FROM account WHERE transfer_number = '"
                + targetTransferNumber + "'";
        ResultSet resultSet = DataStorage.statement.executeQuery(queryTransferNumber);
        resultSet.next();
        int count = resultSet.getInt(1);
        if (count > 0) {
            return true;
        } else {
            return false;
        }

    }
    public String generateTransferNumber() throws SQLException {
        Random random = new Random();
        StringBuilder accountNumber = new StringBuilder();
        do {
            for (int i = 0; i < 25; i++) {
                int digit = random.nextInt(10);
                accountNumber.append(digit);
            }

            int firstDigit = random.nextInt(9) + 1;
            accountNumber.insert(0, firstDigit);

        } while (doesTransferNumberExists(accountNumber.toString()));

        return accountNumber.toString();
    }
    public boolean isValidPassword(String password, BufferedReader brinp, DataOutputStream out) throws IOException {
        if (password.length() < 8) {
            out.writeBytes("Password need to be at least 8 characters! Type in new one: \n");
            return false;
        }

        if (!password.matches(".*[A-Z].*") || !password.matches(".*[a-z].*")) {
            out.writeBytes("Password need to have one big and small character! Type in new one: \n");
            return false;
        }

        if (!password.matches(".*\\d.*")) {
            out.writeBytes("Password need to contains at least one digit! Type in new one: \n");
            return false;
        }
        return true;
    }
    public boolean isWithdrawValid(String moneyToWithdraw, String login, BufferedReader brinp, DataOutputStream out) throws IOException, SQLException {
        if(moneyToWithdraw.isEmpty()){
            out.writeBytes("Field cannot be empty: \n");
            return false;
        }
        if(hasOnlyDigit(moneyToWithdraw)){
            out.writeBytes("Amount must contains only digits! Type in different one: \n");
            return false;
        }
        double moneyInDouble = Double.parseDouble(moneyToWithdraw);
        if (moneyInDouble <= 0) {
            out.writeBytes("Wrong input! Type in value above zero: \n");
            return false;
        }
        if (isEnoughMoney(moneyInDouble, login)) {
            out.writeBytes("You don't have enough money! Type in different value or 'back' to return: \n");
            return false;
        }
        return true;
    }
    public boolean isTransferValid(String toTransferNumber, String moneyToTransfer, String login, BufferedReader brinp, DataOutputStream out) throws IOException, SQLException {
        if(toTransferNumber.isEmpty() || moneyToTransfer.isEmpty()){
            out.writeBytes("Fields cannot be empty: Press ENTER to continue: \n");
            return false;
        }
        if(hasOnlyDigit(moneyToTransfer)){
            out.writeBytes("Amount must contains only digits! Press ENTER to continue: \n");
            return false;
        }
        double moneyInDouble = Double.parseDouble(moneyToTransfer);
        if (moneyInDouble <= 0) {
            out.writeBytes("Your input need to be above zero. Press ENTER to continue: \n");
            return false;
        }
        if (!doesTransferNumberExists(toTransferNumber)) {
            out.writeBytes("Transfer number doesn't exists! Press ENTER to continue: \n");
            return false;
        }
        if (isEnoughMoney(moneyInDouble, login)) {
            out.writeBytes("You don't have enough money! Press ENTER to continue or 'back' to return: \n");
            return false;
        }
        return true;
    }
    public boolean isAddValid(String amount, BufferedReader brinp, DataOutputStream out) throws IOException {
        if(amount.isEmpty()){
            out.writeBytes("Field cannot be empty: \n");
            return false;
        }
        if(hasOnlyDigit(amount)){
            out.writeBytes("Amount must contains only digits! Type in different one: \n");
            return false;
        }
        double amountInDouble = Double.parseDouble(amount);
        if(amountInDouble <= 0){
            out.writeBytes("Wrong input! Type in value above zero: \n");
            return false;
        }
        return true;
    }
    public boolean isPeselValid(String pesel, BufferedReader brinp, DataOutputStream out) throws IOException, SQLException {
        if(pesel.length() != 11){
            out.writeBytes("Too short/long PESEL number! Type in correct one: \n");
            return false;
        }
        if(hasOnlyDigitsPeselVersion(pesel)){
            out.writeBytes("PESEL must contains only digits! Type in correct one: \n");
            return false;
        }
        if(doesPeselExists(pesel)){
            out.writeBytes("It is not your PESEL! Type in correct one: \n");
            return false;
        }
        return true;
    }
    private boolean doesPeselExists(String pesel) throws SQLException {
        String queryPesel = "SELECT COUNT(*) FROM account WHERE pesel = '"
                + pesel + "'";
        ResultSet resultSet = DataStorage.statement.executeQuery(queryPesel);
        resultSet.next();
        int count = resultSet.getInt(1);
        if (count > 0) {
            return true;
        } else {
            return false;
        }
    }
    public boolean isLoginValid(String login, BufferedReader brinp, DataOutputStream out) throws SQLException, IOException {
        if (doesLoginExists(login)) {
            out.writeBytes("Login already exists! Type in different one: \n");
            return false;
        }
        if (login.length() < 4){
            out.writeBytes("Login too short! At least 4 characters! Type in different one: \n");
            return false;
        }
        return true;
    }
    private boolean doesLoginExists(String login) throws SQLException {
        String queryLogin = "SELECT COUNT(*) FROM account WHERE login = '"
                + login + "'";
        ResultSet resultSet = DataStorage.statement.executeQuery(queryLogin);
        resultSet.next();
        int count = resultSet.getInt(1);
        if (count > 0) {
            return true;
        } else {
            return false;
        }
    }
    private boolean hasOnlyDigit(String number){
        if(number.charAt(0) == '-'){
            number = number.substring(1);
        }
        for(int i = 0; i < number.length(); i++){
            if(!Character.isDigit(number.charAt(i))){
                return true;
            }

        }
        return false;
    }
    private boolean hasOnlyDigitsPeselVersion(String number){
        for(int i = 0; i < number.length(); i++){
            if(!Character.isDigit(number.charAt(i))){
                return true;
            }

        }
        return false;
    }
    public void handleUserRegistration(BufferedReader brinp, DataOutputStream out) throws IOException, SQLException {
        out.writeBytes("Your login: \n");
        out.flush();
        String username = brinp.readLine();
        while(!isLoginValid(username, brinp, out)){
            username = brinp.readLine();
        }
        out.writeBytes("Your password: \n");
        out.flush();
        String password = brinp.readLine();
        while (!isValidPassword(password, brinp, out)) {
            password = brinp.readLine();
        }
        out.writeBytes("Your name: \n");
        out.flush();
        String name = brinp.readLine();
        while(name.isEmpty()){
            out.writeBytes("Input cannot be empty. Type in different one: \n");
            name = brinp.readLine();
        }
        out.writeBytes("Your surname: \n");
        out.flush();
        String surname = brinp.readLine();
        while(surname.isEmpty()){
            out.writeBytes("Input cannot be empty. Type in different one: \n");
            surname = brinp.readLine();
        }
        out.writeBytes("Your PESEL: \n");
        out.flush();
        String pesel = brinp.readLine();
        while (!isPeselValid(pesel, brinp, out)) {
            pesel = brinp.readLine();
        }
        String queryAccount = "INSERT INTO Account VALUES ('" + username + "','"
                + name + "','" + surname + "','"
                + pesel + "','" + generateTransferNumber()
                + "'," + 0.0 + ");";
        String queryCredential = "INSERT INTO Credential VALUES ('" + username + "','"
                + password + "');";
        String queryNotLogged = "INSERT INTO is_not_logged VALUES ('" + username + "','1');";
        DataStorage.statement.executeUpdate(queryAccount);
        DataStorage.statement.executeUpdate(queryCredential);
        DataStorage.statement.executeUpdate(queryNotLogged);
    }
    public String processCommand(String line, String login, BufferedReader brinp, DataOutputStream out) throws SQLException, IOException {
        switch (line.toLowerCase()) {
            case "balance":
                return queryBalance(login);
            case "add money":
                out.writeBytes("How much money to add: \n");
                String amountToAdd = brinp.readLine();
                while (!isAddValid(amountToAdd, brinp, out)) {
                    amountToAdd = brinp.readLine();
                }
                return addMoney(login, Double.parseDouble(amountToAdd));

            case "withdraw money":
                out.writeBytes("How much money to withdraw: \n");
                String amountToWithdraw = brinp.readLine();
                while (!isWithdrawValid(amountToWithdraw, login, brinp, out)) {
                    amountToWithdraw = brinp.readLine();
                    if(amountToWithdraw.equals("back")){
                        return "Press ENTER to continue \n";
                    }
                }
                return withdrawMoney(login, Double.parseDouble(amountToWithdraw));

            case "transfer money":
                out.writeBytes("Type in transfer number: \n");
                String targetTransferNumber = brinp.readLine();
                out.writeBytes("How much money to transfer: \n");
                String amountToTransfer = brinp.readLine();
                while(!isTransferValid(targetTransferNumber, amountToTransfer, login, brinp, out)){
                    String back = brinp.readLine();
                    if(back.equals("back")){
                        return "Press ENTER to continue \n";
                    }
                    out.writeBytes("Type in transfer number: \n");
                    targetTransferNumber = brinp.readLine();
                    out.writeBytes("How much money to transfer: \n");
                    amountToTransfer = brinp.readLine();
                }
                return transferMoney(login, targetTransferNumber, Double.parseDouble(amountToTransfer));
            default:
                return "Unknown command. Press ENTER to try again!\n";
        }
    }
    private boolean isPeselValidAdmin(String pesel, BufferedReader brinp, DataOutputStream out) throws IOException, SQLException {
        if(!hasOnlyDigitsAdmin(pesel)){
            out.writeBytes("PESEL must contains only digits! Type in correct one: \n");
            return true;
        }
        if(doesPeselExists(pesel)){
            out.writeBytes("PESEL already exists in database! Type in correct one: \n");
            return true;
        }
        if(pesel.length() != 11){
            out.writeBytes("Too short/long PESEL number! Type in correct one: \n");
            return true;
        }
        return false;
    }
    private boolean hasOnlyDigitsAdmin(String number){
        for(int i = 0; i < number.length(); i++){
            if(!Character.isDigit(number.charAt(i))){
                return false;
            }

        }
        return true;
    }
    public String processCommandAdmin(String line, BufferedReader brinp, DataOutputStream out) throws SQLException, IOException {

        switch (line.toLowerCase()) {
            case "add client":
                out.writeBytes("Login: \n");
                out.flush();
                String username = brinp.readLine();
                while(!isLoginValid(username, brinp, out)){
                    username = brinp.readLine();
                }
                out.writeBytes("Password: \n");
                out.flush();
                String password = brinp.readLine();
                while (!isValidPassword(password, brinp, out)) {
                    password = brinp.readLine();
                }
                out.writeBytes("Name: \n");
                out.flush();
                String name = brinp.readLine();
                while(name.isEmpty()){
                    out.writeBytes("Field cannot be empty. Type in different output: \n");
                    name = brinp.readLine();
                }
                out.writeBytes("Surname: \n");
                out.flush();
                String surname = brinp.readLine();
                while(surname.isEmpty()){
                    out.writeBytes("Field cannot be empty. Type in different output: \n");
                    surname = brinp.readLine();
                }
                out.writeBytes("PESEL: \n");
                out.flush();
                String pesel = brinp.readLine();
                while (isPeselValidAdmin(pesel, brinp, out)) {
                    pesel = brinp.readLine();
                }
                return addClient(username, password, name, surname, pesel, generateTransferNumber());
            case "modify client":
                out.writeBytes("Write in account's login to modify: \n");
                String usernameToModify = brinp.readLine();
                String queryCount = "SELECT COUNT(*) AS count FROM account WHERE login = '" + usernameToModify + "'";
                ResultSet resultSet = DataStorage.statement.executeQuery(queryCount);
                resultSet.next();
                int count = resultSet.getInt(1);
                if (count <= 0) {
                    return "Client doesn't exists. Press ENTER to continue: \n";
                }
                out.writeBytes("Write in new name or 'previous' to keep the same name: \n");
                String newName = brinp.readLine();
                while(newName.isEmpty()){
                    out.writeBytes("Field cannot be empty. Type in different one: \n");
                    newName = brinp.readLine();
                }
                if(newName.equals("previous")){
                    String queryPreviousName = "SELECT name FROM account WHERE login = '" + usernameToModify + "'";
                    ResultSet resultSetPreviousName = DataStorage.statement.executeQuery(queryPreviousName);
                    resultSetPreviousName.next();
                    newName = resultSetPreviousName.getString("name");
                }
                out.writeBytes("Write in new surname or 'previous' to keep the same surname: \n");
                String newSurname = brinp.readLine();
                while(newSurname.isEmpty()){
                    out.writeBytes("Field cannot be empty. Type in different one: \n");
                    newSurname = brinp.readLine();
                }
                if(newSurname.equals("previous")){
                    String queryPreviousSurname = "SELECT surname FROM account WHERE login = '" + usernameToModify + "'";
                    ResultSet resultSetPreviousSurname = DataStorage.statement.executeQuery(queryPreviousSurname);
                    resultSetPreviousSurname.next();
                    newSurname = resultSetPreviousSurname.getString("surname");
                }
                out.writeBytes("Write in new PESEL or 'previous' to keep the same PESEL: \n");
                String newPESEL = brinp.readLine();
                if(newPESEL.equals("previous")){
                    String queryPreviousPESEL = "SELECT pesel FROM account WHERE login = '" + usernameToModify + "'";
                    ResultSet resultSetPreviousPESEL = DataStorage.statement.executeQuery(queryPreviousPESEL);
                    resultSetPreviousPESEL.next();
                    newPESEL = resultSetPreviousPESEL.getString("pesel");
                }
                else {
                    while (isPeselValidAdmin(newPESEL, brinp, out)) {
                        newPESEL = brinp.readLine();
                    }
                }
                return modifyClient(usernameToModify, newName, newSurname, newPESEL);
            case "delete client":
                out.writeBytes("Write in account's login to delete: \n");
                String usernameToDelete = brinp.readLine();
                return deleteClient(usernameToDelete);
            case "client list":
                return "clientListGUI\n";
            default:
                return "Unknown command. Press ENTER to try again!\n";
        }
    }

}
