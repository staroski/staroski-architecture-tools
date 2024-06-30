package br.com.staroski.tools.analysis.analyzers;

/**
 * Utility class that allows adding aand removing listeners without using collections.<br>
 * It is an implementation of the <I>Composite</I> design pattern.
 *
 * @author Staroski, Ricardo Artur
 */
@SuppressWarnings("unchecked")
public final class Listeners implements DependencyAnalyzerListener {

    // The implementation of this class is based on AWTEventMulticaster
    // Allowing method calls to be propagated to multiple objects as if they were a single one
    // This eliminates the need to write loops to iterate over the objects

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

}