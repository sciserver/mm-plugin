package ai.jhu.edu;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

import java.awt.*;
import java.io.File;

import ij.ImagePlus;
import ij.WindowManager;

public class PluginUI extends JFrame{
    private JButton getImageButton;
    private JButton getPSFButton;
    private JTextField psfTextField;
    private JTextField imageTextField;
    private ImagePlus psfImage;
    private ImagePlus inputImage;
    private JComboBox<Integer> iterationJComboBox;
    private JButton calculateButton;

    public PluginUI() {
        setTitle("FLFM Plugin");
        setLayout(new BorderLayout());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // This will house the buttons for setting up the calculation

        JPanel controlPanel = initControlPanel();
        add(controlPanel, BorderLayout.NORTH);
        setVisible(true);
    }

    private ImagePlus getImageFromFile(String title) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle(title);
        fileChooser.setFileFilter(new FileNameExtensionFilter("TIFF Files", "tif", "tiff"));
        int returnValue = fileChooser.showOpenDialog(this);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            return new ImagePlus(selectedFile.getAbsolutePath());
        }
        return null;
    }

    private ImagePlus getImageFromOpenWindows() {
        String[] windowTitles = WindowManager.getImageTitles();
        if (windowTitles.length == 0) {
            System.out.println("No ImageJ windows found going to disk instead.");
            return null;
        }
        String selectedTitle = (String) JOptionPane.showInputDialog(this, "Select an image:", "Open Images",
                JOptionPane.PLAIN_MESSAGE, null, windowTitles, windowTitles[0]);
        if (selectedTitle != null) {
            return WindowManager.getImage(selectedTitle);
        }
        return null;
    }

    private JPanel initControlPanel() {
        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        gbc.fill = GridBagConstraints.HORIZONTAL;
        int row = 0;

        // Row 1 - Buttons for PSF Selection
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 2;
        getPSFButton = new JButton("Open PSF Image");
        // Add action listener to the PSF button to open the file in psfTextField
        getPSFButton.addActionListener(e -> {
            ImagePlus img = getImageFromOpenWindows();
            if (img == null) {
                img = getImageFromFile("Select PSF Image");
            }
            if (img != null){
                psfImage = img;
                psfTextField.setText(psfImage.getTitle());
            }
            else {
                psfTextField.setText("No PSF Selected");
            }
        });
        controlPanel.add(getPSFButton, gbc);

        gbc.gridx = 2;
        gbc.gridy = row;
        gbc.gridwidth = 4;
        psfTextField = new JTextField("No PSF Selected");
        psfTextField.setEditable(false); // Make the text field non-editable
        controlPanel.add(psfTextField, gbc);

        // Row 2 - Buttons for Input Image Selection
        row++; // Move to the next row
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 2;
        getImageButton = new JButton("Open Input Image");
        // Add action listener to the Input button to open the file in imageTextField
        getImageButton.addActionListener(e -> {
            ImagePlus img = getImageFromOpenWindows();
            if (img == null) {
                img = getImageFromFile("Select Input Image");
            }
            if (img != null){
                inputImage = img;
                imageTextField.setText(inputImage.getTitle());
                inputImage.show();
            }
            else {
                imageTextField.setText("No Input Selected");
            }
        });
        controlPanel.add(getImageButton, gbc);

        gbc.gridx = 2;
        gbc.gridy = row;
        gbc.gridwidth = 4;
        imageTextField = new JTextField("No Input Selected");
        imageTextField.setEditable(false); // Make the text field non-editable
        controlPanel.add(imageTextField, gbc);


        // Row 3 - Iteration and Calculate Button
        row++; // Move to the next row
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 1; // Reset grid width to 1 for the label
        controlPanel.add(new JLabel("Iterations:"), gbc);
        gbc.gridx = 1;
        gbc.gridy = row;
        iterationJComboBox = new JComboBox<Integer>(getValidChoices());
        iterationJComboBox.setSelectedIndex(0); // Set default selection to the first item
        controlPanel.add(iterationJComboBox, gbc);
        gbc.gridx = 2;
        gbc.gridwidth = 4;
        gbc.gridy = row;
        calculateButton = new JButton("Calculate");
        controlPanel.add(calculateButton, gbc);

        return controlPanel;

    }

    private Integer[] getValidChoices() {
        // This method should return the valid choices for iterations
        // For example, we can return an array of integers from 1 to 10
        Integer[] choices = new Integer[10];
        for (int i = 0; i < 10; i++) {
            choices[i] = i + 1;
        }
        return choices;
    }

    public static void main(String[] args) {
        // Create a simple GUI to display "Hello, World!"
        SwingUtilities.invokeLater(() -> {
            PluginUI pluginUI = new PluginUI();
            pluginUI.setVisible(true);
            pluginUI.pack(); // Adjust the window size based on its components;
            pluginUI.setLocationRelativeTo(null); // Center the window
        });
    }
}
