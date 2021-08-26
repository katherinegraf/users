package com.kg.users.services

import com.kg.users.models.PublicToken
import com.kg.users.models.Token
import com.kg.users.repo.TokensRepository
import com.kg.users.repo.UsersRepository
import com.kg.users.utils.STRING_LENGTH
import org.apache.commons.lang3.RandomStringUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.util.logging.Logger

@Service
class TokenService {
    @Autowired
    private lateinit var tokenRepo: TokensRepository

    @Autowired
    val usersRepo: UsersRepository? = null

    val logger = Logger.getLogger("logger")

    fun generateNewToken(id: Long): PublicToken {
        val newToken = Token(
                tokenId = generateRandomAlphanumericTokenID(),
                userId = id,
                created = LocalDateTime.now(),
                expires = LocalDateTime.now().plusMinutes(10),
                revoked = false
        )
        logger.info(newToken.toString())
        tokenRepo.save(newToken)
        return convertTokenToPublicToken(newToken)
    }

    fun convertTokenToPublicToken(token: Token): PublicToken {
        return PublicToken(
                tokenId = token.tokenId,
                userId = token.userId,
                revoked = token.revoked
        )
    }

    fun validateToken(tokenId: String): Boolean {
        val token = tokenRepo.findByTokenId(tokenId)
        return if (token != null) {
            return if (LocalDateTime.now() > token.expires) {
                token.revoked = true
                tokenRepo.save(token)
                false
            } else {
                true
            }
        } else {
            false
        }
    }

    fun validateUser(
            username: String,
            password: String
    ): Boolean {
        val passwordToCompare = usersRepo?.findByUsername(username)?.password
        return passwordToCompare == password
    }

    fun generateRandomAlphanumericTokenID(): String {
        return RandomStringUtils.randomAlphanumeric(STRING_LENGTH)
    }
}