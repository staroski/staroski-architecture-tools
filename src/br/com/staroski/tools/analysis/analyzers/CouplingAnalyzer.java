package br.com.staroski.tools.analysis.analyzers;

import java.util.Set;

import br.com.staroski.tools.analysis.Metrics;
import br.com.staroski.tools.analysis.Project;
import br.com.staroski.tools.analysis.MetricsVisitors;

/**
 * This class iterates over a {@link Set} of {@link ProjectImpl} and computes its <b>efferent coupling</b> ("Ce" - output dependencies) and <b>afferent
 * coupling</b> ("Ca" - input dependencies).<br>
 * For short:<br>
 * "Ce" means "how many components I depend on".<br>
 * "Ca" means "how many components depend on me".
 *
 * @author Staroski, Ricardo Artur
 */
public final class CouplingAnalyzer {

    public void analyze(Set<Project> projects) {
        for (Project project : projects) {
            updateStats(project, projects);
        }
    }

    private void updateStats(Project project, Set<Project> allProjects) {
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
