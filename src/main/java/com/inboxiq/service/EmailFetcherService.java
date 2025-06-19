//package com.inboxiq.service;
//
//import jakarta.mail.*;
//import jakarta.mail.Flags.Flag;
//import jakarta.mail.search.FlagTerm;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//@Service
//public class EmailFetcherService {
//
//    @Autowired
//    private Session mailSession;
//
//    public void fetchUnreadEmails(String email, String password) {
//        try {
//            Store store = mailSession.getStore("imaps");
//            store.connect("imap.gmail.com", email, password);
//
//            Folder inbox = store.getFolder("INBOX");
//            inbox.open(Folder.READ_WRITE);
//
//            Message[] messages = inbox.search(new FlagTerm(new Flags(Flag.SEEN), false));
//
//            for (Message message : messages) {
//                String subject = message.getSubject();
//                Object content = message.getContent();
//                String body = (content instanceof String) ? (String) content : content.toString();
//
//                // 💡 TODO: Save to DB via EmailService
//                System.out.println("Subject: " + subject);
//                System.out.println("Body: " + body);
//
//                message.setFlag(Flag.SEEN, true); // mark as read
//            }
//
//            inbox.close(false);
//            store.close();
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//}


package com.inboxiq.service;

import com.inboxiq.model.Email;
import com.inboxiq.repository.EmailRepository;
import jakarta.mail.*;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.search.FlagTerm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Properties;

@Service
public class EmailFetcherService {

    @Autowired
    private EmailRepository emailRepository;


    public void fetchUnreadEmails() {
        String host = "imap.gmail.com";
        String username = "illimitedingeniouscoders@gmail.com"; // 🔁 Replace with your Gmail address
        String password = "myxl afor eyih wkub";  // 🔁 Use 16-char App Password (no spaces)

        Properties props = new Properties();
        props.put("mail.store.protocol", "imaps");
        props.put("mail.imap.host", host);
        props.put("mail.imap.port", "993");
        props.put("mail.imap.ssl.enable", "true");
        props.put("mail.debug", "false");

        try {
            Session session = Session.getInstance(props);
            Store store = session.getStore("imaps");
            store.connect(host, username, password);

            Folder inbox = store.getFolder("INBOX");
            inbox.open(Folder.READ_WRITE);

          //  Message[] messages = inbox.search(new FlagTerm(new Flags(Flags.Flag.SEEN), false)); // Unread mails

            int total = inbox.getMessageCount();
            int start = Math.max(1, total - 10 + 1); // last 10 mails

            Message[] messages = inbox.getMessages(start, total);



            System.out.println("Unread Email Count: " + messages.length);

            for (Message message : messages) {
//                System.out.println("Subject: " + message.getSubject());
//                System.out.println("From: " + message.getFrom()[0]);
//                System.out.println("Received Date: " + message.getReceivedDate());
//                System.out.println("-----------------------------------");

               // message.setFlag(Flags.Flag.SEEN, true);

                String subject = message.getSubject();
                String sender = message.getFrom()[0].toString();
                Object content = message.getContent();
                String body = (content instanceof String) ? (String) content : content.toString();

                Email e = new Email();
                e.setSubject(subject);
                e.setBody(body);
                e.setSender(sender);
                e.setReceivedAt(LocalDateTime.now());
                e.setRead(false); // unread on fetch
                e.setCategory("Uncategorized");

                emailRepository.save(e);

                message.setFlag(Flags.Flag.SEEN, true);

                System.out.println("-----------------------------------Read");

            }

            inbox.close(false);
            store.close();

        } catch (Exception e) {
            System.err.println("❌ Error while reading email: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
