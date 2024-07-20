package br.com.staroski.ui;

import java.awt.Color;
import java.awt.Dimension;

import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import javax.swing.text.StyledEditorKit;

/**
 * This is a {@link JTextPane} specialization that simulates an operational system console and it allows to print colored text using the overloaded
 * <tt>print</tt> and <tt>println</tt> methods.
 * 
 * @author Staroski, Ricardo Artur
 */
@SuppressWarnings("serial")
public final class ConsoleTextPane extends JTextPane {

    public ConsoleTextPane() {
        setEditorKit(new StyledEditorKit());
        setEditable(false);
        setFont(UI.MONOSPACED);
        setBackground(Color.BLACK);
        setForeground(Color.GREEN);
    }

    public void clear() {
        setText("");
    }

    @Override
    public Dimension getPreferredSize() {
        // Avoid substituting the minimum width for the preferred width when the viewport is too narrow
        return getUI().getPreferredSize(this);
    }

    @Override
    public boolean getScrollableTracksViewportWidth() {
        // Only track viewport width when the viewport is wider than the preferred width
        return getUI().getPreferredSize(this).width <= getParent().getSize().width;
    }

    public void print(String text) {
        SimpleAttributeSet style = new SimpleAttributeSet();
        StyleConstants.setForeground(style, getForeground());
        StyleConstants.setBackground(style, getBackground());
        print(text, style);
    }

    public void print(String text, Color foreground) {
        SimpleAttributeSet style = new SimpleAttributeSet();
        StyleConstants.setForeground(style, foreground);
        StyleConstants.setBackground(style, getBackground());
        print(text, style);

    }

    public void print(String text, Color foreground, Color background) {
        SimpleAttributeSet style = new SimpleAttributeSet();
        StyleConstants.setForeground(style, foreground);
        StyleConstants.setBackground(style, background);
        print(text, style);
    }

    public void print(String text, SimpleAttributeSet style) {
        try {
            final StyledDocument doc = getStyledDocument();
            doc.insertString(doc.getLength(), text, style);
            setCaretPosition(doc.getLength());
            moveCaretToStartOfLine();
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }

    public void println() {
        print("\n");
    }

    public void println(String text) {
        print(text);
        println();
    }

    public void println(String text, Color foreground) {
        print(text, foreground);
        println();
    }

    public void println(String text, Color foreground, Color background) {
        print(text, foreground, background);
        println();
    }

    public void println(String text, SimpleAttributeSet style) {
        print(text, style);
        println();
    }

    private void moveCaretToStartOfLine() {
        try {
            int pos = getCaretPosition();
            int line = getStyledDocument().getDefaultRootElement().getElementIndex(pos);
            int start = getStyledDocument().getDefaultRootElement().getElement(line).getStartOffset();
            setCaretPosition(start);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
