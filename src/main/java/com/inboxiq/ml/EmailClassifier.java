package com.inboxiq.ml;

import com.inboxiq.model.Email;
import com.inboxiq.repository.EmailRepository;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Component;
import smile.classification.KNN;
import smile.nlp.normalizer.SimpleNormalizer;
import smile.nlp.stemmer.PorterStemmer;
import smile.nlp.dictionary.EnglishStopWords;
import smile.nlp.tokenizer.SimpleTokenizer;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class EmailClassifier {

    private KNN<double[]> model;
    private Map<String, Integer> vocabulary;
    private final List<String> categories = List.of("Job", "Promotion", "Finance");

    @Autowired
    private EmailRepository emailRepository;

    public EmailClassifier(EmailRepository emailRepository) {
        this.emailRepository = emailRepository;
        trainModelFromDatabase();
    }

    public void trainModelFromDatabase() {
        List<Email> trainingEmails = emailRepository.findByCategoryIsNotNull();

        List<EmailTrainingData> trainingData = new ArrayList<>();
        for (Email email : trainingEmails) {
            int labelIndex = categories.indexOf(email.getCategory());
            if (labelIndex != -1) {
                String combinedText = (email.getSubject() + " " + email.getBody()).trim();
                trainingData.add(new EmailTrainingData(combinedText, labelIndex));
            }
        }

        if (trainingData.isEmpty()) {
            System.out.println("⚠️ No training data found in DB. Model not trained.");
            return;
        }

        // Preprocess and tokenize
        List<String[]> tokenized = trainingData.stream()
                .map(data -> preprocess(data.getText()))
                .collect(Collectors.toList());

        // Build vocabulary
        vocabulary = buildVocabulary(tokenized);

        // Convert to vectors
        double[][] features = tokenized.stream()
                .map(this::vectorize)
                .toArray(double[][]::new);

        int[] labels = trainingData.stream()
                .mapToInt(EmailTrainingData::getLabel)
                .toArray();

        // Train model
        model = KNN.fit(features, labels, 3);
        System.out.println("✅ Model trained on " + trainingData.size() + " emails.");
    }

    public String classify(String text) {
        if (model == null || vocabulary == null) {
            return "Model not trained";
        }

        String[] tokens = preprocess(text);
        double[] featureVector = vectorize(tokens);

        if (Arrays.stream(featureVector).sum() == 0) {
            return "Unknown";
        }

        int predicted = model.predict(featureVector);
        return categories.get(predicted);
    }

    // Preprocessing text
    private String[] preprocess(String text) {
        var tokenizer = new SimpleTokenizer(false);
        var normalizer = SimpleNormalizer.getInstance();
        var stemmer = new PorterStemmer();

        String[] tokens = tokenizer.split(normalizer.normalize(text));
        List<String> processed = new ArrayList<>();

        for (String token : tokens) {
            token = token.toLowerCase();
            token = stemmer.stem(token);
            if (!EnglishStopWords.DEFAULT.contains(token)) {
                processed.add(token);
            }
        }

        return processed.toArray(new String[0]);
    }

    private Map<String, Integer> buildVocabulary(List<String[]> tokenizedTexts) {
        Map<String, Integer> vocab = new HashMap<>();
        int index = 0;
        for (String[] tokens : tokenizedTexts) {
            for (String token : tokens) {
                if (!vocab.containsKey(token)) {
                    vocab.put(token, index++);
                }
            }
        }
        return vocab;
    }

    private double[] vectorize(String[] tokens) {
        double[] vector = new double[vocabulary.size()];
        for (String token : tokens) {
            Integer idx = vocabulary.get(token);
            if (idx != null) {
                vector[idx]++;
            }
        }
        return vector;
    }

    // Inner DTO for training data
    private static class EmailTrainingData {
        private final String text;
        private final int label;

        public EmailTrainingData(String text, int label) {
            this.text = text;
            this.label = label;
        }

        public String getText() {
            return text;
        }

        public int getLabel() {
            return label;
        }
    }
}
