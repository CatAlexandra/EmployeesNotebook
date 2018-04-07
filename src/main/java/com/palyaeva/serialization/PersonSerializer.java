package com.palyaeva.serialization;

import com.palyaeva.entity.Person;

import java.util.List;

/**
 * Interface for data serialization and deserialization
 * (saving data from program to file
 * and loading data from file into program)
 */
public interface PersonSerializer {
    /**
     * Saves given list of persons to file specified by file path.
     *
     * @param persons  list of Persons
     * @param filePath path to data file
     */
    void serialize(List<Person> persons, String filePath);

    /**
     * Loads persons from file into list.
     *
     * @param filePath path to data file
     * @return List of Persons received from file
     */
    List<Person> deserialize(String filePath);
}
