package br.com.staroski.tools.analysis.ui;

import java.awt.Component;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;

/**
 * Utility class to open various dialogs for user interaction.
 *
 * @author Staroski, Ricardo Artur
 */
final class Dialogs {

    private static JFileChooser fileChooser;

    public static synchronized boolean confirm(final Component parent, final String title, final String message) {
        int optionType = JOptionPane.OK_CANCEL_OPTION;
        int messageType = JOptionPane.QUESTION_MESSAGE;
        int option = JOptionPane.showConfirmDialog(parent, message, title, optionType, messageType);
        return option == JOptionPane.OK_OPTION;
    }

    public static synchronized File openFile(final Component parent, final String description, final String extension) {
        final JFileChooser fileChooser = getFileChooser();

        fileChooser.setFileFilter(new FileFilter() {
            @Override
            public boolean accept(File file) {
                return file.isDirectory() || file.getName().toLowerCase().endsWith(extension.toLowerCase());
            }

            @Override
            public String getDescription() {
                return description + " (*" + extension + ")";
            }
        });

        int result = fileChooser.showOpenDialog(parent);
        if (result == JFileChooser.APPROVE_OPTION) {
            return fileChooser.getSelectedFile();
        }
        return null;
    }

    public static synchronized File saveFile(final Component parent) {
        JFileChooser fileChooser = getFileChooser();

        int result = fileChooser.showSaveDialog(parent);
        if (result == JFileChooser.APPROVE_OPTION) {
            return fileChooser.getSelectedFile();
        }
        return null;
    }

    public static synchronized void showError(final Component owner, final Throwable error) {
        Object message = error.getClass().getSimpleName() + "\n" + error.getLocalizedMessage();
        String title = "Error";
        int messageType = JOptionPane.ERROR_MESSAGE;
        JOptionPane.showMessageDialog(owner, message, title, messageType);
    }

    public static synchronized void showInformation(final Component owner, final String title, final String message) {
        int messageType = JOptionPane.INFORMATION_MESSAGE;
        JOptionPane.showMessageDialog(owner, message, title, messageType);
    }

    public static synchronized void showWarning(final Component owner, final String message) {
        String title = "Warning";
        int messageType = JOptionPane.WARNING_MESSAGE;
        JOptionPane.showMessageDialog(owner, message, title, messageType);
    }

    private static JFileChooser getFileChooser() {
        if (fileChooser == null) {
            fileChooser = new JFileChooser();
            fileChooser.setCurrentDirectory(new File(System.getProperty("user.dir")));
        }
        fileChooser.resetChoosableFileFilters();
        return fileChooser;
    }

    private Dialogs() {}
}
