package br.com.staroski.ui;

import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.Window;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Comparator;
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

    static {
        setLocale(Locale.getDefault(), false);
    }

    public static final Font MONOSPACED = new Font("Monospaced", Font.PLAIN, 12);

    public static final Locale UNITED_STATES = new Locale.Builder().setLanguage("en").setRegion("US").build();
    public static final Locale GERMANY = new Locale.Builder().setLanguage("de").setRegion("DE").build();
    public static final Locale BRAZIL = new Locale.Builder().setLanguage("pt").setRegion("BR").build();

    private static JFileChooser fileChooser;

    /**
     * Emits an audio beep.
     */
    public static void beep() {
        Toolkit.getDefaultToolkit().beep();
    }

    public static void centralizeOnActiveScreen(final Window window) {
        centralizeOnScreen(window, getActiveScreen());
    }

    public static void centralizeOnScreen(final Window window, final GraphicsDevice screen) {
        final Rectangle screenBounds = screen.getDefaultConfiguration().getBounds();
        final Rectangle windowBounds = window.getBounds();
        final int x = screenBounds.x + (screenBounds.width - windowBounds.width) / 2;
        final int y = screenBounds.y + (screenBounds.height - windowBounds.height) / 2;
        window.setLocation(x, y);
    }

    public static GraphicsDevice getActiveScreen() {
        final Point mousePointer = MouseInfo.getPointerInfo().getLocation();

        for (GraphicsDevice screen : getAllScreens()) {
            final Rectangle bounds = screen.getDefaultConfiguration().getBounds();
            if (bounds.contains(mousePointer)) {
                return screen;
            }
        }
        return getDefaultScreen();
    }

    public static Set<GraphicsDevice> getAllScreens() {
        final GraphicsEnvironment graphicsEnvironment = GraphicsEnvironment.getLocalGraphicsEnvironment();
        final Comparator<GraphicsDevice> comparator = (a, b) -> a.getIDstring().compareTo(b.getIDstring());
        final Set<GraphicsDevice> screens = new TreeSet<>(comparator);
        screens.addAll(Arrays.asList(graphicsEnvironment.getScreenDevices()));
        return screens;
    }

    public static GraphicsDevice getDefaultScreen() {
        return GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
    }

    /**
     * @return The current {@link Locale} being used by the user interface.
     */
    public static Locale getLocale() {
        return Locale.getDefault();
    }

    public static Set<Locale> getLocales() {
        return new TreeSet<>(Arrays.asList(UNITED_STATES, GERMANY, BRAZIL));
    }

    public static synchronized String getText(Locale locale, String property, Object... params) {
        final Locale oldLocale = setLocale(locale, false);
        try {
            return getText(property, params);
        } finally {
            setLocale(oldLocale, false);
        }
    }

    public static String getText(String property, Object... params) {
        String message = UIManager.getString(property);
        if (params != null && params.length > 0) {
            message = MessageFormat.format(message, params);
        }
        return message;
    }

    public static synchronized File saveFile(final Component parent, final String description, final String extension) {
        JFileChooser fileChooser = getFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
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

    public static synchronized File selectDirectory(final Component parent) {
        return selectDirectory(parent, null);
    }

    public static synchronized File selectDirectory(final Component parent, File startIn) {
        final JFileChooser fileChooser = getFileChooser();
        if (startIn != null) {
            fileChooser.setCurrentDirectory(startIn);
        }
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int result = fileChooser.showOpenDialog(parent);
        if (result == JFileChooser.APPROVE_OPTION) {
            return fileChooser.getSelectedFile();
        }
        return null;
    }

    public static synchronized File selectFile(final Component parent, final String description, final String extension) {
        final JFileChooser fileChooser = getFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
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

    public static Cursor setCursor(final Component parent, final Cursor newCursor) {
        Cursor oldCursor = parent.getCursor();
        parent.setCursor(newCursor);
        return oldCursor;
    }

    /**
     * Sets the new {@link Locale} of the user interface notifying any {@link Window} and {@link Component}s that implement {@link I18N} interface.
     * 
     * @param locale The new {@link Locale} to set.
     * @return The previous {@link Locale}.
     */
    public static Locale setLocale(Locale locale) {
        return setLocale(locale, true);
    }

    public static synchronized boolean showConfirmation(final Component parent, final String title, final String message) {
        int optionType = JOptionPane.YES_NO_OPTION;
        int messageType = JOptionPane.QUESTION_MESSAGE;
        beep();
        int option = JOptionPane.showConfirmDialog(parent, message, title, optionType, messageType);
        return option == JOptionPane.YES_OPTION;
    }

    public static synchronized void showError(final Component parent, final String title, final Throwable error) {
        Object message = error.getClass().getSimpleName() + "\n" + error.getLocalizedMessage();
        int messageType = JOptionPane.ERROR_MESSAGE;
        beep();
        JOptionPane.showMessageDialog(parent, message, title, messageType);
    }

    public static synchronized void showInformation(final Component parent, final String title, final String message) {
        int messageType = JOptionPane.INFORMATION_MESSAGE;
        beep();
        JOptionPane.showMessageDialog(parent, message, title, messageType);
    }

    public static synchronized void showWarning(final Component parent, final String title, final String message) {
        int messageType = JOptionPane.WARNING_MESSAGE;
        beep();
        JOptionPane.showMessageDialog(parent, message, title, messageType);
    }

    private static void applyI18N(Component component, Locale locale) {
        if (component instanceof I18N i18n) {
            i18n.onLocaleChange(locale);
        }
        if (component instanceof Container container) {
            for (Component child : container.getComponents()) {
                applyI18N(child, locale);
            }
        }
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

    /**
     * Sets the new {@link Locale} of the user interface.
     * 
     * @param locale     The new {@link Locale} to set.
     * @param notifyI18N Flag to notify or not the {@link Window}s implementing {@link I18N}.
     * @return The previous {@link Locale}.
     */
    private static Locale setLocale(Locale locale, boolean notifyI18N) {
        final Locale oldLocale = getLocale();
        try {
            Locale.setDefault(locale);

            String language = locale.getLanguage() + "_" + locale.getCountry();
            InputStream input = UI.class.getResourceAsStream("/properties/language_" + language + ".properties");
            Properties properties = new Properties();
            properties.load(input);

            Set<Entry<Object, Object>> entries = properties.entrySet();
            for (Entry<Object, Object> entry : entries) {
                UIManager.put(entry.getKey(), entry.getValue());
            }

            if (notifyI18N) {
                for (Window window : Frame.getWindows()) {
                    applyI18N(window, locale);
                }
                System.out.println("Locale changed from " + oldLocale + " to " + locale);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return oldLocale;
    }

    // non instantiable utility class
    private UI() {}
}
