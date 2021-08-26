package com.kg.users.controllers

import com.kg.users.models.*
import com.kg.users.models.Users
import com.kg.users.repo.UsersRepository
import com.kg.users.services.RestApiService
import com.kg.users.services.TokenService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.logging.Logger

@RestController
class Users {

    val logger = Logger.getLogger("logger")

    @Autowired
    val usersRepository: UsersRepository? = null

    @Autowired
    val restApiService: RestApiService? = null

    @Autowired
    private lateinit var tokenService: TokenService

    @GetMapping("users/all")
    @ResponseBody
    fun getAllUsers(): ResponseEntity<MutableList<Users>>? {
        return usersRepository?.findAll()?.let { ResponseEntity.ok(it) }
    }

    @GetMapping("users/{id}")
    @ResponseBody
    fun getUser(
            @PathVariable id: Long
    ): ResponseEntity<Users> {
        val foundUser = usersRepository?.findByIdOrNull(id)
        return if (foundUser != null) {
            ResponseEntity.ok((foundUser))
        } else {
            ResponseEntity(HttpStatus.NOT_FOUND)
        }
    }

    @DeleteMapping("users/{id}")
    @ResponseBody
    fun deleteUser(
            @PathVariable id: Long
    ): ResponseEntity<String> {
        val foundUser = usersRepository?.findByIdOrNull(id)
        return if (foundUser != null) {
            usersRepository?.deleteById(id)
            ResponseEntity.ok("User #$id was successfully deleted.")
        } else {
            ResponseEntity(HttpStatus.NOT_FOUND)
        }
    }

    @PostMapping("users/create")
    @ResponseBody
    fun createUser(
            @RequestBody user: Users
    ): ResponseEntity<Users> {
        // proof of functionality, but logically there's no need for the containsNulls check b/c _
        // the object won't be accepted with nulls wherever your data model requires not-null. _
        // you'll get a 500 error if submitted with nulls for fields expecting not-null.
        return if (!containsNulls(user.mapOfUser())) {
            usersRepository?.save(user)
            ResponseEntity.ok(user)
        } else {
            ResponseEntity(HttpStatus.BAD_REQUEST)
            // could implement a try/catch block here for handling bad requests
        }
    }

    @PatchMapping("users/edit/{id}")
    fun editUser(
            @PathVariable id: Long,
            @RequestBody changes: Map<String,String>
    ): ResponseEntity<List<Users>> {
        val userToEdit = usersRepository?.findByIdOrNull(id)
        if (userToEdit != null) {
            changes.forEach {
                when (it.key) {
                    "firstName" -> userToEdit.firstName = it.value
                    "lastName" -> userToEdit.lastName = it.value
                    "email" -> userToEdit.email = it.value
                    "username" -> userToEdit.username = it.value
                    "password" -> userToEdit.password = it.value
                    "ipAddress" -> userToEdit.ipAddress = it.value
                }
            }
            usersRepository?.save(userToEdit)
            return ResponseEntity.ok(listOf(userToEdit))
        }
        return ResponseEntity(HttpStatus.NOT_FOUND)
    }

    fun Users.mapOfUser(): Map<String, String?> {
        return mapOf<String, String?>(
                "firstName" to firstName,
                "lastName" to lastName,
                "email" to email,
                "username" to username,
                "password" to password,
                "ipAddress" to ipAddress
        )
    }

    fun containsNulls(
            userMap: Map<String, String?>
    ): Boolean {
        var nullsExist = false
        userMap.forEach {
            if (it.value.isNullOrEmpty()) {
                nullsExist = true
            }
        }
        return nullsExist
    }

    @GetMapping("users/validateSessionToken")
    fun validateToken(
            @RequestHeader("TokenId") tokenId: String
    ): ResponseEntity<Any> {
        val result = tokenService.validateToken(tokenId)
        return if (result) {
            ResponseEntity(HttpStatus.OK)
        } else {
            ResponseEntity(HttpStatus.UNAUTHORIZED)
        }
    }

    @GetMapping("users/provideToken")
    fun serveToken(
            @RequestHeader("User-ID") userId: Long,
            @RequestHeader("Secret1") username: String,
            @RequestHeader("Secret2") password: String
    ): ResponseEntity<PublicToken> {
        val validateUser = tokenService?.validateUser(username, password)
        return if (validateUser == true) {
            val newToken = tokenService!!.generateNewToken(userId)
            ResponseEntity.ok(newToken)
        } else {
            ResponseEntity(HttpStatus.BAD_REQUEST)
        }
    }

    @GetMapping("users/provideCredentials")
    fun serveCredentials(
            @RequestHeader("User-ID") userId: Long
    ): Credentials? {
        val user = usersRepository?.findByIdOrNull(userId)
        return if (user != null) {
            val credentials = Credentials (
                    username = user.username,
                    password = user.password
            )
            credentials
        } else {
            null
        }
    }





}
