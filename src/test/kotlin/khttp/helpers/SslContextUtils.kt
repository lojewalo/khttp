package khttp.helpers

import java.security.KeyStore
import java.security.SecureRandom
import java.util.*
import javax.net.ssl.KeyManagerFactory
import javax.net.ssl.SSLContext

object SslContextUtils {

    fun createFromKeyMaterial(keyStorePath: String, keyStorePassword: CharArray): SSLContext {
        val inputStream = this::class.java.classLoader.getResourceAsStream(keyStorePath)
        Objects.requireNonNull(inputStream)

        val keyStore = KeyStore.getInstance("PKCS12")
        keyStore.load(inputStream, keyStorePassword)

        val keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm())
        keyManagerFactory.init(keyStore, keyStorePassword)

        val sslContext = SSLContext.getInstance("TLSv1.2")
        sslContext.init(keyManagerFactory.keyManagers, null, SecureRandom())

        inputStream.close()

        return sslContext
    }

}