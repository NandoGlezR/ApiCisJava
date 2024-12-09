package com.jala.university.api.application.service;

import com.jala.university.api.application.service.impl.ValidationServiceImpl;
import com.jala.university.api.domain.exceptions.format.InvalidEmailFormatException;
import com.jala.university.api.domain.exceptions.format.InvalidPasswordFormatException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;

class ValidationServiceTest {
  @InjectMocks
  private ValidationServiceImpl validationService;

  @BeforeEach
  public void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void testValidationEmail() {
    assertTrue(validationService.isValidEmail("user@valid.institution"));
    assertTrue(validationService.isValidEmail("student@jala.university"));
    assertTrue(validationService.isValidEmail("institute@domain01.instituonaldomain"));
  }

  @Test
  void testInvalidEmail() {
    assertFalse(validationService.isValidEmail(""));
    assertFalse(validationService.isValidEmail("@"));
    assertFalse(validationService.isValidEmail("studentjala.university"));
    assertFalse(validationService.isValidEmail("student@jalauniversity"));
    assertFalse(validationService.isValidEmail("student@jala.university_global"));
    assertFalse(validationService.isValidEmail("@example.com"));
  }

  @Test
  void testValidPassword() {
    assertTrue(validationService.isValidPassword("P4ssW0rd_T3st"));
    assertTrue(validationService.isValidPassword("C0nTr4s3ñ4_x3"));
  }

  @Test
  void testInvalidPassword() {
    assertFalse(validationService.isValidPassword(""));
    assertFalse(validationService.isValidPassword("12345678"));
    assertFalse(validationService.isValidPassword("asdfghjkl"));
    assertFalse(validationService.isValidPassword("ABCfghj9"));
    assertFalse(validationService.isValidPassword("_987-3424"));
    assertFalse(validationService.isValidPassword("1234567"));
    assertFalse(validationService.isValidPassword("12345678123456781"));
  }

  @Test
  void testIsValidAndEmail(){
    assertDoesNotThrow(() -> validationService.isValidEmailAndPassword("test@gmail.com", "P4ssW0rd_"));
  }

  @Test
  void testFaildEmailAndPasswordConfirm(){
    assertThrows(InvalidEmailFormatException.class, () -> validationService.isValidEmailAndPassword("testgmail.com", "P4ssW0rd_"));
    assertThrows(InvalidPasswordFormatException.class, () -> validationService.isValidEmailAndPassword("test@gmail.com", "P4ssW0rd"));
  }
}
