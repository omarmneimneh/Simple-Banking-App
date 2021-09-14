package banking;

import org.sqlite.SQLiteDataSource;

import java.sql.*;

public class Database {
    static String url = "jdbc:sqlite:";
    private SQLiteDataSource dataSource;
    private PreparedStatement preparedStatement;
    private final String DELETE_STATEMENT = ("DELETE FROM card WHERE number = ?");
    private final String CHECK_STATEMENT = ("SELECT * FROM card WHERE number = ? AND pin = ?");
    private final String ADD_BALANCE = ("UPDATE card SET balance = balance + ? WHERE number = ?");
    private final String SUBTRACT_BALANCE= ("UPDATE card SET balance=balance - ? WHERE number = ?");
    private final String SELECT_STATEMENT = ("SELECT balance FROM card WHERE number = ?");
    private final String TRANSFER_CHECK_STATEMENT = ("SELECT * FROM card WHERE number = ?");
    Database(String fileName){
        this.dataSource = new SQLiteDataSource();
        this.dataSource.setUrl(url+fileName);

        //Create database and table named cards when program starts
        try (Connection con = this.dataSource.getConnection()){
            try (Statement statement = con.createStatement()){
                statement.executeUpdate("CREATE TABLE IF NOT EXISTS card("+
                        "id INTEGER,"+
                        "number TEXT,"+
                        "pin TEXT,"+
                        "balance INTEGER DEFAULT 0)");
            } catch (SQLException e){
                e.printStackTrace();
            }
        } catch (SQLException e){
            e.printStackTrace();
        }
    }

    public boolean checkForCard(String number, String pin){
        boolean result = false;
        try (Connection con = this.dataSource.getConnection();
            final var sql = con.prepareStatement(CHECK_STATEMENT)){
            sql.setString(1, number);
            sql.setString(2, pin);
            ResultSet resultSet = sql.executeQuery();
            if(resultSet.next())result=true;

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return result;
    }

    public void addCard(int id, String number, String pin, int balance){
        try (Connection con = this.dataSource.getConnection()) {
            try (Statement statement = con.createStatement()) {
                statement.executeUpdate("INSERT INTO card VALUES " +
                        "(" + id + ", " + number + ", " + pin + ", " + balance + ")");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int viewBalance(String cardNum){
        int balance;
        try (Connection con = this.dataSource.getConnection();
             final var sql = con.prepareStatement(SELECT_STATEMENT)) {
                sql.setString(1, cardNum);
                sql.execute();
                ResultSet resultSet = sql.getResultSet();
                if(resultSet.next()) return resultSet.getInt(1);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return 0;
    }

    public void deleteCard(String number){
        try (Connection con = this.dataSource.getConnection();
             final var sql = con.prepareStatement(DELETE_STATEMENT)) {
                 sql.setString(1, number);
                 sql.executeUpdate();
            }catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public void addBalance(int amount, String number){
        try (Connection con = this.dataSource.getConnection();
             final var sql = con.prepareStatement(ADD_BALANCE)){
            sql.setInt(1, amount);
            sql.setString(2,number);
            sql.executeUpdate();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public void subtractBalance(int amount, String number){
        try (Connection con = this.dataSource.getConnection();
             final var sql = con.prepareStatement(SUBTRACT_BALANCE)){
            sql.setInt(1, amount);
            sql.setString(2,number);
            sql.executeUpdate();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public boolean transferCheck(String number){
        boolean result = false;
        try (Connection con = this.dataSource.getConnection();
             final var sql = con.prepareStatement(TRANSFER_CHECK_STATEMENT)){
            sql.setString(1, number);
            ResultSet resultSet = sql.executeQuery();
            if(resultSet.next())result=true;

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return result;
    }



}