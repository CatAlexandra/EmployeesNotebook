package com.palyaeva;

import com.palyaeva.entity.Employee;
import com.palyaeva.entity.Manager;
import com.palyaeva.entity.Person;
import com.palyaeva.serialization.PersonSerializer;
import com.palyaeva.validation.PersonValidator;
import com.palyaeva.validation.ValidationException;

import java.text.MessageFormat;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

/**
 *
 *
 *
 *
 *
 *
 *
 *
 * */
public class NotebookSystem {

    private List<Person> workers;

    private final String filePath;

    private final PersonSerializer serializer;

    private final PersonValidator validator;

    private final Scanner scanner = new Scanner(System.in);

    public NotebookSystem(String filePath, PersonSerializer serializer, PersonValidator validator) {
        this.serializer = serializer;
        this.filePath = filePath;
        this.validator = validator;
    }

    private void init() {
        workers = serializer.deserialize(filePath);
        Runtime.getRuntime().addShutdownHook(new Thread(() -> serializer.serialize(workers, filePath)));
    }

    public void run() {
        init();
        boolean isFinish = false;

        System.out.println("Hello! I'm a notebook for keeping information about employees.");
        System.out.println("You can see the list of commands below:");
        printCommands();
        while (!isFinish) {
            System.out.println("Type a command");
            String inputCommand = scanner.nextLine();

            switch (inputCommand) {
                case "?":
                    printCommands();
                    break;
                case "0":
                    isFinish = true;
                    break;
                case "1":
                    System.out.println(workers.size());
                    printWorkersList();
                    break;
                case "2":
                    addNote();
                    break;
                case "3":
                    deleteNote();
                    break;
                case "4":

                    break;
                case "5":

                    break;
                default:
                    System.err.println("Invalid command");
                    break;
            }
            // switch

            // 1 show list
            // 2 add note
            // 3 delete note
            // 4 search for notes:
            //      1 by first name
            //      2 by last name
            //      3 by phone number
            // 5 sort notes:
            //      1 by last name
            //      2 by year of birth
            // 0 - exit
            // ? - list of commands
        }
    }

    private void deleteNote() {
        boolean isValid = false;
        while (!isValid) {
            System.out.println("Type number of employee to delete: ");
            String numberInput = scanner.nextLine();
            int number;
            try {
                number = Integer.parseInt(numberInput);
                if (workers.size() < number) {
                    System.err.println("Number should be less then " + workers.size());
                } else if (number < 0) {
                    System.err.println("Number should be non-negative");
                } else {
                    workers.remove(number);
                }
            } catch (NumberFormatException nfe) {
                System.err.println("Number is incorrect - should be integer number.");
            }
        }
    }

    private void addNote() {
        System.out.println("Add new note.\nChoose post of worker:\n1 - employee\n2 - manager");
        String inputCommand = scanner.nextLine();
        Person person;
        switch (inputCommand) {
            case "1":
                workers.add(readEmployee());
                break;
            case "2":
                workers.add(readManager());
                break;
            default:
                System.out.println("Invalid command");
                break;
        }
    }

    private Employee readEmployee() {
        String firstName = readName("First name: ", "First name");
        String lastName = readName("Last name: ", "Last name");
        int birthYear = Integer.parseInt(readParameter("Birth Year: ", validator::validateBirthYear));
        String phoneNumber = readParameter("Phone Number: ", validator::validatePhoneNumber);
        String manager = readManager("Manager's first name and last name: ");
        return new Employee(firstName, lastName, birthYear, phoneNumber, manager);
    }

    private Manager readManager() {
        String firstName = readName("First name: ", "First name");
        String lastName = readName("Last name: ", "Last name");
        int birthYear = Integer.parseInt(readParameter("Birth Year: ", validator::validateBirthYear));
        String phoneNumber = readParameter("Phone Number: ", validator::validatePhoneNumber);
        String department = readParameter("Department: ", validator::validateDepartment);
        return new Manager(firstName, lastName, birthYear, phoneNumber, department);
    }

    @FunctionalInterface
    interface Validator {
        void validate(String data) throws ValidationException;
    }

    // reads birth year, phone number, department
    private String readParameter(String promptForInput, Validator validator) {
        boolean isValid = false;
        while (!isValid) {
            System.out.println(promptForInput);
            String parameter = scanner.nextLine().trim();
            try {
                validator.validate(parameter);
                isValid = true;
                return parameter;
            } catch (ValidationException exception) {
                System.err.println(exception.getMessage());
            }
        }
        return "";
    }

    // reads first name and last name
    private String readName(String promptForInput, String nameType) {
        boolean isValid = false;
        while (!isValid) {
            System.out.println(promptForInput);
            String name = scanner.nextLine().trim();
            try {
                validator.validateName(name, nameType);
                isValid = true;
                return name;
            } catch (ValidationException exception) {
                System.err.println(exception.getMessage());
            }
        }
        return "";
    }

    private String readManager(String promptForInput) {
        boolean isValid = false;
        while (!isValid) {
            System.out.println(promptForInput);
            String managerName = scanner.nextLine().trim();
            try {
                validator.validateManager(managerName, workers);
                isValid = true;
                return managerName;
            } catch (ValidationException exception) {
                System.err.println(exception.getMessage());
            }
        }
        return "";
    }

    private List<Person> findByLastName(String lastName) {
        return workers.stream().filter(person -> person.getLastName().equals(lastName)).collect(Collectors.toList());
    }

    private void printWorkersList() {
        String template = "{0} {1} {2} {3} {4} {5}";
        //MessageFormat mf = new MessageFormat(template);

        System.out.println(MessageFormat.format(template, "Post", "First name", "Last name", "Year of birth", "Manager", "Department"));

        for (Person worker : workers) {

            if (worker instanceof Manager) {
                System.out.println(MessageFormat.format(template,
                        "Manager",
                        worker.getFirstName(),
                        worker.getLastName(),
                        worker.getBirthYear(),
                        " - ",
                        ((Manager) worker).getDepartment()));
            }
            if (worker instanceof Employee) {
                System.out.println(MessageFormat.format(template,
                        "Employee",
                        worker.getFirstName(),
                        worker.getLastName(),
                        worker.getBirthYear(),
                        ((Employee) worker).getManager(),
                        " - "));
            }
        }
    }

    private void printCommands() {
        System.out.println("1 - show all notes\n" +
                "2 - add note\n" +
                "3 - delete note\n" +
                "4 - search for notes\n" +
                "5 - sort notes\n" +
                "0 - exit\n" +
                "? - list of commands\n");
    }
}
