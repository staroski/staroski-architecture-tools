package br.com.staroski.tools.analysis;

import java.io.File;
import java.util.Set;

/**
 * This class represents a name.<br>
 * Instances of this class are created through the static method {@link Projects#get(File)} passing the name directory as parameter.<br>
 * You can also get a list of projects inside a directory tree using the static method {@link Projects#scan(File)}.
 *
 * @author Staroski, Ricardo Artur
 */
public interface Project extends Comparable<Project> {

    /**
     * Get all the dependencies of this {@link Project}.
     */
    Set<Dependency> getAllDependencies();

    /**
     * Get the directory of this {@link Project}.
     */
    File getDirectory();

    /**
     * Get all the dependencies of type "lib" of this {@link Project}.
     */
    Set<Dependency> getLibDependencies();

    /**
     * Gets the {@link Metrics} of this {@link Project}.
     */
    Metrics getMetrics();

    /**
     * Get the name of this {@link Project}.
     */
    String getName();

    /**
     * Get all other {@link Project}s that are dependencies of this one.
     */
    Set<Project> getProjectDependencies();

    /**
     * Get all the dependencies of type "src" of this {@link Project}.
     */
    Set<Dependency> getSrcDependencies();

}