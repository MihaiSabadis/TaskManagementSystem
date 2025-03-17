package BusinessLogic;

import DataAccess.DataPersist;
import DataModel.Employee;
import DataModel.Task;

import java.util.*;

public class TaskManagement {

    private final Set<Task> allTasks = new HashSet<>();
    private final Map<Employee, List<Task>> employeeTaskAssignments = new HashMap<>();
    private final DataPersist dataPersist = new DataPersist();
    private static final String TASKS_FILE = "tasks.txt";
    private static final String ASSIGNMENTS_FILE = "employee_task_assignments.txt";

    public TaskManagement() {
        loadData(); // Load data on construction
    }

    // Add an employee to the system
    public void addEmployee(Employee employee) {
        employeeTaskAssignments.putIfAbsent(employee, new ArrayList<>());
    }

    // Add a task to the system
    public void addTask(Task task) {
        allTasks.add(task);
    }

    // Assign an existing task to an existing employee
    public void assignTaskToEmployee(int idEmployee, int idTask) {
        Employee targetEmployee = findEmployeeById(idEmployee);
        Task targetTask = findTaskById(idTask);

        if (targetEmployee == null) {
            throw new IllegalArgumentException("Employee with ID " + idEmployee + " not found.");
        }
        if (targetTask == null) {
            throw new IllegalArgumentException("Task with ID " + idTask + " not found.");
        }

        List<Task> assignedTasks = employeeTaskAssignments.get(targetEmployee);
        if (!assignedTasks.contains(targetTask)) {
            assignedTasks.add(targetTask);
            targetTask.setIsAssigned(true);
        }
    }

    // Calculate total work duration for an employee
    public int calculateEmployeeWorkDuration(int idEmployee) {
        Employee targetEmployee = findEmployeeById(idEmployee);
        if (targetEmployee != null && employeeTaskAssignments.containsKey(targetEmployee)) {
            int totalDuration = 0;
            for (Task task : employeeTaskAssignments.get(targetEmployee)) {
                if(task.getStatusTask().equals("UNCOMPLETED")) {
                    totalDuration += task.estimateDuration();
                }
            }
            return totalDuration;
        }
        return 0;
    }

    // Modify task status (placeholder due to immutability)
    public void modifyTaskStatus(int idEmployee, int idTask, boolean completeIt) {
        Employee targetEmployee = findEmployeeById(idEmployee);
        if (targetEmployee != null && employeeTaskAssignments.containsKey(targetEmployee)) {
            for (Task task : employeeTaskAssignments.get(targetEmployee)) {
                if (task.getIdTask() == idTask) {
                    if (completeIt) {
                        task.setStatusTask("COMPLETED");
                    }
                    else {
                        task.setStatusTask("UNCOMPLETED");
                    }
                    break;
                }
            }
        }
    }

    // Save all data
    public void saveData() {
        try {
            dataPersist.saveTasks(allTasks, TASKS_FILE);
            dataPersist.saveEmployeeTaskAssignments(employeeTaskAssignments, ASSIGNMENTS_FILE);
            System.out.println("Data saved successfully.");
        } catch (RuntimeException e) {
            System.err.println("Error saving data: " + e.getMessage());
        }
    }

    // Load all data
    public void loadData() {
        try {
            // Clear current state
            allTasks.clear();
            employeeTaskAssignments.clear();

            // Load employee-task assignments
            Map<Employee, List<Task>> assignments = dataPersist.loadEmployeeTaskAssignments(ASSIGNMENTS_FILE);
            employeeTaskAssignments.putAll(assignments);

            // Load standalone tasks
            Set<Task> tasks = dataPersist.loadTasks(TASKS_FILE);
            allTasks.addAll(tasks);
        } catch (RuntimeException e) {
            System.out.println("No previous data found or error loading: " + e.getMessage());
        }
    }

    // Helper methods
    private Employee findEmployeeById(int idEmployee) {
        return employeeTaskAssignments.keySet().stream()
                .filter(emp -> emp.getIdEmployee() == idEmployee)
                .findFirst()
                .orElse(null);
    }

    private Task findTaskById(int idTask) {
        return allTasks.stream()
                .filter(task -> task.getIdTask() == idTask)
                .findFirst()
                .orElse(null);
    }

    // Getters for external access
    public Set<Employee> getAllEmployees() {
        return new HashSet<>(employeeTaskAssignments.keySet()); // Defensive copy
    }

    public Set<Task> getAllTasks() {
        return new HashSet<>(allTasks); // Defensive copy
    }

    public Map<Employee, List<Task>> getEmployeeTaskAssignments() {
        return new HashMap<>(employeeTaskAssignments); // Defensive copy
    }
}