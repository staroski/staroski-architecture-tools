package br.com.staroski.tools.analysis.generators;

import java.io.File;
import java.io.PrintWriter;
import java.time.Duration;
import java.time.Instant;
import java.util.Set;

import br.com.staroski.tools.analysis.Metrics;
import br.com.staroski.tools.analysis.Project;
import br.com.staroski.tools.analysis.Projects;
import br.com.staroski.tools.analysis.analyzers.AbstractionAnalyzer;
import br.com.staroski.tools.analysis.analyzers.DependencyAnalyzer;
import br.com.staroski.utils.Arguments;

/**
 * Traverses a directory tree and generates content for a CSV file with all the metrics computed for each {@link Project} found.
 *
 * @author Staroski, Ricardo Artur
 */
public final class ScatterPlotGenerator {

    private static final String PARAM_REPOSITORY = "-r";
    private static final String PARAM_OUTPUT = "-o";

    public static void main(String[] args) {
        try {
            Arguments arguments = new Arguments(args, PARAM_REPOSITORY, PARAM_OUTPUT);

            File repository = new File(arguments.getArgument(PARAM_REPOSITORY));
            File output = new File(arguments.getArgument(PARAM_OUTPUT));

            ScatterPlotGenerator program = new ScatterPlotGenerator();
            String programName = program.getClass().getSimpleName();
            System.out.printf("Starting %s...%n", programName);

            Duration elapsed = program.execute(repository, output);

            System.out.printf("Finished %s in %02d:%02d:%02d%n", programName, elapsed.toHours(),
                    elapsed.toMinutesPart(), elapsed.toSecondsPart());
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    private ScatterPlotGenerator() {}

    public Duration execute(File repository, File output) throws Exception {
        final Instant start = Instant.now();

        final Set<Project> projects = Projects.scan(repository);

        final DependencyAnalyzer dependencyAnalyzer = new DependencyAnalyzer();
        final AbstractionAnalyzer abstractionAnalyzer = new AbstractionAnalyzer();

        dependencyAnalyzer.analyze(projects);
        abstractionAnalyzer.analyze(projects);

        System.out.printf("Generating file \"%s\"...%n", output.getCanonicalPath());
        PrintWriter outputFile = new PrintWriter(output);

        // print stats
        System.out.printf("%nShowing %d Projects Stats:%n", projects.size());
        System.out.println("Name,D,I,A,Ce,Ca,Nc,Na,DAG");
        outputFile.println("Name,D,I,A,Ce,Ca,Nc,Na,DAG");
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
            outputFile.printf("%s,%s,%s,%s,%d,%d,%d,%d,%d%n", name, d, i, a, ce, ca, nc, na, dag);
        }

        outputFile.flush();
        outputFile.close();
        System.out.printf("File \"%s\" successfully generated!%n", output.getCanonicalPath());

        final Instant end = Instant.now();
        return Duration.between(start, end);
    }
}
