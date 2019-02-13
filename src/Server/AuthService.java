package Server;

import java.sql.*;
import java.util.ArrayList;

public class AuthService {
    private static Connection connection;
    private static Statement stmt;
    private static PreparedStatement prsmt;

    public static void connect() throws SQLException {
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:userslist.db");
            stmt = connection.createStatement();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static String getNickName(String login, String pass) throws SQLException {
        String sql = String.format("SELECT nickname FROM main \n" +
                "WHERE login = '%s'\n" +
                "AND password = '%s'", login, pass);
        ResultSet rs = stmt.executeQuery(sql);
       if (rs.next()) {
            return rs.getString(1);
        }else return null;
    }

    public static void addNickInBlackList(String nick, String nickForBlock) throws SQLException {
        String query = String.format("INSERT INTO blacklists (nickname, blockednick)\n" +
                "VALUES ('%s', '%s' )", nick, nickForBlock);
        prsmt = connection.prepareStatement(query);
        prsmt.executeUpdate();
    }

    public static void removeNickFromBlackList(String nick, String nickForRemove) throws SQLException {
        String query = String.format("DELETE FROM blacklists\n" +
                "WHERE nickname = '%s' AND blockednick = '%s'", nick, nickForRemove);
        prsmt = connection.prepareStatement(query);
        prsmt.executeUpdate();
    }

    public static void clearBlackList(String myNick) throws SQLException {
        String query = String.format("DELETE FROM blacklists\n" +
                "WHERE nickname = '%s'", myNick);
        prsmt = connection.prepareStatement(query);
        prsmt.executeUpdate();
    }

    public static int howManyInBl(String myNick) throws SQLException {
        String sql = String.format("SELECT COUNT(blockednick) FROM blacklists\n" +
                "WHERE nickname = '%s'", myNick);
        ResultSet resultSet = stmt.executeQuery(sql);
        int count = resultSet.getInt(1);
        return count;
    }

    public static ArrayList<String> showMyBlackList(String myNick) throws SQLException {
        ArrayList<String> result = new ArrayList<>();
            String query = String.format("SELECT blockednick FROM blacklists\n" +
                    "WHERE nickname = '%s'", myNick);
        prsmt = connection.prepareStatement(query);
        ResultSet rs = prsmt.executeQuery();
        while (rs.next()) {
            result.add(rs.getString(1));
        }
        return result;
        }

    public static boolean return_match(String nickname, String blockedNick) throws SQLException {
        String sql = String.format("SELECT COUNT(blockednick) FROM blacklists\n" +
                "WHERE nickname = '%s' AND blockednick = '%s'", nickname, blockedNick);
        ResultSet resultSet = stmt.executeQuery(sql);
        if (resultSet.getInt(1) > 0) {
            return true;
        }
        else return false;
    }

    public static void addNewUser(String login, String password, String password_repeat, String nickname) {


    }




    public static void disconnect() {
        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


}
