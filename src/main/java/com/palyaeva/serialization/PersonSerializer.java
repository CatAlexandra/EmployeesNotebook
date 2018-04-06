package com.palyaeva.serialization;

import com.palyaeva.entity.Person;

import java.util.List;

/**
 * Interface for data serialization and deserialization
 * (saving data from program to file
 * and loading data from file into program)
 * Implemented in {@link PersonXMLSerializer}
 */
public interface PersonSerializer {
    /**
     * Saves given list of persons to xml file specified by file path.
     * Used when user finishes working with the application and closes it.
     *
     * @param persons  list of Persons
     * @param filePath path to data file
     */
    void serialize(List<Person> persons, String filePath);

    /**
     * Loads persons from xml file into list.
     * Used when user starts working with application.
     *
     * @param filePath path to data file
     * @return List of Persons received from file
     */
    List<Person> deserialize(String filePath);
}
