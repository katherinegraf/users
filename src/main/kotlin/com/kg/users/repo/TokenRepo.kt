package com.kg.users.repo

import com.kg.users.models.Token
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface TokensRepository : JpaRepository<Token, Long> {

    fun findByTokenId(tokenId: String): Token?
}