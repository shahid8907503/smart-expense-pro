package com.smart.expense

import com.smart.expense.java_core.FinancialMathJavaService
import org.junit.Assert.assertEquals
import org.junit.Test

class FinancialMathJavaServiceTest {

    @Test
    fun testStandardEmiCalculation() {
        // Principal = 100,000, 12% annual interest, 12 months tenure
        // Standard reducing EMI formula yields approx 8,884.88
        val emi = FinancialMathJavaService.calculateMonthlyEmi(100000.0, 12.0, 12)
        assertEquals(8884.88, emi, 1.0)
    }

    @Test
    fun testZeroInterestEmi() {
        // Principal = 12,000, 0% annual interest, 12 months
        // EMI should be exactly 1,000.00
        val emi = FinancialMathJavaService.calculateMonthlyEmi(12000.0, 0.0, 12)
        assertEquals(1000.0, emi, 0.01)
    }

    @Test
    fun testInvalidInputsReturnZero() {
        assertEquals(0.0, FinancialMathJavaService.calculateMonthlyEmi(0.0, 10.0, 12), 0.001)
        assertEquals(0.0, FinancialMathJavaService.calculateMonthlyEmi(-5000.0, 10.0, 12), 0.001)
        assertEquals(0.0, FinancialMathJavaService.calculateMonthlyEmi(50000.0, 10.0, 0), 0.001)
        assertEquals(0.0, FinancialMathJavaService.calculateMonthlyEmi(50000.0, 10.0, -6), 0.001)
    }

    @Test
    fun testTotalInterestPayable() {
        // Principal = 100,000, EMI = 8,884.88, 12 months -> Total = 106,618.56 -> Interest = 6,618.56
        val interest = FinancialMathJavaService.calculateTotalInterest(100000.0, 8884.88, 12)
        assertEquals(6618.56, interest, 1.0)
    }
}

