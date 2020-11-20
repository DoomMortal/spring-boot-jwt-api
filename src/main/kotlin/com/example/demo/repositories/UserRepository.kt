package com.example.demo.repositories

import com.example.demo.domain.User
import com.example.demo.exceptions.EtAuthException

interface UserRepository {

    @Throws(EtAuthException::class)
    fun create(firstName: String, lastName: String, email: String, password: String): Int

    @Throws(EtAuthException::class)
    fun findByEmailAndPassword(email: String?, password: String?): User?

    fun getCountByEmail(email: String): Int?

    fun findById(userId: Int): User?

}