package GraphicalUserInterface.Views;

import javax.swing.*;
import java.awt.*;

public class EmployeeView {
    private final JPanel panel;
    private final JList<String> employeeList;
    private final DefaultListModel<String> employeeListModel;

    public EmployeeView() {
        panel = new JPanel(new BorderLayout());
        employeeListModel = new DefaultListModel<>();
        employeeList = new JList<>(employeeListModel);
        panel.add(new JLabel("Employees (ID - Name):"), BorderLayout.NORTH);
        panel.add(new JScrollPane(employeeList), BorderLayout.CENTER);
    }

    public JPanel getPanel() {
        return panel;
    }

    public JList<String> getEmployeeList() {
        return employeeList;
    }

    public DefaultListModel<String> getEmployeeListModel() {
        return employeeListModel;
    }
}
