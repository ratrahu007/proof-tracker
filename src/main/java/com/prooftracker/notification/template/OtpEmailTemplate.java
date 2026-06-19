package com.prooftracker.notification.template;

public final class OtpEmailTemplate {

    private OtpEmailTemplate() {
    }

    public static String build(String otp) {

        return """
                Your verification code is:

                %s

                This OTP is valid for 5 minutes.
                """
                .formatted(otp);
    }
}