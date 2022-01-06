package pr.security

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import pr.config
import pr.pathConfig
import pr.updateConfig
import java.io.File
import java.security.MessageDigest
import java.util.*
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec

private lateinit var secretKey: SecretKeySpec

fun setKey() {
    print("Password for encryption: ")
    val myKey = readLine()
    var key = myKey?.toByteArray(charset("UTF-8"))
    val sha: MessageDigest? = MessageDigest.getInstance("SHA-1")
    if (sha == null || key == null)
        return
    key = sha.digest(key)
    key = key.copyOf(16)
    secretKey = SecretKeySpec(key, "AES")
}

fun encrypt(strToEncrypt: String): String {
    val cipher = Cipher.getInstance("AES/ECB/PKCS5Padding")
    cipher.init(Cipher.ENCRYPT_MODE, secretKey)
    return Base64.getEncoder().encodeToString(
        cipher.doFinal
            (strToEncrypt.toByteArray(charset("UTF-8")))
    )
}

fun decrypt(strToDecrypt: String?): String {
    val cipher = Cipher.getInstance("AES/ECB/PKCS5PADDING")
    cipher.init(Cipher.DECRYPT_MODE, secretKey)
    return String(
        cipher.doFinal(
            Base64.getDecoder().decode(strToDecrypt)
        )
    )
}

fun initCrypto() {
    setKey()
    if (!File(pathConfig).exists()) {
        println("Config file does not exist, creating another one...")
        updateConfig()
    }
    val input = File(pathConfig).readText()
    config = Json.decodeFromString(input)
    config.email = decrypt(config.email)
    config.password = decrypt(config.password)
}