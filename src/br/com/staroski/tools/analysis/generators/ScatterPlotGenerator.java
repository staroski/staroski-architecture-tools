package br.com.staroski.tools.analysis.generators;

import java.io.File;
import java.time.Duration;
import java.time.Instant;
import java.util.Set;
import java.util.TreeSet;

import br.com.staroski.tools.analysis.Metrics;
import br.com.staroski.tools.analysis.Project;
import br.com.staroski.tools.analysis.Projects;
import br.com.staroski.tools.analysis.analyzers.AbstractionAnalyzer;
import br.com.staroski.tools.analysis.analyzers.DependencyAnalyzer;

/**
 * Traverses a directory tree and generates content for a CSV file with all the metrics computed for each {@link ProjectImpl} found.
 *
 * @author Staroski, Ricardo Artur
 */
public final class ScatterPlotGenerator implements Generator {

    public static void main(String[] args) {
        try {
            Generator program = new ScatterPlotGenerator();
            String programName = program.getClass().getSimpleName();
            System.out.printf("Starting %s...%n", programName);

            File repository = new File("S:\\workspaces\\staroski\\example-system");
            Duration elapsed = program.execute(repository);

            System.out.printf("Finished %s in %02d:%02d:%02d%n", programName, elapsed.toHours(),
                    elapsed.toMinutesPart(), elapsed.toSecondsPart());
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    private ScatterPlotGenerator() {}

    @Override
    public Duration execute(File repository) throws Exception {

        boolean withCyclesOnly = true;

        final Instant start = Instant.now();

        final Set<Project> projects = Projects.scan(repository);

        final DependencyAnalyzer dependencyAnalyzer = new DependencyAnalyzer();
        final AbstractionAnalyzer abstractionAnalyzer = new AbstractionAnalyzer();

        dependencyAnalyzer.analyze(projects);
        abstractionAnalyzer.analyze(projects);

        final Set<Project> filtered = withCyclesOnly ? new TreeSet<>() : projects;
        if (withCyclesOnly) {
            for (Project p : projects) {
                if (!p.getMetrics().isAcyclic()) {
                    filtered.add(p);
                }
            }
        }

        // print stats
        System.out.printf("%nShowing %d Projects Stats:%n", filtered.size());
        System.out.println("ID; Name; D; I; A; Ce; Ca; Nc; Na; DAG");
        int id = 0;
        for (Project project : filtered) {
            if (withCyclesOnly) {
                if (!project.getMetrics().isAcyclic()) {
                    printStats(++id, project);
                }
            } else {
                printStats(++id, project);
            }
        }

        final Instant end = Instant.now();
        return Duration.between(start, end);
    }

    private void printStats(int id, Project project) {
        Metrics stats = project.getMetrics();
        try {
            String d = String.format("%.2f", stats.getDistance()).replace('.', ',');
            String i = String.format("%.2f", stats.getInstability()).replace('.', ',');
            String a = String.format("%.2f", stats.getAbstractness()).replace('.', ',');
            int ce = stats.getOutputDependencies();
            int ca = stats.getInputDependencies();
            int nc = stats.getConcreteTypes();
            int na = stats.getAbstractTypes();
            int dag = stats.isAcyclic() ? 1 : 0;

            System.out.printf("%02X; %s; %s; %s; %s; %d; %d; %d; %d; %d%n", id, project, d, i, a, ce, ca, nc, na, dag);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
