package br.com.staroski.tools.analysis.analyzers;

import java.util.Set;

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

    private final CycleAnalyzer cycleAnalyzer = new CycleAnalyzer();

    private DependencyAnalyzerListener listener = new DependencyAnalyzerListener() {

        @Override
        public void onCouplingAnalysisStarted(DependencyAnalysisEvent event) {
            System.out.println("Coupling analysis of project \"" + event.getProject().getName() + "\" started...");
        }

        @Override
        public void onCouplingAnalysisFinished(DependencyAnalysisEvent event) {
            System.out.println("Coupling analysis of project \"" + event.getProject().getName() + "\" finished!");
        }

        public void onCycleAnalysisStarted(DependencyAnalysisEvent event) {
            System.out.println("Circular dependencies analysis of project \"" + event.getProject().getName() + "\" started...");
        };

        @Override
        public void onCycleAnalysisFinished(DependencyAnalysisEvent event) {
            System.out.println("Circular dependencies analysis of project \"" + event.getProject().getName() + "\" finished!");
        }
    };

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

    private boolean isAcyclic(Project project, Set<Project> projects) {
        Metrics stats = project.getMetrics();
        if ((stats.getInputDependencies() < 1) || (stats.getOutputDependencies() < 2)) {
            return true; // ignoring because there is no more than one depending on me
        }
        return cycleAnalyzer.isAcyclic(project);
    }

    private void updateAcyclicStats(Set<Project> projects, Project project) {
        listener.onCycleAnalysisStarted(new DependencyAnalysisEvent(project));

        project.getMetrics().accept(MetricsVisitors.setAcyclic(isAcyclic(project, projects)));

        listener.onCycleAnalysisFinished(new DependencyAnalysisEvent(project));
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
