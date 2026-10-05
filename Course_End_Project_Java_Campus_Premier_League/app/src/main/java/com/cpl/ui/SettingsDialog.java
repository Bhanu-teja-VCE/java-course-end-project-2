package com.cpl.ui;

import com.cpl.db.Database;
import com.cpl.db.DbConfig;
import com.cpl.db.SchemaInstaller;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.io.File;

/**
 * Settings and Database Connection configuration dialog.
 * Demonstrates:
 * - Unit IV: JDialog, JTextField, JPasswordField, GridLayout
 */
public class SettingsDialog extends JDialog {
    private static final long serialVersionUID = 1L;

    private final JTextField hostField = new JTextField();
    private final JTextField portField = new JTextField();
    private final JTextField dbField = new JTextField();
    private final JTextField userField = new JTextField();
    private final JPasswordField passField = new JPasswordField();

    private final DbConfig config;

    public SettingsDialog(JFrame parent, DbConfig config) {
        super(parent, "MySQL Database Settings", true);
        this.config = config;

        setSize(420, 320);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout(10, 10));

        JPanel form = new JPanel(new GridLayout(5, 2, 8, 8));
        form.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        form.add(new JLabel("Host:"));
        hostField.setText(config.getHost());
        form.add(hostField);

        form.add(new JLabel("Port:"));
        portField.setText(String.valueOf(config.getPort()));
        form.add(portField);

        form.add(new JLabel("Database:"));
        dbField.setText(config.getDatabase());
        form.add(dbField);

        form.add(new JLabel("Username:"));
        userField.setText(config.getUser());
        form.add(userField);

        form.add(new JLabel("Password:"));
        passField.setText(config.getPassword());
        form.add(passField);

        add(form, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 8));
        JButton testBtn = new JButton("Test Connection");
        JButton installBtn = new JButton("Install Tables");
        JButton saveBtn = new JButton("Save & Apply");

        testBtn.addActionListener(e -> {
            applyFields();
            boolean ok = Database.testConnection(config);
            if (ok) {
                JOptionPane.showMessageDialog(this, "Connection successful!", "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Connection failed. Please verify credentials.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        installBtn.addActionListener(e -> {
            applyFields();
            try {
                SchemaInstaller.installSchema(config);
                JOptionPane.showMessageDialog(this, "Tables and Stored Procedures installed successfully!",
                        "Schema Installed", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Installation error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        saveBtn.addActionListener(e -> {
            applyFields();
            try {
                config.saveToFile(new File("db.properties"));
            } catch (Exception ignored) {}
            dispose();
        });

        btnPanel.add(testBtn);
        btnPanel.add(installBtn);
        btnPanel.add(saveBtn);
        add(btnPanel, BorderLayout.SOUTH);
    }

    private void applyFields() {
        config.setHost(hostField.getText().trim());
        try {
            config.setPort(Integer.parseInt(portField.getText().trim()));
        } catch (NumberFormatException ignored) {}
        config.setDatabase(dbField.getText().trim());
        config.setUser(userField.getText().trim());
        config.setPassword(new String(passField.getPassword()));
    }
}
