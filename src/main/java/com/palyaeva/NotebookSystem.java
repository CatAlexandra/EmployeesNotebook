package com.palyaeva;

import com.palyaeva.entity.Employee;
import com.palyaeva.entity.Manager;
import com.palyaeva.entity.Person;
import com.palyaeva.serialization.PersonSerializer;
import com.palyaeva.validation.PersonValidator;
import com.palyaeva.validation.ValidationException;

import java.util.Comparator;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

import static com.palyaeva.Printer.printlnError;
import static com.palyaeva.Printer.printlnSuccess;

/**
 * Class for making following operations with notes:
 * <ul>
 * <li>Save notes to file and upload from file</li>
 * <li>Show all employees</li>
 * <li>Add new notes</li>
 * <li>Delete notes</li>
 * <li>Search for notes</li>
 * <li>Sort notes</li>
 * </ul>
 */
class NotebookSystem {

    private final String filePath;
    private final PersonSerializer serializer;
    private final PersonValidator validator;
    private final Scanner scanner = new Scanner(System.in);
    private List<Person> workers;

    NotebookSystem(String filePath, PersonSerializer serializer, PersonValidator validator) {
        this.serializer = serializer;
        this.filePath = filePath;
        this.validator = validator;
    }

    /**
     * Loads information from file into List<Person>
     * and provides saving data from that list into
     * file when user finishes working with the application and closes it
     */
    private void init() {
        workers = serializer.deserialize(filePath);
        Runtime.getRuntime().addShutdownHook(new Thread(() -> serializer.serialize(workers, filePath)));
    }

    /**
     * Main method for running application.
     * Gives user follows console menu:
     * <ul>
     * <li> 1 - show list of notes</li>
     * <li> 2 - add note:
     * <ul>
     * <li> 1 - employee</li>
     * <li> 2 - manager</li>
     * <li> 3 - back to menu</li>
     * </ul>
     * </li>
     * <li> 3 - delete note</li>
     * <li> 4 - search for notes:
     * <ul>
     * <li> 1 - by first name</li>
     * <li> 2 - by last name</li>
     * <li> 3 - by phone number</li>
     * <li> 4 - back to menu</li>
     * </ul>
     * </li>
     * <li> 5 - sort notes:
     * <ul>
     * <li> 1 - by last name</li>
     * <li> 2 - by year of birth</li>
     * <li> 3 - back to menu</li>
     * </ul>
     * </li>
     * <li> 0 - exit</li>
     * <li> ? - list of commands</li>
     * </ul>
     */
    void run() {
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
                    printWorkers(workers);
                    break;
                case "2":
                    addNote();
                    break;
                case "3":
                    deleteNote();
                    break;
                case "4":
                    search();
                    break;
                case "5":
                    sort();
                    break;
                default:
                    printlnError("Invalid command");
                    break;
            }
        }
    }

    /**
     * Deletes note from the list.
     * Note is specified by its number in the list.
     */
    /*
    There is a tricky test-case:
    If we delete a manager, we can't find him for employees who obey him.
    So next validation will face a problem.
    If we exit the program and start it again, we will get message "Manager not found!"
    and we will lost employees who had deleted manager, because of validateManager method.

    I don't know exactly how to handle it right, because every employee should have a manager.
     */
    private void deleteNote() {
        boolean isValid = false;
        if (workers.isEmpty()) {
            System.out.println("Workers list is empty - there is nothing to delete");
        } else {
            printWorkers(workers);
            System.out.println("Type number of employee to delete: ");
            while (!isValid) {
                String numberInput = scanner.nextLine();
                int number;
                try {
                    number = Integer.parseInt(numberInput) - 1;
                    if (workers.size() <= number) {
                        printlnError("Number should be less or equal to " + workers.size());
                    } else if (number < 0) {
                        printlnError("Number should be positive");
                    } else {
                        workers.remove(number);
                        printlnSuccess("Note was deleted successfully!");
                        isValid = true;
                    }
                } catch (NumberFormatException nfe) {
                    printlnError("Number is incorrect - should be integer number.");
                }
            }
        }
    }

    /**
     * Adds new note to the list - employee or manager.
     */
    private void addNote() {
        boolean isValid = false;
        System.out.println("Add new note.\n");
        while (!isValid) {
            System.out.println("Choose post of worker:\n" +
                    "1 - employee\n" +
                    "2 - manager\n\n" +
                    "3 - back to menu");
            String inputCommand = scanner.nextLine();
            switch (inputCommand) {
                case "1":
                    if (workers.isEmpty()) {
                        System.out.println("List of workers is empty. Please add a manager first.");
                        continue;
                    } else {
                        workers.add(readEmployee());
                        printlnSuccess("Employee was added successfully!");
                        isValid = true;
                        break;
                    }
                case "2":
                    workers.add(readManager());
                    printlnSuccess("Manager was added successfully!");
                    isValid = true;
                    break;
                case "3":
                    return;
                default:
                    printlnError("Invalid command");
                    break;
            }
        }
    }

    private Employee readEmployee() {
        String firstName = readName("First name: ", "First name");
        String lastName = readName("Last name: ", "Last name");
        int birthYear = Integer.parseInt(readParameter("Birth Year: ",
                validator::validateBirthYear));
        String phoneNumber = readParameter("Phone Number: ", validator::validatePhoneNumber);
        String manager = readManagersName();
        return new Employee(firstName, lastName, birthYear, phoneNumber, manager);
    }

    private Manager readManager() {
        String firstName = readName("First name: ", "First name");
        String lastName = readName("Last name: ", "Last name");
        int birthYear = Integer.parseInt(readParameter("Birth Year: ",
                validator::validateBirthYear));
        String phoneNumber = readParameter("Phone Number: ", validator::validatePhoneNumber);
        String department = readParameter("Department: ", validator::validateDepartment);
        return new Manager(firstName, lastName, birthYear, phoneNumber, department);
    }

    /**
     * Method for reading parameters with same signature in validation methods.
     * Reads parameters: birth year, phone number or department.
     *
     * @param promptForInput prompt for user input
     * @param validator      reference to validation method for that parameter
     * @return read parameter
     */
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
                printlnError(exception.getMessage());
            }
        }
        return "";
    }

    /**
     * Reads first name or last name
     *
     * @param promptForInput prompt for user input
     * @param nameType       "first name" or "last name"
     * @return read name
     */
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
                printlnError(exception.getMessage());
            }
        }
        return "";
    }

    /**
     * Method for reading full name of employee's manager
     *
     * @return full name - first name and last name
     */
    private String readManagersName() {
        boolean isValid = false;
        while (!isValid) {
            System.out.println("Manager's first name:");
            String managerFirstName = scanner.nextLine().trim();
            System.out.println("Manager's last name:");
            String managerLastName = scanner.nextLine().trim();
            String fullName = managerFirstName + " " + managerLastName;
            try {
                validator.validateManager(fullName, workers);
                isValid = true;
                return fullName;
            } catch (ValidationException exception) {
                printlnError(exception.getMessage());
            }
        }
        return "";
    }

    /**
     * Search for notes:
     * <ul>
     * <li> 1 by first name</li>
     * <li> 2 by last name</li>
     * <li> 3 by phone number</li>
     * <li> 4 back to menu</li>
     * </ul>
     */
    private void search() {
        boolean isValid = false;
        while (!isValid) {
            System.out.println("Search by:\n" +
                    "1 - first name\n" +
                    "2 - last name\n" +
                    "3 - phone number\n\n" +
                    "4 - back to menu");
            String inputCommand = scanner.nextLine();
            List<Person> resultList;
            switch (inputCommand) {
                case "1":
                    resultList = findByFirstName();
                    isValid = true;
                    break;
                case "2":
                    resultList = findByLastName();
                    isValid = true;
                    break;
                case "3":
                    resultList = findByPhoneNumber();
                    isValid = true;
                    break;
                case "4":
                    return;
                default:
                    printlnError("Invalid command");
                    continue;
            }
            if (resultList.isEmpty()) {
                System.out.println("No notes found");
            } else {
                printWorkers(resultList);
            }
        }
    }

    /**
     * Searching by last name by "contains" method - you can type only part of the name
     *
     * @return list of found workers
     */
    private List<Person> findByLastName() {
        System.out.println("Last name: ");
        String lastName = scanner.nextLine().trim().toLowerCase();
        return workers.stream().filter(person ->
                person.getLastName().toLowerCase().contains(lastName))
                .collect(Collectors.toList());
    }

    /**
     * Searching by first name by "contains" method - you can type only part of the name
     *
     * @return list of found workers
     */
    private List<Person> findByFirstName() {
        System.out.println("First name: ");
        String firstName = scanner.nextLine().trim().toLowerCase();
        return workers.stream()
                .filter(person -> person.getFirstName().toLowerCase().contains(firstName))
                .collect(Collectors.toList());
    }

    /**
     * Searching by phone number by "equals" method - you should type full number
     *
     * @return list of found workers
     */
    private List<Person> findByPhoneNumber() {
        String phoneNumber = readParameter("Phone number: ", validator::validatePhoneNumber);
        return workers.stream()
                .filter(person -> person.getPhoneNumber().equals(phoneNumber))
                .collect(Collectors.toList());
    }

    /**
     * Method for list sorting.
     * Sorting notes:
     * <ul>
     * <li> 1 by last name</li>
     * <li> 2 by year of birth</li>
     * <li> 3 back to menu</li>
     * </ul>
     */
    private void sort() {
        boolean isValid = false;
        while (!isValid) {
            System.out.println("Sort by:\n" +
                    "1 - last name\n" +
                    "2 - year of birth\n\n" +
                    "3 - back to menu");
            String inputCommand = scanner.nextLine();
            switch (inputCommand) {
                case "1":
                    workers.sort((worker1, worker2) ->
                            worker1.getLastName().compareToIgnoreCase(worker2.getLastName()));
                    isValid = true;
                    break;
                case "2":
                    workers.sort(Comparator.comparingInt(Person::getBirthYear));
                    isValid = true;
                    break;
                case "3":
                    return;
                default:
                    printlnError("Invalid command");
                    continue;
            }
            printWorkers(workers);
        }
    }

    private void printWorkers(List<Person> workersList) {
        if (workers.isEmpty()) {
            System.out.println("Workers list is empty");
        } else {
            String format = "%-5s%-9s%-12s%-12s%-5s%-13s%-20s%-20s\n";

            System.out.format(format,
                    "Id", "Post", "First name", "Last name", "Year",
                    "Phone number", "Manager", "Department");
            int i = 1;
            for (Person worker : workersList) {
                if (worker instanceof Manager) {
                    System.out.format(format,
                            i,
                            "Manager",
                            worker.getFirstName(),
                            worker.getLastName(),
                            worker.getBirthYear(),
                            worker.getPhoneNumber(),
                            "-",
                            ((Manager) worker).getDepartment());
                }
                if (worker instanceof Employee) {
                    System.out.format(format,
                            i,
                            "Employee",
                            worker.getFirstName(),
                            worker.getLastName(),
                            worker.getBirthYear(),
                            worker.getPhoneNumber(),
                            ((Employee) worker).getManager(),
                            "-");
                }
                i++;
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

    /**
     * Functional Interface for method {@link NotebookSystem#readParameter(String, Validator)}
     * Helps this method to accept different validation methods with same signature
     */
    @FunctionalInterface
    private interface Validator {
        void validate(String data) throws ValidationException;
    }
}
