package com.palyaeva.validation;

import com.palyaeva.entity.Manager;
import com.palyaeva.entity.Person;

import java.util.List;

/**
 * Default implementation of {@link PersonValidator}
 */
public class PersonValidatorImpl implements PersonValidator {
    /**
     * Validates first name and last name
     * Valid name is longer then 1 letter and shorter than 12 letters
     * and contains only letters
     *
     * @param name     first or last name of person
     * @param nameType is "first name" or "last name"
     * @throws ValidationException if name is incorrect
     */
    @Override
    public void validateName(String name, String nameType) throws ValidationException {
        if (name == null) {
            throw new ValidationException("No such child. " + nameType + " is NULL");
        } else {

            boolean isValid = name.chars().allMatch(Character::isLetter) &&
                    name.length() >= 2 && name.length() <= 11;

            if (!isValid) {
                throw new ValidationException(nameType +
                        " should not contain non-letter symbols," +
                        " and should be longer than 1 " +
                        " and shorter than 12 letters");
            }
        }
    }

    /**
     * Checks that year is >= 1900 and <= 2002
     *
     * @param birthYear year
     * @throws ValidationException if year is incorrect
     */
    @Override
    public void validateBirthYear(String birthYear) throws ValidationException {
        if (birthYear == null) {
            throw new ValidationException("No such child. Birth year is NULL");
        } else {
            try {
                int year = Integer.parseInt(birthYear);
                if (year < 1900 || year > 2002) {
                    throw new ValidationException("Invalid year of birth");
                }
            } catch (NumberFormatException nfe) {
                throw new ValidationException("Invalid year of birth");
            }
        }
    }

    /**
     * Checks only russian phone numbers
     *
     * @param number russian phone number -
     *               begins with +7/7/8 and contains 10 numbers
     * @throws ValidationException if number is incorrect
     */
    @Override
    public void validatePhoneNumber(String number) throws ValidationException {
        if (number == null) {
            throw new ValidationException("No such child. Phone number is NULL");
        } else {
            boolean isValid = number.matches("^((\\+7|7|8)([0-9]){10})$");
            if (!isValid) {
                throw new ValidationException("Invalid phone number");
            }
        }
    }

    /**
     * Checks if manager with given full name exists
     *
     * @param managerName manager's first and last name
     * @param persons     list of all workers - managers and employees
     * @throws ValidationException if manager is not found
     */
    @Override
    public void validateManager(String managerName, List<Person> persons) throws ValidationException {
        if (managerName == null) {
            throw new ValidationException("No such child. Manager is NULL");
        } else {
            boolean isFound = false;

            String[] fullName = managerName.split("\\s+");
            if (fullName.length != 2) {
                throw new ValidationException("Invalid manager full name - " +
                        "should contain first name and last name of existing manager.");
            } else {
                String firstName = fullName[0];
                String lastName = fullName[1];

                for (Person person : persons) {
                    if (person instanceof Manager
                            && person.getFirstName().equals(firstName)
                            && person.getLastName().equals(lastName)) {
                        isFound = true;
                        break;
                    }
                }
                if (!isFound) {
                    throw new ValidationException("Manager not found!");
                }
            }
        }
    }

    /**
     * Checks that department name contains only letters and spaces
     *
     * @param department manager's department
     * @throws ValidationException if department name is incorrect
     */
    @Override
    public void validateDepartment(String department) throws ValidationException {
        if (department == null) {
            throw new ValidationException("No such child. department is NULL.");
        } else {
            boolean isValid = department.matches("[A-Za-z]+\\s?");
            if (!isValid) {
                throw new ValidationException("Invalid department name");
            }
        }
    }
}
