package com.oxygenxml.samples.savehook;

import ro.sync.exml.plugin.Plugin;
import ro.sync.exml.plugin.PluginDescriptor;

/**
 * Workspace access plugin. 
 */
public class SaveHookPlugin extends Plugin {
  /**
   * The static plugin instance.
   */
  private static SaveHookPlugin instance = null;

  /**
   * Constructs the plugin.
   * 
   * @param descriptor The plugin descriptor
   */
  public SaveHookPlugin(PluginDescriptor descriptor) {
    super(descriptor);

    if (instance != null) {
      throw new IllegalStateException("Already instantiated!");
    }
    instance = this;
  }
  
  /**
   * Get the plugin instance.
   * 
   * @return the shared plugin instance.
   */
  public static SaveHookPlugin getInstance() {
    return instance;
  }
}