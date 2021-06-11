package br.com.ufmg.coltec.locamais.ui.inputlimit;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

/**
 * Created by elias on 14/11/18.
 */
public class JTextFieldLimit extends PlainDocument {
    private int limit;

    public JTextFieldLimit(int limit) {
        super();
        this.limit = limit;
    }

    public void insertString(int offset, String str, AttributeSet attr) throws BadLocationException {
        if (str == null)
            return;

        if ((getLength() + str.length()) <= limit) {
            super.insertString(offset, str, attr);
        }
    }
}