package org.railways;

public class Passenger {
    private int id;
    private String name;
    private int age;
    private String berthPreference;
    private String status;

    public Passenger(String name, int age, String berthPreference) {
        this.name = name;
        this.age = age;
        this.berthPreference = berthPreference;
        this.status = "";
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }

    public String getBerthPreference() {
        return berthPreference;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Passenger ID: " + id + ", Name: " + name + ", Age: " + age +
                ", Berth Preference: " + berthPreference + ", Status: " + status;
    }
}
