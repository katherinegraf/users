package com.kg.users.models

import com.github.kittinunf.fuel.core.ResponseDeserializable
import com.google.gson.Gson

data class Pet (
        var name: String?,
        var ownerId: Long?,
        var age: Long?,
        var gender: String?,
        var color: String?,
        var type: String?
) {
    class Deserializer : ResponseDeserializable<Pet> {
        override fun deserialize(content: String): Pet =
                Gson().fromJson(content, Pet::class.java)
    }
}
