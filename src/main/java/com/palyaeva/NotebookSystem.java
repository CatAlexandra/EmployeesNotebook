package com.palyaeva;

import com.palyaeva.entity.Person;
import com.palyaeva.serialization.PersonSerializer;
import com.palyaeva.validation.PersonValidator;

import java.util.List;

public class NotebookSystem {

    private List<Person> people;

    private final String filePath;

    private final PersonSerializer serializer;

    private final PersonValidator validator;

    public NotebookSystem(String filePath, PersonSerializer serializer, PersonValidator validator) {
        this.serializer = serializer;
        this.filePath = filePath;
        this.validator = validator;
    }

    private void init() {
        people = serializer.deserialize(filePath);
        Runtime.getRuntime().addShutdownHook(new Thread(() -> serializer.serialize(people, filePath)));
    }

    public void run() {
        init();
        boolean isFinish = false;
        while (!isFinish) {
            // switch
        }
    }
}
