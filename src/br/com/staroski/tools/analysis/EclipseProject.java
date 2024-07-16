package br.com.staroski.tools.analysis;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Optional;
import java.util.Set;

/**
 * This class represents a <b><a href="https://www.eclipse.org/downloads/">eclipse</a> project</b>.<br>
 * Instances of this class are created through the static method {@link Projects#get(File)} passing the name directory as parameter.<br>
 * You can also get a list of projects inside a directory tree using the static method {@link Projects#scan(File)}.
 *
 * @author Staroski, Ricardo Artur
 */
final class EclipseProject extends AbstractProject {

    public static final String PROJECT_FILE = ".project";
    public static final String CLASSPATH_FILE = ".classpath";

    /**
     * Creates a new {@link EclipseProject} for the specified folder.
     * 
     * @param projectFolder THe project's folder.
     */
    EclipseProject(File projectFolder) {
        super(projectFolder);
        File classpathFile = projectFolder.listFiles((folder, file) -> file.equals(CLASSPATH_FILE))[0];
        readClasspathFile(projectFolder, classpathFile);
    }

    private void readClasspathFile(File projectFolder, File classpathFile) {
        try (BufferedReader reader = new BufferedReader(new FileReader(classpathFile))) {
            final Set<Dependency> allDependencies = getAllDependencies();
            final Set<Dependency> srcDependencies = getSrcDependencies();
            final Set<Dependency> libDependencies = getLibDependencies();
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.contains("path=\"src\"")) {
                    continue;
                } else {
                    if (line.contains("kind=\"lib\"")) {
                        Optional<Dependency> result = readDependency(projectFolder, line, Dependency.KIND_LIB);
                        if (result.isPresent()) {
                            Dependency dependency = result.get();
                            libDependencies.add(dependency);
                            allDependencies.add(dependency);
                        }
                    } else if (line.contains("kind=\"src\"")) {
                        Optional<Dependency> result = readDependency(projectFolder, line, Dependency.KIND_SRC);
                        if (result.isPresent()) {
                            Dependency dependency = result.get();
                            srcDependencies.add(dependency);
                            allDependencies.add(dependency);
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Optional<Dependency> readDependency(File projectFolder, String line, String kind) throws IOException {
        final File repository = projectFolder.getParentFile();
        Dependency value = null;
        int begin = line.indexOf("path=\"") + 6;
        int end = line.indexOf("\"", begin);
        String path = line.substring(begin, end);
        try {
            if (path.startsWith("/")) {
                path = repository.getCanonicalPath() + path;
            } else {
                path = projectFolder.getCanonicalPath() + "/" + path;
            }
            path = path.replace('\\', File.separatorChar);
            path = path.replace('/', File.separatorChar);
            File artifact = new File(path);
            value = new Dependency(kind, artifact);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Optional.ofNullable(value);
    }
}
