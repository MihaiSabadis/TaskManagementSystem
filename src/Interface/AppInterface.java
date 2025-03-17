package Interface;

import BusinessLogic.TaskManagement;
import DataModel.ComplexTask;
import DataModel.Employee;
import DataModel.SimpleTask;
import DataModel.Task;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class AppInterface {

    private DefaultListModel<String> employeeListModel;
    private JList<String> employeeList;

    private DefaultListModel<String> assignedTaskListModel;
    private JList<String> assignedTaskList;

    private DefaultListModel<String> allTaskListModel;
    private JList<String> allTaskList;


    private Map<String, DefaultListModel<String>> employeeTasks;
    private TaskManagement taskManagement;
    private int nextEmployeeId = 1;
    private int nextTaskId = 1;

    public AppInterface() {
        taskManagement = new TaskManagement();

        JFrame frame = new JFrame("Employee & Task Management");
        frame.setSize(1000, 400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setLayout(new BorderLayout());

        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                taskManagement.saveData();
            }
        });

        // Updated button panel with 6 rows
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(6, 1, 10, 10));

        JButton addEmployeeButton = new JButton("Add Employee");
        addEmployeeButton.addActionListener(e -> addEmployee());

        JButton addTaskButton = new JButton("Add Task");
        addTaskButton.addActionListener(e -> addTask());

        JButton assignTaskButton = new JButton("Assign Task to Employee");
        assignTaskButton.addActionListener(e -> assignTaskToEmployee());

        JButton workDurationButton = new JButton("Calculate Work Duration");
        workDurationButton.addActionListener(e -> calculateWorkDuration());

        JButton modifyStatusButton = new JButton("Modify Task Status");
        modifyStatusButton.addActionListener(e -> modifyTaskStatus());

        JButton deleteComponentButton = new JButton("Delete Component Task");
        deleteComponentButton.addActionListener(e -> deleteComponentTask());

        buttonPanel.add(addEmployeeButton);
        buttonPanel.add(addTaskButton);
        buttonPanel.add(assignTaskButton);
        buttonPanel.add(workDurationButton);
        buttonPanel.add(modifyStatusButton);
        buttonPanel.add(deleteComponentButton);

        employeeListModel = new DefaultListModel<>();
        assignedTaskListModel = new DefaultListModel<>();
        allTaskListModel = new DefaultListModel<>();
        employeeList = new JList<>(employeeListModel);
        assignedTaskList = new JList<>(assignedTaskListModel);
        allTaskList = new JList<>(allTaskListModel);
        employeeTasks = new HashMap<>();
        populateGuiFromTaskManagement();

        employeeList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                updateAssignedTaskList();
            }
        });

        JPanel employeePanel = new JPanel(new BorderLayout());
        employeePanel.add(new JLabel("Employees (ID - Name):"), BorderLayout.NORTH);
        employeePanel.add(new JScrollPane(employeeList), BorderLayout.CENTER);

        JPanel assignedTaskPanel = new JPanel(new BorderLayout());
        assignedTaskPanel.add(new JLabel("Assigned Tasks (ID - Name - Status):"), BorderLayout.NORTH);
        assignedTaskPanel.add(new JScrollPane(assignedTaskList), BorderLayout.CENTER);

        JPanel allTaskPanel = new JPanel(new BorderLayout());
        allTaskPanel.add(new JLabel("All Tasks (ID - Name):"), BorderLayout.NORTH);
        allTaskPanel.add(new JScrollPane(allTaskList), BorderLayout.CENTER);

        JPanel centerPanel = new JPanel(new GridLayout(1, 3));
        centerPanel.add(employeePanel);
        centerPanel.add(assignedTaskPanel);
        centerPanel.add(allTaskPanel);

        frame.add(buttonPanel, BorderLayout.NORTH);
        frame.add(centerPanel, BorderLayout.CENTER);

        frame.setVisible(true);
    }

    // New method to delete component tasks
    private void deleteComponentTask() {
        // Select a complex task from all tasks
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
                "Select Complex Task:",
                "Delete Component Task",
                JOptionPane.QUESTION_MESSAGE,
                null,
                complexTaskEntries,
                complexTaskEntries[0]);

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
                "Delete Component",
                JOptionPane.QUESTION_MESSAGE,
                null,
                componentTaskEntries,
                componentTaskEntries[0]);

        if (selectedComponentEntry == null) return;

        Task componentToDelete = findTaskByEntry(selectedComponentEntry);
        if (componentToDelete != null) {
            int confirm = JOptionPane.showConfirmDialog(null,
                    "Are you sure you want to delete '" + componentToDelete.getNameTask() +
                            "' from '" + selectedComplexTask.getNameTask() + "'?",
                    "Confirm Deletion",
                    JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                selectedComplexTask.deleteTask(componentToDelete);
                JOptionPane.showMessageDialog(null,
                        "Component task '" + componentToDelete.getNameTask() +
                                "' deleted from '" + selectedComplexTask.getNameTask() + "'");
                updateAssignedTaskList(); // Update GUI if affected
            }
        }
    }

    private void modifyTaskStatus() {
        String selectedEmployeeEntry = getSelectedEmployeeEntry();
        if (selectedEmployeeEntry == null) return;

        Employee employee = findEmployeeByEntry(selectedEmployeeEntry);
        if (employee == null) return;

        DefaultListModel<String> employeeTaskList = employeeTasks.get(selectedEmployeeEntry);
        if (employeeTaskList.isEmpty()) {
            JOptionPane.showMessageDialog(null, "No tasks assigned to " + employee.getName() + "!");
            return;
        }

        String[] taskEntries = Collections.list(employeeTaskList.elements()).toArray(new String[0]);
        String selectedTaskEntry = (String) JOptionPane.showInputDialog(null,
                "Select Task to Modify Status:",
                "Task Status",
                JOptionPane.QUESTION_MESSAGE,
                null,
                taskEntries,
                taskEntries[0]);

        if (selectedTaskEntry == null) return;

        Task selectedTask = findTaskByEntry(selectedTaskEntry);
        if (selectedTask != null) {
            String[] statusOptions = {"COMPLETED", "UNCOMPLETED"};
            String newStatus = (String) JOptionPane.showInputDialog(null,
                    "Select New Status for '" + selectedTask.getNameTask() + "':",
                    "Change Status",
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    statusOptions,
                    selectedTask.getStatusTask());

            if (newStatus != null) {
                taskManagement.modifyTaskStatus(
                        employee.getIdEmployee(),
                        selectedTask.getIdTask(),
                        newStatus.equals("COMPLETED")
                );
                JOptionPane.showMessageDialog(null,
                        "Task '" + selectedTask.getNameTask() + "' status updated to " + newStatus);
                updateAssignedTaskList();
            }
        }
    }

    private void addEmployee() {
        String name = JOptionPane.showInputDialog("Enter Employee Name:");
        if (name != null && !name.trim().isEmpty()) {
            Employee employee = new Employee(nextEmployeeId++, name);
            taskManagement.addEmployee(employee);
            String employeeEntry = String.format("%d - %s", employee.getIdEmployee(), employee.getName());
            employeeListModel.addElement(employeeEntry);
            employeeTasks.put(employeeEntry, new DefaultListModel<>());
            updateAssignedTaskList();
        }
    }

    private void addTask() {
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
            allTaskListModel.addElement(taskEntry);
            JOptionPane.showMessageDialog(null, "Task '" + taskName + "' created. You can assign it later.");
        }
    }

    private void assignTaskToEmployee() {
        String selectedEmployeeEntry = getSelectedEmployeeEntry();
        if (selectedEmployeeEntry == null) return;

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
                "Select Task to Assign:",
                "Available Tasks",
                JOptionPane.QUESTION_MESSAGE,
                null,
                taskEntries,
                taskEntries[0]);

        if (selectedTaskEntry == null) return;

        Task selectedTask = findTaskByEntry(selectedTaskEntry);
        if (selectedTask != null) {
            DefaultListModel<String> employeeTaskList = employeeTasks.get(selectedEmployeeEntry);
            String taskEntry = String.format("%d - %s (%s)",
                    selectedTask.getIdTask(),
                    selectedTask.getNameTask(),
                    selectedTask.getStatusTask());
            if (employeeTaskList.contains(taskEntry)) {
                JOptionPane.showMessageDialog(null,
                        "Task '" + selectedTask.getNameTask() + "' is already assigned to " + employee.getName() + "!");
                return;
            }

            try {
                taskManagement.assignTaskToEmployee(employee.getIdEmployee(), selectedTask.getIdTask());
                employeeTaskList.addElement(taskEntry);
                updateAssignedTaskList();
                JOptionPane.showMessageDialog(null,
                        "Task '" + selectedTask.getNameTask() + "' assigned to " + employee.getName());
            } catch (IllegalStateException e) {
                JOptionPane.showMessageDialog(null, e.getMessage());
            }
        }
    }

    private void calculateWorkDuration() {
        String selectedEmployeeEntry = getSelectedEmployeeEntry();
        if (selectedEmployeeEntry == null) return;

        Employee employee = findEmployeeByEntry(selectedEmployeeEntry);
        if (employee == null) return;

        int totalDuration = taskManagement.calculateEmployeeWorkDuration(employee.getIdEmployee());
        JOptionPane.showMessageDialog(null,
                "Total work duration for " + employee.getName() + " (ID: " + employee.getIdEmployee() + "): " + totalDuration + " hours");
    }

    private String getSelectedEmployeeEntry() {
        String selectedEmployeeEntry = employeeList.getSelectedValue();
        if (selectedEmployeeEntry == null) {
            JOptionPane.showMessageDialog(null, "Select an employee first!");
            return null;
        }
        return selectedEmployeeEntry;
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

    private Task findTaskByEntry(String entry) {
        int id = Integer.parseInt(entry.split(" - ")[0]);
        return taskManagement.getAllTasks().stream()
                .filter(t -> t.getIdTask() == id)
                .findFirst()
                .orElse(null);
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

            return new SimpleTask(nextTaskId++, false, taskName, startHour, endHour);
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
                            allTaskListModel.addElement(String.format("%d - %s", component.getIdTask(), component.getNameTask()));
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

    private void updateAssignedTaskList() {
        assignedTaskListModel.clear();
        String selectedEmployeeEntry = employeeList.getSelectedValue();
        if (selectedEmployeeEntry != null) {
            Employee employee = findEmployeeByEntry(selectedEmployeeEntry);
            if (employee != null) {
                List<Task> tasks = taskManagement.getEmployeeTaskAssignments().get(employee);
                if (tasks != null) {
                    for (Task task : tasks) {
                        String displayText = String.format("%d - %s (%s)",
                                task.getIdTask(),
                                task.getNameTask(),
                                task.getStatusTask());
                        assignedTaskListModel.addElement(displayText);
                    }
                }
            }
        }
    }

    private void populateGuiFromTaskManagement() {
        employeeListModel.clear();
        assignedTaskListModel.clear();
        allTaskListModel.clear();
        employeeTasks.clear();

        for (Employee emp : taskManagement.getAllEmployees()) {
            String employeeEntry = String.format("%d - %s", emp.getIdEmployee(), emp.getName());
            employeeListModel.addElement(employeeEntry);
            DefaultListModel<String> taskListModelForEmployee = new DefaultListModel<>();
            List<Task> assignedTasks = taskManagement.getEmployeeTaskAssignments().get(emp);
            if (assignedTasks != null) {
                for (Task task : assignedTasks) {
                    String taskEntry = String.format("%d - %s (%s)",
                            task.getIdTask(),
                            task.getNameTask(),
                            task.getStatusTask());
                    taskListModelForEmployee.addElement(taskEntry);
                }
            }
            employeeTasks.put(employeeEntry, taskListModelForEmployee);
            nextEmployeeId = Math.max(nextEmployeeId, emp.getIdEmployee() + 1);
        }

        for (Task task : taskManagement.getAllTasks()) {
            String taskEntry = String.format("%d - %s", task.getIdTask(), task.getNameTask());
            allTaskListModel.addElement(taskEntry);
            nextTaskId = Math.max(nextTaskId, task.getIdTask() + 1);
        }

        updateAssignedTaskList();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(AppInterface::new);
    }
}