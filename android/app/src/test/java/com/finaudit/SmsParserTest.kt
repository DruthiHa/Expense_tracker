package com.finaudit

import com.finaudit.domain.parser.SmsParser
import org.junit.Assert.*
import org.junit.Test

class SmsParserTest {

    private val parser = SmsParser()

    @Test
    fun testHdfcDebitSms() {
        val sms = "Alert: Your HDFC Bank Card ending 1234 has been debited for Rs 1,500.00 at Swiggy on 06-Aug-2026."
        val parsed = parser.parse(sms)
        assertNotNull(parsed)
        assertEquals(1500.0, parsed!!.amount, 0.0)
        assertEquals("DEBIT", parsed.direction)
        assertEquals("Swiggy", parsed.merchantName)
        assertEquals("1234", parsed.accountLast4)
    }

    @Test
    fun testSbiDebitSms() {
        val sms = "Txn of Rs 450.00 debited from SBI A/c XX5678 to Zomato on 06-Aug-2026."
        val parsed = parser.parse(sms)
        assertNotNull(parsed)
        assertEquals(450.0, parsed!!.amount, 0.0)
        assertEquals("DEBIT", parsed.direction)
        assertEquals("Zomato", parsed.merchantName)
        assertEquals("5678", parsed.accountLast4)
    }

    @Test
    fun testIciciDebitSms() {
        val sms = "Your A/c XX3456 has been debited with INR 2,499.00 on 05-Aug-2026. Info: Amazon Pay."
        val parsed = parser.parse(sms)
        assertNotNull(parsed)
        assertEquals(2499.0, parsed!!.amount, 0.0)
        assertEquals("DEBIT", parsed.direction)
        assertEquals("Amazon Pay", parsed.merchantName)
        assertEquals("3456", parsed.accountLast4)
    }

    @Test
    fun testCreditSms() {
        val sms = "Rs 55,000.00 credited to A/c XX4321 by TCS Salary."
        val parsed = parser.parse(sms)
        assertNotNull(parsed)
        assertEquals(55000.0, parsed!!.amount, 0.0)
        assertEquals("CREDIT", parsed.direction)
        assertEquals("TCS Salary", parsed.merchantName)
        assertEquals("4321", parsed.accountLast4)
    }

    @Test
    fun testFallbackHeuristics() {
        val sms = "Paid Rs. 120 at Local Chai Tapri."
        val parsed = parser.parse(sms)
        assertNotNull(parsed)
        assertEquals(120.0, parsed!!.amount, 0.0)
        assertEquals("DEBIT", parsed.direction)
        assertEquals("Local Chai Tapri", parsed.merchantName)
    }
}
