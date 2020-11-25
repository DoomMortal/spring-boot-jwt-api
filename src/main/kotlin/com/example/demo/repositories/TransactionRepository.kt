package com.example.demo.repositories

import com.example.demo.domain.Transaction
import com.example.demo.exceptions.EtBadRequestException
import com.example.demo.exceptions.EtResourceNotFoundException


interface TransactionRepository {

    fun findAll(userId: Int, categoryId: Int): List<Transaction>

    @Throws(EtResourceNotFoundException::class)
    fun findById(userId: Int, categoryId: Int, transactionId: Int): Transaction?

    @Throws(EtBadRequestException::class)
    fun create(userId: Int, categoryId: Int, amount: Double, note: String, transactionDate: Long): Int?

    @Throws(EtBadRequestException::class)
    fun update(userId: Int, categoryId: Int, transactionId: Int, transaction: Transaction)

    @Throws(EtResourceNotFoundException::class)
    fun removeById(userId: Int, categoryId: Int, transactionId: Int)

}