package GraphicalUserInterface.Controllers;

import BusinessLogic.TaskManagement;
import BusinessLogic.Utility;
import GraphicalUserInterface.Views.MainView;
import GraphicalUserInterface.Views.EmployeeView;
import GraphicalUserInterface.Views.TaskView;
import GraphicalUserInterface.Views.ButtonPanelView;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class MainController {
    private final TaskManagement taskManagement;
    private final Utility utility;
    private final MainView mainView;
    private final EmployeeController employeeController;
    private final TaskController taskController;
    private final UtilityController utilityController;

    public MainController() {
        taskManagement = new TaskManagement();
        utility = new Utility(taskManagement);
        mainView = new MainView();
        EmployeeView employeeView = new EmployeeView();
        TaskView taskView = new TaskView();
        ButtonPanelView buttonPanelView = new ButtonPanelView();

        employeeController = new EmployeeController(taskManagement, employeeView, taskView);
        taskController = new TaskController(taskManagement, employeeView, taskView);
        utilityController = new UtilityController(utility, employeeView);

        mainView.setEmployeePanel(employeeView.getPanel());
        mainView.setTaskPanel(taskView.getPanel());
        mainView.setButtonPanel(buttonPanelView.getPanel());

        setupButtonListeners(buttonPanelView);
        mainView.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                taskManagement.saveData();
            }
        });

        employeeController.populateEmployeeList();
        taskController.populateAllTasks();
        mainView.display();
    }

    private void setupButtonListeners(ButtonPanelView buttonPanelView) {
        buttonPanelView.getAddEmployeeButton().addActionListener(e -> employeeController.addEmployee());
        buttonPanelView.getAddTaskButton().addActionListener(e -> taskController.addTask());
        buttonPanelView.getAssignTaskButton().addActionListener(e -> taskController.assignTaskToEmployee());
        buttonPanelView.getWorkDurationButton().addActionListener(e -> taskController.calculateWorkDuration());
        buttonPanelView.getModifyStatusButton().addActionListener(e -> taskController.modifyTaskStatus());
        buttonPanelView.getDeleteComponentButton().addActionListener(e -> taskController.deleteComponentTask());
        buttonPanelView.getHighDurationButton().addActionListener(e -> utilityController.showHighWorkDurationEmployees());
        buttonPanelView.getTaskStatusButton().addActionListener(e -> utilityController.showTaskStatusCounts());
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(MainController::new);
    }
}