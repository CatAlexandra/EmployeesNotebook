package com.palyaeva;

import com.palyaeva.serialization.PersonSerializer;
import com.palyaeva.serialization.PersonXMLSerializer;
import com.palyaeva.validation.PersonValidator;
import com.palyaeva.validation.PersonValidatorImpl;

import java.io.File;

/**
 * Main class for running application.
 * Application is a notebook for storing information
 * about employees and managers and make operations with it.
 *
 * @see NotebookSystem
 */
public class Application {
    /*
     * Data file is "person.xml" in folder "data"
     */
    private static final String FILE_PATH = System.getProperty("user.dir") +
            File.separator +
            "data" +
            File.separator +
            "persons.xml";

    public static void main(String[] args) {
        PersonValidator validator = new PersonValidatorImpl();
        PersonSerializer serializer = new PersonXMLSerializer(validator);
        NotebookSystem notebookSystem = new NotebookSystem(FILE_PATH, serializer, validator);
        notebookSystem.run();
    }
}
