package saros.intellij.ui.wizards.pages.moduleselection;

import com.intellij.openapi.ui.ComboBox;
import java.awt.event.ItemListener;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nullable;
import javax.swing.JComboBox;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a non-editable combo-box containing objects of the given type. For each object, a
 * <code>String</code> representation is held which is used to display the object in the combo-box.
 *
 * @param <T> the type of object contained in the combo-box
 */
class ObjectComboBox<T> {
  private final Map<String, T> objectMapping;
  private final JComboBox<String> comboBox;

  ObjectComboBox() {
    this.objectMapping = new HashMap<>();
    this.comboBox = new ComboBox<>();

    this.comboBox.setEditable(false);
  }

  /**
   * Returns the contained combo-box object. The returned object should not be modified directly.
   *
   * @return the contained combo-box object
   */
  @NotNull
  JComboBox<String> getComboBox() {
    return comboBox;
  }

  /**
   * Adds an item listener to the contained combo-box.
   *
   * @param itemListener the item listener to add to the contained combo-box
   */
  void registerItemListener(@NotNull ItemListener itemListener) {
    comboBox.addItemListener(itemListener);
  }

  /**
   * Adds an entry to the combo-box.
   *
   * @param representation the string representation displayed in the combo-box
   * @param object the object to add to the combo-box
   */
  void addEntry(@NotNull String representation, @NotNull T object) {
    objectMapping.put(representation, object);

    comboBox.addItem(representation);
  }

  /**
   * Returns the object whose representation is currently selected in the combo-box.
   *
   * @return the object whose representation is currently selected in the combo-box or <code>null
   *     </code> if there is no selection
   */
  @Nullable
  T getSelectedEntry() {
    int selectedEntryIndex = comboBox.getSelectedIndex();

    if (selectedEntryIndex == -1) {
      return null;
    }

    return objectMapping.get(comboBox.getItemAt(selectedEntryIndex));
  }

  /**
   * Returns a collection of all contained object entries.
   *
   * @return a collection of all contained object entries
   */
  @NotNull
  Collection<T> getContainedEntries() {
    return objectMapping.values();
  }

  /**
   * Selects the representation of the given object in the combo-box if it is contained in the
   * mapping.
   *
   * @param entryObject the object whose representation to select in the combo-box
   */
  void selectEntry(@NotNull T entryObject) {
    objectMapping
        .keySet()
        .stream()
        .filter(representation -> objectMapping.get(representation).equals(entryObject))
        .findFirst()
        .ifPresent(comboBox::setSelectedItem);
  }

  /** Clears the current selection of the contained combo-box. */
  void clearSelection() {
    comboBox.setSelectedIndex(-1);
  }

  /** Removes all entries from the combo-box and drops the held representation mapping. */
  void removeAll() {
    objectMapping.clear();

    comboBox.removeAllItems();
  }
}
