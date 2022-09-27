package ru.dolbik.springcourse.FirstSecurityApp.util;

public class AuthErrorResponse {
    private String message;

    public AuthErrorResponse(String message) {
        this.message = message;
    }

    public AuthErrorResponse() {}
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
