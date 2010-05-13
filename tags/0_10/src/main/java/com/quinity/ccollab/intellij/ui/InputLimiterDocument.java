package com.quinity.ccollab.intellij.ui;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

/**
 * Met deze klasse kan de lengte van de gebruikersinvoer in UI-components gelimiteerd worden.
 */
public class InputLimiterDocument extends PlainDocument {
	private int limit;

	InputLimiterDocument(int limit) {
		super();
		this.limit = limit;
	}

	InputLimiterDocument(int limit, boolean upper) {
		super();
		this.limit = limit;
	}

	public void insertString(int offset, String str, AttributeSet attr) throws BadLocationException {
		if (str == null) {
			return;
		}

		if ((getLength() + str.length()) <= limit) {
			super.insertString(offset, str, attr);
		}
	}
}
