package br.com.staroski.tools.analysis.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
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

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.WindowConstants;

import br.com.staroski.tools.analysis.Cycle;
import br.com.staroski.tools.analysis.Project;
import br.com.staroski.tools.analysis.Projects;
import br.com.staroski.tools.analysis.analyzers.AbstractionAnalysisEvent;
import br.com.staroski.tools.analysis.analyzers.DefaultMetricsAnalyzerListener;
import br.com.staroski.tools.analysis.analyzers.DependencyAnalysisEvent;
import br.com.staroski.tools.analysis.analyzers.MetricsAnalyzer;
import br.com.staroski.ui.ConsoleTextPane;
import br.com.staroski.ui.I18N;
import br.com.staroski.ui.UI;

/**
 * This is a dialog for the metrics analyzer tool, part of the Staroski's Architecture Tools.
 *
 * @author Staroski, Ricardo Artur
 */
@SuppressWarnings({ "serial", "unused" })
final class MetricsCollectorUI extends JDialog implements I18N {

    private class InnerListener extends DefaultMetricsAnalyzerListener {

        @Override
        public void onAbstractionAnalysisStarted(AbstractionAnalysisEvent event) {
            consoleTextPane.print(UI.getText("MetricsCollectorUI.collect.onAbstractionAnalysisStarted", event.getProject().getName()));
        }

        @Override
        public void onAbstractionAnalysisFinished(AbstractionAnalysisEvent event) {
            consoleTextPane.print("    ");
            consoleTextPane.println(UI.getText("MetricsCollectorUI.collect.onAbstractionAnalysisFinished"), Color.BLACK, Color.GREEN);
        }

        @Override
        public void onCouplingAnalysisStarted(DependencyAnalysisEvent event) {
            consoleTextPane.print(UI.getText("MetricsCollectorUI.collect.onCouplingAnalysisStarted", event.getProject().getName()));
        }

        @Override
        public void onCouplingAnalysisFinished(DependencyAnalysisEvent event) {
            consoleTextPane.print("    ");
            consoleTextPane.println(UI.getText("MetricsCollectorUI.collect.onCouplingAnalysisFinished"), Color.BLACK, Color.GREEN);
        }

        @Override
        public void onCycleAnalysisStarted(DependencyAnalysisEvent event) {
            consoleTextPane.print(UI.getText("MetricsCollectorUI.collect.onCycleAnalysisStarted", event.getProject().getName()));
        }

        @Override
        public void onCycleAnalysisFinished(DependencyAnalysisEvent event) {
            consoleTextPane.print("    ");
            List<Cycle> cycles = event.getCycles();
            String message = UI.getText("MetricsCollectorUI.collect.onCycleAnalysisFinished", cycles.size());
            consoleTextPane.println(message, Color.BLACK, cycles.isEmpty() ? Color.GREEN : Color.YELLOW);
        }

        @Override
        public void onMetricsAnalysisFinished(Instant end, Duration elapsed) {
            String duration = String.format("%02d:%02d:%02d", elapsed.toHours(), elapsed.toMinutesPart(), elapsed.toSecondsPart());
            consoleTextPane.println(UI.getText("MetricsCollectorUI.collect.onMetricsAnalysisFinished", duration));
        }

        @Override
        public void onMetricsAnalysisStarted(Instant start) {
            consoleTextPane.println(UI.getText("MetricsCollectorUI.collect.onMetricsAnalysisStarted"));
        }

        @Override
        public void onMetricsCollected(Set<Project> projects) {
            projectsFound = projects.isEmpty() ? null : projects;
            String metricsCsv = "";
            if (!projects.isEmpty()) {
                metricsCsv = Projects.getMetricsCsv(projects);
            }
            consoleTextPane.println(UI.getText("MetricsCollectorUI.collect.onMetricsCollected", projects.size(), metricsCsv));
            UI.beep();
        }

        @Override
        public void onProjectFound(Project project) {
            consoleTextPane.println(UI.getText("MetricsCollectorUI.collect.onProjectFound", project.getName()));
        }

        @Override
        public void onProjectScanStarted(File directory) {
            try {
                consoleTextPane.println(UI.getText("MetricsCollectorUI.collect.onProjectScanStarted", directory.getCanonicalPath()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private JTextField textFieldDirectory;
    private JButton buttonDirectory;
    private JLabel labelDirectory;
    private JButton buttonCollect;
    private ConsoleTextPane consoleTextPane;
    private JButton buttonLoadCsv;

    private MetricsCollectorUIListener listener;
    private Set<Project> projectsFound;

    public MetricsCollectorUI(JFrame owner) {
        super(owner, UI.getText("MetricsCollectorUI.title"), true);
        setIconImages(createIcons());
        setMinimumSize(new Dimension(640, 480));
        setSize(new Dimension(1024, 640));
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
            projectsFound = null;
            consoleTextPane.clear();
            updateState(false);
        }
        super.setVisible(visible);
    }

    public MetricsCollectorUI withListener(MetricsCollectorUIListener listener) {
        this.listener = listener;
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
        if (projectsFound == null) {
            String title = UI.getText("MetricsCollectorUI.loadCsv.warning.title");
            String message = UI.getText("MetricsCollectorUI.loadCsv.warning.message");
            UI.showWarning(this, title, message);
            return;
        }

        String title = UI.getText("MetricsCollectorUI.loadCsv.title");
        String message = UI.getText("MetricsCollectorUI.loadCsv.message");
        if (UI.showConfirmation(this, title, message)) {
            if (listener != null) {
                listener.onProjectsFounds(projectsFound);
            }
            dispose();
        }
    }

    private void chooseDirectory() {
        File directory = UI.selectDirectory(this, getSelectedDirectory());
        if (directory != null) {
            try {
                final String path = directory.getCanonicalPath();
                textFieldDirectory.setText(path);
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
        projectsFound = null;
        consoleTextPane.clear();

        Runnable process = () -> runMetricsAnalyzer(directory);
        new Thread(process, "MetricsAnalyzerThread").start();
    }

    private JComponent createDetailPanel() {
        consoleTextPane = new ConsoleTextPane();
        JScrollPane scrollPane = new JScrollPane(consoleTextPane);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        return scrollPane;
    }

    private List<BufferedImage> createIcons() {
        return Arrays.asList(Images.METRICS_ANALYZER_24);
    }

    private JPanel createNorthPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        JPanel selectionPanel = createSelectionPanel();

        panel.add(selectionPanel, BorderLayout.CENTER);

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

        buttonCollect = new JButton(UI.getText("MetricsCollectorUI.collect.button.text"));
        buttonCollect.setToolTipText(UI.getText("MetricsCollectorUI.collect.button.hint"));
        buttonCollect.addActionListener(event -> collectMetrics());

        buttonLoadCsv = new JButton(UI.getText("MetricsCollectorUI.loadCsv.button.text"));
        buttonLoadCsv.setToolTipText(UI.getText("MetricsCollectorUI.loadCsv.button.hint"));
        buttonLoadCsv.addActionListener(event -> askForLoadMetrics());

        panel.add(buttonCollect);
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

    private void printHtml(String html) {
        String currentContent = consoleTextPane.getText();
        int lastParagraphEndIndex = currentContent.lastIndexOf("</p>");
        if (lastParagraphEndIndex == -1) {
            lastParagraphEndIndex = currentContent.lastIndexOf("</body>");
        }
        String before = currentContent.substring(0, lastParagraphEndIndex);
        String after = currentContent.substring(lastParagraphEndIndex);
        String newContent = before + html + after;
        consoleTextPane.setText(newContent);
    }

    private void runMetricsAnalyzer(File directory) {
        try {
            updateState(true);
            final MetricsAnalyzer metricsAnalyzer = new MetricsAnalyzer();
            metricsAnalyzer.addMetricsAnalyzerListener(new InnerListener());
            metricsAnalyzer.analyze(directory);
        } catch (Exception e) {
            e.printStackTrace();
            String title = UI.getText("MetricsCollectorUI.collect.error.title");
            UI.showError(MetricsCollectorUI.this, title, e);
        } finally {
            updateState(false);
        }
    }

    private void updateState(boolean running) {
        int type = running ? Cursor.WAIT_CURSOR : Cursor.DEFAULT_CURSOR;
        UI.setCursor(this, Cursor.getPredefinedCursor(type));
        final boolean enabled = !running;
        textFieldDirectory.setEditable(enabled);
        buttonDirectory.setEnabled(enabled);
        buttonCollect.setEnabled(enabled);
        buttonLoadCsv.setEnabled(enabled && projectsFound != null);
    }
}
