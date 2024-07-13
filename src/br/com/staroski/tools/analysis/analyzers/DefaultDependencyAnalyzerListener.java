package br.com.staroski.tools.analysis.analyzers;

/**
 * This is an empty implementation of the {@link DependencyAnalyzerListener}.
 * 
 * @author Staroski, Ricardo Artur
 */
public class DefaultDependencyAnalyzerListener implements DependencyAnalyzerListener {

    @Override
    public void onCouplingAnalysisStarted(DependencyAnalysisEvent event) {}

    @Override
    public void onCouplingAnalysisFinished(DependencyAnalysisEvent event) {}

    @Override
    public void onCycleAnalysisStarted(DependencyAnalysisEvent event) {}

    @Override
    public void onCycleAnalysisFinished(DependencyAnalysisEvent event) {}

}
