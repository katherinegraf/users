package com.kg.users.controllers

import com.kg.users.models.Pet
import com.kg.users.models.Users
import com.kg.users.repo.UsersRepository
import com.kg.users.services.RestApiService
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
        logger.info(userToEdit.toString())
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

    @PostMapping("users/pets/create")
    fun createPetForUser(): ResponseEntity<Pet> {
        val petToCreate = Pet(
                name = "Sallie",
                ownerId = 211,
                age = 21,
                gender = "F",
                color = "mixed",
                type = "cat"
        )
        val apiResult = RestApiService.createPetByAPI(petToCreate)
        logger.info(apiResult.toString())
        return if (apiResult != null) {
            ResponseEntity.ok(apiResult)
        } else {
            ResponseEntity(HttpStatus.BAD_REQUEST)
        }
    }

}
