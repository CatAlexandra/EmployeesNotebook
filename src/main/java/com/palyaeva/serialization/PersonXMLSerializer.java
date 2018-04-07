package com.palyaeva.serialization;

import com.palyaeva.entity.Employee;
import com.palyaeva.entity.Manager;
import com.palyaeva.entity.Person;
import com.palyaeva.validation.PersonValidator;
import com.palyaeva.validation.ValidationException;
import org.jdom2.DocType;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class PersonXMLSerializer implements PersonSerializer {

    /*
     * Values of constants are tag names in xml file
     */
    private static final String ROOT_ELEMENT_NAME = "persons";

    private static final String MANAGER_CONTAINER_ELEMENT_NAME = "managers";
    private static final String EMPLOYEE_CONTAINER_ELEMENT_NAME = "employees";

    private static final String PERSON_ELEMENT_NAME = "person";
    private static final String MANAGER_ELEMENT_NAME = "manager";
    private static final String EMPLOYEE_ELEMENT_NAME = "employee";

    private static final String FIRST_NAME = "first_name";
    private static final String LAST_NAME = "last_name";
    private static final String BIRTH_YEAR = "birth_year";
    private static final String PHONE_NUMBER = "phone_number";
    private static final String DEPARTMENT = "department";
    private static final String MANAGER = "manager";

    private final PersonValidator validator;

    public PersonXMLSerializer(PersonValidator validator) {
        this.validator = validator;
    }

    /**
     * Saves given list of persons to xml file specified by file path.
     *
     * @param persons  list of Persons
     * @param filePath path to data file
     */
    public void serialize(List<Person> persons, String filePath) {
        Element rootElement = new Element(ROOT_ELEMENT_NAME);

        Element managers = new Element(MANAGER_CONTAINER_ELEMENT_NAME);
        Element employees = new Element(EMPLOYEE_CONTAINER_ELEMENT_NAME);

        for (Person person : persons) {
            if (person instanceof Manager) {
                managers.addContent(serializeManager((Manager) person));
            } else if (person instanceof Employee) {
                employees.addContent(serializeEmployee((Employee) person));
            } else {
                System.err.println("Unknown type: " + person.getClass().getName());
            }
        }

        rootElement.addContent(managers);
        rootElement.addContent(employees);

        Document document = new Document(rootElement);
        document.setDocType(new DocType("xml"));

        createDirectories(filePath);

        try (FileOutputStream stream = new FileOutputStream(filePath)) {
            XMLOutputter xmlOutputter = new XMLOutputter(Format.getPrettyFormat());
            xmlOutputter.output(document, stream);
        } catch (IOException exception) {
            System.err.println("Problem occurred when saving file: " + exception.getMessage());
            // logging exception :)
        }
    }

    /**
     * Creates new directory for data file if it doesn't exist.
     * (In our case creates directory "data")
     * And does nothing if directory exists.
     *
     * @param filePath full path to data file
     */
    private void createDirectories(String filePath) {
        String path = filePath.substring(0, filePath.lastIndexOf(File.separatorChar));
        Path directoryPath = Paths.get(path);
        try {
            Files.createDirectories(directoryPath).toFile();
        } catch (IOException exception) {
            System.err.println("Problem occurred with creating directory: " + exception.getMessage());
        }
    }

    private Element serializePerson(Person person) {
        Element personElement = new Element(PERSON_ELEMENT_NAME);
        personElement.addContent(new Element(FIRST_NAME).setText(person.getFirstName()));
        personElement.addContent(new Element(LAST_NAME).setText(person.getLastName()));
        personElement.addContent(new Element(BIRTH_YEAR).setText(String.valueOf(person.getBirthYear())));
        personElement.addContent(new Element(PHONE_NUMBER).setText(person.getPhoneNumber()));

        return personElement;
    }

    private Element serializeManager(Manager manager) {
        Element managerElement = serializePerson(manager);
        managerElement.setName(MANAGER_ELEMENT_NAME);
        managerElement.addContent(new Element(DEPARTMENT).setText(manager.getDepartment()));

        return managerElement;
    }

    private Element serializeEmployee(Employee employee) {
        Element employeeElement = serializePerson(employee);
        employeeElement.setName(EMPLOYEE_ELEMENT_NAME);
        employeeElement.addContent(new Element(MANAGER).setText(employee.getManager()));

        return employeeElement;
    }

    /**
     * Loads persons from xml file into list.
     *
     * @param filePath path to data file
     * @return List of Persons received from file
     */
    public List<Person> deserialize(String filePath) {
        SAXBuilder builder = new SAXBuilder();
        File xmlFile = new File(filePath);
        List<Person> personList = new ArrayList<>();
        if (xmlFile.exists()) {
            try {
                Document document = builder.build(xmlFile);
                Element rootNode = document.getRootElement();
                if (rootNode != null) {
                    Element managersElement = rootNode.getChild(MANAGER_CONTAINER_ELEMENT_NAME);
                    personList.addAll(deserializeManagers(managersElement));

                    Element employeesElement = rootNode.getChild(EMPLOYEE_CONTAINER_ELEMENT_NAME);
                    personList.addAll(deserializeEmployees(employeesElement, personList));
                }
            } catch (IOException exception) {
                System.err.println("Problem occurred while reading file: " + exception.getMessage());
                // logging exception :)
            } catch (JDOMException exception) {
                System.err.println("Bad xml file: " + exception.getMessage());
                // logging exception :)
            }
        }
        return personList;
    }

    private List<Employee> deserializeEmployees(Element employeesElement, List<Person> personList) {
        List<Employee> employeesList = new ArrayList<>();

        if (employeesElement != null) {
            List<Element> employeesElementChildren = employeesElement.getChildren();
            for (Element child : employeesElementChildren) {
                try {
                    String firstName = child.getChildText(FIRST_NAME).trim();
                    String lastName = child.getChildText(LAST_NAME).trim();
                    String birthYear = child.getChildText(BIRTH_YEAR).trim();
                    String phoneNumber = child.getChildText(PHONE_NUMBER).trim();
                    String manager = child.getChildText(MANAGER).trim();

                    validator.validateName(firstName, "First name");
                    validator.validateName(lastName, "Last name");
                    validator.validateBirthYear(birthYear);
                    validator.validatePhoneNumber(phoneNumber);
                    validator.validateManager(manager, personList);

                    Employee employee = new Employee(firstName, lastName, Integer.parseInt(birthYear),
                            phoneNumber, manager);
                    employeesList.add(employee);
                } catch (ValidationException exception) {
                    System.err.println(exception.getMessage());
                }
            }
        }
        return employeesList;
    }

    private List<Manager> deserializeManagers(Element managersElement) {
        List<Manager> managersList = new ArrayList<>();

        if (managersElement != null) {
            List<Element> managersElementChildren = managersElement.getChildren();
            for (Element child : managersElementChildren) {
                try {
                    String firstName = child.getChildText(FIRST_NAME).trim();
                    String lastName = child.getChildText(LAST_NAME).trim();
                    String birthYear = child.getChildText(BIRTH_YEAR).trim();
                    String phoneNumber = child.getChildText(PHONE_NUMBER).trim();
                    String department = child.getChildText(DEPARTMENT).trim();

                    validator.validateName(firstName, "First name");
                    validator.validateName(lastName, "Last name");
                    validator.validateBirthYear(birthYear);
                    validator.validatePhoneNumber(phoneNumber);
                    validator.validateDepartment(department);

                    Manager manager = new Manager(firstName, lastName, Integer.parseInt(birthYear),
                            phoneNumber, department);
                    managersList.add(manager);
                } catch (ValidationException exception) {
                    System.err.println(exception.getMessage());
                }
            }
        }
        return managersList;
    }
}
