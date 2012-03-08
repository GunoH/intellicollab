package nl.guno.ccollab.intellij.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicRadioButtonUI;

public class CheckBoxList extends JList {
  private static final Border noFocusBorder = new EmptyBorder(1, 1, 1, 1);
  private static final int DEFAULT_CHECK_BOX_WIDTH = 20;

  public CheckBoxList(final ListModel dataModel, final CheckBoxListListener checkBoxListListener) {
    super(dataModel);
    setCellRenderer(new CellRenderer());
    setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    setBorder(BorderFactory.createEtchedBorder());
    addKeyListener(new KeyAdapter() {
      @Override
      public void keyTyped(KeyEvent e) {
        if (e.getKeyChar() == ' ') {
          int[] indices = CheckBoxList.this.getSelectedIndices();
          for (int index : indices) {
            if (index >= 0) {
              JCheckBox checkbox = (JCheckBox)getModel().getElementAt(index);
              setSelected(checkbox, index, checkBoxListListener);
            }
          }
        }
      }
    });
    addMouseListener(new MouseAdapter() {
      @Override
      public void mouseClicked(MouseEvent e) {
        if (isEnabled()) {
          int index = locationToIndex(e.getPoint());

          if (index != -1) {
            JCheckBox checkbox = (JCheckBox)getModel().getElementAt(index);
            int iconArea;
            try {
              iconArea = ((BasicRadioButtonUI)checkbox.getUI()).getDefaultIcon().getIconWidth();
            }
            catch (ClassCastException c) {
              iconArea = DEFAULT_CHECK_BOX_WIDTH;
            }
            if (e.getX() < iconArea) {
              setSelected(checkbox, index, checkBoxListListener);
            }
          }
        }
      }
    });
  }

  private void setSelected(JCheckBox checkbox, int index, CheckBoxListListener checkBoxListListener) {
    boolean value = !checkbox.isSelected();
    checkbox.setSelected(value);
    repaint();
    checkBoxListListener.checkBoxSelectionChanged(index, value);
  }

  private class CellRenderer implements ListCellRenderer {
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
      JCheckBox checkbox = (JCheckBox)value;
      checkbox.setBackground(getBackgound(isSelected, checkbox));
      checkbox.setForeground(getForeGround(isSelected, checkbox));
      checkbox.setEnabled(isEnabled());
      checkbox.setFont(getFont(checkbox));
      checkbox.setFocusPainted(false);
      checkbox.setBorderPainted(true);
      checkbox.setBorder(isSelected ? UIManager.getBorder("List.focusCellHighlightBorder") : noFocusBorder);
      return checkbox;
    }
  }

  protected Font getFont(final JCheckBox checkbox) {
    return getFont();
  }

  protected Color getBackgound(final boolean isSelected, final JCheckBox checkbox) {
      return isSelected ? getSelectionBackground() : getBackground();
    }

  protected Color getForeGround(final boolean isSelected, final JCheckBox checkbox) {
    return isSelected ? getSelectionForeground() : getForeground();
  }

}
