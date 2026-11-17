package com.smart.expense.security

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Properties
import javax.mail.Authenticator
import javax.mail.Message
import javax.mail.PasswordAuthentication
import javax.mail.Session
import javax.mail.Transport
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage

/**
 * Live Google SMTP Mail Dispatcher
 * Sends genuine activation & verification emails directly via Google's official SMTP relay
 * using an authenticated App Password.
 */
object GmailSmtpDispatcher {

    private const val SMTP_HOST = "smtp.gmail.com"
    private const val SMTP_PORT = "587"
    private const val SENDER_EMAIL = "syedshahid7943@gmail.com"
    private const val APP_PASSWORD = "atawgeobbbisgleo"

    suspend fun sendVerificationEmail(recipientEmail: String, activationCode: String): Result<Boolean> {
        return withContext(Dispatchers.IO) {
            try {
                val props = Properties().apply {
                    put("mail.smtp.auth", "true")
                    put("mail.smtp.starttls.enable", "true")
                    put("mail.smtp.host", SMTP_HOST)
                    put("mail.smtp.port", SMTP_PORT)
                    put("mail.smtp.ssl.protocols", "TLSv1.2")
                    put("mail.smtp.connectiontimeout", "10000")
                    put("mail.smtp.timeout", "10000")
                }

                val session = Session.getInstance(props, object : Authenticator() {
                    override fun getPasswordAuthentication(): PasswordAuthentication {
                        return PasswordAuthentication(SENDER_EMAIL, APP_PASSWORD)
                    }
                })

                val message = MimeMessage(session).apply {
                    setFrom(InternetAddress(SENDER_EMAIL, "SmartExpense Security"))
                    setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail))
                    subject = "SmartExpense — Your Email Verification Code: $activationCode"

                    val htmlContent = """
                        <div style="font-family: Arial, sans-serif; max-width: 520px; margin: auto; padding: 24px; border: 1px solid #dcdcdc; border-radius: 14px; background: #ffffff;">
                            <div style="display: flex; align-items: center; margin-bottom: 16px;">
                                <h2 style="color: #1b5e20; margin: 0;">SmartExpense Account Activation</h2>
                            </div>
                            <p style="font-size: 15px; color: #333333; line-height: 22px;">Hello,</p>
                            <p style="font-size: 15px; color: #333333; line-height: 22px;">
                                Thank you for creating an account with <strong>SmartExpense</strong>. 
                                To activate your account and verify your email address, enter the 6-digit verification code below:
                            </p>
                            <div style="background: #e8f5e9; border: 2px dashed #2e7d32; border-radius: 10px; padding: 20px; text-align: center; margin: 24px 0;">
                                <span style="font-size: 34px; font-weight: bold; letter-spacing: 8px; color: #1b5e20; font-family: monospace;">$activationCode</span>
                            </div>
                            <p style="font-size: 13.5px; color: #555555; line-height: 20px;">
                                Enter this code in the app to complete your registration. This code is valid for 15 minutes.
                            </p>
                            <hr style="border: none; border-top: 1px solid #eeeeee; margin: 24px 0;" />
                            <p style="font-size: 11.5px; color: #888888; margin: 0;">
                                This is an automated email sent from the SmartExpense Security Engine. If you did not request this, please disregard.
                            </p>
                        </div>
                    """.trimIndent()

                    setContent(htmlContent, "text/html; charset=utf-8")
                }

                Transport.send(message)
                Result.success(true)
            } catch (e: Exception) {
                e.printStackTrace()
                Result.failure(e)
            }
        }
    }

    suspend fun sendPasswordResetEmail(recipientEmail: String, resetCode: String): Result<Boolean> {
        return withContext(Dispatchers.IO) {
            try {
                val props = Properties().apply {
                    put("mail.smtp.auth", "true")
                    put("mail.smtp.starttls.enable", "true")
                    put("mail.smtp.host", SMTP_HOST)
                    put("mail.smtp.port", SMTP_PORT)
                    put("mail.smtp.ssl.protocols", "TLSv1.2")
                    put("mail.smtp.connectiontimeout", "7000")
                    put("mail.smtp.timeout", "7000")
                }

                val session = Session.getInstance(props, object : Authenticator() {
                    override fun getPasswordAuthentication(): PasswordAuthentication {
                        return PasswordAuthentication(SENDER_EMAIL, APP_PASSWORD)
                    }
                })

                val message = MimeMessage(session).apply {
                    setFrom(InternetAddress(SENDER_EMAIL, "SmartExpense Security"))
                    setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail))
                    subject = "SmartExpense — Password Reset Code: $resetCode"

                    val htmlContent = """
                        <div style="font-family: Arial, sans-serif; max-width: 520px; margin: auto; padding: 24px; border: 1px solid #dcdcdc; border-radius: 14px; background: #ffffff;">
                            <div style="display: flex; align-items: center; margin-bottom: 16px;">
                                <h2 style="color: #2563eb; margin: 0;">SmartExpense Password Reset</h2>
                            </div>
                            <p style="font-size: 15px; color: #333333; line-height: 22px;">Hello,</p>
                            <p style="font-size: 15px; color: #333333; line-height: 22px;">
                                We received a request to reset the password for your <strong>SmartExpense</strong> account.
                                Enter the 6-digit security code below in the app to set a new password:
                            </p>
                            <div style="background: #eff6ff; border: 2px dashed #2563eb; border-radius: 10px; padding: 20px; text-align: center; margin: 24px 0;">
                                <span style="font-size: 34px; font-weight: bold; letter-spacing: 8px; color: #1d4ed8; font-family: monospace;">$resetCode</span>
                            </div>
                            <p style="font-size: 13.5px; color: #555555; line-height: 20px;">
                                This code is valid for 15 minutes. If you did not request a password reset, you can safely ignore this email.
                            </p>
                            <hr style="border: none; border-top: 1px solid #eeeeee; margin: 24px 0;" />
                            <p style="font-size: 11.5px; color: #888888; margin: 0;">
                                SmartExpense Security Engine · 100% Private & Protected
                            </p>
                        </div>
                    """.trimIndent()

                    setContent(htmlContent, "text/html; charset=utf-8")
                }

                Transport.send(message)
                Result.success(true)
            } catch (e: Exception) {
                e.printStackTrace()
                Result.failure(e)
            }
        }
    }
}

