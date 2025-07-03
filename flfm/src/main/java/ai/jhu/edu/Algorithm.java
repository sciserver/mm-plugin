package ai.jhu.edu;

public class Algorithm implements Runnable {

  private final String modelName;
  private final String psfPath;
  private final String inputPath;
  private final int iterations;

  public Algorithm(String modelName, String psfPath, String inputPath, int iterations) {
    this.modelName = modelName;
    this.psfPath = psfPath;
    this.inputPath = inputPath;
    this.iterations = iterations;
  }

  @Override
  public void run() {
    // Implement the algorithm logic here
    System.out.println("Running algorithm with:");
    System.out.println("Model: " + modelName);
    System.out.println("PSF Path: " + psfPath);
    System.out.println("Input Path: " + inputPath);
    System.out.println("Iterations: " + iterations);

    // sleep for 5 seconds to simulate processing
    try {
      Thread.sleep(5000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      System.err.println("Algorithm interrupted: " + e.getMessage());
    }
    // Placeholder for actual processing logic
  }
}
