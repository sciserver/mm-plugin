package ai.jhu.edu;

import ij.*;
import ij.gui.*;
import ij.plugin.frame.*;
import ij.process.*;
import java.awt.*;

public class PluginUI extends PlugInFrame {

  public PluginUI() {
    super("PluginUI");
  }

  public void run(String arg) {
    add(new SharedPanel());
    pack();
    GUI.center(this);
    show();
  }
}
