package ru.dolbik.springcourse.FirstSecurityApp.util;

public class AuthException extends RuntimeException{
    public AuthException(String msg){
        super(msg);
    }

}
