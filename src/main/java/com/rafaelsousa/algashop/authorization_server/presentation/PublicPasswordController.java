package com.rafaelsousa.algashop.authorization_server.presentation;

import com.rafaelsousa.algashop.authorization_server.application.user.management.PasswordManagementApplicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/change-password")
@RequiredArgsConstructor
public class PublicPasswordController {
    public static final String MESSAGE_ATTRIBUTE = "message";
    public static final String SUCCESS_ATTRIBUTE = "success";

    private final PasswordManagementApplicationService passwordManagementService;

    @GetMapping
    public String passwordForm(
            @RequestParam(name = "token", required = false) String token, Model model) {
        if (token == null || token.isBlank()) {
            model.addAttribute(MESSAGE_ATTRIBUTE, "Invalid token.");
            model.addAttribute(SUCCESS_ATTRIBUTE, false);
            return "password-message";
        }

        model.addAttribute("token", token);
        return "password-form";
    }

    @PostMapping(consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public String changePassword(
            @RequestParam("token") String token,
            @RequestParam("newPassword") String newPassword,
            Model model) {
        try {
            passwordManagementService.changePasswordWithToken(token, newPassword);
            model.addAttribute(MESSAGE_ATTRIBUTE, "Password changed successfully.");
            model.addAttribute(SUCCESS_ATTRIBUTE, true);
        } catch (AccessDeniedException _) {
            model.addAttribute(MESSAGE_ATTRIBUTE, "Invalid token.");
            model.addAttribute(SUCCESS_ATTRIBUTE, false);
        }

        return "password-message";
    }
}
