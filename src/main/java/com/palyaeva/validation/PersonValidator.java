package com.palyaeva.validation;

import com.palyaeva.entity.Manager;

import java.util.List;

public interface PersonValidator {

    void validateName(String name) throws ValidationException;

    void validateBirthYear(String year) throws ValidationException;

    void validatePhoneNumber(String number) throws ValidationException;

    void validateManager(String managerName, List<Manager> managers) throws ValidationException;

    void validateDepartment(String department) throws ValidationException;
}
