package com.example.demo.services

import com.example.demo.domain.User
import com.example.demo.exceptions.EtAuthException
import com.example.demo.repositories.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.regex.Pattern

@Service
@Transactional
class UserServiceImpl : UserService {

    @Autowired
    var userRepository: UserRepository? = null

    override fun validateUser(email: String, password: String): User? {
        var emailLower = ""
        if(email != null) emailLower = email.toLowerCase();
        return userRepository?.findByEmailAndPassword(emailLower, password);
    }

    override fun registerUser(firstName: String, lastName: String, email: String, password: String): User? {
        val pattern: Pattern = Pattern.compile("^(.+)@(.+)$")
        var emailFormated = ""
        if (email != null) emailFormated = email.toLowerCase()
        if (!pattern.matcher(emailFormated).matches()) throw EtAuthException("Invalid email format")
        val count = userRepository!!.getCountByEmail(email)
        if (count!! > 0) throw EtAuthException("Email already in use")
        val userId = userRepository!!.create(firstName, lastName, emailFormated, password)
        return userRepository!!.findById(userId)
    }
}