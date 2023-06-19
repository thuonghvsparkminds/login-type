package com.example.logintype.service;

public interface TotpService {

    boolean verifyCode(String totpCode, String secret);

    String generateTotpBySecret(String secret);
}
