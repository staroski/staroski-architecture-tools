package br.com.staroski.tools;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.plaf.metal.MetalLookAndFeel;

import br.com.staroski.tools.analysis.ui.AnalysisToolsUI;
import br.com.staroski.tools.analysis.ui.UI;

/**
 * This class executes the user interface for the Staroski's Architecture Tools.
 * 
 * @author Staroski, Ricardo Artur
 */
public final class Execute {

    public static void main(String... args) {
        try {
            UI.setLocale(UI.BRAZIL);
            
            // apply look and feel
            applyLookAndFeel();

            // show gui
            SwingUtilities.invokeLater(() -> new AnalysisToolsUI().setVisible(true));
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    private static void applyLookAndFeel() throws UnsupportedLookAndFeelException {
        try {
            UIManager.setLookAndFeel(new MetalLookAndFeel());
            JFrame.setDefaultLookAndFeelDecorated(true);
            JDialog.setDefaultLookAndFeelDecorated(true);
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    private Execute() {}
}
