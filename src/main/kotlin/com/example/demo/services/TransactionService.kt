package com.example.demo.services

import com.example.demo.domain.Transaction
import com.example.demo.exceptions.EtBadRequestException
import com.example.demo.exceptions.EtResourceNotFoundException


interface TransactionService {

    fun fetchAllTransactions(userId: Int, categoryId: Int): List<Transaction>

    @Throws(EtResourceNotFoundException::class)
    fun fetchTransactionById(userId: Int, categoryId: Int, transactionId: Int): Transaction?

    @Throws(EtBadRequestException::class)
    fun addTransaction(userId: Int, categoryId: Int, amount: Double, note: String, transactionDate: Long): Transaction

    @Throws(EtBadRequestException::class)
    fun updateTransaction(userId: Int, categoryId: Int, transactionId: Int, transaction: Transaction)

    @Throws(EtResourceNotFoundException::class)
    fun removeTransaction(userId: Int, categoryId: Int, transactionId: Int)

}