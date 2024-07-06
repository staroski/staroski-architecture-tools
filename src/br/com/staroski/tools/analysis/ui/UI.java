package br.com.staroski.tools.analysis.ui;

import java.awt.Component;
import java.awt.Frame;
import java.awt.Window;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Locale;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.filechooser.FileFilter;

/**
 * Utility class with methods related to the user interface.
 *
 * @author Staroski, Ricardo Artur
 */
public final class UI {

    public static final Locale UNITED_STATES = new Locale("en", "US");
    public static final Locale GERMANY = new Locale("de", "DE");
    public static final Locale BRAZIL = new Locale("pt", "BR");

    private static JFileChooser fileChooser;

    public static synchronized Set<Locale> getLocales() {
        return new TreeSet<>(Arrays.asList(UNITED_STATES, GERMANY, BRAZIL));
    }
    
    public static String getString(String property) {
        return UIManager.getString(property);
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

    public static synchronized File saveFile(final Component parent, final String description, final String extension) {
        JFileChooser fileChooser = getFileChooser();

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
        
        int result = fileChooser.showSaveDialog(parent);
        if (result == JFileChooser.APPROVE_OPTION) {
            return fileChooser.getSelectedFile();
        }
        return null;
    }

    public static void setLocale(Locale locale) {
        try {
            String language = locale.getLanguage() + "_" + locale.getCountry();
            InputStream input = UI.class.getResourceAsStream("/properties/language_" + language + ".properties");
            Properties properties = new Properties();
            properties.load(input);

            Set<Entry<Object, Object>> entries = properties.entrySet();
            for (Entry<Object, Object> entry : entries) {
                UIManager.put(entry.getKey(), entry.getValue());
            }

            Locale.setDefault(locale);
            Window[] windows = Frame.getWindows();
            for (Window window : windows) {
                SwingUtilities.updateComponentTreeUI(window);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static synchronized boolean showConfirmation(final Component parent, final String title, final String message) {
        int optionType = JOptionPane.OK_CANCEL_OPTION;
        int messageType = JOptionPane.QUESTION_MESSAGE;
        int option = JOptionPane.showConfirmDialog(parent, message, title, optionType, messageType);
        return option == JOptionPane.OK_OPTION;
    }

    public static synchronized void showError(final Component owner, final String title, final Throwable error) {
        Object message = error.getClass().getSimpleName() + "\n" + error.getLocalizedMessage();
        int messageType = JOptionPane.ERROR_MESSAGE;
        JOptionPane.showMessageDialog(owner, message, title, messageType);
    }

    public static synchronized void showInformation(final Component owner, final String title, final String message) {
        int messageType = JOptionPane.INFORMATION_MESSAGE;
        JOptionPane.showMessageDialog(owner, message, title, messageType);
    }

    public static synchronized void showWarning(final Component owner, final String title, final String message) {
        int messageType = JOptionPane.WARNING_MESSAGE;
        JOptionPane.showMessageDialog(owner, message, title, messageType);
    }

    private static JFileChooser getFileChooser() {
        if (fileChooser == null) {
            fileChooser = new JFileChooser();
            fileChooser.setCurrentDirectory(new File(System.getProperty("user.dir")));
        }
        fileChooser.resetChoosableFileFilters();
        SwingUtilities.updateComponentTreeUI(fileChooser);
        return fileChooser;
    }

    private UI() {}
}
