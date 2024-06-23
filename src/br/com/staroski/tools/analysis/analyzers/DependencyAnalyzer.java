package br.com.staroski.tools.analysis.analyzers;

import java.util.Set;

import br.com.staroski.tools.analysis.Metrics;
import br.com.staroski.tools.analysis.Project;
import br.com.staroski.tools.analysis.MetricsVisitors;

/**
 * This class iterates over a {@link Set} of {@link Project} and computes some dependency metrics on it like: number of <b>abstract types</b> ("Na"), number of
 * <b>concrete types</b> ("Nc"), <b>abstractness</b> ("A") and <b>instability</b> ("I").<br/>
 * After computing, those metrics are available through the {@link Project#getMetrics()} method.
 *
 * @author Staroski, Ricardo Artur
 */
public final class DependencyAnalyzer {

    private final CycleAnalyzer cycleAnalyzer = new CycleAnalyzer();

    public DependencyAnalyzer() {}

    public void analyze(Set<Project> projects) {
        for (Project project : projects) {
            updateCouplingStats(project, projects);
        }
        for (Project project : projects) {
            updateAcyclicStats(projects, project);
        }
    }

    private boolean isAcyclic(Project project, Set<Project> projects) {
        System.out.print("Checking circular dependencies of name \"" + project.getName() + "\" started...");
        try {
            Metrics stats = project.getMetrics();
            if ((stats.getInputDependencies() < 1) || (stats.getOutputDependencies() < 2)) {
                return true; // ignoring because there is no more than one depending on me
            }

            if (cycleAnalyzer.isAcyclic(project)) {
                System.out.print("    name is acyclic");
                return true;
            }
            return false;
        } finally {
            System.out.println("     Done!");
        }
    }

    private void updateAcyclicStats(Set<Project> projects, Project project) {
        project.getMetrics().accept(MetricsVisitors.setAcyclic(isAcyclic(project, projects)));
    }

    private void updateCouplingStats(Project project, Set<Project> allProjects) {
        String name = project.getName();
        System.out.print("Coupling analysis  of name \"" + name + "\" started...");

        Metrics stats = project.getMetrics();

        // compute efferent coupling
        int outputDependencies = project.getProjectDependencies().size();
        for (int i = 0; i < outputDependencies; i++) {
            stats.accept(MetricsVisitors.incrementOutputDependencies());
        }

        // compute afferent coupling
        for (Project otherProject : allProjects) {
            if (project.equals(otherProject)) {
                continue;
            }
            if (otherProject.getProjectDependencies().contains(project)) {
                stats.accept(MetricsVisitors.incrementInputDependencies());
            }
        }

        System.out.println("    Done!");
    }
}
