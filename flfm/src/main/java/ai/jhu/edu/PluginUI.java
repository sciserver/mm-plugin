package ai.jhu.edu;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

import java.awt.*;

public class PluginUI extends JFrame{
    private JFrame frame;
    private JButton getImageButton;
    private JButton getPSFButton;

    // Row 2 --  Panel that displays two images side by side
    private JPanel imagePanel;
    private JLabel imageLabel1;
    private JLabel imageLabel2;

    // Row 3
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

    private JPanel initControlPanel() {
        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        gbc.fill = GridBagConstraints.HORIZONTAL;


        int row = 0;

        // Row 1 - Buttons for PSF and Input Image Selection
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0.25;
        gbc.gridwidth = 1; // Reset grid width to 1 for the label
        getPSFButton = new JButton("Open PSF Image");
        // Add action listener to the PSF button to open the file in psfTextField
        getPSFButton.addActionListener(e -> {
            // Open a file chooser dialog to select the PSF file
            // Open a file chooser dialog to select the PSF file
            JFileChooser psfChooser = new JFileChooser();
            psfChooser.setFileFilter(new FileNameExtensionFilter("PGM and TIFF files", "pgm", "tif", "tiff"));
            int returnValue = psfChooser.showOpenDialog(controlPanel);
            if (returnValue == JFileChooser.APPROVE_OPTION) {
                // Get the selected file and set it to the text field
                java.io.File selectedFile = psfChooser.getSelectedFile();
                psfTextField.setText(selectedFile.getAbsolutePath());
            }
        });




        controlPanel.add(getPSFButton, gbc);
        gbc.gridx = 1;
        gbc.gridy = row;
        gbc.weightx = 0.75;
        gbc.gridwidth = 1; // Reset grid width to 1 for the button
        getImageButton = new JButton("Get Input Image");




        // Row 2 - Image Panel
        row++; // Move to the next row
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0.25;
        gbc.gridwidth = 1; // Reset grid width to 1 for the label
        controlPanel.add(new JLabel("Images:"), gbc);
        gbc.gridx = 1;
        gbc.gridy = row;
        gbc.weightx = 0.75;
        gbc.gridwidth = 2; // Span two columns for the image panel

        imagePanel = new JPanel();
        imagePanel.setLayout(new GridLayout(1, 2, 10, 10)); // Two columns for side
        imageLabel1 = new JLabel("PSF Preview");
        imageLabel1.setHorizontalAlignment(SwingConstants.CENTER);
        imageLabel1.setPreferredSize(new Dimension(200, 200)); // Set preferred size for the label


        imageLabel2 = new JLabel("Input Preview");
        imageLabel2.setHorizontalAlignment(SwingConstants.CENTER);
        imageLabel2.setPreferredSize(new Dimension(200, 200)); // Set preferred size for the label
        imagePanel.add(imageLabel1);
        imagePanel.add(imageLabel2);
        controlPanel.add(imagePanel, gbc);

        // Row 3 - Iteration and Calculate Button
        row++; // Move to the next row
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0.25;
        gbc.gridwidth = 1; // Reset grid width to 1 for the label
        controlPanel.add(new JLabel("Iterations:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = row;
        gbc.weightx = 0.75;
        iterationJComboBox = new JComboBox<Integer>(getValidChoices());
        iterationJComboBox.setSelectedIndex(0); // Set default selection to the first item
        controlPanel.add(iterationJComboBox, gbc);

        gbc.gridx = 2;
        gbc.gridy = row;
        gbc.weightx = 0.75;
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
            pluginUI.setSize(400, 200);
            pluginUI.setVisible(true);
            pluginUI.setLocationRelativeTo(null); // Center the window
        });
    }
}
