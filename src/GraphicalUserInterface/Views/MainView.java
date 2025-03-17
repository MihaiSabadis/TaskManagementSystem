package GraphicalUserInterface.Views;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowListener;

public class MainView {
    private final JFrame frame;
    private JPanel employeePanel;
    private JPanel taskPanel;
    private JPanel buttonPanel;

    public MainView() {
        frame = new JFrame("Employee & Task Management");
        frame.setSize(1000, 400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setLayout(new BorderLayout());
    }

    public void setEmployeePanel(JPanel employeePanel) {
        this.employeePanel = employeePanel;
        frame.add(employeePanel, BorderLayout.WEST);
    }

    public void setTaskPanel(JPanel taskPanel) {
        this.taskPanel = taskPanel;
        frame.add(taskPanel, BorderLayout.CENTER);
    }

    public void setButtonPanel(JPanel buttonPanel) {
        this.buttonPanel = buttonPanel;
        frame.add(buttonPanel, BorderLayout.NORTH);
    }

    public void display() {
        frame.setVisible(true);
    }

    public void addWindowListener(WindowListener listener) {
        frame.addWindowListener(listener);
    }
}