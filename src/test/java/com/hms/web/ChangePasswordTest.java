package com.hms.web;

import com.codeborne.selenide.Configuration;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.*;


public class ChangePasswordTest {

    private final String BASE_URL = "http://localhost:8080/SmartHospital";

    @BeforeAll
    public static void setUpAll() {
        Configuration.browserSize = "1280x800";
        Configuration.headless = false;
    }

    @BeforeEach
    public void openPage() {
        open(BASE_URL + "/change_password.jsp");
    }

    @Test
    public void shouldShowErrorOnMismatchPassword() {
        $("#newPassword").setValue("abc123");
        $("#confirmPassword").setValue("xyz789");
        $("button[type='submit']").click();
        $("#error-msg").shouldBe(visible).shouldHave(text("Passwords do not match"));
    }

    @AfterEach
    public void tearDown() {
        closeWebDriver();
    }
}
