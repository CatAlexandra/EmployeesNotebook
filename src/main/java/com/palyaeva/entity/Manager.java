package com.palyaeva.entity;

/**
 * Class for storing managers information.
 * Has field {@link Manager#department} in addition to Person's fields
 */
public class Manager extends Person {

    private final String department;

    public Manager(String firstName, String lastName, int birthYear, String phoneNumber, String department) {
        super(firstName, lastName, birthYear, phoneNumber);
        this.department = department;
    }

    public String getDepartment() {
        return department;
    }
}
