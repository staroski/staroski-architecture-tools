package br.com.staroski.tools.analysis;

import java.io.File;

/**
 * This class represents a <b><a href="https://maven.apache.org/download.cgi">Maven</a> project</b>.<br>
 * Instances of this class are created through the static method {@link Projects#get(File)} passing the name directory as parameter.<br>
 * You can also get a list of projects inside a directory tree using the static method {@link Projects#scan(File)}.
 *
 * @author Staroski, Ricardo Artur
 */
final class MavenProject extends AbstractProject {

    public static final String POM_FILE = "pom.xml";

    /**
     * Creates a new {@link MavenProject} for the specified folder.
     * 
     * @param projectFolder THe project's folder.
     */
    MavenProject(File projectFolder) {
        super(projectFolder);
        // TODO
        throw new UnsupportedOperationException("Maven projects are not yet supported!");
    }
}
