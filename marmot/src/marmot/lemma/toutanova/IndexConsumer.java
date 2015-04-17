package marmot.lemma.toutanova;

import java.io.Serializable;

import marmot.core.Feature;
import marmot.util.DynamicWeights;
import marmot.util.Encoder;
import marmot.util.SymbolTable;

public abstract class IndexConsumer implements Serializable {

	private static final long serialVersionUID = 1L;
	private SymbolTable<Feature> feature_map_;
	private int num_pos_bits_;
	protected DynamicWeights weights_;

	public abstract void consume(int index);

	public IndexConsumer(DynamicWeights weights, SymbolTable<Feature> feature_map, int num_pos_bits) {
		setWeights(weights);
		feature_map_ = feature_map;
		num_pos_bits_ = num_pos_bits;
	}
	
	public void consume(ToutanovaInstance instance, Encoder encoder) {
		int index = feature_map_.toIndex(encoder.getFeature(), -1, getInsert());
		consume(index);
		
		if (num_pos_bits_ >= 0 && instance.getPosTagIndex() >= 0) {
			encoder.append(instance.getPosTagIndex(), num_pos_bits_);
			index = feature_map_.toIndex(encoder.getFeature(), -1, getInsert());
			consume(index);
		}
	}
	
	protected abstract boolean getInsert();

	public void setWeights(DynamicWeights weights) {
		weights_ = weights;
	}
	
}
