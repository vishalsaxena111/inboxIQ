package com.inboxiq.scheduler;

import com.inboxiq.service.EmailFetcherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class EmailScheduler {

    @Autowired
    private EmailFetcherService emailFetcherService;

    private final String EMAIL = "your_email@gmail.com"; // use app password
    private final String PASSWORD = "your_app_password";

    @Scheduled(fixedDelay = 60000) // every 1 minute
    public void checkMails() {
        emailFetcherService.fetchUnreadEmails();
    }
}
