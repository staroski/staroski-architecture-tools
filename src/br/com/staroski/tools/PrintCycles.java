package br.com.staroski.tools;

import java.io.File;

import br.com.staroski.tools.analysis.Project;
import br.com.staroski.tools.analysis.Projects;
import br.com.staroski.tools.analysis.analyzers.ShallowCycleAnalyzer;

public final class PrintCycles {

	public static void main(String[] args) {
		try {
			File repository = new File("repository path");

			String projectName = "project name";

			Project project = Projects.get(new File(repository, projectName));

			ShallowCycleAnalyzer cycleAnalyzer = new ShallowCycleAnalyzer();
			cycleAnalyzer.analyze(project);
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}
}