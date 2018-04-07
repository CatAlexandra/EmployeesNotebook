package com.palyaeva.validation;

import com.palyaeva.entity.Person;

import java.util.List;

/**
 * Validates fields of
 * {@link com.palyaeva.entity.Manager}
 * and
 * {@link com.palyaeva.entity.Employee}
 */
public interface PersonValidator {

    /**
     * Validates first name and last name
     *
     * @param name     name of person
     * @param nameType is "first name" or "last name"
     * @throws ValidationException if name is incorrect
     */
    void validateName(String name, String nameType) throws ValidationException;

    /**
     * Validates year of birth
     *
     * @param year year of birth
     * @throws ValidationException if year is incorrect
     */
    void validateBirthYear(String year) throws ValidationException;

    /**
     * Validates phone number
     *
     * @param number phone number
     * @throws ValidationException if phone number is incorrect
     */
    void validatePhoneNumber(String number) throws ValidationException;

    /**
     * Checks if employee's manager exists
     *
     * @param managerName manager's first and last name
     * @param personList  list of all persons
     * @throws ValidationException if manager is not found
     */
    void validateManager(String managerName, List<Person> personList) throws ValidationException;

    /**
     * Validates manager's department
     *
     * @param department manager's department
     * @throws ValidationException if department is incorrect
     */
    void validateDepartment(String department) throws ValidationException;
}
