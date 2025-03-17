package GraphicalUserInterface.Controllers;

import BusinessLogic.Utility;
import DataModel.Employee;
import GraphicalUserInterface.Views.EmployeeView;

import javax.swing.*;
import java.util.List;
import java.util.Map;

public class UtilityController {
    private final Utility utility;


    public UtilityController(Utility utility, EmployeeView employeeView) {
        this.utility = utility;

    }

    public void showHighWorkDurationEmployees() {
        List<Employee> highDurationEmployees = utility.getEmployeesWithHighWorkDuration();
        StringBuilder result = new StringBuilder("Employees with Work Duration > 40 Hours:\n");
        if (highDurationEmployees.isEmpty()) {
            result.append("No employees found.");
        } else {
            for (Employee emp : highDurationEmployees) {
                int duration = utility.getTaskManagement().calculateEmployeeWorkDuration(emp.getIdEmployee());
                result.append(String.format("%d - %s: %d hours%n",
                        emp.getIdEmployee(), emp.getName(), duration));
            }
        }
        JOptionPane.showMessageDialog(null, result.toString(),
                "High Work Duration Employees", JOptionPane.INFORMATION_MESSAGE);
    }

    public void showTaskStatusCounts() {
        Map<String, Map<String, Integer>> statusCounts = utility.getTaskStatusCounts();
        StringBuilder result = new StringBuilder("Task Status Counts per Employee:\n");
        if (statusCounts.isEmpty()) {
            result.append("No employees with assigned tasks.");
        } else {
            for (Map.Entry<String, Map<String, Integer>> entry : statusCounts.entrySet()) {
                String name = entry.getKey();
                Map<String, Integer> counts = entry.getValue();
                result.append(String.format("%s - Completed: %d, Uncompleted: %d%n",
                        name, counts.get("COMPLETED"), counts.get("UNCOMPLETED")));
            }
        }
        JOptionPane.showMessageDialog(null, result.toString(),
                "Task Status Counts", JOptionPane.INFORMATION_MESSAGE);
    }
}