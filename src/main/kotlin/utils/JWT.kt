package main.kotlin.utils

import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.exceptions.JWTCreationException
import com.auth0.jwt.exceptions.JWTVerificationException
import org.joda.time.DateTime
import org.joda.time.Duration

interface JWT {
    fun create(subject: String, expiresIn: Duration): String?
    fun verify(token: String): String?
    fun isValid(token: String): Boolean
}

class JavaJWT(private val secret: String, private val issuer: String) : JWT {
    override fun create(subject: String, expiresIn: Duration): String? {
        return try {
            val algorithm = Algorithm.HMAC256(secret)
            com.auth0.jwt.JWT
                .create()
                .withIssuer(issuer)
                .withSubject(subject)
                .withExpiresAt(DateTime.now().plus(expiresIn).toDate())
                .sign(algorithm)
        } catch (e: JWTCreationException) {
            Logger.error("Unable to create JWT Token ${e.message}")
            null
        }
    }

    override fun verify(token: String): String? {
        return try {
            val decodedToken = verifier.verify(token)
            decodedToken.subject
        } catch (e: JWTVerificationException) {
            Logger.info("Unable to verify JWT token ${e.message}")
            null
        }
    }

    override fun isValid(token: String): Boolean {
        return try {
            verifier.verify(token)
            true
        } catch (e: JWTVerificationException) {
            Logger.info("Unable to verify JWT token ${e.message}")
            false
        }
    }

    private val verifier by lazy {
        val algorithm = Algorithm.HMAC256(secret)
        com.auth0.jwt.JWT
            .require(algorithm)
            .acceptExpiresAt(60)
            .build()
    }
}