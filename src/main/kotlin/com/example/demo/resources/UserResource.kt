package com.example.demo.resources

import com.example.demo.Constants
import com.example.demo.domain.User
import com.example.demo.services.UserService
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.util.MultiValueMap
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.*
import kotlin.collections.HashMap

@RestController
@RequestMapping("/api/users")
class UserResource {

    @Autowired
    var userService: UserService? = null

    @PostMapping("/login")
    fun loginUser(@RequestBody userMap: Map<String?, Any?>): ResponseEntity<Map<String, String>> {
        val email = userMap["email"] as String?
        val password = userMap["password"] as String?
        val user = userService!!.validateUser(email!!, password!!)

        return ResponseEntity(generateJWTToken(user!!), HttpStatus.OK)
    }

    @PostMapping("/register")
    fun registerUser(@RequestBody userMap: Map<String, Any>): ResponseEntity<Map<String, String>> {
        val firstName: String = userMap.get("firstName") as String
        val lastName: String = userMap.get("lastName") as String
        val email: String = userMap.get("email") as String
        val password: String = userMap.get("password") as String

        /*val map = hashMapOf<String, String>()
        map["message"] = "registered successfully"
        return ResponseEntity(map, HttpStatus.OK)*/

        val user: User? = userService!!.registerUser(firstName, lastName, email, password)
        return ResponseEntity(generateJWTToken(user!!), HttpStatus.OK)
    }

    private fun generateJWTToken(user: User): Map<String, String> {
        val timestamp = System.currentTimeMillis()
        val token: String = Jwts.builder().signWith(SignatureAlgorithm.HS256, Constants.API_SECRET_KEY)
                .setIssuedAt(Date(timestamp))
                .setExpiration(Date(timestamp + Constants.TOKEN_VALIDITY))
                .claim("userId", user.userId)
                .claim("email", user.email)
                .claim("firstName", user.firstName)
                .claim("lastName", user.lastName)
                .compact()

        //val map: MultiValueMap<String, String> = HashSetValueHashMap()

        //val map : MultiValueMap<String?, String?> = HashMap<Any, Any>();
        //val map = HashMap<String, String>()
        //val map : MultiValueMap<String, String>? = null
        //map?.add("token", token)

        val map = hashMapOf<String, String>()
        map["token"] = token


        //map["token"] = token
        return map
    }

}
