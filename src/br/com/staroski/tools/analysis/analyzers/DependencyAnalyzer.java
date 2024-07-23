package br.com.staroski.tools.analysis.analyzers;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import br.com.staroski.tools.analysis.Cycle;
import br.com.staroski.tools.analysis.Metrics;
import br.com.staroski.tools.analysis.MetricsVisitors;
import br.com.staroski.tools.analysis.Project;

/**
 * This class iterates over a {@link Set} of {@link Project} and computes some dependency metrics on it like: number of <b>abstract types</b> ("Na"), number of
 * <b>concrete types</b> ("Nc"), <b>abstractness</b> ("A") and <b>instability</b> ("I").<br/>
 * After computing, those metrics are available through the {@link Project#getMetrics()} method.
 *
 * @author Staroski, Ricardo Artur
 */
public final class DependencyAnalyzer {

    private DependencyAnalyzerListener listener = new DefaultDependencyAnalyzerListener();

    public DependencyAnalyzer() {}

    public void addDependencyAnalyzerListener(DependencyAnalyzerListener listener) {
        this.listener = Listeners.addDependencyAnalyzerListener(this.listener, listener);
    }

    public void removeDependencyAnalyzerListener(DependencyAnalyzerListener listener) {
        this.listener = Listeners.removeDependencyAnalyzerListener(this.listener, listener);
    }

    public void analyze(Set<Project> projects) {
        for (Project project : projects) {
            updateCouplingStats(project, projects);
        }
        for (Project project : projects) {
            updateAcyclicStats(projects, project);
        }
    }

    private List<Cycle> getCycles(Project project) {
        Metrics metrics = project.getMetrics();
        if ((metrics.getOutputDependencies() == 0)) {
            return Collections.emptyList(); // ignoring because I depend no one
        }
        if ((metrics.getInputDependencies() == 0)) {
            return Collections.emptyList(); // ignoring because no one depends on me
        }

        ShallowCycleAnalyzer cycleAnalyzer = new ShallowCycleAnalyzer();
        return cycleAnalyzer.analyze(project);
    }

    private void updateAcyclicStats(Set<Project> projects, Project project) {
        listener.onCycleAnalysisStarted(new DependencyAnalysisEvent(project));

        List<Cycle> cycles = getCycles(project);

        project.getMetrics().accept(MetricsVisitors.setCycles(cycles));

        listener.onCycleAnalysisFinished(new DependencyAnalysisEvent(project, cycles));
    }

    private void updateCouplingStats(Project project, Set<Project> allProjects) {
        listener.onCouplingAnalysisStarted(new DependencyAnalysisEvent(project));

        Metrics metrics = project.getMetrics();

        // compute efferent coupling
        int outputDependencies = project.getProjectDependencies().size();
        for (int i = 0; i < outputDependencies; i++) {
            metrics.accept(MetricsVisitors.incrementOutputDependencies());
        }

        // compute afferent coupling
        for (Project otherProject : allProjects) {
            if (project.equals(otherProject)) {
                continue;
            }
            if (otherProject.getProjectDependencies().contains(project)) {
                metrics.accept(MetricsVisitors.incrementInputDependencies());
            }
        }

        listener.onCouplingAnalysisFinished(new DependencyAnalysisEvent(project));
    }
}