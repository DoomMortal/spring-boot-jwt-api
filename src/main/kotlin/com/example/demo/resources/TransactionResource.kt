package com.example.demo.resources

import com.example.demo.domain.Transaction
import com.example.demo.services.TransactionService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*
import javax.servlet.http.HttpServletRequest

@RestController
@RequestMapping("/api/categories/{categoryId}/transactions")
class TransactionResource {

    @Autowired
    var transactionService: TransactionService? = null

    @GetMapping("")
    fun getAllTransactions(request: HttpServletRequest,
                           @PathVariable("categoryId") categoryId: Int): ResponseEntity<List<Transaction>> {
        val userId = request.getAttribute("userId") as Int
        val transactions: List<Transaction> = transactionService!!.fetchAllTransactions(userId, categoryId)
        return ResponseEntity(transactions, HttpStatus.OK)
    }

    @GetMapping("/{transactionId}")
    fun getTransactionById(request: HttpServletRequest,
                           @PathVariable("categoryId") categoryId: Int?,
                           @PathVariable("transactionId") transactionId: Int?): ResponseEntity<Transaction> {
        val userId = request.getAttribute("userId") as Int
        val transaction: Transaction? = transactionService!!.fetchTransactionById(userId, categoryId!!, transactionId!!)
        return ResponseEntity(transaction, HttpStatus.OK)
    }

    @PostMapping("")
    fun addTransaction(request: HttpServletRequest,
                       @PathVariable("categoryId") categoryId: Int,
                       @RequestBody transactionMap: Map<String?, Any>): ResponseEntity<Transaction> {
        val userId = request.getAttribute("userId") as Int
        val amount = java.lang.Double.valueOf(transactionMap["amount"].toString())
        val note = transactionMap["note"] as String
        val transactionDate = transactionMap["transactionDate"] as Long

        val transaction: Transaction = transactionService!!.addTransaction(userId, categoryId, amount, note, transactionDate)
        return ResponseEntity(transaction, HttpStatus.CREATED)
    }

    @PutMapping("/{transactionId}")
    fun updateTransaction(request: HttpServletRequest,
                          @PathVariable("categoryId") categoryId: Int?,
                          @PathVariable("transactionId") transactionId: Int?,
                          @RequestBody transaction: Transaction?): ResponseEntity<Map<String, Boolean>> {
        val userId = request.getAttribute("userId") as Int
        transactionService!!.updateTransaction(userId, categoryId!!, transactionId!!, transaction!!)
        val map: MutableMap<String, Boolean> = HashMap()
        map["success"] = true
        return ResponseEntity(map, HttpStatus.OK)
    }

    @DeleteMapping("/{transactionId}")
    fun deleteTransaction(request: HttpServletRequest,
                          @PathVariable("categoryId") categoryId: Int?,
                          @PathVariable("transactionId") transactionId: Int?): ResponseEntity<Map<String, Boolean>> {
        val userId = request.getAttribute("userId") as Int
        transactionService!!.removeTransaction(userId, categoryId!!, transactionId!!)
        val map: MutableMap<String, Boolean> = HashMap()
        map["success"] = true
        return ResponseEntity(map, HttpStatus.OK)
    }
}