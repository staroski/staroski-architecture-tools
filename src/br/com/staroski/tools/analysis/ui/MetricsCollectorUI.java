package br.com.staroski.tools.analysis.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.WindowConstants;

/**
 * This is a dialog for the metrics analyzer tool, part of the Staroski's Architecture Tools.
 *
 * @author Staroski, Ricardo Artur
 */
@SuppressWarnings("serial")
public final class MetricsCollectorUI extends JDialog implements I18N {

    private JTextField textFieldDirectory;
    private JButton buttonDirectory;
    private JLabel labelDirectory;
    private JButton buttonCollect;

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

        mainPanel.add(createSelectionPanel(), BorderLayout.NORTH);
        mainPanel.add(createCollectPanel(), BorderLayout.SOUTH);

        setContentPane(mainPanel);
    }

    @Override
    public void onLocaleChange(Locale newLocale) {
        setTitle(UI.getText("MetricsCollectorUI.title"));

        labelDirectory.setText(UI.getText("MetricsCollectorUI.selectFolder.label"));
        buttonDirectory.setToolTipText(UI.getText("MetricsCollectorUI.selectFolder.button.hint"));

        buttonCollect.setText(UI.getText("MetricsCollectorUI.collect.button.text"));
        buttonCollect.setToolTipText(UI.getText("MetricsCollectorUI.collect.button.hint"));
    }

    private void askForExit() {
        String title = UI.getText("MetricsCollectorUI.exit.title");
        String message = UI.getText("MetricsCollectorUI.exit.message");
        if (UI.showConfirmation(this, title, message)) {
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

    private JPanel createCollectPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        buttonCollect = new JButton(UI.getText("MetricsCollectorUI.collect.button.text"));
        buttonCollect.setToolTipText(UI.getText("MetricsCollectorUI.collect.button.hint"));

        panel.add(buttonCollect);
        return panel;
    }

    private List<BufferedImage> createIcons() {
        return Arrays.asList(Images.METRICS_ANALYZER_24);
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
}
