package com.palyaeva.entity;

public abstract class Person {

    private final String firstName;

    private final String lastName;

    private final int birthYear;

    private final String phoneNumber;

    public Person(String firstName, String lastName, int birthYear, String phoneNumber) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.birthYear = birthYear;
        this.phoneNumber = phoneNumber;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public int getBirthYear() {
        return birthYear;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }
}
