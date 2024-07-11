package br.com.staroski.tools.analysis.analyzers;

import java.awt.AWTEventMulticaster;
import java.io.File;
import java.time.Duration;
import java.time.Instant;
import java.util.Set;

import br.com.staroski.tools.analysis.Project;
import br.com.staroski.tools.analysis.ProjectScanListener;

/**
 * Utility class that allows adding and removing listeners without using collections.<br>
 * It is an implementation of the <I>Composite</I> design pattern.<br>
 * The implementation of this class is based on {@link AWTEventMulticaster}.<br>
 * It allows method calls to be propagated to multiple objects as if they were a single one.<br>
 * This eliminates the need to write loops to iterate over the objects.
 *
 * @author Staroski, Ricardo Artur
 */
@SuppressWarnings("unchecked")
final class Listeners implements DependencyAnalyzerListener, AbstractionAnalyzerListener, CouplingAnalyzerListener,
        ProjectScanListener, MetricsAnalyzerListener {

    public static MetricsAnalyzerListener addMetricsAnalyzerListener(MetricsAnalyzerListener existing, MetricsAnalyzerListener toAdd) {
        return add(existing, toAdd);
    }

    public static MetricsAnalyzerListener removeMetricsAnalyzerListener(MetricsAnalyzerListener existing, MetricsAnalyzerListener toRemove) {
        return remove(existing, toRemove);
    }

    public static ProjectScanListener addProjectScanListener(ProjectScanListener existing, ProjectScanListener toAdd) {
        return add(existing, toAdd);
    }

    public static ProjectScanListener removeMProjectScanListener(ProjectScanListener existing, ProjectScanListener toRemove) {
        return remove(existing, toRemove);
    }

    public static CouplingAnalyzerListener addCouplingAnalyzerListener(CouplingAnalyzerListener existing, CouplingAnalyzerListener toAdd) {
        return add(existing, toAdd);
    }

    public static CouplingAnalyzerListener removeCouplingAnalyzerListener(CouplingAnalyzerListener existing, CouplingAnalyzerListener toRemove) {
        return remove(existing, toRemove);
    }

    public static AbstractionAnalyzerListener addAbstractionAnalyzerListener(AbstractionAnalyzerListener existing, AbstractionAnalyzerListener toAdd) {
        return add(existing, toAdd);
    }

    public static AbstractionAnalyzerListener removeAbstractionAnalyzerListener(AbstractionAnalyzerListener existing, AbstractionAnalyzerListener toRemove) {
        return remove(existing, toRemove);
    }

    public static DependencyAnalyzerListener addDependencyAnalyzerListener(DependencyAnalyzerListener existing, DependencyAnalyzerListener toAdd) {
        return add(existing, toAdd);
    }

    public static DependencyAnalyzerListener removeDependencyAnalyzerListener(DependencyAnalyzerListener existing, DependencyAnalyzerListener toRemove) {
        return remove(existing, toRemove);
    }

    // the real implementation, called by the public methods
    private static <T> T add(T existing, T toAdd) {
        if (existing == null) {
            return toAdd;
        }
        if (toAdd == null) {
            return existing;
        }
        return (T) new Listeners(existing, toAdd);
    }

    // the real implementation, called by the public methods
    private static <T> T remove(T existing, T toRemove) {
        if (existing == toRemove || existing == null) {
            return null;
        }
        if (existing instanceof Listeners) {
            Listeners tuple = (Listeners) existing;
            if (toRemove == tuple.a) {
                return (T) tuple.b;
            }
            if (toRemove == tuple.b) {
                return (T) tuple.a;
            }
            T a = remove((T) tuple.a, toRemove);
            T b = remove((T) tuple.b, toRemove);
            if (a == tuple.a && b == tuple.b) {
                return (T) tuple;
            }
            return add(a, b);
        }
        return existing;
    }

    private final Object a;
    private final Object b;

    private Listeners(Object a, Object b) {
        this.a = a;
        this.b = b;
    }

    @Override
    public String toString() {
        return a + " -> " + b;
    }

    @Override
    public void onCouplingAnalysisStarted(DependencyAnalysisEvent event) {
        ((DependencyAnalyzerListener) a).onCouplingAnalysisStarted(event);
        ((DependencyAnalyzerListener) b).onCouplingAnalysisStarted(event);

    }

    @Override
    public void onCouplingAnalysisFinished(DependencyAnalysisEvent event) {
        ((DependencyAnalyzerListener) a).onCouplingAnalysisFinished(event);
        ((DependencyAnalyzerListener) b).onCouplingAnalysisFinished(event);
    }

    @Override
    public void onCycleAnalysisStarted(DependencyAnalysisEvent event) {
        ((DependencyAnalyzerListener) a).onCycleAnalysisStarted(event);
        ((DependencyAnalyzerListener) b).onCycleAnalysisStarted(event);
    }

    @Override
    public void onCycleAnalysisFinished(DependencyAnalysisEvent event) {
        ((DependencyAnalyzerListener) a).onCycleAnalysisFinished(event);
        ((DependencyAnalyzerListener) b).onCycleAnalysisFinished(event);
    }

    @Override
    public void onAbstractionAnalysisStarted(AbstractionAnalysisEvent event) {
        ((AbstractionAnalyzerListener) a).onAbstractionAnalysisStarted(event);
        ((AbstractionAnalyzerListener) b).onAbstractionAnalysisStarted(event);
    }

    @Override
    public void onAbstractionAnalysisFinished(AbstractionAnalysisEvent event) {
        ((AbstractionAnalyzerListener) a).onAbstractionAnalysisFinished(event);
        ((AbstractionAnalyzerListener) b).onAbstractionAnalysisFinished(event);
    }

    @Override
    public void onFileParsingStarted(AbstractionAnalysisEvent event) {
        ((AbstractionAnalyzerListener) a).onFileParsingStarted(event);
        ((AbstractionAnalyzerListener) b).onFileParsingStarted(event);
    }

    @Override
    public void onFileParsingFinished(AbstractionAnalysisEvent event) {
        ((AbstractionAnalyzerListener) a).onFileParsingFinished(event);
        ((AbstractionAnalyzerListener) b).onFileParsingFinished(event);
    }

    @Override
    public void onCouplingAnalysisStarted(CouplingAnalysisEvent event) {
        ((CouplingAnalyzerListener) a).onCouplingAnalysisStarted(event);
        ((CouplingAnalyzerListener) b).onCouplingAnalysisStarted(event);
    }

    @Override
    public void onCouplingAnalysisFinished(CouplingAnalysisEvent event) {
        ((CouplingAnalyzerListener) a).onCouplingAnalysisFinished(event);
        ((CouplingAnalyzerListener) b).onCouplingAnalysisFinished(event);
    }

    @Override
    public void onDirectoryEnter(File directory) {
        ((ProjectScanListener) a).onDirectoryEnter(directory);
        ((ProjectScanListener) b).onDirectoryEnter(directory);
    }

    @Override
    public void onProjectFound(Project project) {
        ((ProjectScanListener) a).onProjectFound(project);
        ((ProjectScanListener) b).onProjectFound(project);
    }

    @Override
    public void onDirectoryExit(File directory) {
        ((ProjectScanListener) a).onDirectoryExit(directory);
        ((ProjectScanListener) b).onDirectoryExit(directory);
    }

    @Override
    public void onMetricsAnalysisStarted(Instant start) {
        ((MetricsAnalyzerListener) a).onMetricsAnalysisStarted(start);
        ((MetricsAnalyzerListener) b).onMetricsAnalysisStarted(start);
    }

    @Override
    public void onMetricsCollected(Set<Project> projects) {
        ((MetricsAnalyzerListener) a).onMetricsCollected(projects);
        ((MetricsAnalyzerListener) b).onMetricsCollected(projects);
    }

    @Override
    public void onMetricsAnalysisFinished(Instant end, Duration elapsed) {
        ((MetricsAnalyzerListener) a).onMetricsAnalysisFinished(end, elapsed);
        ((MetricsAnalyzerListener) b).onMetricsAnalysisFinished(end, elapsed);
    }
}