package com.quinity.ccollab.intellij.ui;

import com.smartbear.ccollab.datamodel.Review;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import java.awt.Component;

public class ReviewComboboxRenderer extends JLabel implements ListCellRenderer {
	public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
		Review review = (Review) value;
		
		if (isSelected) {
			setBackground(list.getSelectionBackground());
			setForeground(list.getSelectionForeground());
		} else {
			setBackground(list.getBackground());
			setBackground(list.getForeground());
		}
		
		setText(review.getId() + " " + review.getTitle());
		
		return this;
	}
}
