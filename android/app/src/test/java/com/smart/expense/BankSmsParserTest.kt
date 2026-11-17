package com.smart.expense

import com.smart.expense.util.BankSmsParser
import org.junit.Assert.*
import org.junit.Test

class BankSmsParserTest {

    @Test
    fun testHdfcUpiDebitAlert() {
        val sms = "Dear Customer, INR 350.00 has been debited from your A/c **4120 towards Swiggy UPI on 12-Nov-26. UPI Ref 43128912."
        val parsed = BankSmsParser.parse(sms, "com.google.android.apps.messaging")
        assertNotNull(parsed)
        assertEquals(350.0, parsed!!.amount, 0.01)
        assertEquals("expense", parsed.type)
        assertEquals("Food & Dining", parsed.category)
    }

    @Test
    fun testSbiSalaryCreditAlert() {
        val sms = "Your A/C 9876 has been credited with INR 50,000.00 on 01-Nov-26 by Salary transfer. Available Bal INR 52,410.00."
        val parsed = BankSmsParser.parse(sms, "com.google.android.apps.messaging")
        assertNotNull(parsed)
        assertEquals(50000.0, parsed!!.amount, 0.01)
        assertEquals("income", parsed.type)
        assertEquals("Salary", parsed.category)
    }

    @Test
    fun testOtpMessageIsIgnored() {
        val otpSms = "Dear Customer, 482910 is your secret OTP for transaction of Rs 500 at Amazon. Do not share with anyone."
        val parsed = BankSmsParser.parse(otpSms, "com.google.android.apps.messaging")
        assertNull("OTP messages must be completely ignored by the bank tracker", parsed)
    }

    @Test
    fun testPromotionalOfferIsIgnored() {
        val promoSms = "Congratulations! You have won cashback up to Rs 1000 on your next recharge. Apply now!"
        val parsed = BankSmsParser.parse(promoSms, "com.google.android.apps.messaging")
        assertNull("Promotional offers must be completely ignored", parsed)
    }
}
