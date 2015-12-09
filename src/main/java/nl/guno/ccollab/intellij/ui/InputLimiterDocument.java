package nl.guno.ccollab.intellij.ui;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

/**
 * Limits the length of a JTextComponent.
 */
class InputLimiterDocument extends PlainDocument {
    private final int limit;

    InputLimiterDocument(int limit) {
        super();
        this.limit = limit;
    }

    @Override
    public void insertString(int offset, String str, AttributeSet attr) throws BadLocationException {
        if (str == null) {
            return;
        }

        if ((getLength() + str.length()) <= limit) {
            super.insertString(offset, str, attr);
        }
    }
}
