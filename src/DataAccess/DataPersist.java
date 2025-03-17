package DataAccess;

import DataModel.Employee;
import DataModel.Task;

import java.io.*;
import java.util.*;

public class DataPersist {

    // Save tasks
    public void saveTasks(Set<Task> tasks, String fileName) {
        try (FileOutputStream fileOut = new FileOutputStream(fileName);
             ObjectOutputStream oos = new ObjectOutputStream(fileOut)) {
            oos.writeObject(tasks);
            System.out.println("Tasks have been serialized successfully.");
        } catch (IOException e) {
            throw new RuntimeException("Error saving tasks: " + e.getMessage(), e);
        }
    }

    // Load tasks
    public Set<Task> loadTasks(String fileName) {
        try (FileInputStream fileInputStream = new FileInputStream(fileName);
             ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream)) {
            return (Set<Task>) objectInputStream.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("Error loading tasks: " + e.getMessage(), e);
        }
    }

    // Save employee-task assignments
    public void saveEmployeeTaskAssignments(Map<Employee, List<Task>> assignments, String fileName) {
        try (FileOutputStream fileOut = new FileOutputStream(fileName);
             ObjectOutputStream oos = new ObjectOutputStream(fileOut)) {
            oos.writeObject(assignments);
            System.out.println("Employee-task assignments have been serialized successfully.");
        } catch (IOException e) {
            throw new RuntimeException("Error saving employee-task assignments: " + e.getMessage(), e);
        }
    }

    // Load employee-task assignments
    public Map<Employee, List<Task>> loadEmployeeTaskAssignments(String fileName) {
        try (FileInputStream fileInputStream = new FileInputStream(fileName);
             ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream)) {
            return (Map<Employee, List<Task>>) objectInputStream.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("Error loading employee-task assignments: " + e.getMessage(), e);
        }
    }
}