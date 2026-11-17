package com.smart.expense

import com.smart.expense.voice.NlpTransactionParser
import org.junit.Assert.*
import org.junit.Test

class NlpTransactionParserTest {

    @Test
    fun testEnglishExpenseParsing() {
        val input = "Spent 450 rupees on dinner"
        val parsed = NlpTransactionParser.parse(input, "en")
        assertEquals(450.0, parsed.amount, 0.01)
        assertEquals("expense", parsed.type)
        assertEquals("Food", parsed.category)
        assertTrue(parsed.isComplete)
    }

    @Test
    fun testHindiVoiceExpenseParsing() {
        val input = "Maine 200 rupaye petrol ke liye diye"
        val parsed = NlpTransactionParser.parse(input, "hi")
        assertEquals(200.0, parsed.amount, 0.01)
        assertEquals("expense", parsed.type)
        assertEquals("Travel", parsed.category)
        assertTrue(parsed.isComplete)
    }

    @Test
    fun testTeluguVoiceExpenseWithDateParsing() {
        val input = "Ninna 500 bhojanam chesam"
        val parsed = NlpTransactionParser.parse(input, "te")
        assertEquals(500.0, parsed.amount, 0.01)
        assertEquals("expense", parsed.type)
        assertEquals("Food", parsed.category)
        assertEquals("Yesterday", parsed.relativeDateLabel)
        assertTrue(parsed.isComplete)
    }

    @Test
    fun testSalaryIncomeDetection() {
        val input = "Salary of 65000 credited"
        val parsed = NlpTransactionParser.parse(input, "en")
        assertEquals(65000.0, parsed.amount, 0.01)
        assertEquals("income", parsed.type)
        assertEquals("Salary", parsed.category)
    }

    @Test
    fun testPaymentMethodDetectionGPay() {
        val input = "Paid 120 via Google Pay for snacks"
        val parsed = NlpTransactionParser.parse(input, "en")
        assertEquals(120.0, parsed.amount, 0.01)
        assertEquals("UPI", parsed.paymentMethod)
        assertEquals("Food", parsed.category)
    }

    @Test
    fun testSpokenNumberWordParsing() {
        val input = "Spent fifty rupees on chai"
        val parsed = NlpTransactionParser.parse(input, "en")
        assertEquals(50.0, parsed.amount, 0.01)
        assertEquals("Food", parsed.category)
    }

    @Test
    fun testMissingAmountRequiresClarification() {
        val input = "Bought shoes at mall"
        val parsed = NlpTransactionParser.parse(input, "en")
        assertFalse(parsed.isComplete)
        assertEquals("amount", parsed.clarificationField)
    }
}

