package com.jala.university.api.controller;

import com.jala.university.api.application.service.ResetPasswordService;
import com.jala.university.api.domain.exceptions.format.InvalidEmailFormatException;
import com.jala.university.api.domain.exceptions.format.InvalidPasswordFormatException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/account/password-reset")
public class PasswordResetController {

  private final ResetPasswordService resetPasswordService;

  @Autowired
  public PasswordResetController(ResetPasswordService resetPasswordService) {
    this.resetPasswordService = resetPasswordService;

  }

  @Operation(summary = "Initiate Password Reset",
      description = "Sends a password reset email to the specified email address.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "202", description = "If the email exists, a password recovery link has been sent."),
      @ApiResponse(responseCode = "400", description = "Invalid email format."),
      @ApiResponse(responseCode = "500", description = "An error occurred while processing your request.")
  })
  @PutMapping
  public final ResponseEntity<String> requestPasswordReset(@RequestParam("email") String email) {
    try {
      resetPasswordService.sendPasswordResetEmail(email);
    } catch (MessagingException e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while processing your request.");
    } catch (InvalidEmailFormatException e) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid email format.");
    }
    return ResponseEntity.accepted().body("If the email exists, a password recovery link has been sent.");
  }

  @Operation(summary = "Update Password",
      description = "Uses a provided token and the new password to reset the password.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Password has been updated."),
      @ApiResponse(responseCode = "400", description = "Invalid or expired token."),
      @ApiResponse(responseCode = "400", description = "Invalid password format.")
  })
  @PatchMapping
  public final ResponseEntity<String> updatePassword(@RequestParam("token") UUID token, @RequestParam("password") String password) {
    try {
      if (!resetPasswordService.resetPassword(token, password)) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid or expired token");
      }
    } catch (InvalidPasswordFormatException e) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid password format.");
    }
    return ResponseEntity.ok("Password has been updated.");
  }
}
