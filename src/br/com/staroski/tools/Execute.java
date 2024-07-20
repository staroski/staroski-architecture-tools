package br.com.staroski.tools;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import br.com.staroski.tools.analysis.ui.AnalysisToolsUI;
import br.com.staroski.ui.UI;

/**
 * This class executes the user interface for the Staroski's Architecture Tools.
 * 
 * @author Staroski, Ricardo Artur
 */
public final class Execute {

    public static void main(String... args) {
        try {
            applyLookAndFeel();
            SwingUtilities.invokeLater(() -> new AnalysisToolsUI().setVisible(true));
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    private static void applyLookAndFeel() throws UnsupportedLookAndFeelException {
        try {
            UI.setLocale(UI.UNITED_STATES);
            JFrame.setDefaultLookAndFeelDecorated(true);
            JDialog.setDefaultLookAndFeelDecorated(true);
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    private Execute() {}
}
