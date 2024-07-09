package br.com.staroski.tools.analysis.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
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
public final class MetricsAnalyzerUI extends JDialog implements I18N {

    public MetricsAnalyzerUI(JFrame owner) {
        super(owner, UI.getString("MetricsAnalyzerUI.title"), true);
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

        mainPanel.add(createSearchPanel(), BorderLayout.NORTH);

        setContentPane(mainPanel);
    }

    private JPanel createSearchPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        JLabel labelDirectory = new JLabel("Directory:");
        JTextField textFieldDirectory = new JTextField();
        JButton buttonDirectory = new JButton("...");

        panel.add(labelDirectory, BorderLayout.WEST);
        panel.add(textFieldDirectory, BorderLayout.CENTER);
        panel.add(buttonDirectory, BorderLayout.EAST);

        panel.setPreferredSize(textFieldDirectory.getPreferredSize());

        return panel;
    }

    @Override
    public void onLocaleChange(Locale newLocale) {
        setTitle(UI.getString("MetricsAnalyzerUI.title"));
    }

    private void askForExit() {
        String title = UI.getString("MetricsAnalyzerUI.exit.title");
        String message = UI.getString("MetricsAnalyzerUI.exit.message");
        if (UI.showConfirmation(this, title, message)) {
            dispose();
        }
    }

    private List<BufferedImage> createIcons() {
        return Arrays.asList(Images.METRICS_ANALYZER_24);
    }
}
