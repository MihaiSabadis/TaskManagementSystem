package GraphicalUserInterface.Controllers;

import BusinessLogic.TaskManagement;
import DataModel.Employee;
import DataModel.Task;
import GraphicalUserInterface.Views.EmployeeView;
import GraphicalUserInterface.Views.TaskView;

import javax.swing.*;
import java.util.List;

public class EmployeeController {
    private final TaskManagement taskManagement;
    private final EmployeeView employeeView;
    private final TaskView taskView;
    private int nextEmployeeId = 1;

    public EmployeeController(TaskManagement taskManagement, EmployeeView employeeView, TaskView taskView) {
        this.taskManagement = taskManagement;
        this.employeeView = employeeView;
        this.taskView = taskView;

        employeeView.getEmployeeList().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                updateAssignedTaskList();
            }
        });
    }

    public void addEmployee() {
        String name = JOptionPane.showInputDialog("Enter Employee Name:");
        if (name != null && !name.trim().isEmpty()) {
            Employee employee = new Employee(nextEmployeeId++, name);
            taskManagement.addEmployee(employee);
            String employeeEntry = String.format("%d - %s", employee.getIdEmployee(), employee.getName());
            employeeView.getEmployeeListModel().addElement(employeeEntry);
            updateAssignedTaskList();
        }
    }

    public void populateEmployeeList() {
        for (Employee emp : taskManagement.getAllEmployees()) {
            String employeeEntry = String.format("%d - %s", emp.getIdEmployee(), emp.getName());
            employeeView.getEmployeeListModel().addElement(employeeEntry);
            nextEmployeeId = Math.max(nextEmployeeId, emp.getIdEmployee() + 1);
        }
    }

    private void updateAssignedTaskList() {
        taskView.getAssignedTaskListModel().clear();
        String selectedEmployeeEntry = employeeView.getEmployeeList().getSelectedValue();
        if (selectedEmployeeEntry != null) {
            Employee employee = findEmployeeByEntry(selectedEmployeeEntry);
            if (employee != null) {
                List<Task> tasks = taskManagement.getEmployeeTaskAssignments().get(employee);
                if (tasks != null) {
                    for (Task task : tasks) {
                        String displayText = String.format("%d - %s (%s)",
                                task.getIdTask(), task.getNameTask(), task.getStatusTask());
                        taskView.getAssignedTaskListModel().addElement(displayText);
                    }
                }
            }
        }
    }

    public Employee findEmployeeByEntry(String entry) {
        int id = Integer.parseInt(entry.split(" - ")[0]);
        for (Employee emp : taskManagement.getAllEmployees()) {
            if (emp.getIdEmployee() == id) {
                return emp;
            }
        }
        JOptionPane.showMessageDialog(null, "Employee not found!");
        return null;
    }

}