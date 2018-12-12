package net.tnemc.config;

import com.hellyard.cuttlefish.CuttlefishBuilder;
import com.hellyard.cuttlefish.composer.yaml.YamlComposer;
import com.hellyard.cuttlefish.grammar.yaml.YamlNode;

import java.io.File;
import java.util.LinkedList;

/**
 * Created by creatorfromhell.
 *
 * The New Config Library Minecraft Server Plugin
 *
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0
 * International License. To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/
 * or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 */
public class CommentedConfiguration extends ConfigSection {

  private final File file;
  private final File defaults;

  /**
   * Constructor for {@link CommentedConfiguration}.
   * @param file The file that will be our final configuration file.
   * @param defaults The file that contains our default configurations.
   */
  public CommentedConfiguration(final File file, final File defaults) {
    super(null);
    this.file = file;
    this.defaults = defaults;
  }

  /**
   * Loads our configurations, reading the defaults file if needed.
   */
  public void load() {
    load(true);
  }

  protected void decodeNodes(LinkedList<YamlNode> nodes) {

    for(YamlNode node : nodes) {

      ConfigSection finished = new ConfigSection(node);
      final String[] split = node.getNode().split("\\.");

      ConfigSection parent = this;

      if(split.length > 1) {
        for(int i = 0; i < split.length; i++) {

          if(i == (split.length - 1)) {
            parent.children.put(split[i], finished);
          } else {
            parent = parent.getSection(split[i]);
          }
        }
      } else {
        children.put(node.getNode(), finished);
      }
    }
  }

  /**
   * Loads our configurations, copying over defaults that are not present in our file if needed.
   */
  public void load(boolean copyDefaults) {
    final File load = (file.exists())? file : defaults;
    final LinkedList<YamlNode> loaded = (LinkedList<YamlNode>)new CuttlefishBuilder(load, "yaml").build().getNodes();

    if(copyDefaults && defaults != null) {

      LinkedList<YamlNode> copied = new LinkedList<>();
      final CommentedConfiguration defaultConfig = new CommentedConfiguration(defaults, null);
      defaultConfig.load(false);

      for(YamlNode yamlNode : defaultConfig.getNodeValues()) {
        if(!loaded.contains(yamlNode)) {
          copied.add(yamlNode);
        } else {
          copied.add(loaded.get(loaded.indexOf(yamlNode)));
        }
      }
      decodeNodes(copied);
    } else {
      decodeNodes(loaded);
    }
    save(file);
  }

  /**
   * Used to save our configuration with the file provided in the constructor.
   * @return True if saved, otherwise false.
   */
  public boolean save() {
    return save(file);
  }

  /**
   * Used to save our configuration file.
   * @param file The file to save our configuration to.
   * @return True if saved, otherwise false.
   */
  public boolean save(File file) {
    if(!file.exists()) {
      try {
        file.createNewFile();
      } catch(Exception ignore) {
        return false;
      }
    }
    return new YamlComposer().compose(file, getNodeValues());
  }
}