package ai.jhu.edu;

import ij.ImagePlus;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.Optional;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

// ClassLoader.getResourceAsStream("models/model.onnx")

public class SharedPanel extends JPanel {

  private JButton getImageButton;
  private JButton getPSFButton;
  private JTextField psfTextField;
  private JTextField imageTextField;
  private ij.ImagePlus psfImage;
  private ij.ImagePlus inputImage;
  private JComboBox<String> iterationJComboBox;
  private JButton calculateButton;
  private String[] modelPaths;

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

    // Row 1 - PSF Button and Text Field ===================================
    gbc.gridx = 0;
    gbc.gridy = row;
    gbc.gridwidth = 2;
    getPSFButton = new JButton(Constants.BTN_PSF);
    // Add action listener to the PSF button to open the file in psfTextField
    getPSFButton.addActionListener(
        e -> {
          psfImage = UtilsUI.getImage(this, Constants.LBL_SELECT_PSF);

          psfTextField.setText(
              Optional.ofNullable(psfImage).map(ImagePlus::getTitle).orElse(Constants.LBL_NO_PSF));
        });
    this.add(getPSFButton, gbc);

    gbc.gridx = 2;
    gbc.gridy = row;
    gbc.gridwidth = 4;
    psfTextField = new JTextField(Constants.LBL_NO_PSF);
    psfTextField.setEditable(false); // Make the text field non-editable
    this.add(psfTextField, gbc);
    // =====================================================================

    row++; // Move to the next row
    // Row 2 - Image Button and Text Field =================================
    gbc.gridx = 0;
    gbc.gridy = row;
    gbc.gridwidth = 2;
    getImageButton = new JButton(Constants.BTN_INPUT);
    // Add action listener to the Input button to open the file in imageTextField
    getImageButton.addActionListener(
        e -> {
          inputImage = UtilsUI.getImage(this, Constants.LBL_SELECT_INPUT);

          imageTextField.setText(
              Optional.ofNullable(inputImage)
                  .map(ImagePlus::getTitle)
                  .orElse(Constants.LBL_NO_INPUT));
        });
    this.add(getImageButton, gbc);

    gbc.gridx = 2;
    gbc.gridy = row;
    gbc.gridwidth = 4;
    imageTextField = new JTextField(Constants.LBL_NO_INPUT);
    imageTextField.setEditable(false); // Make the text field non-editable
    this.add(imageTextField, gbc);
    // =====================================================================

    // Row 3 - Iteration and Calculate Button ==============================
    row++; // Move to the next row
    gbc.gridx = 0;
    gbc.gridy = row;
    gbc.gridwidth = 1; // Reset grid width to 1 for the label
    this.add(new JLabel(Constants.LBL_ITERATIONS), gbc);

    gbc.gridx = 1;
    gbc.gridy = row;

    modelPaths = Optional.of(UtilsUI.getModelLocations()).orElse(new String[] {});

    //extract the valid choices for iterations from the model paths
    String[] validChoices = java.util.Arrays.stream(modelPaths)
        .map(path -> path.replaceAll("[^0-9]", ""))
        .filter(path -> !path.isEmpty())
        .distinct()
        .map(Integer::parseInt)
        .sorted()
        .map(String::valueOf)
        .toArray(String[]::new);

    iterationJComboBox =
        new JComboBox<String>(validChoices);
    iterationJComboBox.setSelectedIndex(0); // Set default selection to the first item
    this.add(iterationJComboBox, gbc);

    gbc.gridx = 2;
    gbc.gridwidth = 4;
    gbc.gridy = row;
    calculateButton = new JButton(Constants.BTN_CALCULATE);
    calculateButton.addActionListener(e -> {
        // Get the selected model name
        String modelName = modelPaths[iterationJComboBox.getSelectedIndex()];

        System.out.println(modelName);

        // Get the PSF and input image paths
        // String psfPath = Optional.ofNullable(psfImage).map(ImagePlus::getTitle).orElse("");
        // String inputPath =
        //     Optional.ofNullable(inputImage).map(ImagePlus::getTitle).orElse("");

        // // Create and run the algorithm
        // Algorithm algorithm = new Algorithm(modelName, psfPath, inputPath, 1);
        // new Thread(algorithm).start();
    });


    this.add(calculateButton, gbc);
    // =====================================================================
  }
}
