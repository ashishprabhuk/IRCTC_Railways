package org.railways;

import java.sql.*;
import java.util.*;

class Trains {
    private static final int MAX_SEATS = 3;
    private static final int MAX_RAC = 1;
    private static final int MAX_WL = 1;

    private List<Passenger> passengers = new ArrayList<>();
    private List<Passenger> racPassengers = new ArrayList<>();
    private List<Passenger> waitingList = new ArrayList<>();

    // Method to book a train ticket for a passenger
//    public boolean bookTicket(Passenger passenger) {
//        try (Connection connection = DB.getConnection()) {
//            String sql;
//            if (passengers.size() < MAX_SEATS) {
//                // Assign berth based on preference and availability
//                if (passenger.getBerthPreference().equals("L") && countBerthPreference("L") < 7) {
//                    passenger.setStatus("L");
//                } else if (passenger.getBerthPreference().equals("M") && countBerthPreference("M") < 7) {
//                    passenger.setStatus("M");
//                } else if (passenger.getBerthPreference().equals("U") && countBerthPreference("U") < 7) {
//                    passenger.setStatus("U");
//                } else {
//                    passenger.setStatus("L"); // Default to Lower Berth if preference is full
//                }
//                sql = "INSERT INTO passengers (name, age, berth_preference, status) VALUES (?, ?, ?, ?)";
//            } else if (racPassengers.size() < MAX_RAC) {
//                passenger.setStatus("RAC");
//                sql = "INSERT INTO rac_passengers (name, age, berth_preference, status) VALUES (?, ?, ?, ?)";
//            } else if (waitingList.size() < MAX_WL) {
//                passenger.setStatus("WL");
//                sql = "INSERT INTO waiting_list (name, age, berth_preference, status) VALUES (?, ?, ?, ?)";
//            } else {
//                return false; // No more tickets available
//            }
//
//            try (PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
//                statement.setString(1, passenger.getName());
//                statement.setInt(2, passenger.getAge());
//                statement.setString(3, passenger.getBerthPreference());
//                statement.setString(4, passenger.getStatus());
//
//                int affectedRows = statement.executeUpdate();
//
//                if (affectedRows > 0) {
//                    try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
//                        if (generatedKeys.next()) {
//                            passenger.setId(generatedKeys.getInt(1));
//                        }
//                    }
//                    return true;
//                }
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//        return false;
//    }

    public boolean bookTicket(Passenger passenger) {
        try (Connection connection = DB.getConnection()) {
            String sql;
            int count;

            // Check the number of booked passengers
            sql = "SELECT COUNT(*) FROM passengers";
            try (Statement statement = connection.createStatement();
                 ResultSet resultSet = statement.executeQuery(sql)) {
                resultSet.next();
                count = resultSet.getInt(1);
            }
            if (count < MAX_SEATS) {
                // Assign berth based on preference and availability
                if (passenger.getBerthPreference().equals("L") && countBerthPreference("L", connection) < 3) {
                    passenger.setStatus("L");
                } else if (passenger.getBerthPreference().equals("M") && countBerthPreference("M", connection) < 2) {
                    passenger.setStatus("M");
                } else if (passenger.getBerthPreference().equals("U") && countBerthPreference("U", connection) < 2) {
                    passenger.setStatus("U");
                } else {
                    passenger.setStatus("L"); // Default to Lower Berth if preference is full
                }
                sql = "INSERT INTO passengers (name, age, berth_preference, status) VALUES (?, ?, ?, ?)";
            } else {
                // Check the number of RAC passengers
                sql = "SELECT COUNT(*) FROM rac_passengers";
                try (Statement statement = connection.createStatement();
                     ResultSet resultSet = statement.executeQuery(sql)) {
                    resultSet.next();
                    count = resultSet.getInt(1);
                }
                if (count < MAX_RAC) {
                    passenger.setStatus("RAC");
                    sql = "INSERT INTO rac_passengers (name, age, berth_preference, status) VALUES (?, ?, ?, ?)";
                } else {
                    // Check the number of waiting list passengers
                    sql = "SELECT COUNT(*) FROM waiting_list";
                    try (Statement statement = connection.createStatement();
                         ResultSet resultSet = statement.executeQuery(sql)) {
                        resultSet.next();
                        count = resultSet.getInt(1);
                    }
                    if (count < MAX_WL) {
                        passenger.setStatus("WL");
                        sql = "INSERT INTO waiting_list (name, age, berth_preference, status) VALUES (?, ?, ?, ?)";
                    } else {
                        return false; // No more tickets available
                    }
                }
            }

            try (PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                statement.setString(1, passenger.getName());
                statement.setInt(2, passenger.getAge());
                statement.setString(3, passenger.getBerthPreference());
                statement.setString(4, passenger.getStatus());

                int affectedRows = statement.executeUpdate();

                if (affectedRows > 0) {
                    try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                        if (generatedKeys.next()) {
                            passenger.setId(generatedKeys.getInt(1));
                        }
                    }
                    return true;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private int countBerthPreference(String preference, Connection connection) throws SQLException {
        String sql = "SELECT COUNT(*) FROM passengers WHERE berth_preference = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, preference);
            try (ResultSet resultSet = statement.executeQuery()) {
                resultSet.next();
                return resultSet.getInt(1);
            }
        }
    }



    // Method to cancel a ticket by passenger ID
//    public boolean cancelTicket(int passengerId) {
//        try (Connection connection = DB.getConnection()) {
//            String sql = "DELETE FROM passengers WHERE id = ?";
//            try (PreparedStatement statement = connection.prepareStatement(sql)) {
//                statement.setInt(1, passengerId);
//                int affectedRows = statement.executeUpdate();
//
//                if (affectedRows > 0) {
//                    promoteFromRAC();
//                    moveFromWaitingListToRAC();
//                    return true; // Ticket canceled successfully
//                }
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//        return false;
//    }

    public boolean cancelTicket(int passengerId) {
        try (Connection connection = DB.getConnection()) {
            String sql = "DELETE FROM passengers WHERE id = ?";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setInt(1, passengerId);
                int affectedRows = statement.executeUpdate();

                if (affectedRows > 0) {
                    promoteFromRAC();
                    moveFromWaitingListToRAC();
                    return true; // Ticket canceled successfully
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }


    // Getter method to retrieve the list of booked passengers
    public List<Passenger> getPassengers() {
        List<Passenger> passengers = new ArrayList<>();
        try (Connection connection = DB.getConnection()) {
            String sql = "SELECT * FROM passengers";
            try (Statement statement = connection.createStatement();
                 ResultSet resultSet = statement.executeQuery(sql)) {

                while (resultSet.next()) {
                    Passenger passenger = new Passenger(
                            resultSet.getString("name"),
                            resultSet.getInt("age"),
                            resultSet.getString("berth_preference")
                    );
                    passenger.setId(resultSet.getInt("id"));
                    passenger.setStatus(resultSet.getString("status"));
                    passengers.add(passenger);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return passengers;
    }

    // Getter method to retrieve the list of RAC passengers
    public List<Passenger> getRACPassengers() {
        List<Passenger> racPassengers = new ArrayList<>();
        try (Connection connection = DB.getConnection()) {
            String sql = "SELECT * FROM rac_passengers";
            try (Statement statement = connection.createStatement();
                 ResultSet resultSet = statement.executeQuery(sql)) {

                while (resultSet.next()) {
                    Passenger passenger = new Passenger(
                            resultSet.getString("name"),
                            resultSet.getInt("age"),
                            resultSet.getString("berth_preference")
                    );
                    passenger.setId(resultSet.getInt("id"));
                    passenger.setStatus(resultSet.getString("status"));
                    racPassengers.add(passenger);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return racPassengers;
    }

    // Getter method to retrieve the list of passengers on the waiting list
    public List<Passenger> getWaitingList() {
        List<Passenger> waitingList = new ArrayList<>();
        try (Connection connection = DB.getConnection()) {
            String sql = "SELECT * FROM waiting_list";
            try (Statement statement = connection.createStatement();
                 ResultSet resultSet = statement.executeQuery(sql)) {

                while (resultSet.next()) {
                    Passenger passenger = new Passenger(
                            resultSet.getString("name"),
                            resultSet.getInt("age"),
                            resultSet.getString("berth_preference")
                    );
                    passenger.setId(resultSet.getInt("id"));
                    passenger.setStatus(resultSet.getString("status"));
                    waitingList.add(passenger);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return waitingList;
    }

    // Method to promote a passenger from RAC to booked if there's space
//    private void promoteFromRAC() {
//        if (!racPassengers.isEmpty()) {
//            Passenger promotedPassenger = racPassengers.remove(0);
//            promotedPassenger.setStatus("L");
//            passengers.add(promotedPassenger);
//        }
//    }
//
//    // Method to move a passenger from the waiting list to RAC if there's space
//    public void moveFromWaitingListToRAC() {
//        if (!waitingList.isEmpty() && racPassengers.size() < MAX_RAC) {
//            Passenger passengerToMove = waitingList.remove(0);
//            passengerToMove.setStatus("RAC");
//            racPassengers.add(passengerToMove);
//        }
//    }

    private void promoteFromRAC() {
        if (!racPassengers.isEmpty()) {
            Passenger promotedPassenger = racPassengers.remove(0);
            promotedPassenger.setStatus("L");

            // Update database
            try (Connection connection = DB.getConnection()) {
                String sql = "DELETE FROM rac_passengers WHERE id = ?";
                try (PreparedStatement statement = connection.prepareStatement(sql)) {
                    statement.setInt(1, promotedPassenger.getId());
                    statement.executeUpdate();
                }
                sql = "INSERT INTO passengers (name, age, berth_preference, status) VALUES (?, ?, ?, ?)";
                try (PreparedStatement statement = connection.prepareStatement(sql)) {
                    statement.setString(1, promotedPassenger.getName());
                    statement.setInt(2, promotedPassenger.getAge());
                    statement.setString(3, promotedPassenger.getBerthPreference());
                    statement.setString(4, promotedPassenger.getStatus());
                    statement.executeUpdate();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

            passengers.add(promotedPassenger);
        }
    }

    public void moveFromWaitingListToRAC() {
        if (!waitingList.isEmpty() && racPassengers.size() < MAX_RAC) {
            Passenger passengerToMove = waitingList.remove(0);
            passengerToMove.setStatus("RAC");

            // Update database
            try (Connection connection = DB.getConnection()) {
                String sql = "DELETE FROM waiting_list WHERE id = ?";
                try (PreparedStatement statement = connection.prepareStatement(sql)) {
                    statement.setInt(1, passengerToMove.getId());
                    statement.executeUpdate();
                }
                sql = "INSERT INTO rac_passengers (name, age, berth_preference, status) VALUES (?, ?, ?, ?)";
                try (PreparedStatement statement = connection.prepareStatement(sql)) {
                    statement.setString(1, passengerToMove.getName());
                    statement.setInt(2, passengerToMove.getAge());
                    statement.setString(3, passengerToMove.getBerthPreference());
                    statement.setString(4, passengerToMove.getStatus());
                    statement.executeUpdate();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

            racPassengers.add(passengerToMove);
        }
    }


    // Method to print all booked tickets and the total number of filled tickets
    public void printBookedTickets() {
        System.out.println("List of Booked Tickets:");
        int totalFilledTickets = 0;

        // Print booked passengers
        for (Passenger passenger : passengers) {
            System.out.println(passenger);
            totalFilledTickets++;
        }

        // Print RAC passengers
        for (Passenger passenger : racPassengers) {
            System.out.println(passenger);
            totalFilledTickets++;
        }

        System.out.println("Total number of filled tickets: " + totalFilledTickets);
    }

    // Method to print all available (unoccupied) tickets and the total number of unoccupied tickets
    public void printAvailableTickets() {
        System.out.println("List of Available (Unoccupied) Tickets:");
        int totalAvailableTickets = 0;

        // Print available (unoccupied) seats
        if (passengers.size() < MAX_SEATS) {
            for (int i = passengers.size() + 1; i <= MAX_SEATS; i++) {
                System.out.println("Seat Number: " + i + " - Status: L (Lower Berth)");
                totalAvailableTickets++;
            }
        }

        // Print available (unoccupied) RAC seats
        if (racPassengers.size() < MAX_RAC) {
            for (int i = racPassengers.size() + 1; i <= MAX_RAC; i++) {
                System.out.println("RAC Seat Number: " + i);
                totalAvailableTickets++;
            }
        }

        // Print available (unoccupied) waiting list seats
        if (waitingList.size() < MAX_WL) {
            for (int i = waitingList.size() + 1; i <= MAX_WL; i++) {
                System.out.println("Waiting List Seat Number: " + i);
                totalAvailableTickets++;
            }
        }

        System.out.println("Total number of available (unoccupied) tickets: " + totalAvailableTickets);
    }

    // Helper method to count passengers with a specific berth preference
    private int countBerthPreference(String preference) {
        int count = 0;
        for (Passenger passenger : passengers) {
            if (passenger.getBerthPreference().equals(preference)) {
                count++;
            }
        }
        return count;
    }
}