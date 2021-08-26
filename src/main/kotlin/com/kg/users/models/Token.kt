package com.kg.users.models

import java.time.LocalDateTime
import java.time.LocalTime
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name = "tokens")
data class Token (
        @Id
        @Column(name = "id")
        var tokenId: String,

        @Column(name = "userId")
        var userId: Long,

        @Column(name = "created")
        var created: LocalDateTime,

        @Column(name = "expires")
        var expires: LocalDateTime,

        @Column(name = "revoked")
        var revoked: Boolean
)

class PublicToken (
        var tokenId: String,
        var userId: Long,
        var revoked: Boolean
)
