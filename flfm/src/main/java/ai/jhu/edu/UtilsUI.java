package ai.jhu.edu;

import ij.ImagePlus;
import java.awt.Component;
import java.io.File;
import java.net.URL;
import java.nio.file.DirectoryStream;
import java.nio.file.Path;
import java.util.Optional;
import java.util.jar.JarFile;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

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
    String selectedTitle =
        (String)
            javax.swing.JOptionPane.showInputDialog(
                parent,
                Constants.LBL_SELECT_IMG,
                "Open Images",
                javax.swing.JOptionPane.PLAIN_MESSAGE,
                null,
                windowTitles,
                windowTitles[0]);
    if (selectedTitle != null) {
      return ij.WindowManager.getImage(selectedTitle);
    }
    return null;
  }

  public static ImagePlus getImage(Component parent, String title) {
    return Optional.ofNullable(getImageFromOpenWindows(parent))
        .orElseGet(() -> getImageFromFile(parent, title));
  }

  public static String[] getModelLocationsFromFile() {
    Path modelsPath = java.nio.file.Paths.get("flfm", "src", "main", "resources", "models");

    String[] modelPaths = null;
    try (DirectoryStream<Path> stream =
        java.nio.file.Files.newDirectoryStream(modelsPath, "*.pt")) {
      modelPaths =
          java.util.stream.StreamSupport.stream(stream.spliterator(), false)
              .map(Path::toString)
              .sorted()
              .toArray(String[]::new);
    } catch (Exception e) {
      e.printStackTrace();
    }

    return modelPaths;
  }

  public static String[] getModelLocationsFromJar() {
    String folderName = "models";
    String[] result = null;
    try {
      URL url = UtilsUI.class.getClassLoader().getResource(folderName);
      if (url != null) {
        String path = url.getPath();
        if (path.contains(".jar!")) {
          String jarPath = path.substring(0, path.indexOf("!")).replace("file:", "");

          try (JarFile jarFile = new JarFile(jarPath)) {
            result =
                java.util.Collections.list(jarFile.entries()).stream()
                    .filter(
                        entry ->
                            !entry.isDirectory()
                                && entry.getName().startsWith(folderName + "/")
                                && entry.getName().endsWith(".pt"))
                    .map(entry -> entry.getName().substring(folderName.length() + 1))
                    .sorted()
                    .toArray(String[]::new);
          }
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }

    return result;
  }

  public static String[] getModelLocations() {
    return Optional.ofNullable(getModelLocationsFromJar())
        .orElseGet(() -> getModelLocationsFromFile());
  }
}
