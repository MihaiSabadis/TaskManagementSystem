package BusinessLogic;

import DataModel.Employee;
import DataModel.Task;

import java.util.*;
import java.util.stream.Collectors;

public class Utility {
    private final TaskManagement taskManagement;

    public Utility(TaskManagement taskManagement) {
        this.taskManagement = taskManagement;
    }

    /**
     * Returns a list of employees with work duration > 40 hours, sorted in ascending order by work duration.
     * @return List<Employee> sorted by work duration
     */
    public List<Employee> getEmployeesWithHighWorkDuration() {
        List<Employee> employees = new ArrayList<>(taskManagement.getAllEmployees());

        return employees.stream()
                .filter(employee -> taskManagement.calculateEmployeeWorkDuration(employee.getIdEmployee()) > 40)
                .sorted(Comparator.comparingInt(emp -> taskManagement.calculateEmployeeWorkDuration(emp.getIdEmployee())))
                .collect(Collectors.toList());
    }

    /**
     * Calculates the number of completed and uncompleted tasks for each employee.
     * Returns a map with employee names as keys and a nested map of task status counts as values.
     * @return Map<String, Map<String, Integer>> with task status counts per employee
     */
    public Map<String, Map<String, Integer>> getTaskStatusCounts() {
        Map<String, Map<String, Integer>> result = new HashMap<>();

        Map<Employee, List<Task>> assignments = taskManagement.getEmployeeTaskAssignments();
        for (Map.Entry<Employee, List<Task>> entry : assignments.entrySet()) {
            Employee employee = entry.getKey();
            List<Task> tasks = entry.getValue();

            Map<String, Integer> statusCounts = new HashMap<>();
            statusCounts.put("COMPLETED", 0);
            statusCounts.put("UNCOMPLETED", 0);

            if (tasks != null) {
                for (Task task : tasks) {
                    String status = task.getStatusTask();
                    statusCounts.put(status, statusCounts.get(status) + 1);
                }
            }

            result.put(employee.getName(), statusCounts);
        }

        return result;
    }

    public TaskManagement getTaskManagement() {
        return taskManagement;
    }
}