package com.example.plugins.utils

import net.axay.simplekotlinmail.email.emailBuilder
import org.simplejavamail.api.email.Email

fun getResetEmailTemplate(email :String,nickname: String,resetCode: Int): Email = emailBuilder {
    from("clovercreations784@gmail.com")
    to(email)
    withSubject("ChesterHunt Reset Code")

    withPlainText(
        "Hi, $nickname" +
                "\n" +
                "We got a request to reset your ChesterHunts password.\n" +
                "Enter $resetCode to reset the password.\n" +
                "\n" +
                "If you didn't request a password reset, ignore this message.\n" +
                "\n" +
                "Kind regards, \n" +
                "Team Clover."
    )

}