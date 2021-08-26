package com.kg.users.services

import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.fuel.httpPost
import com.github.kittinunf.result.Result
import com.google.gson.Gson
import com.kg.users.models.Pet
import com.kg.users.models.Token
import com.kg.users.utils.PETS_API_BASE_URL
import org.springframework.stereotype.Service
import java.util.logging.Logger

@Service
class RestApiService {

    val logger = Logger.getLogger("logger")

    fun createPetByAPI(pet: Pet): Pet? {
        val petJson = Gson().toJson(pet)
        //logger.info(petJson.toString())
        val (request, response, result) = PETS_API_BASE_URL.plus("createFromUser")
                .httpPost()
                .header(
                        "Content-type" to "application/json",
                        "API-key" to "ABC123"
                )
                .body(petJson)
                .responseObject(Pet.Deserializer())
        //logger.info("request = ${request.toString()}")
        //logger.info("response = ${response.toString()}")
        //logger.info("result = ${result.toString()}")


        return when (result) {
            is Result.Failure -> {
                //logger.info(result.toString())
                val exception = result.getException()
                logger.info("Exception is ${exception.toString()}")
                null
            }
            is Result.Success -> {
                val (apiResult) = result
                apiResult
            }
        }
    }
}