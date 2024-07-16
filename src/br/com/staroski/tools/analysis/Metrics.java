package br.com.staroski.tools.analysis;

/**
 * Stores some architectural metrics regarding a {@link Project}.
 * 
 * @author Staroski, Ricardo Artur
 */
public interface Metrics {

    /**
     * Submits the specified {@link MetricsVisitor} to this {@link Metrics} object.
     */
    public void accept(MetricsVisitor visitor);

    /**
     * Returns the "A", <b>Abstractness</b>.
     */
    double getAbstractness();

    /**
     * Returns the "Na", number of <b>Abstract Types</b>.
     */
    int getAbstractTypes();

    /**
     * Returns the "Nc", number of <b>Concrete Types</b>.
     */
    int getConcreteTypes();

    /**
     * Returns the "D", <b>Distance</b> from The Main Sequence.
     */
    double getDistance();

    /**
     * Returns the "Ca", <b>Afferent Coupling</b>, it means "how many components depend on me".
     */
    int getInputDependencies();

    /**
     * Returns the "I", <b>Instability</b>.
     */
    double getInstability();

    /**
     * Returns the "Ce", <b>Efferent Coupling</b>, it means "how many components I depend on".
     */
    int getOutputDependencies();

    /**
     * Returns the {@link Project} this {@link Metrics} objects reffers to.
     */
    Project getProject();

    /**
     * Returns the sum of "Na" and "Nc".
     */
    int getTotalTypes();

    /**
     * Returns <tt>true</tt> if the {@link Project} is a "DAG", <b>Directed Acyclic Graph</b>, that is, if it has no circular dependencies.
     */
    boolean isAcyclic();

}