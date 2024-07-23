package br.com.staroski.tools.analysis;

/**
 * Visitor interface to be used by the {@link Metrics} class.<br>
 * Implementations of this interface are responsable to change the state of a {@link Metrics} object since this class doesn't provide public setter methods.
 * 
 * @author Staroski, Ricardo Artur
 */
public interface MetricsVisitor {

    void visit(Metrics metrics);
}
