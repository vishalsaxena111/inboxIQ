package com.inboxiq.ml;

import smile.classification.KNN;
import smile.nlp.normalizer.SimpleNormalizer;
import smile.nlp.stemmer.PorterStemmer;
import smile.nlp.dictionary.EnglishStopWords;
import smile.nlp.tokenizer.SimpleTokenizer;

import java.util.*;
import java.util.stream.Collectors;

public class EmailClassifier {

    private final KNN<double[]> model;
    private final Map<String, Integer> vocabulary;
    private final String[] categories = {"Job", "Promotion", "Finance"};

    public EmailClassifier() {
        List<String> trainingTexts = List.of(
                "Your interview schedule is confirmed",
                "50% off on all electronics",
                "Your bank statement is available",
                "New job opportunity from LinkedIn",
                "Mega sale this weekend only",
                "Salary credited to your account"
        );

        int[] labels = {0, 1, 2, 0, 1, 2}; // Job, Promotion, Finance

        // Preprocessing: tokenize and normalize
        List<String[]> tokenized = trainingTexts.stream()
                .map(this::preprocess)
                .collect(Collectors.toList());

        // Build vocabulary from training data
        vocabulary = buildVocabulary(tokenized);

        // Convert text to numerical feature vectors
        double[][] features = tokenized.stream()
                .map(this::vectorize)
                .toArray(double[][]::new);

        // Train KNN model (k=3)
        model = KNN.fit(features, labels, 3);
    }

    public String classify(String text) {
        String[] tokens = preprocess(text);
        double[] featureVector = vectorize(tokens);
        if (Arrays.stream(featureVector).sum() == 0) {
            return "Unknown";
        }
        int predicted = model.predict(featureVector);
        return categories[predicted];
    }

    // -----------------------
    // Helper Methods
    // -----------------------

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
}
