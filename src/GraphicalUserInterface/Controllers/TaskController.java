package GraphicalUserInterface.Controllers;

import BusinessLogic.TaskManagement;
import DataModel.ComplexTask;
import DataModel.Employee;
import DataModel.SimpleTask;
import DataModel.Task;
import GraphicalUserInterface.Views.EmployeeView;
import GraphicalUserInterface.Views.TaskView;

import javax.swing.*;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class TaskController {
    private final TaskManagement taskManagement;
    private final EmployeeView employeeView;
    private final TaskView taskView;
    private int nextTaskId = 1;

    public TaskController(TaskManagement taskManagement, EmployeeView employeeView, TaskView taskView) {
        this.taskManagement = taskManagement;
        this.employeeView = employeeView;
        this.taskView = taskView;
    }

    public void addTask() {
        String[] taskTypes = {"Simple Task", "Complex Task"};
        String taskType = (String) JOptionPane.showInputDialog(null, "Select Task Type:",
                "Task Type", JOptionPane.QUESTION_MESSAGE, null, taskTypes, taskTypes[0]);

        if (taskType == null) return;

        String taskName = JOptionPane.showInputDialog("Enter Task Name:");
        if (taskName == null || taskName.trim().isEmpty()) return;

        Task task;
        if (taskType.equals("Simple Task")) {
            task = createSimpleTask(taskName);
        } else {
            task = createComplexTask(taskName);
        }

        if (task != null) {
            taskManagement.addTask(task);
            String taskEntry = String.format("%d - %s", task.getIdTask(), task.getNameTask());
            taskView.getAllTaskListModel().addElement(taskEntry);
            JOptionPane.showMessageDialog(null, "Task '" + taskName + "' created. You can assign it later.");
        }
    }

    public void assignTaskToEmployee() {
        String selectedEmployeeEntry = employeeView.getEmployeeList().getSelectedValue();
        if (selectedEmployeeEntry == null) {
            JOptionPane.showMessageDialog(null, "Select an employee first!");
            return;
        }

        Employee employee = findEmployeeByEntry(selectedEmployeeEntry);
        if (employee == null) return;

        Set<Task> allTasks = taskManagement.getAllTasks();
        if (allTasks.isEmpty()) {
            JOptionPane.showMessageDialog(null, "No tasks available to assign!");
            return;
        }

        String[] taskEntries = allTasks.stream()
                .filter(task -> !task.isAssigned())
                .map(task -> String.format("%d - %s", task.getIdTask(), task.getNameTask()))
                .toArray(String[]::new);

        if (taskEntries.length == 0) {
            JOptionPane.showMessageDialog(null, "All tasks are already assigned!");
            return;
        }

        String selectedTaskEntry = (String) JOptionPane.showInputDialog(null,
                "Select Task to Assign:", "Available Tasks", JOptionPane.QUESTION_MESSAGE,
                null, taskEntries, taskEntries[0]);

        if (selectedTaskEntry == null) return;

        Task selectedTask = findTaskByEntry(selectedTaskEntry);
        if (selectedTask != null) {
            try {
                taskManagement.assignTaskToEmployee(employee.getIdEmployee(), selectedTask.getIdTask());
                String taskEntry = String.format("%d - %s (%s)",
                        selectedTask.getIdTask(), selectedTask.getNameTask(), selectedTask.getStatusTask());
                taskView.getAssignedTaskListModel().addElement(taskEntry);
                JOptionPane.showMessageDialog(null,
                        "Task '" + selectedTask.getNameTask() + "' assigned to " + employee.getName());
            } catch (IllegalStateException e) {
                JOptionPane.showMessageDialog(null, e.getMessage());
            }
        }
    }

    public void calculateWorkDuration() {
        String selectedEmployeeEntry = employeeView.getEmployeeList().getSelectedValue();
        if (selectedEmployeeEntry == null) {
            JOptionPane.showMessageDialog(null, "Select an employee first!");
            return;
        }

        Employee employee = findEmployeeByEntry(selectedEmployeeEntry);
        if (employee == null) return;

        int totalDuration = taskManagement.calculateEmployeeWorkDuration(employee.getIdEmployee());
        JOptionPane.showMessageDialog(null,
                "Total work duration for " + employee.getName() + " (ID: " + employee.getIdEmployee() + "): " + totalDuration + " hours");
    }

    public void modifyTaskStatus() {
        String selectedEmployeeEntry = employeeView.getEmployeeList().getSelectedValue();
        if (selectedEmployeeEntry == null) {
            JOptionPane.showMessageDialog(null, "Select an employee first!");
            return;
        }

        Employee employee = findEmployeeByEntry(selectedEmployeeEntry);
        if (employee == null) return;

        if (taskView.getAssignedTaskListModel().isEmpty()) {
            JOptionPane.showMessageDialog(null, "No tasks assigned to " + employee.getName() + "!");
            return;
        }

        String[] taskEntries = Collections.list(taskView.getAssignedTaskListModel().elements()).toArray(new String[0]);
        String selectedTaskEntry = (String) JOptionPane.showInputDialog(null,
                "Select Task to Modify Status:", "Task Status", JOptionPane.QUESTION_MESSAGE,
                null, taskEntries, taskEntries[0]);

        if (selectedTaskEntry == null) return;

        Task selectedTask = findTaskByEntry(selectedTaskEntry);
        if (selectedTask != null) {
            String[] statusOptions = {"COMPLETED", "UNCOMPLETED"};
            String newStatus = (String) JOptionPane.showInputDialog(null,
                    "Select New Status for '" + selectedTask.getNameTask() + "':",
                    "Change Status", JOptionPane.QUESTION_MESSAGE,
                    null, statusOptions, selectedTask.getStatusTask());

            if (newStatus != null) {
                taskManagement.modifyTaskStatus(employee.getIdEmployee(), selectedTask.getIdTask(),
                        newStatus.equals("COMPLETED"));
                taskView.getAssignedTaskListModel().clear();
                List<Task> tasks = taskManagement.getEmployeeTaskAssignments().get(employee);
                if (tasks != null) {
                    for (Task task : tasks) {
                        taskView.getAssignedTaskListModel().addElement(
                                String.format("%d - %s (%s)", task.getIdTask(), task.getNameTask(), task.getStatusTask()));
                    }
                }
                JOptionPane.showMessageDialog(null,
                        "Task '" + selectedTask.getNameTask() + "' status updated to " + newStatus);
            }
        }
    }

    public void deleteComponentTask() {
        Set<Task> allTasks = taskManagement.getAllTasks();
        String[] complexTaskEntries = allTasks.stream()
                .filter(task -> task instanceof ComplexTask)
                .map(task -> String.format("%d - %s", task.getIdTask(), task.getNameTask()))
                .toArray(String[]::new);

        if (complexTaskEntries.length == 0) {
            JOptionPane.showMessageDialog(null, "No complex tasks available!");
            return;
        }

        String selectedComplexTaskEntry = (String) JOptionPane.showInputDialog(null,
                "Select Complex Task:", "Delete Component Task", JOptionPane.QUESTION_MESSAGE,
                null, complexTaskEntries, complexTaskEntries[0]);

        if (selectedComplexTaskEntry == null) return;

        ComplexTask selectedComplexTask = (ComplexTask) findTaskByEntry(selectedComplexTaskEntry);
        if (selectedComplexTask == null) {
            JOptionPane.showMessageDialog(null, "Complex task not found!");
            return;
        }

        List<Task> componentTasks = selectedComplexTask.getComponentTasks();
        if (componentTasks.isEmpty()) {
            JOptionPane.showMessageDialog(null,
                    "No component tasks in '" + selectedComplexTask.getNameTask() + "'!");
            return;
        }

        String[] componentTaskEntries = componentTasks.stream()
                .map(task -> String.format("%d - %s", task.getIdTask(), task.getNameTask()))
                .toArray(String[]::new);

        String selectedComponentEntry = (String) JOptionPane.showInputDialog(null,
                "Select Component Task to Delete from '" + selectedComplexTask.getNameTask() + "':",
                "Delete Component", JOptionPane.QUESTION_MESSAGE,
                null, componentTaskEntries, componentTaskEntries[0]);

        if (selectedComponentEntry == null) return;

        Task componentToDelete = findTaskByEntry(selectedComponentEntry);
        if (componentToDelete != null) {
            int confirm = JOptionPane.showConfirmDialog(null,
                    "Are you sure you want to delete '" + componentToDelete.getNameTask() +
                            "' from '" + selectedComplexTask.getNameTask() + "'?",
                    "Confirm Deletion", JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                selectedComplexTask.deleteTask(componentToDelete);
                JOptionPane.showMessageDialog(null,
                        "Component task '" + componentToDelete.getNameTask() +
                                "' deleted from '" + selectedComplexTask.getNameTask() + "'");
                updateAssignedTasksForSelectedEmployee();
            }
        }
    }

    private void updateAssignedTasksForSelectedEmployee() {
        String selectedEmployeeEntry = employeeView.getEmployeeList().getSelectedValue();
        if (selectedEmployeeEntry != null) {
            Employee employee = findEmployeeByEntry(selectedEmployeeEntry);
            if (employee != null) {
                taskView.getAssignedTaskListModel().clear();
                List<Task> tasks = taskManagement.getEmployeeTaskAssignments().get(employee);
                if (tasks != null) {
                    for (Task task : tasks) {
                        taskView.getAssignedTaskListModel().addElement(
                                String.format("%d - %s (%s)", task.getIdTask(), task.getNameTask(), task.getStatusTask()));
                    }
                }
            }
        }
    }

    public void populateAllTasks() {
        for (Task task : taskManagement.getAllTasks()) {
            String taskEntry = String.format("%d - %s", task.getIdTask(), task.getNameTask());
            taskView.getAllTaskListModel().addElement(taskEntry);
            nextTaskId = Math.max(nextTaskId, task.getIdTask() + 1);
        }
    }

    private Task findTaskByEntry(String entry) {
        int id = Integer.parseInt(entry.split(" - ")[0]);
        return taskManagement.getAllTasks().stream()
                .filter(t -> t.getIdTask() == id)
                .findFirst()
                .orElse(null);
    }

    private Employee findEmployeeByEntry(String entry) {
        int id = Integer.parseInt(entry.split(" - ")[0]);
        for (Employee emp : taskManagement.getAllEmployees()) {
            if (emp.getIdEmployee() == id) {
                return emp;
            }
        }
        JOptionPane.showMessageDialog(null, "Employee not found!");
        return null;
    }

    private SimpleTask createSimpleTask(String taskName) {
        try {
            String startHourStr = JOptionPane.showInputDialog("Enter Start Hour (0-23):");
            if (startHourStr == null) return null;
            int startHour = Integer.parseInt(startHourStr);

            String endHourStr = JOptionPane.showInputDialog("Enter End Hour (0-23):");
            if (endHourStr == null) return null;
            int endHour = Integer.parseInt(endHourStr);

            if (startHour < 0 || endHour < 0 || endHour > 23 || startHour >= endHour) {
                JOptionPane.showMessageDialog(null, "Invalid hours! Start must be < End and within 0-23.");
                return null;
            }

            return new SimpleTask(nextTaskId++, taskName, startHour, endHour);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Invalid number format for hours!");
            return null;
        }
    }

    private ComplexTask createComplexTask(String taskName) {
        ComplexTask complexTask = new ComplexTask(nextTaskId++, taskName);

        int addComponents = JOptionPane.showConfirmDialog(null,
                "Do you want to add component tasks to this complex task?",
                "Add Components", JOptionPane.YES_NO_OPTION);

        if (addComponents == JOptionPane.YES_OPTION) {
            while (true) {
                String[] options = {"Add New Simple Task", "Add Existing Task", "Finish"};
                String choice = (String) JOptionPane.showInputDialog(null, "Choose an action:",
                        "Complex Task Components", JOptionPane.QUESTION_MESSAGE, null, options, options[0]);

                if (choice == null || choice.equals("Finish")) break;

                if (choice.equals("Add New Simple Task")) {
                    String componentName = JOptionPane.showInputDialog("Enter Component Task Name:");
                    if (componentName != null && !componentName.trim().isEmpty()) {
                        SimpleTask component = createSimpleTask(componentName);
                        if (component != null) {
                            taskManagement.addTask(component);
                            taskView.getAllTaskListModel().addElement(
                                    String.format("%d - %s", component.getIdTask(), component.getNameTask()));
                            complexTask.addTask(component);
                        }
                    }
                } else if (choice.equals("Add Existing Task")) {
                    Set<Task> allTasks = taskManagement.getAllTasks();
                    if (allTasks.isEmpty()) {
                        JOptionPane.showMessageDialog(null, "No existing tasks available!");
                        continue;
                    }

                    String[] taskEntries = allTasks.stream()
                            .filter(task -> !task.isAssigned())
                            .map(task -> String.format("%d - %s", task.getIdTask(), task.getNameTask()))
                            .toArray(String[]::new);
                    if (taskEntries.length == 0) {
                        JOptionPane.showMessageDialog(null, "All tasks are already assigned!");
                        continue;
                    }

                    String selectedTaskEntry = (String) JOptionPane.showInputDialog(null, "Select Task:",
                            "Existing Tasks", JOptionPane.QUESTION_MESSAGE, null, taskEntries, taskEntries[0]);

                    if (selectedTaskEntry != null) {
                        allTasks.stream()
                                .filter(t -> String.format("%d - %s", t.getIdTask(), t.getNameTask()).equals(selectedTaskEntry))
                                .findFirst().ifPresent(complexTask::addTask);
                    }
                }
            }
        }

        return complexTask;
    }
}