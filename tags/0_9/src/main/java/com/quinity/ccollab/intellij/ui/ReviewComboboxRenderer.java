package com.quinity.ccollab.intellij.ui;

import com.smartbear.ccollab.datamodel.Review;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import java.awt.*;

public class ReviewComboboxRenderer extends JLabel implements ListCellRenderer {

	public ReviewComboboxRenderer() {
		setOpaque(true);
	}

	public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
		Review review = (Review) value;
		
		if (isSelected) {
			setBackground(Color.BLUE);
		} else {
			setBackground(list.getBackground());
		}
		
		setText(review.getId() + " " + review.getTitle());
		
		return this;
	}
}
