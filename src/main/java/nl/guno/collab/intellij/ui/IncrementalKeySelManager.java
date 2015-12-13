package nl.guno.collab.intellij.ui;

import org.jetbrains.annotations.NotNull;

import java.util.Date;

import javax.swing.ComboBoxModel;
import javax.swing.JComboBox;

abstract class IncrementalKeySelManager implements JComboBox.KeySelectionManager {
 
    private long lastKeyPressTime = new Date().getTime();
    private StringBuffer searchString = new StringBuffer("");
 
    @Override
    public int selectionForKey(char aKey, @NotNull ComboBoxModel aModel) {
        try {
            long actKeyPressTime = new Date().getTime();

            if (lastKeyPressTime + 1000 < actKeyPressTime) {
                searchString = new StringBuffer("");
            }

            if (aKey >= 33 && aKey <= 127) {
                searchString = searchString.append(aKey);
            }

            lastKeyPressTime = actKeyPressTime;

            for (int i = 0; i < aModel.getSize(); i++) {
                Object element = aModel.getElementAt(i);
                if (element == null) {
                    continue;
                }
                String listItem = getDisplayedText(element);

                if (listItem.startsWith(searchString.toString().toUpperCase())) {
                    return i;
                }
            }
            return -1;
        } catch (Exception ex) {
            return -1;
        }
    }

    protected abstract String getDisplayedText(Object object);
}
