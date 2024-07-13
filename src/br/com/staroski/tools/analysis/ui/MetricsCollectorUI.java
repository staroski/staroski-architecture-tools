package br.com.staroski.tools.analysis.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.function.Consumer;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import br.com.staroski.tools.analysis.Project;
import br.com.staroski.tools.analysis.Projects;
import br.com.staroski.tools.analysis.analyzers.AbstractionAnalysisEvent;
import br.com.staroski.tools.analysis.analyzers.DefaultMetricsAnalyzerListener;
import br.com.staroski.tools.analysis.analyzers.DependencyAnalysisEvent;
import br.com.staroski.tools.analysis.analyzers.MetricsAnalyzer;

/**
 * This is a dialog for the metrics analyzer tool, part of the Staroski's Architecture Tools.
 *
 * @author Staroski, Ricardo Artur
 */
@SuppressWarnings("serial")
public final class MetricsCollectorUI extends JDialog implements I18N {

    private class InnerListener extends DefaultMetricsAnalyzerListener {

        @Override
        public void onAbstractionAnalysisStarted(AbstractionAnalysisEvent event) {
            println(UI.getText("MetricsCollectorUI.collect.onAbstractionAnalysisStarted", event.getProject().getName()));
        }

        @Override
        public void onCouplingAnalysisStarted(DependencyAnalysisEvent event) {
            println(UI.getText("MetricsCollectorUI.collect.onCouplingAnalysisStarted", event.getProject().getName()));
        }

        @Override
        public void onCycleAnalysisStarted(DependencyAnalysisEvent event) {
            println(UI.getText("MetricsCollectorUI.collect.onCycleAnalysisStarted", event.getProject().getName()));
        }

        @Override
        public void onMetricsAnalysisFinished(Instant end, Duration elapsed) {
            String duration = String.format("%02d:%02d:%02d", elapsed.toHours(), elapsed.toMinutesPart(), elapsed.toSecondsPart());
            println(UI.getText("MetricsCollectorUI.collect.onMetricsAnalysisFinished", duration));
        }

        @Override
        public void onMetricsAnalysisStarted(Instant start) {
            println(UI.getText("MetricsCollectorUI.collect.onMetricsAnalysisStarted"));
        }

        @Override
        public void onMetricsCollected(Set<Project> projects) {
            if (!projects.isEmpty()) {
                metricsCsv = Projects.getMetricsCsv(projects);
            }
            println(UI.getText("MetricsCollectorUI.collect.onMetricsCollected", projects.size(), metricsCsv));
        }

        @Override
        public void onProjectFound(Project project) {
            println(UI.getText("MetricsCollectorUI.collect.onProjectFound", project.getName()));
        }

        @Override
        public void onProjectScanStarted(File directory) {
            try {
                println(UI.getText("MetricsCollectorUI.collect.onProjectScanStarted", directory.getCanonicalPath()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private JTextField textFieldDirectory;
    private JButton buttonDirectory;
    private JLabel labelDirectory;
    private JButton buttonCollect;
    private JTextArea textAreaDetails;
    private String metricsCsv;
    private JButton buttonLoadCsv;
    private Consumer<String> csvConsumer;

    public MetricsCollectorUI(JFrame owner) {
        super(owner, UI.getText("MetricsCollectorUI.title"), true);
        setIconImages(createIcons());
        setMinimumSize(new Dimension(640, 480));
        setSize(new Dimension(640, 480));
        setLocationRelativeTo(owner);

        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                askForExit();
            }
        });

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        mainPanel.add(createNorthPanel(), BorderLayout.NORTH);
        mainPanel.add(createDetailPanel(), BorderLayout.CENTER);
        mainPanel.add(createSouthPanel(), BorderLayout.SOUTH);

        setContentPane(mainPanel);
    }

    @Override
    public void onLocaleChange(Locale newLocale) {
        setTitle(UI.getText("MetricsCollectorUI.title"));

        labelDirectory.setText(UI.getText("MetricsCollectorUI.selectFolder.label"));
        buttonDirectory.setToolTipText(UI.getText("MetricsCollectorUI.selectFolder.button.hint"));

        buttonCollect.setText(UI.getText("MetricsCollectorUI.collect.button.text"));
        buttonCollect.setToolTipText(UI.getText("MetricsCollectorUI.collect.button.hint"));

        buttonLoadCsv.setText(UI.getText("MetricsCollectorUI.loadCsv.button.text"));
        buttonLoadCsv.setToolTipText(UI.getText("MetricsCollectorUI.loadCsv.button.hint"));
    }

    @Override
    public void setVisible(boolean visible) {
        if (visible) {
            metricsCsv = null;
            textAreaDetails.setText("");
        }
        super.setVisible(visible);
    }

    public MetricsCollectorUI withCsvConsumer(Consumer<String> csvConsumer) {
        this.csvConsumer = csvConsumer;
        return this;
    }

    private void askForExit() {
        String title = UI.getText("MetricsCollectorUI.exit.title");
        String message = UI.getText("MetricsCollectorUI.exit.message");
        if (UI.showConfirmation(this, title, message)) {
            dispose();
        }
    }

    private void askForLoadMetrics() {
        if (metricsCsv == null) {
            String title = UI.getText("MetricsCollectorUI.loadCsv.warning.title");
            String message = UI.getText("MetricsCollectorUI.loadCsv.warning.message");
            UI.showWarning(this, title, message);
            return;
        }

        String title = UI.getText("MetricsCollectorUI.loadCsv.title");
        String message = UI.getText("MetricsCollectorUI.loadCsv.message");
        if (UI.showConfirmation(this, title, message)) {
            if (csvConsumer != null) {
                csvConsumer.accept(metricsCsv);
            }
            dispose();
        }
    }

    private void chooseDirectory() {
        File directory = UI.selectDirectory(this, getSelectedDirectory());
        if (directory != null) {
            try {
                textFieldDirectory.setText(directory.getCanonicalPath());
            } catch (IOException e) {
                e.printStackTrace();
                String title = UI.getText("MetricsCollectorUI.selectFolder.error.title");
                UI.showError(this, title, e);
            }
        }
    }

    private void collectMetrics() {
        File directory = getSelectedDirectory();
        if (directory == null) {
            String title = UI.getText("MetricsCollectorUI.collect.warning.title");
            String message = UI.getText("MetricsCollectorUI.collect.warning.message");
            UI.showWarning(this, title, message);
            return;
        }
        metricsCsv = null;
        textAreaDetails.setText("");

        Runnable process = new Runnable() {

            @Override
            public void run() {
                try {
                    final MetricsAnalyzer metricsAnalyzer = new MetricsAnalyzer();
                    metricsAnalyzer.addMetricsAnalyzerListener(new InnerListener());
                    metricsAnalyzer.analyze(directory);
                } catch (Exception e) {
                    e.printStackTrace();
                    String title = UI.getText("MetricsCollectorUI.collect.error.title");
                    UI.showError(MetricsCollectorUI.this, title, e);
                }
            }
        };

        new Thread(process, "MetricsAnalyzerThread").start();
    }

    private JComponent createDetailPanel() {
        textAreaDetails = new JTextArea();
        textAreaDetails.setEditable(false);

        JScrollPane scrollPane = new JScrollPane(textAreaDetails);
        return scrollPane;
    }

    private List<BufferedImage> createIcons() {
        return Arrays.asList(Images.METRICS_ANALYZER_24);
    }

    private JPanel createNorthPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        JPanel selectionPanel = createSelectionPanel();

        panel.add(selectionPanel, BorderLayout.CENTER);

        buttonCollect = new JButton(UI.getText("MetricsCollectorUI.collect.button.text"));
        buttonCollect.setToolTipText(UI.getText("MetricsCollectorUI.collect.button.hint"));
        buttonCollect.addActionListener(event -> collectMetrics());

        panel.add(buttonCollect, BorderLayout.EAST);

        return panel;
    }

    private JPanel createSelectionPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        labelDirectory = new JLabel(UI.getText("MetricsCollectorUI.selectFolder.label"));

        textFieldDirectory = new JTextField();
        Dimension size = textFieldDirectory.getPreferredSize();

        buttonDirectory = new JButton("...");
        buttonDirectory.setToolTipText(UI.getText("MetricsCollectorUI.selectFolder.button.hint"));
        buttonDirectory.addActionListener(event -> chooseDirectory());
        buttonDirectory.setPreferredSize(new Dimension(30, (int) size.getHeight()));

        panel.add(labelDirectory, BorderLayout.WEST);
        panel.add(textFieldDirectory, BorderLayout.CENTER);
        panel.add(buttonDirectory, BorderLayout.EAST);

        panel.setPreferredSize(size);
        return panel;
    }

    private JPanel createSouthPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        buttonLoadCsv = new JButton(UI.getText("MetricsCollectorUI.loadCsv.button.text"));
        buttonLoadCsv.setToolTipText(UI.getText("MetricsCollectorUI.loadCsv.button.hint"));
        buttonLoadCsv.addActionListener(event -> askForLoadMetrics());

        panel.add(buttonLoadCsv);
        return panel;
    }

    private File getSelectedDirectory() {
        String path = textFieldDirectory.getText();
        if (path != null && !path.isEmpty()) {
            File directory = new File(path);
            if (directory.exists()) {
                return directory;
            }
        }
        return null;
    }

    private void println(String message) {
        SwingUtilities.invokeLater(() -> textAreaDetails.append(message + "\n"));
    }
}
