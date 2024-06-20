package org.JDBC_Demo;
import java.sql.*;

public class Database {
    private static final String URL = "jdbc:mysql://localhost:3306/railway_resv";
    private static final String Username = "root";
    private static final String Password = "root";
    private static int TRAIN_NO = 1;

    public static void main(String[] args) {
        insertDataBase();
        readDataBase();
    }

    static void readDataBase(){
        try {
            Connection conn = DriverManager.getConnection(URL,Username,Password);
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery("SELECT * FROM Trains"); // return resultSet obj
            while(rs.next()){
                System.out.println("Train ID: "+rs.getInt(1));
                System.out.println("Train Name: "+rs.getInt(1));
                System.out.println("Total Seats : "+rs.getInt(1));
                System.out.println("Available Seats : "+rs.getInt(1));
                System.out.println("RAC : "+rs.getInt(1));
                System.out.println("WaitList : "+rs.getInt(1));
            }
            conn.close();
        }catch (SQLException exception){
            System.err.println(exception.getMessage());
            exception.printStackTrace();
        }
    }

    static void insertDataBase(){
        try {
            Connection conn = DriverManager.getConnection(URL,Username,Password);
            Statement st = conn.createStatement();
            int rows = st.executeUpdate("INSERT INTO Trains VALUES (" + TRAIN_NO + "," + "'Express', 200, 200, 50,30)");
            System.out.println("Number of rows affected : " + rows);
            conn.close();
        }catch (SQLException exception){
            System.err.println(exception.getMessage());
            exception.printStackTrace();
        }
    }
}

