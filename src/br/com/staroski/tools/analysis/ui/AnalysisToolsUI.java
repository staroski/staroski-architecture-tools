package br.com.staroski.tools.analysis.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

/**
 * This is the main window for the Staroski's Architecture Tools.
 *
 * @author Staroski, Ricardo Artur
 */
@SuppressWarnings("serial")
public final class AnalysisToolsUI extends JFrame {

    private final JPanel mainPanel;
    private final DispersionChartPanel dispersionChartPanel;

    public AnalysisToolsUI() {
        super("Staroski Architecture Tools");
        setIconImages(createIcons());
        setMinimumSize(new Dimension(640, 480));
        setSize(new Dimension(1366, 768));
        setLocationRelativeTo(null);

        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                askForExit();
            }
        });

        setJMenuBar(createMenuBar());

        mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        dispersionChartPanel = new DispersionChartPanel();
        mainPanel.add(dispersionChartPanel, BorderLayout.CENTER);

        setContentPane(mainPanel);
    }

    private void askForExit() {
        if (Dialogs.confirm(this, "Exit", "Do you really want to exit?")) {
            System.exit(0);
        }
    }

    private List<BufferedImage> createIcons() {
        List<BufferedImage> icons = Arrays.asList(//
                Images.TOOLS_16, //
                Images.TOOLS_32, //
                Images.TOOLS_64, //
                Images.TOOLS_128, //
                Images.TOOLS_256);
        return icons;
    }

    private JMenuBar createMenuBar() {
        JMenu menuFile = new JMenu("File");

        // Criação dos itens do menu "Arquivo"
        JMenuItem menuItemImport = new JMenuItem("Import CSV");
        menuItemImport.addActionListener(event -> importCsv());

        JMenuItem menuItemExport = new JMenuItem("Export CSV");
        menuItemExport.addActionListener(event -> exportCsv());

        JMenuItem menuItemExit = new JMenuItem("Exit");
        menuItemExit.addActionListener(event -> askForExit());

        menuFile.add(menuItemImport);
        menuFile.add(menuItemExport);
        menuFile.addSeparator();
        menuFile.add(menuItemExit);

        JMenuBar menuBar = new JMenuBar();
        menuBar.add(menuFile);
        return menuBar;
    }

    private void exportCsv() {
        final File fileToSave = Dialogs.openFile(this, "CSV files", ".csv");
        if (fileToSave == null) {
            return;
        }
        boolean canSave = true;
        if (fileToSave.exists()) {
            canSave = Dialogs.confirm(this, "Existing File", "The file \"" + fileToSave.getName() + "\" already exists.\nDo you want to overwrite it?");
        }
        if (canSave) {
            try {
                final String csv = dispersionChartPanel.getCsvContent();

                OpenOption[] options = {
                        StandardOpenOption.CREATE,
                        StandardOpenOption.WRITE,
                        StandardOpenOption.TRUNCATE_EXISTING
                };
                Files.writeString(fileToSave.toPath(), csv, options);

                Dialogs.showInformation(this, "Success", "CSV successfully exported!");
            } catch (IOException e) {
                e.printStackTrace();
                Dialogs.showError(this, e);
            }
        }
    }

    private void importCsv() {
        File fileToImport = Dialogs.openFile(this, "CSV files", ".csv");
        if (fileToImport != null) {
            dispersionChartPanel.setCsvFile(fileToImport);
        }
    }
}
