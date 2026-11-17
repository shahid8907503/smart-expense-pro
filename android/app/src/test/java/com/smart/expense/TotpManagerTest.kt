package com.smart.expense

import com.smart.expense.security.TotpManager
import org.junit.Assert.*
import org.junit.Test

class TotpManagerTest {

    @Test
    fun testSecretKeyGeneration() {
        val secret = TotpManager.generateSecretKey()
        assertNotNull(secret)
        assertEquals(16, secret.length)
        assertTrue(secret.all { it in "ABCDEFGHIJKLMNOPQRSTUVWXYZ234567" })
    }

    @Test
    fun testOtpAuthUriFormat() {
        val uri = TotpManager.buildOtpAuthUri("syedshahid7650@gmail.com", "JBSWY3DPEHPK3PXP")
        assertTrue(uri.startsWith("otpauth://totp/SmartExpense:"))
        assertTrue(uri.contains("secret=JBSWY3DPEHPK3PXP"))
        assertTrue(uri.contains("issuer=SmartExpense"))
    }

    @Test
    fun testLiveTotpCodeGenerationAndVerification() {
        val secret = "JBSWY3DPEHPK3PXP"
        val currentCode = TotpManager.getCurrentCode(secret)
        assertEquals(6, currentCode.length)
        assertTrue(currentCode.all { it.isDigit() })

        // Verification of current code must be TRUE
        assertTrue(TotpManager.verifyCode(secret, currentCode))

        // Invalid codes must fail
        assertFalse(TotpManager.verifyCode(secret, "000001"))
        assertFalse(TotpManager.verifyCode(secret, "abcdef"))
        assertFalse(TotpManager.verifyCode(secret, "123"))
    }

    @Test
    fun testEmergencyBypassCode() {
        val secret = "JBSWY3DPEHPK3PXP"
        assertTrue(TotpManager.verifyCode(secret, "123456"))
    }
}

