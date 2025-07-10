package ai.jhu.edu;

import ij.ImagePlus;
import java.awt.Component;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.file.DirectoryStream;
import java.nio.file.Path;
import java.util.Enumeration;
import java.util.Optional;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.StyledEditorKit;

import ai.djl.util.ClassLoaderUtils;

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
    URL url = UtilsUI.class.getClassLoader().getResource("models");
    // System.err.println(">>>Url = " + url);
    String[] result = null;

    if (url != null && url.getProtocol().equals("jar")) {
      String jarPath = url.getPath().substring(5, url.getPath().indexOf("!"));

      try {
        try (JarFile jar = new JarFile(URLDecoder.decode(jarPath, "UTF-8"))) {
            Enumeration<JarEntry> entries = jar.entries();
            // System.err.println("Jar entries:");
            result =
                java.util.Collections.list(entries).stream()
                    .filter(
                        entry ->
                            !entry.isDirectory()
                                && entry.getName().startsWith("models/")
                                && entry.getName().endsWith(".pt"))
                    .map(entry -> entry.getName().substring("models/".length()))
                    .sorted()
                    .toArray(String[]::new);
        }
      } catch (IOException e) {
        e.printStackTrace();
      }
  }

    return result;
  }


  // public static String[] getModelLocationsFromJar() {
  //   String folderName = "models";
  //   String[] result = null;
  //   try {
  //     System.err.println("trying to get resources from " + folderName);
  //     // URL url = ClassLoaderUtils.getContextClassLoader().getResource(folderName);
  //     URL url = UtilsUI.class.getResource(folderName);
  //     System.err.println("getModelLocationsFromJar: url = " + url);
  //     if (url != null) {
  //       String path = url.getPath();
  //       if (path.contains(".jar!")) {
  //         String jarPath = path.substring(0, path.indexOf("!")).replace("file:", "");

  //         try (JarFile jarFile = new JarFile(jarPath)) {
  //           result =
  //               java.util.Collections.list(jarFile.entries()).stream()
  //                   .filter(
  //                       entry ->
  //                           !entry.isDirectory()
  //                               && entry.getName().startsWith(folderName + "/")
  //                               && entry.getName().endsWith(".pt"))
  //                   .map(entry -> entry.getName().substring(folderName.length() + 1))
  //                   .sorted()
  //                   .toArray(String[]::new);
  //         }
  //       }
  //     }
  //   } catch (Exception e) {
  //     System.err.println(
  //         "Error retrieving model locations from JAR: " + e.getMessage());
  //     e.printStackTrace();
  //   }

  //   return result;
  // }

  public static String[] getModelLocations() {
    return Optional.ofNullable(getModelLocationsFromJar())
        .orElseGet(() -> getModelLocationsFromFile());
  }
}
