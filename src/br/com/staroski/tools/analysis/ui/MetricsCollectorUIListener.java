package br.com.staroski.tools.analysis.ui;

import java.util.Set;

import br.com.staroski.tools.analysis.Project;

/**
 * Listener for the {@link MetricsCollectorUI} class.
 * 
 * @author Staroski, Ricardo Artur
 */
interface MetricsCollectorUIListener {

    void onProjectsFounds(Set<Project> projects);
}
