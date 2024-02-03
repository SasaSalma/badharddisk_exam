package manajemenfile;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ManajemenFile extends JFrame {

    private JTextField fileNameField, extensionField, sizeField;
    private JButton saveButton, deleteButton;
    private JTable table;
    private DefaultTableModel tableModel;
    private JLabel memorySizeLabel;

    private int memorySizeLeft = 10000;
    
    private void setColumnWidth(JLabel label, JTextField textField) {
        int labelWidth = label.getPreferredSize().width;
        int textFieldWidth = textField.getPreferredSize().width;

        int maxWidth = Math.max(labelWidth, textFieldWidth);

        label.setPreferredSize(new Dimension(maxWidth, label.getPreferredSize().height));
        textField.setPreferredSize(new Dimension(maxWidth, textField.getPreferredSize().height));
    }

    public ManajemenFile() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 240);

        fileNameField = new JTextField();
        extensionField = new JTextField();
        sizeField = new JTextField();

        saveButton = new JButton("Save");
        deleteButton = new JButton("Delete");

        String[] columnNames = {"File Name", "Extension", "Size in MB"};
        tableModel = new DefaultTableModel(columnNames, 0);
        table = new JTable(tableModel) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table.getColumnModel().getColumn(0).setPreferredWidth(40);
        table.getColumnModel().getColumn(1).setPreferredWidth(40);
        table.getColumnModel().getColumn(2).setPreferredWidth(20);
        table.setShowGrid(true);
        table.setGridColor(Color.BLACK);

        memorySizeLabel = new JLabel("Memory Size Left : " + memorySizeLeft + " MB");
        JLabel harddiskLabel = new JLabel("Harddisk");

        setColumnWidth(new JLabel("File name   "), fileNameField);
        setColumnWidth(new JLabel("Extension  "), extensionField);
        setColumnWidth(new JLabel("Size in MB "), sizeField);
        
        setLayout(new BorderLayout());

        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        headerPanel.add(harddiskLabel);

        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new GridLayout(3, 2, 8, 8));
        leftPanel.setMaximumSize(new Dimension(200, 150));

        addRow(leftPanel, new JLabel("File name   "), fileNameField);
        addRow(leftPanel, new JLabel("Extension  "), extensionField);
        addRow(leftPanel, new JLabel("Size in MB "), sizeField);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.add(saveButton);
        buttonPanel.add(deleteButton);

        leftPanel.add(buttonPanel);

        JPanel combinedLeftPanel = new JPanel(new BorderLayout());
        combinedLeftPanel.add(leftPanel, BorderLayout.CENTER);
        combinedLeftPanel.add(buttonPanel, BorderLayout.SOUTH);
        combinedLeftPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10)); // Menambahkan ruang di sekeliling panel input

        JScrollPane tableScrollPane = new JScrollPane(table);
        tableScrollPane.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 10)); // Menambahkan ruang di sekeliling tabel
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.add(tableScrollPane, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new FlowLayout());
        bottomPanel.add(memorySizeLabel);

        add(headerPanel, BorderLayout.NORTH);
        add(combinedLeftPanel, BorderLayout.WEST);
        add(centerPanel, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveButtonClicked();
            }
        });

        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteButtonClicked();
            }
        });

        memorySizeLeft = 10000;
        memorySizeLabel.setText("Memory Size Left : " + memorySizeLeft + " MB");

        setVisible(true);
    }

    private void addRow(JPanel panel, JLabel label, JTextField textField) {
        JPanel rowPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 2));
        rowPanel.add(label);
        rowPanel.add(textField);
        panel.add(rowPanel);
        
        setColumnWidth(label, textField);
    }


    private void saveButtonClicked() {
    String fileName = fileNameField.getText().toLowerCase(); // Ubah menjadi lowercase
    String extension = extensionField.getText().toLowerCase(); // Ubah menjadi lowercase
    String sizeText = sizeField.getText();

    if (fileName.isEmpty() || extension.isEmpty() || sizeText.isEmpty()) {
        JOptionPane.showMessageDialog(this, "All fields must be filled.", "Error", JOptionPane.ERROR_MESSAGE);
        return;
    }

    try {
        double size = Double.parseDouble(sizeText);
        if (size <= 0) {
            JOptionPane.showMessageDialog(this, "Size must be a positive number.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        for (int i = 0; i < table.getRowCount(); i++) {
            String existingFileName = ((String) table.getValueAt(i, 0)).toLowerCase(); // Ubah menjadi lowercase
            String existingExtension = ((String) table.getValueAt(i, 1)).toLowerCase(); // Ubah menjadi lowercase
            if (fileName.equals(existingFileName) && extension.equals(existingExtension)) {
                JOptionPane.showMessageDialog(this, "File with the same name and extension already exists.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        if (size > memorySizeLeft) {
            JOptionPane.showMessageDialog(this, "Not enough Memory Size Left to store the file.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String formattedSize = String.valueOf(size);
        if (formattedSize.endsWith(".0")) {
            formattedSize = formattedSize.substring(0, formattedSize.length() - 2);
        }

        Object[] rowData = {fileName, extension, formattedSize};
        tableModel.addRow(rowData);

        memorySizeLeft -= size;
        memorySizeLabel.setText("Memory Size Left: " + memorySizeLeft + " MB");

        fileNameField.setText("");
        extensionField.setText("");
        sizeField.setText("");

    } catch (NumberFormatException ex) {
        JOptionPane.showMessageDialog(this, "Size must be a valid number.", "Error", JOptionPane.ERROR_MESSAGE);
    }
}


    private void deleteButtonClicked() {
    int selectedRow = table.getSelectedRow();

    if (selectedRow == -1) {
        JOptionPane.showMessageDialog(this, "Please select a row to delete.", "Error", JOptionPane.ERROR_MESSAGE);
        return;
    }

    String deletedFileSizeString = (String) table.getValueAt(selectedRow, 2);

    double deletedFileSize = Double.parseDouble(deletedFileSizeString);

    tableModel.removeRow(selectedRow);

    memorySizeLeft += deletedFileSize;
    memorySizeLabel.setText("Memory Size Left: " + memorySizeLeft + " MB");
}

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ManajemenFile::new);
    }
}
