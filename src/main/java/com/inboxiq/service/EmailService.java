package com.inboxiq.service;

import com.inboxiq.model.Email;
import com.inboxiq.nlp.TextProcessor;
import com.inboxiq.repository.EmailRepository;
import opennlp.tools.tokenize.SimpleTokenizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Service
public class EmailService {

    @Autowired
    private EmailRepository emailRepository;

//    public Email saveEmail(Email email) {
//        // Classify before saving
//        String category = classifyAndSave(email);
//        email.setCategory(category);
//        email.setClassifiedAt(LocalDateTime .now());
//
//        return emailRepository.save(email);
//    }

//    private String classifyEmail(String subject, String body) {
//        String content = subject + " " + body;
//
//        // Tokenize the content
//        SimpleTokenizer tokenizer = SimpleTokenizer.INSTANCE;
//        String[] tokens = tokenizer.tokenize(content.toLowerCase());
//
//        List<String> jobKeywords = List.of("job", "hiring", "career", "interview", "vacancy");
//        List<String> spamKeywords = List.of("win", "free", "offer", "buy now", "click here");
//        List<String> importantKeywords = List.of("meeting", "invoice", "project", "deadline");
//
//        int jobScore = 0, spamScore = 0, importantScore = 0;
//
//        for (String token : tokens) {
//            if (jobKeywords.contains(token)) jobScore++;
//            if (spamKeywords.contains(token)) spamScore++;
//            if (importantKeywords.contains(token)) importantScore++;
//        }
//
//        if (jobScore > spamScore && jobScore > importantScore) return "Job";
//        if (spamScore > jobScore && spamScore > importantScore) return "Spam";
//        if (importantScore > jobScore && importantScore > spamScore) return "Important";
//
//        return "General";
//    }



    public List<Email> getAllEmails() {
        return emailRepository.findAll();
    }

    public List<Email> getEmailsByCategory(String category) {
        return emailRepository.findByCategory(category);
    }

    public void classifyAndSave(Email email) throws Exception {
        TextProcessor  processor = new TextProcessor();

        String[] sentences = processor.detectSentences(email.getBody());
        StringBuilder allText = new StringBuilder();
        for (String s : sentences) {
            allText.append(s).append(" ");
        }

        String[] tokens = processor.tokenize(allText.toString().toLowerCase());

        String category = detectCategory(tokens);
        email.setCategory(category);
        email.setClassifiedAt(LocalDateTime.now());
        emailRepository.save(email);
    }

    private String detectCategory(String[] tokens) {
        List<String> tokenList = Arrays.asList(tokens);

        if (tokenList.contains("interview") || tokenList.contains("hiring") || tokenList.contains("job")) {
            return "Job Opportunity";
        } else if (tokenList.contains("win") || tokenList.contains("lottery") || tokenList.contains("prize")) {
            return "Spam";
        } else if (tokenList.contains("urgent") || tokenList.contains("asap") || tokenList.contains("important")) {
            return "Important";
        } else {
            return "General";
        }
        }
}
