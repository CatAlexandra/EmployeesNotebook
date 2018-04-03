package com.palyaeva.serialization;

import com.palyaeva.entity.Person;

import java.util.List;

public interface PersonSerializer {

    void serialize(List<Person> persons, String file);

    List<Person> deserialize(String file);
}
