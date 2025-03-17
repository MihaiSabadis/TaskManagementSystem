package GraphicalUserInterface.Views;

import javax.swing.*;
import java.awt.*;

public class TaskView {
    private final JPanel panel;
    private final JList<String> assignedTaskList;
    private final JList<String> allTaskList;
    private final DefaultListModel<String> assignedTaskListModel;
    private final DefaultListModel<String> allTaskListModel;

    public TaskView() {
        panel = new JPanel(new GridLayout(1, 2));

        assignedTaskListModel = new DefaultListModel<>();
        assignedTaskList = new JList<>(assignedTaskListModel);
        JPanel assignedPanel = new JPanel(new BorderLayout());
        assignedPanel.add(new JLabel("Assigned Tasks (ID - Name - Status):"), BorderLayout.NORTH);
        assignedPanel.add(new JScrollPane(assignedTaskList), BorderLayout.CENTER);

        allTaskListModel = new DefaultListModel<>();
        allTaskList = new JList<>(allTaskListModel);
        JPanel allPanel = new JPanel(new BorderLayout());
        allPanel.add(new JLabel("All Tasks (ID - Name):"), BorderLayout.NORTH);
        allPanel.add(new JScrollPane(allTaskList), BorderLayout.CENTER);

        panel.add(assignedPanel);
        panel.add(allPanel);
    }

    public JPanel getPanel() {
        return panel;
    }

    public DefaultListModel<String> getAssignedTaskListModel() {
        return assignedTaskListModel;
    }

    public DefaultListModel<String> getAllTaskListModel() {
        return allTaskListModel;
    }
}