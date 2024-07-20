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
import java.util.Locale;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

import br.com.staroski.ui.I18N;
import br.com.staroski.ui.UI;

/**
 * This is the main window for the Staroski's Architecture Tools.
 *
 * @author Staroski, Ricardo Artur
 */
@SuppressWarnings("serial")
public final class AnalysisToolsUI extends JFrame implements I18N {

    private JMenu menuFile;
    private JMenuItem menuItemImportCSV;
    private JMenuItem menuItemExportCSV;
    private JMenuItem menuItemExit;
    private JMenu menuLanguage;
    private JMenuItem menuItemPtBr;
    private JMenuItem menuItemEnUs;
    private JMenuItem menuItemDeDe;
    private DispersionChartPanel dispersionChartPanel;
    private JMenuItem menuItemMetricsAnalyzer;

    public AnalysisToolsUI() {
        super(UI.getText("AnalysisToolsUI.title"));
        setIconImages(createIcons());
        setMinimumSize(new Dimension(640, 480));
        setSize(new Dimension(1366, 768));

        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                askForExit();
            }
        });

        setJMenuBar(createMenuBar());

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        dispersionChartPanel = new DispersionChartPanel();
        mainPanel.add(dispersionChartPanel, BorderLayout.CENTER);

        setContentPane(mainPanel);

        UI.centralizeOnActiveScreen(this);
    }

    @Override
    public void onLocaleChange(Locale newLocale) {
        setTitle(UI.getText("AnalysisToolsUI.title"));

        menuFile.setText(UI.getText("AnalysisToolsUI.menu.file"));
        menuItemImportCSV.setText(UI.getText("AnalysisToolsUI.menu.file.importCSV"));
        menuItemExportCSV.setText(UI.getText("AnalysisToolsUI.menu.file.exportCSV"));
        menuItemMetricsAnalyzer.setText(UI.getText("AnalysisToolsUI.menu.file.metricsAnalyzer"));
        menuItemExit.setText(UI.getText("AnalysisToolsUI.menu.file.exit"));

        menuLanguage.setText(UI.getText("AnalysisToolsUI.menu.language"));
        menuItemPtBr.setText(UI.getText("AnalysisToolsUI.menu.language.pt_BR"));
        menuItemEnUs.setText(UI.getText("AnalysisToolsUI.menu.language.en_US"));
        menuItemDeDe.setText(UI.getText("AnalysisToolsUI.menu.language.de_DE"));
    }

    private void askForExit() {
        String title = UI.getText("AnalysisToolsUI.exit.title");
        String message = UI.getText("AnalysisToolsUI.exit.message");
        if (UI.showConfirmation(this, title, message)) {
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
        JMenuBar menuBar = new JMenuBar();
        menuBar.add(createMenuFile());
        menuBar.add(createMenuLanguage());
        return menuBar;
    }

    private JMenu createMenuFile() {
        menuFile = new JMenu(UI.getText("AnalysisToolsUI.menu.file"));

        menuItemMetricsAnalyzer = new JMenuItem(UI.getText("AnalysisToolsUI.menu.file.metricsAnalyzer"), new ImageIcon(Images.METRICS_ANALYZER_24));
        menuItemMetricsAnalyzer.addActionListener(event -> openMetricsAnalyzer());

        menuItemImportCSV = new JMenuItem(UI.getText("AnalysisToolsUI.menu.file.importCSV"), new ImageIcon(Images.IMPORT_CSV_24));
        menuItemImportCSV.addActionListener(event -> importCsv());

        menuItemExportCSV = new JMenuItem(UI.getText("AnalysisToolsUI.menu.file.exportCSV"), new ImageIcon(Images.EXPORT_CSV_24));
        menuItemExportCSV.addActionListener(event -> exportCsv());

        menuItemExit = new JMenuItem(UI.getText("AnalysisToolsUI.menu.file.exit"), new ImageIcon(Images.EXIT_24));
        menuItemExit.addActionListener(event -> askForExit());

        menuFile.add(menuItemMetricsAnalyzer);
        menuFile.addSeparator();
        menuFile.add(menuItemImportCSV);
        menuFile.add(menuItemExportCSV);
        menuFile.addSeparator();
        menuFile.add(menuItemExit);
        return menuFile;
    }

    private void openMetricsAnalyzer() {
        MetricsCollectorUI metricsAnalyzer = new MetricsCollectorUI(this);
        metricsAnalyzer.withCsvConsumer(csv -> dispersionChartPanel.setCsvString(csv));
        metricsAnalyzer.setVisible(true);
    }

    private JMenu createMenuLanguage() {
        menuLanguage = new JMenu(UI.getText("AnalysisToolsUI.menu.language"));

        menuItemEnUs = new JMenuItem(UI.getText("AnalysisToolsUI.menu.language.en_US"), new ImageIcon(Images.LANGUAGE_EN_US_24));
        menuItemEnUs.addActionListener(event -> UI.setLocale(UI.UNITED_STATES));

        menuItemDeDe = new JMenuItem(UI.getText("AnalysisToolsUI.menu.language.de_DE"), new ImageIcon(Images.LANGUAGE_DE_DE_24));
        menuItemDeDe.addActionListener(event -> UI.setLocale(UI.GERMANY));

        menuItemPtBr = new JMenuItem(UI.getText("AnalysisToolsUI.menu.language.pt_BR"), new ImageIcon(Images.LANGUAGE_PT_BR_24));
        menuItemPtBr.addActionListener(event -> UI.setLocale(UI.BRAZIL));

        menuLanguage.add(menuItemEnUs);
        menuLanguage.add(menuItemDeDe);
        menuLanguage.add(menuItemPtBr);
        return menuLanguage;
    }

    private void exportCsv() {
        if (!dispersionChartPanel.hasData()) {
            String title = UI.getText("AnalysisToolsUI.menu.file.exportCSV.warning.title");
            String message = UI.getText("AnalysisToolsUI.menu.file.exportCSV.warning.message");
            UI.showWarning(this, title, message);
            return;
        }
        String description = UI.getText("AnalysisToolsUI.menu.file.exportCSV.description");
        String extension = UI.getText("AnalysisToolsUI.menu.file.exportCSV.type");
        final File fileToSave = UI.saveFile(this, description, extension);
        if (fileToSave == null) {
            return;
        }
        boolean canSave = true;
        if (fileToSave.exists()) {
            String title = UI.getText("AnalysisToolsUI.menu.file.exportCSV.existing.title");
            String message = UI.getText("AnalysisToolsUI.menu.file.exportCSV.existing.message", fileToSave.getName());
            canSave = UI.showConfirmation(this, title, message);
        }
        if (canSave) {
            try {
                final String csv = dispersionChartPanel.getCsvString();

                OpenOption[] options = {
                        StandardOpenOption.CREATE,
                        StandardOpenOption.WRITE,
                        StandardOpenOption.TRUNCATE_EXISTING
                };
                Files.writeString(fileToSave.toPath(), csv, options);

                String title = UI.getText("AnalysisToolsUI.menu.file.exportCSV.success.title");
                String message = UI.getText("AnalysisToolsUI.menu.file.exportCSV.success.message");
                UI.showInformation(this, title, message);
            } catch (IOException e) {
                e.printStackTrace();
                String title = UI.getText("AnalysisToolsUI.menu.file.exportCSV.error.title");
                UI.showError(this, title, e);
            }
        }
    }

    private void importCsv() {
        String description = UI.getText("AnalysisToolsUI.menu.file.importCSV.description");
        String extension = UI.getText("AnalysisToolsUI.menu.file.importCSV.type");
        File fileToImport = UI.selectFile(this, description, extension);
        if (fileToImport != null) {
            dispersionChartPanel.setCsvFile(fileToImport);
        }
    }
}
