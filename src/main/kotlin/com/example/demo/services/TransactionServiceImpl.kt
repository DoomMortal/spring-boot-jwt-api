package com.example.demo.services

import com.example.demo.domain.Transaction
import com.example.demo.exceptions.EtBadRequestException
import com.example.demo.exceptions.EtResourceNotFoundException
import com.example.demo.repositories.TransactionRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class TransactionServiceImpl : TransactionService {

    @Autowired
    var transactionRepository: TransactionRepository? = null

    override fun fetchAllTransactions(userId: Int, categoryId: Int): List<Transaction> {
        return transactionRepository!!.findAll(userId, categoryId)
    }

    override fun fetchTransactionById(userId: Int, categoryId: Int, transactionId: Int): Transaction? {
        return transactionRepository!!.findById(userId, categoryId, transactionId)
    }

    override fun addTransaction(userId: Int, categoryId: Int, amount: Double, note: String, transactionDate: Long): Transaction {
        val transactionId = transactionRepository!!.create(userId, categoryId, amount, note, transactionDate)
        return transactionRepository!!.findById(userId, categoryId, transactionId!!)!!
    }

    @Throws(EtBadRequestException::class)
    override fun updateTransaction(userId: Int, categoryId: Int, transactionId: Int, transaction: Transaction) {
        transactionRepository!!.update(userId, categoryId, transactionId, transaction)
    }

    @Throws(EtResourceNotFoundException::class)
    override fun removeTransaction(userId: Int, categoryId: Int, transactionId: Int) {
        transactionRepository!!.removeById(userId, categoryId, transactionId)
    }

}