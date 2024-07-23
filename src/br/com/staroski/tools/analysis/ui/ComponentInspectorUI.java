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
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

import br.com.staroski.tools.analysis.Project;
import br.com.staroski.ui.I18N;
import br.com.staroski.ui.UI;

/**
 * This is a dialog for the metrics analyzer tool, part of the Staroski's Architecture Tools.
 *
 * @author Staroski, Ricardo Artur
 */
@SuppressWarnings("serial")
final class ComponentInspectorUI extends JDialog implements I18N {

    private ProjectPanel projectPanel;

    public ComponentInspectorUI(JFrame owner) {
        super(owner, UI.getText("ComponentInspectorUI.title"), true);
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

        projectPanel = new ProjectPanel();

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        mainPanel.add(projectPanel, BorderLayout.CENTER);
        setContentPane(mainPanel);
    }

    @Override
    public void onLocaleChange(Locale newLocale) {
        setTitle(UI.getText("ComponentInspectorUI.title"));
    }

    public ComponentInspectorUI withProject(Project project) {
        projectPanel.setProject(project);
        return this;
    }

    private void askForExit() {
        String title = UI.getText("ComponentInspectorUI.exit.title");
        String message = UI.getText("ComponentInspectorUI.exit.message");
        if (UI.showConfirmation(this, title, message)) {
            dispose();
        }
    }

    private List<BufferedImage> createIcons() {
        return Arrays.asList(Images.INSPECT_24);
    }
}
