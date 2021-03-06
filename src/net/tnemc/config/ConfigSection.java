package net.tnemc.config;

import com.hellyard.cuttlefish.grammar.yaml.YamlNode;
import com.hellyard.cuttlefish.grammar.yaml.YamlValue;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by creatorfromhell.
 *
 * The New Config Library Minecraft Server Plugin
 *
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0
 * International License. To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/
 * or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 */
public class ConfigSection {

  private YamlNode baseNode;

  protected boolean debug = false;

  protected LinkedHashMap<String, ConfigSection> children = new LinkedHashMap<>();

  /**
   * Constructor for {@link ConfigSection}.
   * @param baseNode The YamlNode associated with this {@link ConfigSection}.
   */
  public ConfigSection(YamlNode baseNode) {
    this.baseNode = baseNode;
  }

  /**
   * Returns the {@link YamlNode node} associated with this {@link ConfigSection}.
   * @return The {@link YamlNode node} associated with this {@link ConfigSection}.
   */
  public YamlNode getBaseNode() {
    return baseNode;
  }

  /**
   * Used to get the child {@link ConfigSection sections} of this {@link ConfigSection}, which are
   * only 1 level deep in indentation from this {@link ConfigSection}.
   * @return A String Set of the child nodes.
   */
  public Set<String> getKeys() {
    return getKeys(false);
  }


  /**
   * Used to get the child {@link ConfigSection sections} of this {@link ConfigSection}.
   * @return A String Set of the child nodes.
   */
  public Set<String> getKeys(boolean deep) {
    LinkedHashSet<String> keys = new LinkedHashSet<>();

    for(YamlNode node : getNodeValues()) {
      String keyStr = (baseNode == null)? node.getNode() : node.getNode().replace(baseNode.getNode() + ".", "");

      if(!deep) {
        keyStr = keyStr.split("\\.")[0];
      }

      if(!keys.contains(keyStr)) keys.add(keyStr);
    }
    return keys;
  }

  /**
   * Used to get the child {@link ConfigSection sections} of this {@link ConfigSection}, which are
   * only 1 level deep in indentation from this {@link ConfigSection} in the order they are in the config file.
   * @return A String LinkedHashSet of the child nodes.
   */
  public LinkedHashSet<String> getKeysLinked() {
    return getKeysLinked(false);
  }


  /**
   * Used to get the child {@link ConfigSection sections} of this {@link ConfigSection} in the order they are in the config file.
   * @return A String LinkedHashSet of the child nodes.
   */
  public LinkedHashSet<String> getKeysLinked(boolean deep) {
    LinkedHashSet<String> keys = new LinkedHashSet<>();

    for(YamlNode node : getNodeValues()) {
      String keyStr = (baseNode == null)? node.getNode() : node.getNode().replace(baseNode.getNode() + ".", "");

      if(!deep) {
        keyStr = keyStr.split("\\.")[0];
      }

      if(!keys.contains(keyStr)) keys.add(keyStr);
    }
    return keys;
  }

  /**
   * Used to check if this {@link ConfigSection} contains the specified child node.
   * @param node The node to check for.
   * @return True if the node exists, otherwise false.
   */
  public boolean contains(String node) {
    return getSection(node) != null;
  }

  /**
   * Returns the {@link ConfigSection section} associated with the specified string node if it exists, otherwise
   * returns null.
   * @param node The string node to use for the search.
   * @return The {@link ConfigSection section} associated with the specified string node if it exists, otherwise
   * returns null
   */
  public ConfigSection getSection(String node) {
    final String[] nodeSplit = node.split("\\.");

    ConfigSection section = this;

    for(String str : nodeSplit) {
      if(str.equalsIgnoreCase(nodeSplit[nodeSplit.length - 1])) {
        if(section == null) return null;
        return section.children.get(str);
      } else {
        if(section == null) return null;
        section = section.children.get(str);
      }
    }
    return section;
  }

  public ConfigSection getSectionOrCreate(String node) {
    final String[] nodeSplit = node.split("\\.");

    ConfigSection section = this;

    for(String str : nodeSplit) {
      final ConfigSection next = section.children.get(str);
      if(next == null) {
        final YamlNode base = section.getBaseNode();
        section.createSection(new ConfigSection(new YamlNode(base, base.getIndentation() + 2, base.getLineNumber() + 1, str + ":", new LinkedList<>(), str, base.getNode() + "." + str)));
      }
      if(str.equalsIgnoreCase(nodeSplit[nodeSplit.length - 1])) {
        if(section == null) return null;
        return section.children.get(str);
      } else {
        if(section == null) return null;
        section = section.children.get(str);
      }
    }
    return section;
  }

  public ConfigSection getSectionOrCreate(String node, int index) {
    final String[] nodeSplit = node.split("\\.");

    ConfigSection section = this;

    for(String str : nodeSplit) {
      final ConfigSection next = section.children.get(str);
      if(next == null) {
        final YamlNode base = section.getBaseNode();
        int indentation = base.getIndentation() + 2;
        if(section.children.size() > 0) {
          indentation = section.children.values().iterator().next().getBaseNode().getIndentation();
        }
        section.createSection(new ConfigSection(new YamlNode(base, indentation, base.getLineNumber() + 1, str + ":", new LinkedList<>(), str, base.getNode() + "." + str)), index);
      }
      if(str.equalsIgnoreCase(nodeSplit[nodeSplit.length - 1])) {
        if(section == null) return null;
        return section.children.get(str);
      } else {
        if(section == null) return null;
        section = section.children.get(str);
      }
    }
    return section;
  }

  public void setOrCreate(String node, String... values) {
    List<YamlValue> valuesList = new LinkedList<>();
    for(String value : values) {
      valuesList.add(new YamlValue(new ArrayList<>(), value, "String"));
    }
    getSectionOrCreate(node).getBaseNode().setValues(valuesList);
  }

  public void setOrCreate(String node, YamlValue... values) {
    getSectionOrCreate(node).getBaseNode().setValues(new LinkedList<>(Arrays.asList(values)));
  }

  public void setOrCreate(String node, int index, String... values) {
    List<YamlValue> valuesList = new LinkedList<>();
    for(String value : values) {
      valuesList.add(new YamlValue(new ArrayList<>(), value, "String"));
    }
    getSectionOrCreate(node, index).getBaseNode().setValues(valuesList);
  }

  public void setOrCreate(String node, int index, YamlValue... values) {
    getSectionOrCreate(node, index).getBaseNode().setValues(new LinkedList<>(Arrays.asList(values)));
  }

  public void set(String node, String... values) {
    List<YamlValue> valuesList = new LinkedList<>();
    for(String value : values) {
      valuesList.add(new YamlValue(new ArrayList<>(), value, "String"));
    }
    getNode(node).setValues(valuesList);
  }

  public void set(String node, YamlValue... values) {
    getNode(node).setValues(Arrays.asList(values));
  }

  public void setValue(String node, YamlValue value, int number) {
    List<YamlValue> values = getNode(node).getValues();
    if(values.size() < number) {
      number = values.size();
    }
    values.set(number, value);
    getNode(node).setValues(values);
  }

  /**
   * Returns the {@link YamlNode node} associated with the specified string node if it exists, otherwise
   * returns null.
   * @param node The string node to use for the search.
   * @return The {@link YamlNode node} associated with the specified string node if it exists, otherwise
   * returns null
   */
  public YamlNode getNode(String node) {
    final String[] nodeSplit = node.split("\\.");

    ConfigSection section = this;

    for(String str : nodeSplit) {
      if(str.equalsIgnoreCase(nodeSplit[nodeSplit.length - 1])) {
        return section.children.get(str).getBaseNode();
      } else {
        if(section == null) break;
        section = section.children.get(str);
      }
    }
    if(section == null) return null;
    return section.getBaseNode();
  }

  /**
   * Used to get all child {@link YamlNode nodes} of this one.
   * @return A LinkedList of all child {@link YamlNode nodes}.
   */
  public LinkedList<YamlNode> getNodeValues() {
    LinkedList<YamlNode> nodeValues = new LinkedList<>();

    for(ConfigSection node : children.values()) {
      nodeValues.add(node.getBaseNode());
      if(node.children.size() > 0) {
        nodeValues.addAll(node.getNodeValues());
      }
    }
    return nodeValues;
  }

  /**
   * Adds a new {@link ConfigSection section} under this one.
   * @param section The {@link ConfigSection section} to add.
   */
  public void createSection(ConfigSection section) {

    ConfigSection parent = this;
    final String[] split = section.getBaseNode().getNode().split("\\.");


    if(split.length > 1) {
      String nodeLooped = "";
      for(int i = 0; i < split.length; i++) {
        if(i > 0) nodeLooped += ".";
        nodeLooped += split[i];

        if(parent.getBaseNode().getNode().contains(nodeLooped)) continue;

        if(i == (split.length - 1)) {
          parent.children.put(split[i], section);
        } else {
          parent = parent.getSection(split[i]);
        }
      }
    } else {
      children.put(section.getBaseNode().getNode(), section);
    }
  }

  /**
   * Adds a new {@link ConfigSection section} under this one at the specific index.
   * @param section The {@link ConfigSection section} to add.
   */
  public void createSection(ConfigSection section, int index) {

    ConfigSection parent = this;
    final String[] split = section.getBaseNode().getNode().split("\\.");


    if(split.length > 1) {
      String nodeLooped = "";
      for(int i = 0; i < split.length; i++) {
        if(i > 0) nodeLooped += ".";
        nodeLooped += split[i];

        if(parent.getBaseNode().getNode().contains(nodeLooped)) continue;

        if(i == (split.length - 1)) {
          parent.addChildIndex(index, split[i], section);
        } else {
          parent = parent.getSection(split[i]);
        }
      }
    } else {
      addChildIndex(index, section.getBaseNode().getNode(), section);
    }
  }

  public void addChildIndex(int index, String node, ConfigSection section) {
    LinkedHashMap<String, ConfigSection> newChildren = new LinkedHashMap<>();

    int i = 0;
    for(Map.Entry<String, ConfigSection> entry : children.entrySet()) {
      if(i == index) {
        newChildren.put(node, section);
      }
      newChildren.put(entry.getKey(), entry.getValue());

      i++;
    }
    children = newChildren;
  }

  /**
   * Checks to see if a node is associated with a configuration section, i.e. a {@link YamlNode} with
   * no values, but rather only contains children.
   * @param node The string node to use in the check.
   * @return True if the {@link ConfigSection section} with the specified string node exists, and the
   * associated {@link YamlNode node} contains no values.
   */
  public boolean isConfigurationSection(String node) {
    final ConfigSection section = getSection(node);
    if(section == null) return false;

    return section.getBaseNode().getValues().size() == 0;
  }

  public int getInt(String node) {
    return getInt(node, 0);
  }

  public int getInt(String node, int def) {
    final ConfigSection section = getSection(node);
    if(section == null) return def;

    try {
      return Integer.valueOf(section.getBaseNode().getValues().get(0).getValue());
    } catch(Exception ignore) {
      return def;
    }
  }

  public boolean getBool(String node) {
    return getBool(node, false);
  }

  public boolean getBool(String node, boolean def) {
    final ConfigSection section = getSection(node);
    if(section == null) return def;

    try {
      return Boolean.valueOf(section.getBaseNode().getValues().get(0).getValue());
    } catch(Exception ignore) {
      return def;
    }
  }

  public double getDouble(String node) {
    return getDouble(node, 0.0);
  }

  public double getDouble(String node, double def) {
    final ConfigSection section = getSection(node);
    if(section == null) return def;

    try {
      return Double.valueOf(section.getBaseNode().getValues().get(0).getValue());
    } catch(Exception ignore) {
      return def;
    }
  }

  public short getShort(String node) {
    return getShort(node, (short)0);
  }

  public short getShort(String node, short def) {
    final ConfigSection section = getSection(node);
    if(section == null) return def;

    try {
      return Short.valueOf(section.getBaseNode().getValues().get(0).getValue());
    } catch(Exception ignore) {
      return def;
    }
  }

  public float getFloat(String node) {
    return getFloat(node, 0.0f);
  }

  public float getFloat(String node, float def) {
    final ConfigSection section = getSection(node);
    if(section == null) return def;

    try {
      return Float.valueOf(section.getBaseNode().getValues().get(0).getValue());
    } catch(Exception ignore) {
      return def;
    }
  }

  public BigDecimal getBigDecimal(String node) {
    return getBigDecimal(node, BigDecimal.ZERO);
  }

  public BigDecimal getBigDecimal(String node, BigDecimal def) {
    final ConfigSection section = getSection(node);
    if(section == null) return def;

    try {
      return new BigDecimal(section.getBaseNode().getValues().get(0).getValue());
    } catch(Exception ignore) {
      return def;
    }
  }

  public String getString(String node) {
    return getString(node, "");
  }

  public String getString(String node, String def) {
    final ConfigSection section = getSection(node);
    if(section == null) return def;

    debug("Value: " + section.getBaseNode().getValues().get(0).getValue());
    
    return section.getBaseNode().getValues().get(0).getValue();
  }

  public LinkedList<String> getStringList(String node) {
    final ConfigSection section = getSection(node);
    if(section == null) return new LinkedList<>();

    LinkedList<String> stringList = new LinkedList<>();

    for(YamlValue value : section.getBaseNode().getValues()) {
      stringList.add(value.getValue());
    }
    
    return stringList;
  }

  private void debug(String message) {
    if(debug) System.out.println(message);
  }
}