package br.com.staroski.tools.analysis.analyzers;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.Set;

import br.com.staroski.tools.analysis.Metrics;
import br.com.staroski.tools.analysis.Project;
import br.com.staroski.tools.analysis.Projects;

/**
 * Traverses a directory tree scanning for projects and collecting metrics for each {@link Project} found.<br>
 * Use a {@link MetricsAnalyzerListener} to listen for events.
 *
 * @author Staroski, Ricardo Artur
 */
public final class MetricsAnalyzer {

    private class InternalMetricsAnalyzerListener implements MetricsAnalyzerListener {
        @Override
        public void onAbstractionAnalysisFinished(AbstractionAnalysisEvent event) {
            System.out.println("onAbstractionAnalysisFinished: " + event.getProject().getName());
        }

        @Override
        public void onAbstractionAnalysisStarted(AbstractionAnalysisEvent event) {
            System.out.println("onAbstractionAnalysisStarted: " + event.getProject().getName());
        }

        @Override
        public void onCouplingAnalysisFinished(DependencyAnalysisEvent event) {
            System.out.println("onCouplingAnalysisFinished: " + event.getProject().getName());
        }

        @Override
        public void onCouplingAnalysisStarted(DependencyAnalysisEvent event) {
            System.out.println("onCouplingAnalysisStarted: " + event.getProject().getName());
        }

        @Override
        public void onCycleAnalysisFinished(DependencyAnalysisEvent event) {
            System.out.println("onCycleAnalysisFinished: " + event.getProject().getName());
        }

        @Override
        public void onCycleAnalysisStarted(DependencyAnalysisEvent event) {
            System.out.println("onCycleAnalysisStarted: " + event.getProject().getName());
        }

        @Override
        public void onDirectoryEnter(File directory) {
            try {
                System.out.println("onDirectoryEnter: " + directory.getCanonicalPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onDirectoryExit(File directory) {
            try {
                System.out.println("onDirectoryExit: " + directory.getCanonicalPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onFileParsingFinished(AbstractionAnalysisEvent event) {
            try {
                System.out.println("onFileParsingFinished: " + event.getFile().getAbsolutePath());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onFileParsingStarted(AbstractionAnalysisEvent event) {
            try {
                System.out.println("onFileParsingStarted: " + event.getFile().getAbsolutePath());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onMetricsAnalysisFinished(Instant end, Duration elapsed) {
            System.out.printf("onMetricsAnalysisFinished duration: %02d:%02d:%02d%n",
                    elapsed.toHours(), elapsed.toMinutesPart(), elapsed.toSecondsPart());
        }

        @Override
        public void onMetricsAnalysisStarted(Instant start) {
            System.out.println("onMetricsAnalysisStarted");
        }

        @Override
        public void onMetricsCollected(Set<Project> projects) {
            // print stats
            System.out.printf("onMetricsCollected %d projects%n", projects.size());
            System.out.println("Name,D,I,A,Ce,Ca,Nc,Na,DAG");
            for (Project project : projects) {
                Metrics m = project.getMetrics();
                String name = project.getName();
                String d = String.format("%.2f", m.getDistance());
                String i = String.format("%.2f", m.getInstability());
                String a = String.format("%.2f", m.getAbstractness());
                int ce = m.getOutputDependencies();
                int ca = m.getInputDependencies();
                int nc = m.getConcreteTypes();
                int na = m.getAbstractTypes();
                int dag = m.isAcyclic() ? 1 : 0;

                System.out.printf("%s,%s,%s,%s,%d,%d,%d,%d,%d%n", name, d, i, a, ce, ca, nc, na, dag);
            }
        }

        @Override
        public void onProjectFound(Project project) {
            System.out.println("onProjectFound: " + project.getName());
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
