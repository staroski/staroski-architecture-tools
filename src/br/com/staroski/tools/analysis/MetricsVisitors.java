package br.com.staroski.tools.analysis;

/**
 * As the {@link Metrics} interface doesnt has setter methods this class provides some {@link MetricsVisitor} implementations to update some specific propeties.
 * 
 * @author Staroski, Ricardo Artur
 */
public final class MetricsVisitors {

    /**
     * Provides an {@link MetricsVisitor} that increments the abstract types by one.
     */
    public static MetricsVisitor incrementAbstractTypes() {
        return new AbstractTypesUpdater();
    }

    /**
     * Provides an {@link MetricsVisitor} that increments the concrete types by one.
     */
    public static MetricsVisitor incrementConcreteTypes() {
        return new ConcreteTypesUpdater();
    }

    /**
     * Provides an {@link MetricsVisitor} that increments the input dependencies (afferent coupling) by one.
     */
    public static MetricsVisitor incrementInputDependencies() {
        return new InputDependenciesUpdater();
    }

    /**
     * Provides an {@link MetricsVisitor} that increments the output dependencies (efferent coupling) by one.
     */
    public static MetricsVisitor incrementOutputDependencies() {
        return new OutputDependenciesUpdater();
    }

    /**
     * Provides an {@link MetricsVisitor} that sets the acyclic property.
     */
    public static MetricsVisitor setAcyclic(boolean acyclic) {
        return new AcyclicUpdater(acyclic);
    }

    private MetricsVisitors() {}
}
