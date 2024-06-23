package br.com.staroski.tools.analysis.generators;

import java.io.File;
import java.time.Duration;

/**
 * Abstract type for classes that traverse a directory tree and generate something.
 *
 * @author Staroski, Ricardo Artur
 */
public interface Generator {

    /**
     * Executes this {@link Generator} on the specified directory tree.
     *
     * @param directory The directory tree to be traversed.
     * @return The duration of the execution.
     * @throws Exception If something wrong occurs.
     */
    public Duration execute(File directory) throws Exception;
}
