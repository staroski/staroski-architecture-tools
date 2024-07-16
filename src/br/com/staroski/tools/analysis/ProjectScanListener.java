package br.com.staroski.tools.analysis;

import java.io.File;

/**
 * Listener interface for the {@link Projects#scan(java.io.File, ProjectScanListener) Projects.scan} method.
 * 
 * @author Staroski, Ricardo Artur
 */
public interface ProjectScanListener {

    public void onProjectScanStarted(File directory);

    public void onDirectoryEnter(File directory);

    public void onProjectFound(Project project);

    public void onDirectoryExit(File directory);

    public void onProjectScanFinished(File directory);
}
