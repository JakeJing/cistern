package marmot.lemma;

import java.util.List;
import java.util.Map;

import marmot.lemma.SimpleLemmatizerTrainer.Options;

public class SimpleLemmatizer implements LemmatizerGenerator {

	private static final String SEPARATOR = "%|%|%";
	private Map<String, List<String>> map_;
	private Options options_;

	public SimpleLemmatizer(Options options, Map<String, List<String>> map) {
		map_ = map;
		options_ = options;
	}

	public static String toKey(Instance instance) {
		String pos_tag = instance.getPosTag();
		String form = instance.getForm();
		if (pos_tag != null) {
			return String.format("%s%s%s", form, SEPARATOR, pos_tag);
		}
		return null;
	}

	@Override
	public String lemmatize(Instance instance) {
		List<String> lemmas = null;
		String key = null;

		if (options_.getUsePos()) {
			key = toKey(instance);
			if (key != null) {
				lemmas = map_.get(key);
				if (lemmas != null && (!options_.getAbstainIfAmbigous() || lemmas.size() == 1 )) {
					return lemmas.get(0);
				}
			}
		}

		if (options_.getUseBackup()) {
			key = toSimpleKey(instance);
			if (key != null) {
				lemmas = map_.get(key);
				if (lemmas != null && (!options_.getAbstainIfAmbigous() || lemmas.size() == 1 )) {
					return lemmas.get(0);
				}
			}
		}

		if (options_.getHandleUnseen()) {
			return instance.getForm();
		}

		return null;
	}

	public static String toSimpleKey(Instance instance) {
		return instance.getForm();
	}

	@Override
	public List<String> getCandidates(Instance instance) {
		String key = toSimpleKey(instance);
		List<String> lemmas;
		
		if (key != null) {
				lemmas = map_.get(key);
				if (lemmas != null && (!options_.getAbstainIfAmbigous() || lemmas.size() == 1 )) {
					return lemmas;
				}
			
		}
		
		return null;
	}

}
