package br.com.staroski.tools.analysis;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Stores some architectural metrics regarding a {@link Project}.
 * 
 * @author Staroski, Ricardo Artur
 */
public class Metrics {

    private int abstractClasses;
    private int concreteClasses;
    private int inputDependencies;
    private int outputDependencies;
    private List<Cycle> cycles;

    private final Project project;

    Metrics(Project project) {
        this.project = Objects.requireNonNull(project, Project.class.getSimpleName() + " cannot be null!");
    }

    /**
     * Submits the specified {@link MetricsVisitor} to this {@link Metrics} object.
     */
    public void accept(MetricsVisitor visitor) {
        visitor.visit(this);
    }

    /**
     * Returns the "A", <b>Abstractness</b>.
     */
    public double getAbstractness() {
        double na = getAbstractTypes();
        double nc = getConcreteTypes();
        double sum = nc + na;
        return sum > 0 ? na / sum : 0;
    }

    /**
     * Returns the "Na", number of <b>Abstract Types</b>.
     */
    public int getAbstractTypes() {
        return abstractClasses;
    }

    /**
     * Returns the "Nc", number of <b>Concrete Types</b>.
     */
    public int getConcreteTypes() {
        return concreteClasses;
    }

    /**
     * Returns the "D", <b>Distance</b> from The Main Sequence.
     */
    public double getDistance() {
        double a = getAbstractness();
        double i = getInstability();
        return Math.abs((a + i - 1.0));
    }

    /**
     * Returns the "Ca", <b>Afferent Coupling</b>, it means "how many components depend on me".
     */
    public int getInputDependencies() {
        return inputDependencies;
    }

    /**
     * Returns the "I", <b>Instability</b>.
     */
    public double getInstability() {
        double ce = getOutputDependencies();
        double ca = getInputDependencies();
        double sum = ca + ce;
        return sum > 0 ? ce / sum : 0;
    }

    /**
     * Returns the "Ce", <b>Efferent Coupling</b>, it means "how many components I depend on".
     */
    public int getOutputDependencies() {
        return outputDependencies;
    }

    /**
     * Returns the {@link Project} this {@link Metrics} objects reffers to.
     */
    public Project getProject() {
        return project;
    }

    /**
     * Returns the sum of "Na" and "Nc".
     */
    public int getTotalTypes() {
        return abstractClasses + concreteClasses;
    }

    /**
     * Returns <tt>true</tt> if the {@link Project} is a "DAG", <b>Directed Acyclic Graph</b>, that is, if it has no circular dependencies.
     */
    public boolean isAcyclic() {
        return getCycles().isEmpty();
    }

    /**
     * If the {@link Project} is not a "DAG", <b>Directed Acyclic Graph</b>, then this method can be used to get it's {@link Cycle}s.
     * 
     * @return A {@link List} of {@link Cycle}s or an empty {@link List} if the {@link Project} is a "DAG".
     */
    public List<Cycle> getCycles() {
        return cycles == null ? Collections.emptyList() : Collections.unmodifiableList(cycles);
    }

    void incrementAbstractTypes() {
        abstractClasses++;
    }

    void incrementConcreteTypes() {
        concreteClasses++;
    }

    void incrementInputDependencies() {
        inputDependencies++;
    }

    void incrementOutputDependencies() {
        outputDependencies++;
    }

    void setCycles(List<Cycle> cycles) {
        this.cycles = Objects.requireNonNull(cycles, List.class.getSimpleName() + "<" + Cycle.class.getSimpleName() + "> cannot be null!");
    }
}