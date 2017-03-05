package com.loovjo.jknut.command;

import java.util.Optional;
import java.util.stream.Collectors;

import com.loovjo.jknut.GameLevel;
import com.loovjo.jknut.JKnutStats;

public class JKnutStatisticsCommand implements JKnutCommand {

	@Override
	public Optional<String> run(String a, GameLevel level) {
		String[] args = a.split(" ");

		if (level.owner.isPresent()) {
			JKnutStats stats = level.owner.get().stats;

			if (args[0].equals("reset") && args.length == 1) {
				level.owner.get().stats = new JKnutStats();
				return Optional.of("Statistics reset");
			}
			if (args[0].equals("reset") && args.length == 2) {
				stats.set(args[1], 0);
			}
			if (args[0].equals("set") && args.length == 3) {
				try {
					stats.set(args[1], Double.parseDouble(args[2]));
					return Optional.of(args[1] + " set to " + args[2]);
				} catch (NumberFormatException e) {
					return Optional.of("Could not understand the number " + args[2]);
				}
			}
			if (args[0].equals("set") && args.length == 3) {
				try {
					stats.add(args[1], Double.parseDouble(args[2]));
					return Optional.of(args[1] + " set to " + args[2]);
				} catch (NumberFormatException e) {
					return Optional.of("Could not understand the number " + args[2]);
				}
			}
			if (args[0].equals("inc") && args.length == 2) {
				stats.inc(args[1]);
			}
			if (args[0].equals("dec") && args.length == 2) {
				stats.add(args[1], -1);
			}
			if (args[0].equals("get") && args.length == 2) {
				Optional<Double> val = stats.get(args[1]);
				if (val.isPresent())
					return Optional.of(args[1] + ": " + val.get());
				else
					return Optional.of(args[1] + " has no value");
			}
			if (args[0].equals("stop") && args.length == 2) {
				if (stats.isStopped(args[1])) {
					return Optional.of(args[1] + " is already stopped");
				}
				stats.stop(args[1]);
				return Optional.of("Stopped " + args[1]);
			}
			if (args[0].equals("resume") && args.length == 2) {
				if (!stats.isStopped(args[1])) {
					return Optional.of(args[1] + " isn't stopped");
				}
				stats.resume(args[1]);
				return Optional.of("Resumed " + args[1]);
			}
			if (args[0].equals("list") && args.length == 1) {
				String res = stats.getStats().stream().collect(Collectors.joining(" "));
				if (res.length() == 0)
					return Optional.of("No values to list");
				return Optional.of(res);
			}
			if (args[0].equals("list") && args[1].equals("stopped") && args.length == 2) {
				String res = stats.getStats().stream().filter(s -> stats.isStopped(s)).collect(Collectors.joining(" "));
				if (res.length() == 0)
					return Optional.of("No values stopped");
				return Optional.of(res);
			}
			if (args[0].equals("list") && args[1].equals("not") && args[2].equals("stopped") && args.length == 3) {
				String res = stats.getStats().stream().filter(s -> !stats.isStopped(s))
						.collect(Collectors.joining(" "));
				if (res.length() == 0)
					return Optional.of("All values stopped");
				return Optional.of(res);
			}
			return Optional.of("Command not understood");
		}
		return Optional.of("No scene found");
	}

}
