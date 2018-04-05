package com.palyaeva.validation;

import com.palyaeva.entity.Manager;
import com.palyaeva.entity.Person;

import java.util.List;

public interface PersonValidator {

    void validateName(String name, String nameType) throws ValidationException;

    void validateBirthYear(String year) throws ValidationException;

    void validatePhoneNumber(String number) throws ValidationException;

    void validateManager(String managerName, List<Person> personList) throws ValidationException;

    void validateDepartment(String department) throws ValidationException;
}
