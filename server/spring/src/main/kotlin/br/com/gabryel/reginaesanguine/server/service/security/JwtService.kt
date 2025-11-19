package br.com.gabryel.reginaesanguine.server.service.security

import br.com.gabryel.reginaesanguine.server.configuration.JwtProperties
import io.jsonwebtoken.Jwts
import org.springframework.stereotype.Service
import java.security.KeyFactory
import java.security.PrivateKey
import java.security.PublicKey
import java.security.spec.PKCS8EncodedKeySpec
import java.security.spec.X509EncodedKeySpec
import java.util.Base64
import java.util.Date

@Service
class JwtService(jwtProperties: JwtProperties) : TokenService {
    private val privateKey: PrivateKey = parsePrivateKey(jwtProperties.privateKey)
    private val publicKey: PublicKey = parsePublicKey(jwtProperties.publicKey)
    private val expirationMs = 30 * 24 * 60 * 60 * 1000L

    override fun generateToken(accountId: String): String {
        val now = Date()
        val expiration = Date(now.time + expirationMs)

        return Jwts.builder()
            .subject(accountId)
            .issuedAt(now)
            .expiration(expiration)
            .signWith(privateKey)
            .compact()
    }

    override fun validateToken(token: String): String? = try {
        val claims = Jwts.parser()
            .verifyWith(publicKey)
            .build()
            .parseSignedClaims(token)
            .payload

        claims.subject
    } catch (e: Exception) {
        null
    }

    private fun parsePrivateKey(pem: String): PrivateKey {
        val keyContent = pem
            .replace("-----BEGIN PRIVATE KEY-----", "")
            .replace("-----END PRIVATE KEY-----", "")
            .replace("\\s".toRegex(), "")

        val keyBytes = Base64.getDecoder().decode(keyContent)
        val keySpec = PKCS8EncodedKeySpec(keyBytes)
        return KeyFactory.getInstance("RSA").generatePrivate(keySpec)
    }

    private fun parsePublicKey(pem: String): PublicKey {
        val keyContent = pem
            .replace("-----BEGIN PUBLIC KEY-----", "")
            .replace("-----END PUBLIC KEY-----", "")
            .replace("\\s".toRegex(), "")

        val keyBytes = Base64.getDecoder().decode(keyContent)
        val keySpec = X509EncodedKeySpec(keyBytes)
        return KeyFactory.getInstance("RSA").generatePublic(keySpec)
    }
}
