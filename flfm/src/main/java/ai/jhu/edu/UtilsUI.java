package ai.jhu.edu;

import java.io.File;

import java.awt.Component;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

import ij.ImagePlus;

public class UtilsUI {

    public static ImagePlus getImageFromFile(Component parent, String title) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle(title);
        fileChooser.setFileFilter(new FileNameExtensionFilter("TIFF Files", "tif", "tiff"));
        int returnValue = fileChooser.showOpenDialog(parent);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            return new ImagePlus(selectedFile.getAbsolutePath());
        }
        return null;
    }

    public static ImagePlus getImageFromOpenWindows(Component parent) {
        String[] windowTitles = ij.WindowManager.getImageTitles();
        if (windowTitles.length == 0) {
            System.out.println("No ImageJ windows found going to disk instead.");
            return null;
        }
        String selectedTitle = (String) javax.swing.JOptionPane.showInputDialog(parent, "Select an image:", "Open Images",
                javax.swing.JOptionPane.PLAIN_MESSAGE, null, windowTitles, windowTitles[0]);
        if (selectedTitle != null) {
            return ij.WindowManager.getImage(selectedTitle);
        }
        return null;
    }
}
