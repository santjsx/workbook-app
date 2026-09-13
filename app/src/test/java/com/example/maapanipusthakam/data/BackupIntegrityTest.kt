package com.example.maapanipusthakam.data

import com.example.maapanipusthakam.data.repository.BackupEnvelope
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test
import java.security.MessageDigest

class BackupIntegrityTest {

    private val json = Json { ignoreUnknownKeys = true }

    private fun calculateSha256(input: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val hashBytes = digest.digest(input.toByteArray(Charsets.UTF_8))
        return hashBytes.joinToString("") { "%02x".format(it) }
    }

    @Test
    fun testValidChecksumEnvelope() {
        val payload = """{"works":[],"dailyPages":[],"incomeEntries":[],"workers":[],"labourPayments":[],"expenses":[]}"""
        val checksum = calculateSha256(payload)

        val envelope = BackupEnvelope(
            schemaVersion = 1,
            appVersion = "1.0.0",
            backupDate = 20700L,
            createdAtMillis = 1700000000000L,
            payloadJson = payload,
            checksumSha256 = checksum
        )

        val encoded = json.encodeToString(envelope)
        val decoded = json.decodeFromString<BackupEnvelope>(encoded)

        assertEquals(checksum, decoded.checksumSha256)
        assertEquals(calculateSha256(decoded.payloadJson), decoded.checksumSha256)
    }

    @Test
    fun testTamperedPayloadDetection() {
        val originalPayload = """{"works":[],"dailyPages":[],"incomeEntries":[],"workers":[],"labourPayments":[],"expenses":[]}"""
        val originalChecksum = calculateSha256(originalPayload)

        val tamperedPayload = """{"works":[{"id":1,"name":"Fake"}],"dailyPages":[],"incomeEntries":[],"workers":[],"labourPayments":[],"expenses":[]}"""

        val envelope = BackupEnvelope(
            schemaVersion = 1,
            appVersion = "1.0.0",
            backupDate = 20700L,
            createdAtMillis = 1700000000000L,
            payloadJson = tamperedPayload,
            checksumSha256 = originalChecksum // Mismatched!
        )

        val isMatch = calculateSha256(envelope.payloadJson).equals(envelope.checksumSha256, ignoreCase = true)
        org.junit.Assert.assertFalse("Tampered backup payload should not match checksum", isMatch)
    }
}
