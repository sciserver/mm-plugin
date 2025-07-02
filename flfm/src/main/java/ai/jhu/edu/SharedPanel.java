package ai.jhu.edu;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import ij.ImagePlus;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;


public class SharedPanel extends JPanel {

    private JButton getImageButton;
    private JButton getPSFButton;
    private JTextField psfTextField;
    private JTextField imageTextField;
    private ij.ImagePlus psfImage;
    private ij.ImagePlus inputImage;
    private JComboBox<Integer> iterationJComboBox;
    private JButton calculateButton;

    public SharedPanel() {
        initComponents();
    }

    private void initComponents() {
        // Initialize components here
        // This method should be overridden in subclasses to set up the UI
        setLayout(new GridBagLayout());
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
            ImagePlus img = UtilsUI.getImageFromOpenWindows(this);
            if (img == null) {
                img = UtilsUI.getImageFromFile(this, "Select PSF Image");
            }
            if (img != null){
                psfImage = img;
                psfTextField.setText(psfImage.getTitle());
            }
            else {
                psfTextField.setText("No PSF Selected");
            }
        });
        this.add(getPSFButton, gbc);

        gbc.gridx = 2;
        gbc.gridy = row;
        gbc.gridwidth = 4;
        psfTextField = new JTextField("No PSF Selected");
        psfTextField.setEditable(false); // Make the text field non-editable
        this.add(psfTextField, gbc);

        // Row 2 - Buttons for Input Image Selection
        row++; // Move to the next row
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 2;
        getImageButton = new JButton("Open Input Image");
        // Add action listener to the Input button to open the file in imageTextField
        getImageButton.addActionListener(e -> {
            ImagePlus img = UtilsUI.getImageFromOpenWindows(this);
            if (img == null) {
                img = UtilsUI.getImageFromFile(this, "Select Input Image");
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
        this.add(getImageButton, gbc);

        gbc.gridx = 2;
        gbc.gridy = row;
        gbc.gridwidth = 4;
        imageTextField = new JTextField("No Input Selected");
        imageTextField.setEditable(false); // Make the text field non-editable
        this.add(imageTextField, gbc);


        // Row 3 - Iteration and Calculate Button
        row++; // Move to the next row
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 1; // Reset grid width to 1 for the label
        this.add(new JLabel("Iterations:"), gbc);
        gbc.gridx = 1;
        gbc.gridy = row;
        iterationJComboBox = new JComboBox<Integer>(getValidChoices());
        iterationJComboBox.setSelectedIndex(0); // Set default selection to the first item
        this.add(iterationJComboBox, gbc);
        gbc.gridx = 2;
        gbc.gridwidth = 4;
        gbc.gridy = row;
        calculateButton = new JButton("Calculate");
        this.add(calculateButton, gbc);


    }



}
