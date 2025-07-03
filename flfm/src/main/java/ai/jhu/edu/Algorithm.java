package ai.jhu.edu;

import ai.djl.Device;
import ai.djl.MalformedModelException;
import ai.djl.Model;
import ai.djl.engine.Engine;
import ai.djl.inference.Predictor;
import ai.djl.ndarray.NDArray;
import ai.djl.ndarray.NDManager;
import ai.djl.translate.TranslateException;
import ij.ImagePlus;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Algorithm {
  private Algorithm() {}

  public static DeviceInfo[] getDevices() {
    // Returns the available devices for model inference
    Device[] devices = Engine.getInstance().getDevices();

    DeviceInfo[] deviceInfos;
    if (devices[0].getDeviceType().toLowerCase() == "gpu") {
      // if the first device is a GPU then all listed devices are GPUs and we
      // can add one more CPU device to the list.
      deviceInfos = new DeviceInfo[devices.length + 1];

      for (int i = 0; i < devices.length; i++) {
        deviceInfos[i] = new DeviceInfo("GPU", devices[i].getDeviceId());
      }
      deviceInfos[devices.length] = new DeviceInfo("CPU", -1); // Add CPU as last device
    } else {
      // If the first device is not a GPU, then there is just a single CPU device
      deviceInfos = new DeviceInfo[1];
      deviceInfos[0] = new DeviceInfo("CPU", -1); // Only CPU
    }
    return deviceInfos;
  }

  public static ImagePlus runModel(
      String modelPathStr, DeviceInfo deviceInfo, ImagePlus psfImage, ImagePlus inputImage) {
    ImagePlus outputImage = null;

    try (NDManager manager = NDManager.newBaseManager(Device.fromName(deviceInfo.toDeviceName()))) {
      NDArray psfArray = ArrayUtils.convertImageToArray(psfImage, manager);
      NDArray inputArray = ArrayUtils.convertImageToArray(inputImage, manager);
      psfArray.divi(psfArray.sum()); // Normalize PSF

      Path modelPath = Paths.get(modelPathStr);
      String modelName = modelPath.getFileName().toString();

      try (Model model = Model.newInstance(modelName, "PyTorch")) {
        try {
          // The model will either be loaded from the specified path
          // if it's being run in the IDE or from the resources/models
          // directory if it's being run as a packaged JAR.S
          ClassLoader classLoader = Algorithm.class.getClassLoader();
          InputStream modelStream = classLoader.getResourceAsStream("models/" + modelPathStr);
          if (modelStream != null) {
            // If the model is found in the resources, load it from the stream
            System.out.println("Loading model from resources: " + modelPathStr);
            model.load(modelStream);
          } else {
            System.out.println("Loading model " + modelName + " from: " + modelPath.getParent());
            model.load(modelPath.getParent());
          }
        } catch (IOException | MalformedModelException e) {
          System.err.println("Error loading model: " + e.getMessage());
          return null;
        }

        try (Predictor<NDArray[], NDArray> predictor = model.newPredictor(new ModelTranslator())) {
          long start = System.currentTimeMillis();
          NDArray out;
          try {
            out = predictor.predict(new NDArray[] {inputArray, psfArray});
          } catch (TranslateException e) {
            System.err.println("Error during prediction: " + e.getMessage());
            return null;
          }
          long end = System.currentTimeMillis();
          System.out.println("Prediction took " + (end - start) / 1000.0 + " s");
          outputImage = ArrayUtils.convertArrayToImage(out);
        }
      }
    }
    return outputImage;
  }
}
