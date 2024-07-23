package br.com.staroski.tools.analysis.generators;

import java.io.File;
import java.time.Duration;
import java.time.Instant;

import br.com.staroski.tools.analysis.Project;
import br.com.staroski.tools.analysis.analyzers.MetricsAnalyzer;
import br.com.staroski.utils.Arguments;

/**
 * Traverses a directory tree and generates content for a CSV file with all the metrics computed for each {@link Project} found.
 *
 * @author Staroski, Ricardo Artur
 */
public final class MetricsGenerator {

    private static final String PARAM_REPOSITORY = "-r";

    public static void main(String[] args) {
        try {
            Arguments arguments = new Arguments(args, PARAM_REPOSITORY);

            File repository = new File(arguments.getArgument(PARAM_REPOSITORY));

            MetricsGenerator program = new MetricsGenerator();
            String programName = program.getClass().getSimpleName();
            System.out.printf("Starting %s...%n", programName);

            Duration elapsed = program.execute(repository);

            System.out.printf("Finished %s in %02d:%02d:%02d%n", programName, elapsed.toHours(), elapsed.toMinutesPart(), elapsed.toSecondsPart());
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    private MetricsGenerator() {}

    public Duration execute(File repository) throws Exception {
        final Instant start = Instant.now();

        final MetricsAnalyzer metricsAnalyzer = new MetricsAnalyzer();
        metricsAnalyzer.analyze(repository);

        final Instant end = Instant.now();
        return Duration.between(start, end);
    }
}
