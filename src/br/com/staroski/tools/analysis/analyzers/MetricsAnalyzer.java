package br.com.staroski.tools.analysis.analyzers;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.Set;

import br.com.staroski.tools.analysis.Project;
import br.com.staroski.tools.analysis.Projects;

/**
 * Traverses a directory tree scanning for projects and collecting metrics for each {@link Project} found.<br>
 * Use a {@link MetricsAnalyzerListener} to listen for events.
 *
 * @author Staroski, Ricardo Artur
 */
public final class MetricsAnalyzer {

    private class InternalMetricsAnalyzerListener extends DefaultMetricsAnalyzerListener {

        @Override
        public void onAbstractionAnalysisStarted(AbstractionAnalysisEvent event) {
            System.out.println("Running abstraction analysis for project \"" + event.getProject().getName() + "\"");
        }

        @Override
        public void onCouplingAnalysisStarted(DependencyAnalysisEvent event) {
            System.out.println("Running coupling analysis for project \"" + event.getProject().getName() + "\"");
        }

        @Override
        public void onCycleAnalysisStarted(DependencyAnalysisEvent event) {
            System.out.println("Running cycle analysis for project \"" + event.getProject().getName() + "\"");
        }

        @Override
        public void onDirectoryEnter(File directory) {
            try {
                System.out.println("Scanning directory \"" + directory.getCanonicalPath() + "\"");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onFileParsingStarted(AbstractionAnalysisEvent event) {
            try {
                System.out.println("Reading file \"" + event.getFile().getAbsolutePath() + "\"");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onMetricsAnalysisFinished(Instant end, Duration elapsed) {
            System.out.printf("Metrics Analysis Finished in %02d:%02d:%02d%n",
                    elapsed.toHours(), elapsed.toMinutesPart(), elapsed.toSecondsPart());
        }

        @Override
        public void onMetricsAnalysisStarted(Instant start) {
            System.out.println("Metrics Analysis Started");
        }

        @Override
        public void onMetricsCollected(Set<Project> projects) {
            System.out.printf("%nCollected metrics for %d projects%n", projects.size());
            System.out.println("\n" + Projects.getMetricsCsv(projects) + "\n");
        }

        @Override
        public void onProjectScanStarted(File directory) {
            try {
                System.out.println("Scanning for projects in \"" + directory.getCanonicalPath() + "\"");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onProjectFound(Project project) {
            System.out.println("Project found: \"" + project.getName() + "\"");
        }

        @Override
        public void onProjectScanFinished(File directory) {
            System.out.println("Scanning for projects finished!");
        }
    }

    private MetricsAnalyzerListener listener = new InternalMetricsAnalyzerListener();

    public void addMetricsAnalyzerListener(MetricsAnalyzerListener listener) {
        this.listener = Listeners.addMetricsAnalyzerListener(this.listener, listener);
    }

    public void removeMetricsAnalyzerListener(MetricsAnalyzerListener listener) {
        this.listener = Listeners.removeMetricsAnalyzerListener(this.listener, listener);
    }

    public void analyze(File repository) throws Exception {
        Instant start = Instant.now();
        listener.onMetricsAnalysisStarted(start);

        final Set<Project> projects = Projects.scan(repository, listener);

        final DependencyAnalyzer dependencyAnalyzer = new DependencyAnalyzer();
        dependencyAnalyzer.addDependencyAnalyzerListener(listener);
        dependencyAnalyzer.analyze(projects);

        final AbstractionAnalyzer abstractionAnalyzer = new AbstractionAnalyzer();
        abstractionAnalyzer.addAbstractionAnalyzerListener(listener);
        abstractionAnalyzer.analyze(projects);

        listener.onMetricsCollected(projects);

        Instant end = Instant.now();
        Duration elapsed = Duration.between(start, end);
        listener.onMetricsAnalysisFinished(end, elapsed);
    }
}
