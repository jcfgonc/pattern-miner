package jcfgonc.patternminer;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Locale;

import org.apache.commons.math3.random.RandomGenerator;
import org.moeaframework.core.Variable;

import com.githhub.aaronbembenek.querykb.KnowledgeBase;

import graph.StringGraph;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import jcfgonc.patternminer.moea.MutationCode;
import structures.ListOfSet;

public class PatternChromosome implements Variable {
	public StringGraph pattern;
	public double countingTime;
	public double matches;
	public double relationStd;
	public ListOfSet<String> components;
	public Object2IntOpenHashMap<String> relations;
	public int cycles;
	public StringGraph patternWithVars;
	public HashMap<String, String> conceptVariableMap;

	private static final long serialVersionUID = 1449562469642894508L;
	public static StringGraph kbGraph = null;
	public static RandomGenerator random = null;
	public static KnowledgeBase kb = null;

	public void resetInternals() {
		this.countingTime = 0;
		this.matches = 0;
		this.relationStd = 0;
		this.components = null;
		this.relations = null;
		this.cycles = 0;
		this.patternWithVars = null;
		this.conceptVariableMap = null;
	}

	public PatternChromosome(StringGraph pattern) {
		super();
		resetInternals();
		this.pattern = pattern;
	}

	public PatternChromosome(PatternChromosome other) {
		super();
		StringGraph otherPattern = other.pattern;
		this.pattern = new StringGraph(otherPattern);
		this.countingTime = other.countingTime;
		this.matches = other.matches;
		this.relationStd = other.relationStd;
		this.components = new ListOfSet<String>(other.components);
		this.relations = new Object2IntOpenHashMap<String>(other.relations);
		this.cycles = other.cycles;
		this.patternWithVars = new StringGraph(other.patternWithVars);
		this.conceptVariableMap = new HashMap<>(other.conceptVariableMap);
	}

	@Override
	public PatternChromosome copy() {
		return new PatternChromosome(this);
	}

	@Override
	public void randomize() {
		if (!pattern.isEmpty()) { // just to optimize memory
			pattern = new StringGraph();
		}
		this.pattern = PatternFinderUtils.initializePattern(kbGraph, pattern, random);
		PatternFinderUtils.removeAdditionalComponents(this, null); // check for components (old repairing operator)
//		System.err.println("called randomize()");
	}

	@Override
	public String toString() {
		String hash = new BigInteger(patternWithVars.accurateHashCode()).toString(16);
		String cvMap = conceptVariableMap.toString().replaceAll("[ {}]+", "");
		return String.format(Locale.ROOT, "%f", countingTime) + //
				"\t" + relations.size() + //
				"\t" + String.format(Locale.ROOT, "%.3f", relationStd) + //
				"\t" + cycles + //
				// "\t" + (components == null ? null : components.size()) + //
				"\t" + pattern.numberOfEdges() + //
				"\t" + pattern.numberOfVertices() + //
				"\t" + String.format(Locale.ROOT, "%f", matches) + //
				"\t" + patternWithVars + //
				"\t" + pattern + //
				"\t" + cvMap + //
				"\t" + hash;
	}

	public void mutate() {
		MutationCode.mutation(kbGraph, random, pattern, false); // do the mutation IN-PLACE
		PatternFinderUtils.removeAdditionalComponents(this, null); // check for components (old repairing operator)
	}

}
