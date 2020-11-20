package com.example.demo.services

import com.example.demo.domain.User
import com.example.demo.exceptions.EtAuthException

interface UserService {
    @Throws(EtAuthException::class)
    fun validateUser(email: String, password: String): User?

    @Throws(EtAuthException::class)
    fun registerUser(firstName: String, lastName: String, email: String, password: String): User?
}