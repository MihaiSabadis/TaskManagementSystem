package GraphicalUserInterface.Views;

import javax.swing.*;
import java.awt.*;

public class ButtonPanelView {
    private final JPanel panel;
    private final JButton addEmployeeButton;
    private final JButton addTaskButton;
    private final JButton assignTaskButton;
    private final JButton workDurationButton;
    private final JButton modifyStatusButton;
    private final JButton deleteComponentButton;
    private final JButton highDurationButton;
    private final JButton taskStatusButton;

    public ButtonPanelView() {
        panel = new JPanel(new GridLayout(8, 1, 10, 10));
        addEmployeeButton = new JButton("Add Employee");
        addTaskButton = new JButton("Add Task");
        assignTaskButton = new JButton("Assign Task to Employee");
        workDurationButton = new JButton("Calculate Work Duration");
        modifyStatusButton = new JButton("Modify Task Status");
        deleteComponentButton = new JButton("Delete Component Task");
        highDurationButton = new JButton("Employees > 40 Hours");
        taskStatusButton = new JButton("Task Status Counts");

        panel.add(addEmployeeButton);
        panel.add(addTaskButton);
        panel.add(assignTaskButton);
        panel.add(workDurationButton);
        panel.add(modifyStatusButton);
        panel.add(deleteComponentButton);
        panel.add(highDurationButton);
        panel.add(taskStatusButton);
    }

    public JPanel getPanel() {
        return panel;
    }

    public JButton getAddEmployeeButton() {
        return addEmployeeButton;
    }

    public JButton getAddTaskButton() {
        return addTaskButton;
    }

    public JButton getAssignTaskButton() {
        return assignTaskButton;
    }

    public JButton getWorkDurationButton() {
        return workDurationButton;
    }

    public JButton getModifyStatusButton() {
        return modifyStatusButton;
    }

    public JButton getDeleteComponentButton() {
        return deleteComponentButton;
    }

    public JButton getHighDurationButton() {
        return highDurationButton;
    }

    public JButton getTaskStatusButton() {
        return taskStatusButton;
    }
}