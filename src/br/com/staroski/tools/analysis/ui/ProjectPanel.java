package br.com.staroski.tools.analysis.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

import br.com.staroski.tools.analysis.Cycle;
import br.com.staroski.tools.analysis.Metrics;
import br.com.staroski.tools.analysis.Project;
import br.com.staroski.ui.I18N;
import br.com.staroski.ui.UI;

@SuppressWarnings("serial")
final class ProjectPanel extends JPanel implements I18N {

    private JTextField nameField;
    private JTextField directoryField;
    private JTextArea dependenciesArea;
    private JTextArea metricsArea;
    private TitledBorder nameBorder;
    private TitledBorder directoryBorder;
    private TitledBorder dependenciesdBorder;
    private TitledBorder metricsBorder;
    private Project project;

    public ProjectPanel() {
        setLayout(new BorderLayout());

        nameField = new JTextField();
        nameField.setEditable(false);
        nameField.setFont(UI.MONOSPACED);
        nameField.setBackground(Color.WHITE);
        nameBorder = BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLACK),
                UI.getText("ComponentInspectorUI.component.name"),
                TitledBorder.LEFT, TitledBorder.TOP);

        JPanel namePanel = new JPanel(new BorderLayout());
        namePanel.setBorder(nameBorder);
        namePanel.add(nameField, BorderLayout.CENTER);

        directoryField = new JTextField();
        directoryField.setEditable(false);
        directoryField.setFont(UI.MONOSPACED);
        directoryField.setBackground(Color.WHITE);
        directoryBorder = BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLACK),
                UI.getText("ComponentInspectorUI.component.directory"),
                TitledBorder.LEFT, TitledBorder.TOP);

        JPanel directoryPanel = new JPanel(new BorderLayout());
        directoryPanel.setBorder(directoryBorder);
        directoryPanel.add(directoryField, BorderLayout.CENTER);

        dependenciesArea = new JTextArea();
        dependenciesArea.setEditable(false);
        dependenciesArea.setFont(UI.MONOSPACED);
        dependenciesdBorder = BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLACK),
                UI.getText("ComponentInspectorUI.component.dependencies"),
                TitledBorder.LEFT, TitledBorder.TOP);

        JScrollPane dependenciesScrollPane = new JScrollPane(dependenciesArea);
        dependenciesScrollPane.setBorder(dependenciesdBorder);

        metricsArea = new JTextArea();
        metricsArea.setEditable(false);
        metricsArea.setFont(UI.MONOSPACED);
        metricsBorder = BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLACK),
                UI.getText("ComponentInspectorUI.component.metrics"),
                TitledBorder.LEFT, TitledBorder.TOP);

        JScrollPane metricsScrollPane = new JScrollPane(metricsArea);
        metricsScrollPane.setBorder(metricsBorder);

        JPanel centerPanel = new JPanel(new GridLayout(2, 1));
        centerPanel.add(dependenciesScrollPane);
        centerPanel.add(metricsScrollPane);

        JPanel topPanel = new JPanel(new GridLayout(2, 1));
        topPanel.add(namePanel);
        topPanel.add(directoryPanel);

        add(topPanel, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);
    }

    @Override
    public void onLocaleChange(Locale newLocale) {
        nameBorder.setTitle(UI.getText("ComponentInspectorUI.component.name"));
        directoryBorder.setTitle(UI.getText("ComponentInspectorUI.component.directory"));
        dependenciesdBorder.setTitle(UI.getText("ComponentInspectorUI.component.dependencies"));
        metricsBorder.setTitle(UI.getText("ComponentInspectorUI.component.metrics"));

        metricsArea.setText(project != null ? formatMetrics(project.getMetrics()) : "");
    }

    public void setProject(Project project) {
        this.project = project;
        if (project != null) {
            nameField.setText(project.getName());
            directoryField.setText(project.getDirectory().getPath());
            dependenciesArea.setText(formatProjectDependencies(project.getProjectDependencies()));
            metricsArea.setText(formatMetrics(project.getMetrics()));
        } else {
            nameField.setText("");
            directoryField.setText("");
            dependenciesArea.setText("");
            metricsArea.setText("");
        }
    }

    private String formatMetrics(Metrics metrics) {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format(UI.getText("ComponentInspectorUI.component.metrics.distance") + ": %.2f\n", metrics.getDistance()));
        sb.append(String.format(UI.getText("ComponentInspectorUI.component.metrics.instability") + ": %.2f\n", metrics.getInstability()));
        sb.append(String.format(UI.getText("ComponentInspectorUI.component.metrics.abstractness") + ": %.2f\n", metrics.getAbstractness()));
        sb.append(String.format(UI.getText("ComponentInspectorUI.component.metrics.abstractTypes") + ": %d\n", metrics.getAbstractTypes()));
        sb.append(String.format(UI.getText("ComponentInspectorUI.component.metrics.concreteTypes") + ": %d\n", metrics.getConcreteTypes()));
        sb.append(String.format(UI.getText("ComponentInspectorUI.component.metrics.outputDependencies") + ": %d\n", metrics.getOutputDependencies()));
        sb.append(String.format(UI.getText("ComponentInspectorUI.component.metrics.inputDependencies") + ": %d\n", metrics.getInputDependencies()));

        List<Cycle> cycles = metrics.getCycles();
        sb.append(String.format(UI.getText("ComponentInspectorUI.component.metrics.cycles") + ": %d\n", cycles.size()));
        for (Cycle cycle : cycles) {
            sb.append("  ").append(cycle).append("\n");
        }
        return sb.toString();
    }

    private String formatProjectDependencies(Set<Project> dependencies) {
        StringBuilder sb = new StringBuilder();
        for (Project dep : dependencies) {
            sb.append(dep.getName()).append("\n");
        }
        return sb.toString();
    }
}
