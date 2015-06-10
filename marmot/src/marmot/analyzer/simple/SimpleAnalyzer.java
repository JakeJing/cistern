package marmot.analyzer.simple;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Map;

import marmot.analyzer.Analyzer;
import marmot.analyzer.AnalyzerInstance;
import marmot.analyzer.AnalyzerReading;
import marmot.analyzer.AnalyzerTag;
import marmot.util.Numerics;

public class SimpleAnalyzer implements Analyzer {

	private SimpleAnalyzerModel model_;
	private double threshold_; 
	
	public SimpleAnalyzer(SimpleAnalyzerModel model, double threshold) {
		model_ = model;
		threshold_ = threshold;
	}

	@Override
	public Collection<AnalyzerReading> analyze(AnalyzerInstance instance) {
		SimpleAnalyzerInstance simple_instance = model_.getInstance(instance);
		
		double[] scores = new double[model_.getNumTags()];
		model_.score(simple_instance, scores);
		
		
		double max_prob = Double.NEGATIVE_INFINITY;
		AnalyzerTag max_tag = null;
		
		Collection<AnalyzerReading> readings = new LinkedList<>();
		for (Map.Entry<AnalyzerTag, Integer> entry : model_.getTagTable().entrySet()) {
			int tag_index = entry.getValue();
			double score = scores[tag_index];
			double prob = Math.exp(score - Numerics.sumLogProb(score, 0)); 
			
			AnalyzerTag tag = entry.getKey();
			
			if (prob > threshold_) {
				readings.add(new AnalyzerReading(tag, null));
			}
			
			if (prob > max_prob) {
				max_prob = prob;
				max_tag = tag;
			}
			
		}
		
		if (readings.isEmpty()) {
			readings.add(new AnalyzerReading(max_tag, null));
		}
		
		
		//Logger logger = Logger.getLogger(getClass().getName());
		//logger.info(String.format("%s : %s [%s]", instance.getForm(), readings, Arrays.toString(scores)));
		
		return readings;
	}

}