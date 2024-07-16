package br.com.staroski.tools.analysis;

import java.io.File;
import java.util.HashSet;
import java.util.Set;
import java.util.Stack;

public final class PrintProjectCycles {

	public static void main(String[] args) {
		try {
			File repository = new File("path");

			String projectName = "name";

			Project project = Projects.get(new File(repository, projectName));

			PrintProjectCycles analyzer = new PrintProjectCycles();
			analyzer.analyze(project);
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}

	public void analyze(Project project) {
		Set<Project> visited = new HashSet<>();
		Stack<Project> stack = new Stack<>();
		findCycles(project, project, visited, stack, new HashSet<>());
	}

	private void findCycles(Project initial, Project current, Set<Project> visited, Stack<Project> stack,
			Set<Project> recStack) {
		if (recStack.contains(current)) {
			if (current.equals(initial)) {
				printCycle(stack, initial);
			}
			return;
		}

		if (visited.contains(current)) {
			return;
		}

		visited.add(current);
		recStack.add(current);
		stack.push(current);

		for (Project dependency : current.getProjectDependencies()) {
			findCycles(initial, dependency, visited, stack, recStack);
		}

		stack.pop();
		recStack.remove(current);
	}

	private void printCycle(Stack<Project> stack, Project initial) {
		StringBuilder cycle = new StringBuilder();
		boolean cycleStarted = false;

		for (Project p : stack) {
			if (p.equals(initial)) {
				cycleStarted = true;
			}
			if (cycleStarted) {
				cycle.append(p.getName()).append(" -> ");
			}
		}

		cycle.append(initial.getName());
		System.out.println(cycle);
	}
}