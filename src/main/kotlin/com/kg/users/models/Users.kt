package com.kg.users.models

import javax.persistence.*

@Entity
@Table(name = "users")
data class Users (
        @Column(name = "first_name")
        var firstName: String? = null,

        @Column(name = "last_name")
        var lastName: String? = null,

        @Column(name = "email")
        var email: String,

        @Column(name = "username")
        var username: String,

        @Column(name = "password")
        var password: String,

        @Column(name = "ip_address")
        var ipAddress: String? = null
)
{
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        var userId: Long = 0
}