package com.palyaeva.entity;

/**
 * Class for storing employees information.
 * Has field {@link Employee#manager} in addition to Person's fields
 */
public class Employee extends Person {

    private final String manager;

    public Employee(String firstName, String lastName, int birthYear, String phoneNumber, String manager) {
        super(firstName, lastName, birthYear, phoneNumber);
        this.manager = manager;
    }

    public String getManager() {
        return manager;
    }
}
