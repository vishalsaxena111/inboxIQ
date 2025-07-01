package com.inboxiq.nlp;

import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;

import java.io.InputStream;

public class TextProcessor {

    private SentenceModel sentenceModel;
    private TokenizerModel tokenizerModel;

    public TextProcessor() throws Exception {
        try (InputStream sentModel = getClass().getResourceAsStream("/model/en-sent.bin");
             InputStream tokenModel = getClass().getResourceAsStream("/model/en-token.bin")) {

            sentenceModel = new SentenceModel(sentModel);
            tokenizerModel = new TokenizerModel(tokenModel);
        }
    }

    public String[] detectSentences(String text) {
        SentenceDetectorME detector = new SentenceDetectorME(sentenceModel);
        return detector.sentDetect(text);
    }

    public String[] tokenize(String sentence) {
        TokenizerME tokenizer = new TokenizerME(tokenizerModel);
        return tokenizer.tokenize(sentence);
    }
}
