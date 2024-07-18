package br.com.staroski.tools.analysis.generators;

import java.io.File;
import java.io.PrintWriter;
import java.time.Duration;
import java.time.Instant;
import java.util.Set;

import br.com.staroski.tools.analysis.Metrics;
import br.com.staroski.tools.analysis.Project;
import br.com.staroski.tools.analysis.Projects;
import br.com.staroski.tools.analysis.analyzers.CouplingAnalyzer;
import br.com.staroski.tools.analysis.analyzers.DeepCycleChecker;
import br.com.staroski.utils.Arguments;

/**
 * Generates content for <a href="https://graphviz.org/doc/info/lang.html">.dot</a> files to be used with
 * <a href="https://graphviz.org/documentation/">Grapviz</a> tool.<br>
 * Example to generate a PDF with Graphviz:
 *
 * <pre>
 * dot -Tpdf -Kcirco input-file.dot -o output-file.pdf
 * </pre>
 *
 * @author Staroski, Ricardo Artur
 */
public final class GraphvizDotFileGenerator {

    private static final String PARAM_REPOSITORY = "-r";
    private static final String PARAM_FILTER = "-f";
    private static final String PARAM_TITLE = "-t";
    private static final String PARAM_OUTPUT = "-o";

    private static final String FILTER_ALL = "all";
    private static final String FILTER_CYCLES = "cycles";

    public static void main(String[] args) {
        try {
            Arguments arguments = new Arguments(args, PARAM_REPOSITORY, PARAM_FILTER, PARAM_TITLE, PARAM_OUTPUT);

            File repository = new File(arguments.getArgument(PARAM_REPOSITORY));
            String filter = arguments.getArgument(PARAM_FILTER, FILTER_ALL, FILTER_CYCLES);
            String title = arguments.getArgument(PARAM_TITLE);
            File output = new File(arguments.getArgument(PARAM_OUTPUT));

            GraphvizDotFileGenerator program = new GraphvizDotFileGenerator();
            String programName = program.getClass().getSimpleName();
            System.out.printf("Starting %s...%n", programName);

            Duration elapsed = program.execute(repository, filter, title, output);

            System.out.printf("Finished %s in %02d:%02d:%02d%n", programName, elapsed.toHours(), elapsed.toMinutesPart(), elapsed.toSecondsPart());
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    private final DeepCycleChecker cycleChecker = new DeepCycleChecker();

    private GraphvizDotFileGenerator() {}

    public Duration execute(File repository, String filter, String title, File output) throws Exception {
        final Instant start = Instant.now();

        final Set<Project> projects = Projects.scan(repository);

        final CouplingAnalyzer couplingAnalyzer = new CouplingAnalyzer();
        couplingAnalyzer.analyze(projects);

        final String graph;
        if (FILTER_ALL.equals(filter)) {
            graph = createGraphAll(projects, title);
        } else {
            graph = createGraphCycles(projects, title);
        }

        System.out.printf("Generating file \"%s\"...%n", output.getCanonicalPath());
        PrintWriter outputFile = new PrintWriter(output);

        System.out.println(graph);
        outputFile.println(graph);

        outputFile.flush();
        outputFile.close();
        System.out.printf("File \"%s\" successfully generated!%n", output.getCanonicalPath());

        final Instant end = Instant.now();
        return Duration.between(start, end);
    }

    private String createGraphAll(final Set<Project> projects, String graphTitle) {
        System.out.println("Generating full graph...");

        String nodeShape = "rectangle";

        StringBuilder sb = new StringBuilder("digraph Dependencies {\n\n");

        sb.append("    label=\"").append(graphTitle).append("\";\n");
        sb.append("    labelloc=\"t\";\n");
        sb.append("    node [shape=").append(nodeShape).append("];\n");
        sb.append("    rankdir=LR;\n\n");

        // setup colors
        for (Project p : projects) {
            String nodeColor = hasNoCycles("name", p, projects) ? "cyan" : "yellow";
            sb.append("    \"").append(p.getName()).append("\" [fillcolor=\"").append(nodeColor).append("\", style=\"filled\"];\n");
        }
        sb.append("\n");
        // create graph
        for (Project p : projects) {
            sb.append("    ").append("\"" + p.getName() + "\"").append(" -> ").append("{");
            for (Project d : p.getProjectDependencies()) {
                sb.append(" ").append(" \"" + d.getName() + "\"");
            }
            sb.append(" };\n");
        }
        sb.append("}");
        System.out.println("Generating full graph done!");
        return sb.toString();
    }

    private String createGraphCycles(final Set<Project> projects, String graphTitle) {
        System.out.println("Generating cycle graph...");

        String nodeShape = "rectangle";
        String nodeColor = "yellow";

        StringBuilder sb = new StringBuilder("digraph Dependencies {\n\n");

        sb.append("    label=\"").append(graphTitle).append("\";\n");
        sb.append("    labelloc=\"t\";\n");
        sb.append("    node [shape=").append(nodeShape).append("];\n");
        sb.append("    rankdir=LR;\n\n");

        // setup colors
        for (Project p : projects) {
            if (hasNoCycles("name", p, projects)) {
                continue;
            }
            sb.append("    \"").append(p.getName()).append("\" [fillcolor=\"").append(nodeColor)
                    .append("\", style=\"filled\"];\n");
        }
        sb.append("\n");
        // create graph
        for (Project p : projects) {
            if (hasNoCycles("name", p, projects)) {
                continue;
            }
            System.out.println("    Cycle: " + cycleChecker.getCycles(p));
            sb.append("    ").append("\"" + p.getName() + "\"").append(" -> ").append("{");
            for (Project d : p.getProjectDependencies()) {
                if (hasNoCycles("dependency", d, projects)) {
                    continue;
                }
                sb.append(" ").append(" \"" + d.getName() + "\"");
            }
            sb.append(" };\n");
        }
        sb.append("}");
        System.out.println("Generating cycle graph done!");
        return sb.toString();
    }

    private boolean hasNoCycles(String what, Project project, Set<Project> projects) {
        final String name = project.getName();
        Metrics stats = project.getMetrics();
        final int ac = stats.getInputDependencies();
        final int ec = stats.getOutputDependencies();
        if (ac == 0) {
            System.out.println("    ignoring " + what + " with afferent coupling " + ac + " \"" + name + "\"");
            return true;
        }
        if (ec == 0) {
            System.out.println("    ignoring " + what + " with efferent coupling " + ec + " \"" + name + "\"");
            return true;
        }
        if (cycleChecker.isAcyclic(project)) {
            System.out.println("    ignoring acyclic " + what + " \"" + name + "\"");
            return true;
        }
        return false;
    }
}
