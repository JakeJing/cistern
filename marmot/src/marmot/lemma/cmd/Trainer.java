package marmot.lemma.cmd;

import java.util.List;
import java.util.logging.Logger;

import marmot.lemma.Instance;
import marmot.lemma.Lemmatizer;
import marmot.lemma.LemmatizerTrainer;
import marmot.lemma.Options;
import marmot.lemma.Result;
import marmot.morph.io.SentenceReader;
import marmot.util.FileUtils;

public class Trainer {
	
	public static void main(String[] args) {
		
		String model_type = args[0];
		String options_string = args[1];
		String output_file = args[2];
		String train_file = args[3];
		String test_file = args[4];
		String test_file2 = args[5];
		
		Lemmatizer lemmatizer = train(model_type, options_string, train_file);
		
		FileUtils.saveToFile(lemmatizer, output_file);
		
		test(lemmatizer, test_file);
		test(lemmatizer, test_file2);
	}
	
	public static Lemmatizer train(String model_type, String options_string,
			String train_file) {
		
		LemmatizerTrainer trainer;
		try {
			Class<?> trainer_class = Class.forName(model_type);
			trainer = (LemmatizerTrainer) trainer_class.newInstance();
		} catch (Exception e) {
			throw new RuntimeException(e); 
		}
		
		Options options = trainer.getOptions();	
		options.readArguments(options_string);
		
		Logger logger = Logger.getLogger(Trainer.class.getName());
		logger.info(options.report());

		List<Instance> training_instances = Instance.getInstances(new SentenceReader(train_file), options.getLimit());
		Lemmatizer lemmatizer = trainer.train(training_instances, null);
		
		return lemmatizer;
	}

	public static void test(Lemmatizer lemmatizer, String test_file) {
		Result.logTest(lemmatizer, test_file, 50);
	}

}
