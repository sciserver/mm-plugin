package ai.jhu.edu;

import ai.djl.util.ClassLoaderUtils;
import ai.djl.Device;
import ai.djl.MalformedModelException;
import ai.djl.Model;
import ai.djl.engine.Engine;
import ai.djl.engine.EngineException;
import ai.djl.engine.EngineProvider;
import ai.djl.inference.Predictor;
import ai.djl.ndarray.NDArray;
import ai.djl.ndarray.NDManager;
import ai.djl.translate.TranslateException;
import ij.ImagePlus;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Utility class for running deep learning models using DJL (Deep Java Library)
 * within the context of ImageJ plugins.
 * <p>
 * Handles engine initialization, device selection, and model inference.
 */
public class Algorithm {
  /**
   * Singleton instance of the DJL Engine.
   */
  private static Engine engine = null;

  /**
   * Private constructor to prevent instantiation.
   */
  private Algorithm() {
  }

  /**
   * Initializes and returns the DJL Engine instance.
   * <p>
   * Attempts to get the default engine. If unavailable, tries to load the PyTorch engine manually.
   *
   * @return the initialized {@link Engine} instance
   */
  private static Engine initEngine() {
    if (engine != null) {
      return engine;
    }

    // first try to get the default engine but if that fails, try to manually
    // get the PyTorch engine.
    try {
      engine = Engine.getInstance();
    } catch (EngineException e) {
      System.err.println("Default engine not available, trying PyTorch engine.");
      EngineProvider provider = ClassLoaderUtils.initClass(
        // ClassLoaderUtils.getContextClassLoader(),
        Algorithm.class.getClassLoader(),
        EngineProvider.class,
        Constants.PT_ENGINE_CLASS
      );
      // EngineProvider provider = null;
      // try {
      //   // provider = (EngineProvider) Class.forName(Constants.PT_ENGINE_CLASS).newInstance();
      // } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e1) {
      //   e1.printStackTrace();
      // }
      System.out.println("PyTorch engine provider: " + provider);
      if (provider != null) {
        try {
          // System.setProperty("PYTORCH_VERSION", "2.5.1");
          engine = provider.getEngine();
        }
        catch (Exception ee) {
          System.err.println("PyTorch error: " + ee.getMessage());
          System.err.println("torch library name: " + System.mapLibraryName("torch"));
          System.err.println("djl_torch library name: " + System.mapLibraryName("djl_torch"));
          e.printStackTrace();

        }
      } else {
        System.err.println("PyTorch engine not available.");
      }
    }

    return engine;
  }

  /**
   * Returns the available devices for model inference.
   * <p>
   * If the engine is not available, returns a single error device.
   *
   * @return an array of {@link DeviceInfo} representing available devices
   */
  public static DeviceInfo[] getDevices() {
    // Returns the available devices for model inference
    Device[] devices;
    try {
      devices = initEngine().getDevices();
    } catch (EngineException | NullPointerException e){
      e.printStackTrace();
      System.err.println("Engine not available or error retrieving devices: " + e.getMessage());
      return new DeviceInfo[] {
        new DeviceInfo("ERR", -2) // show an error device if the engine is not available
      };
    }

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

  /**
   * Runs the specified model on the given input and PSF images using the selected device.
   *
   * @param modelPathStr the path to the model file
   * @param deviceInfo the device to use for inference
   * @param psfImage the point spread function image
   * @param inputImage the input image to process
   * @return the output {@link ImagePlus} after model inference, or {@code null} if an error occurs
   */
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
          // directory if it's being run as a packaged JAR.
          ClassLoader classLoader = ClassLoaderUtils.getContextClassLoader();
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
