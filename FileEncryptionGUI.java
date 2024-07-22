import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;

public class FileEncryptionGUI extends JFrame {
    private JComboBox<String> driveSelector;
    private JRadioButton ntfsButton, exFatButton, fat32Button;
    private ButtonGroup fileFormatGroup;
    private JTextField criticalFileField;
    private JButton browseButton, actionButton;
    private JRadioButton encryptOption, retrieveOption;
    private ButtonGroup actionGroup;

    public FileEncryptionGUI() {
        setTitle("File Encryption Software");
        setSize(500, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new GridBagLayout());

        // Initialize components
        initComponents();
    }

    private void initComponents() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // External Drive Selector
        JLabel driveLabel = new JLabel("Select External Drive:");
        gbc.gridx = 0;
        gbc.gridy = 0;
        add(driveLabel, gbc);

        driveSelector = new JComboBox<>(detectExternalDrives());
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        add(driveSelector, gbc);

        // File Format Selector
        JLabel formatLabel = new JLabel("Select File Format:");
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        add(formatLabel, gbc);

        JPanel formatPanel = new JPanel();
        ntfsButton = new JRadioButton("NTFS");
        exFatButton = new JRadioButton("exFAT");
        fat32Button = new JRadioButton("FAT32");
        fileFormatGroup = new ButtonGroup();
        fileFormatGroup.add(ntfsButton);
        fileFormatGroup.add(exFatButton);
        fileFormatGroup.add(fat32Button);
        formatPanel.add(ntfsButton);
        formatPanel.add(exFatButton);
        formatPanel.add(fat32Button);

        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        add(formatPanel, gbc);

        // Action Selector (Encrypt or Retrieve)
        JLabel actionLabel = new JLabel("Action:");
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 1;
        add(actionLabel, gbc);

        JPanel actionPanel = new JPanel();
        encryptOption = new JRadioButton("Encrypt");
        retrieveOption = new JRadioButton("Retrieve");
        actionGroup = new ButtonGroup();
        actionGroup.add(encryptOption);
        actionGroup.add(retrieveOption);
        encryptOption.addActionListener(e -> toggleCriticalFileSelection(true));
        retrieveOption.addActionListener(e -> toggleCriticalFileSelection(false));
        actionPanel.add(encryptOption);
        actionPanel.add(retrieveOption);

        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        add(actionPanel, gbc);

        // Critical File Selector
        JLabel fileLabel = new JLabel("Select Critical File:");
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        add(fileLabel, gbc);

        criticalFileField = new JTextField(20);
        criticalFileField.setEditable(false);
        gbc.gridx = 1;
        gbc.gridy = 2;
        add(criticalFileField, gbc);

        browseButton = new JButton("Browse");
        browseButton.addActionListener(e -> selectCriticalFile());
        gbc.gridx = 2;
        gbc.gridy = 2;
        add(browseButton, gbc);

        // Action Button
        actionButton = new JButton("Execute");
        actionButton.addActionListener(e -> executeAction());
        gbc.gridx = 1;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        add(actionButton, gbc);
    }

    private String[] detectExternalDrives() {
        // Dummy implementation, replace with actual drive detection code
        return new String[]{"E:\\", "F:\\"};
    }

    private void selectCriticalFile() {
        JFileChooser fileChooser = new JFileChooser();
        int returnValue = fileChooser.showOpenDialog(this);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            criticalFileField.setText(selectedFile.getAbsolutePath());
        }
    }

    private void toggleCriticalFileSelection(boolean enable) {
        criticalFileField.setEnabled(enable);
        browseButton.setEnabled(enable);
    }

    private void executeAction() {
        String drive = (String) driveSelector.getSelectedItem();
        String format = getSelectedFileFormat();
        String action = getSelectedAction();
        String dummyFolderPath = "dummyFiles";
        String additonalFolderPath = "additionalFiles";

        if (drive == null || format == null || action == null) {
            JOptionPane.showMessageDialog(this, "Please select all options before proceeding.");
            return;
        }

        if ("Encrypt".equals(action)) {
            String filePath = criticalFileField.getText();
            if (filePath.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please select a critical file.");
                return;
            }
            
            Encryptor fileEncryptor = new Encryptor();
            fileEncryptor.DummyFiles(dummyFolderPath, filePath);
            fileEncryptor.AdditionalFiles(additonalFolderPath,filePath);
            fileEncryptor.CombineFiles(drive, dummyFolderPath, additonalFolderPath);
            JOptionPane.showMessageDialog(this, "Encryption Sucessful");


        } else if ("Retrieve".equals(action)) {
            criticalFileField.setText("");
            JOptionPane.showMessageDialog(this, "Retrieving files from drive: " + drive + " with format: " + format);
            // Implement retrieval logic here
        }
    }

    private String getSelectedFileFormat() {
        if (ntfsButton.isSelected()) {
            return "NTFS";
        } else if (exFatButton.isSelected()) {
            return "exFAT";
        } else if (fat32Button.isSelected()) {
            return "FAT32";
        }
        return null;
    }

    private String getSelectedAction() {
        if (encryptOption.isSelected()) {
            return "Encrypt";
        } else if (retrieveOption.isSelected()) {
            return "Retrieve";
        }
        return null;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            FileEncryptionGUI gui = new FileEncryptionGUI();
            gui.setVisible(true);
        });
    }
}
