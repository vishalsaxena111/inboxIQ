package com.inboxiq.controller;

import com.inboxiq.model.Email;
import com.inboxiq.repository.EmailRepository;
import com.inboxiq.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
public class EmailController {

    @Autowired
    private EmailService emailService;

    @Autowired
    private EmailRepository emailRepository;

    @PostMapping
    public String saveEmail(@RequestBody Email email) throws Exception {
        emailService.classifyAndSave(email);
        return "success";
    }

    @GetMapping("/emails")
    public String getEmails(Model model) {
        model.addAttribute("emails", emailRepository.findAll());
        return "emails";
    }

    @GetMapping
    public List<Email> getAllEmails() {
        return emailService.getAllEmails();
    }

    @GetMapping("/category/{category}")
    public List<Email> getByCategory(@PathVariable String category) {
        return emailService.getEmailsByCategory(category);
    }

    @GetMapping("/inbox")
    public String showInbox(Model model) {
        model.addAttribute("emails", emailRepository.findAllByOrderByReceivedAtDesc());
        return "inbox";
    }

    @GetMapping("/email/{id}")
    public String viewEmail(@PathVariable Long id, Model model) {
        Optional<Email> emailOpt = emailRepository.findById(id);
        if (emailOpt.isPresent()) {
            model.addAttribute("email", emailOpt.get());
            return "email-body";
        }
        return "redirect:/inbox";
    }
}
