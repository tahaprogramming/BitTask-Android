package com.example.util

import android.util.Base64
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec
import java.security.MessageDigest

object EncryptionUtils {
    private const val ALGORITHM = "AES"

    fun generateKey(passphrase: String): SecretKeySpec {
        val digest = MessageDigest.getInstance("SHA-256")
        val bytes = passphrase.toByteArray(Charsets.UTF_8)
        val keyBytes = digest.digest(bytes)
        return SecretKeySpec(keyBytes, ALGORITHM)
    }

    fun encrypt(cleartext: String, keySpec: SecretKeySpec): String {
        if (cleartext.isEmpty()) return ""
        return try {
            val cipher = Cipher.getInstance(ALGORITHM)
            cipher.init(Cipher.ENCRYPT_MODE, keySpec)
            val encryptedBytes = cipher.doFinal(cleartext.toByteArray(Charsets.UTF_8))
            Base64.encodeToString(encryptedBytes, Base64.NO_WRAP)
        } catch (e: Exception) {
            "[ENCRYPTION_ERROR]"
        }
    }

    fun decrypt(encryptedText: String, keySpec: SecretKeySpec): String {
        if (encryptedText.isEmpty()) return ""
        return try {
            val cipher = Cipher.getInstance(ALGORITHM)
            cipher.init(Cipher.DECRYPT_MODE, keySpec)
            val decodedBytes = Base64.decode(encryptedText, Base64.NO_WRAP)
            String(cipher.doFinal(decodedBytes), Charsets.UTF_8)
        } catch (e: Exception) {
            "[DECRYPTION_FAILED - KEY MISMATCH]"
        }
    }
}
