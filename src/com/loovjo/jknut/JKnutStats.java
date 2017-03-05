package com.loovjo.jknut;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

public class JKnutStats {

	private HashMap<String, Double> stats = new HashMap<String, Double>();

	private ArrayList<String> stopped = new ArrayList<String>();
	
	public void add(String stat, double amount) {
		if (stopped.contains(stat))
			return;
		if (stats.containsKey(stat)) {
			stats.put(stat, stats.get(stat) + amount);
		} else {
			stats.put(stat, amount);
		}
	}

	public void inc(String stat) {
		if (stopped.contains(stat))
			return;
		if (stats.containsKey(stat)) {
			stats.put(stat, stats.get(stat) + 1);
		} else {
			stats.put(stat, 1.0);
		}
	}

	public void set(String stat, double amount) {
		stats.put(stat, amount);
	}
	
	public void stop(String stat) {
		stopped.add(stat);
	}
	
	public boolean isStopped(String stat) {
		return stopped.contains(stat);
	}
	
	public void resume(String stat) {
		stopped.remove(stat);
	}

	public Optional<Double> get(String stat) {
		return stats.containsKey(stat) ? Optional.of(stats.get(stat)) : Optional.empty();
	}

	public List<String> getStats() {
		return new ArrayList<String>(stats.keySet());
	}
}
