
package com.inboxiq.service;

import com.inboxiq.ml.EmailClassifier;
import com.inboxiq.model.Email;
import com.inboxiq.repository.EmailRepository;
import jakarta.mail.*;
        import jakarta.mail.internet.MimeMultipart;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Properties;

@Service
public class EmailFetcherService {

    @Autowired
    private EmailRepository emailRepository;

    @Autowired
    private EmailClassifier classifier;

    public void fetchUnreadEmails() {
        String host = "imap.gmail.com";
        String username = "illimitedingeniouscoders@gmail.com";
        String password = "myxl afor eyih wkub";

        Properties props = new Properties();
        props.put("mail.store.protocol", "imaps");
        props.put("mail.imap.host", host);
        props.put("mail.imap.port", "993");
        props.put("mail.imap.ssl.enable", "true");

        try {
            Session session = Session.getInstance(props);
            Store store = session.getStore("imaps");
            store.connect(host, username, password);

            Folder inbox = store.getFolder("INBOX");
            inbox.open(Folder.READ_WRITE);

            int total = inbox.getMessageCount();
            int start = Math.max(1, total - 9); // Get last 10 messages

            Message[] messages = inbox.getMessages(start, total);

            System.out.println("📩 Processing " + messages.length + " recent emails...");

            for (Message message : messages) {
                String subject = message.getSubject();
                String sender = message.getFrom()[0].toString();
                String body = extractTextFromMessage(message);

                String fullText = subject + " " + body;
                String predictedCategory = classifier.classify(fullText);

//                Email e = new Email();
//                e.setSubject(subject);
//                e.setBody(body);
//                e.setSender(sender);
//                e.setReceivedAt(LocalDateTime.now());
//                e.setRead(false);
//                e.setCategory(predictedCategory);
//
//                emailRepository.save(e);
//                message.setFlag(Flags.Flag.SEEN, true);

                System.out.println("✅ Email saved: " + subject + " | Category: " + predictedCategory);
            }

            inbox.close(false);
            store.close();

        } catch (Exception e) {
            System.err.println("❌ Error fetching emails: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private String extractTextFromMessage(Message message) throws MessagingException, IOException {
        Object content = message.getContent();
        if (content instanceof String) {
            return (String) content;
        } else if (content instanceof MimeMultipart) {
            MimeMultipart multipart = (MimeMultipart) content;
            StringBuilder result = new StringBuilder();
            for (int i = 0; i < multipart.getCount(); i++) {
                BodyPart part = multipart.getBodyPart(i);
                if (part.isMimeType("text/plain")) {
                    result.append(part.getContent());
                }
            }
            return result.toString();
        }
        return "";
    }
}
