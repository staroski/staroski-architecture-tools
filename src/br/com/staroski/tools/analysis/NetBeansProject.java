package br.com.staroski.tools.analysis;

import java.io.File;

/**
 * This class represents a <b><a href="https://netbeans.apache.org/front/main/download/">NetBeans</a> project</b>.<br>
 * Instances of this class are created through the static method {@link Projects#get(File)} passing the name directory as parameter.<br>
 * You can also get a list of projects inside a directory tree using the static method {@link Projects#scan(File)}.
 *
 * @author Staroski, Ricardo Artur
 */
final class NetBeansProject extends AbstractProject {

    public static final String NBPROJECT_FOLDER = "nbproject";

    /**
     * Creates a new {@link NetBeansProject} for the specified folder.
     * 
     * @param projectFolder THe project's folder.
     */
    NetBeansProject(File projectFolder) {
        super(projectFolder);
        // TODO
        throw new UnsupportedOperationException("NetBeans projects are not yet supported!");
    }
}
