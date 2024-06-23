package br.com.staroski.tools.analysis;

/**
 * Contains some metrics regarding a {@link ProjectImpl}.
 */
final class MetricsImpl implements Metrics {

    private int abstractClasses;
    private int concreteClasses;
    private int inputDependencies;
    private int outputDependencies;
    private boolean acyclic;

    private final Project project;

    MetricsImpl(Project project) {
        assert project != null;
        this.project = project;
    }

    @Override
    public void accept(MetricsVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public double getAbstractness() {
        double na = getAbstractTypes(); // number os abstract types
        double nc = getConcreteTypes(); // number os concrete types
        double sum = nc + na;
        return sum > 0 ? na / sum : 0;
    }

    @Override
    public int getAbstractTypes() {
        return abstractClasses;
    }

    @Override
    public int getConcreteTypes() {
        return concreteClasses;
    }

    @Override
    public double getDistance() {
        double a = getAbstractness();
        double i = getInstability();
        return Math.abs((a + i - 1.0));
    }

    /**
     * Returns the "Ca", <b>afferent coupling</b>, it means "how many components depend on me".
     */
    @Override
    public int getInputDependencies() {
        return inputDependencies;
    }

    @Override
    public double getInstability() {
        double ce = getOutputDependencies(); // efferent coupling
        double ca = getInputDependencies(); // afferent coupling
        double sum = ca + ce;
        return sum > 0 ? ce / sum : 0;
    }

    /**
     * Returns the "Ce", <b>efferent coupling</b>, it means "how many components I depend on".
     */
    @Override
    public int getOutputDependencies() {
        return outputDependencies;
    }

    @Override
    public Project getProject() {
        return project;
    }

    @Override
    public int getTotalTypes() {
        return abstractClasses + concreteClasses;
    }

    @Override
    public boolean isAcyclic() {
        return acyclic;
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

    void setAcyclic(boolean acyclic) {
        this.acyclic = acyclic;
    }
}