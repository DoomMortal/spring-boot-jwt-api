package com.example.demo.repositories

import com.example.demo.domain.Transaction
import com.example.demo.exceptions.EtBadRequestException
import com.example.demo.exceptions.EtResourceNotFoundException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.RowMapper
import org.springframework.jdbc.support.GeneratedKeyHolder
import org.springframework.jdbc.support.KeyHolder
import org.springframework.stereotype.Repository
import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.Statement

@Repository
class TransactionRepositoryImpl : TransactionRepository {

    @Autowired
    var jdbcTemplate: JdbcTemplate? = null

    override fun findAll(userId: Int, categoryId: Int): List<Transaction> {
        return jdbcTemplate!!.query(SQL_FIND_ALL, arrayOf<Any?>(userId, categoryId), transactionRowMapper)
    }

    @Throws(EtResourceNotFoundException::class)
    override fun findById(userId: Int, categoryId: Int, transactionId: Int): Transaction? {
        return try {
            jdbcTemplate!!.queryForObject(SQL_FIND_BY_ID, arrayOf<Any?>(userId, categoryId, transactionId), transactionRowMapper)
        } catch (e: Exception) {
            throw EtResourceNotFoundException("Transaction not found")
        }
    }

    @Throws(EtBadRequestException::class)
    override fun create(userId: Int, categoryId: Int, amount: Double, note: String, transactionDate: Long): Int? {
        return try {
            val keyHolder: KeyHolder = GeneratedKeyHolder()
            jdbcTemplate!!.update({ connection: Connection ->
                val ps: PreparedStatement = connection.prepareStatement(SQL_CREATE, Statement.RETURN_GENERATED_KEYS)
                ps.setInt(1, categoryId)
                ps.setInt(2, userId)
                ps.setDouble(3, amount)
                ps.setString(4, note)
                ps.setLong(5, transactionDate)
                ps
            }, keyHolder)
            keyHolder.keys!!["TRANSACTION_ID"] as Int?
        } catch (e: Exception) {
            throw EtBadRequestException("Invalid request")
        }
    }

    @Throws(EtBadRequestException::class)
    override fun update(userId: Int, categoryId: Int, transactionId: Int, transaction: Transaction) {
        try {

            val keyHolder: KeyHolder = GeneratedKeyHolder()
            jdbcTemplate!!.update({ connection: Connection ->
                val ps: PreparedStatement = connection.prepareStatement(SQL_UPDATE, Statement.RETURN_GENERATED_KEYS)
                ps.setDouble(1, transaction.amount)
                ps.setString(2, transaction.note)
                ps.setLong(3, transaction.transactionDate)
                ps.setInt(4, userId)
                ps.setInt(5, categoryId)
                ps.setInt(6, transactionId)
                ps
            }, keyHolder)

            //jdbcTemplate!!.update(SQL_UPDATE, arrayOf(transaction.amount, transaction.note, transaction.transactionDate, userId, categoryId, transactionId))
        } catch (e: Exception) {
            throw EtBadRequestException("Invalid request")
        }
    }

    @Throws(EtResourceNotFoundException::class)
    override fun removeById(userId: Int, categoryId: Int, transactionId: Int) {
        val count = jdbcTemplate!!.update(SQL_DELETE, arrayOf<Any?>(userId, categoryId, transactionId))
        if (count == 0) throw EtResourceNotFoundException("Transaction not found")
    }

    private val transactionRowMapper: RowMapper<Transaction> = RowMapper<Transaction> { rs, rowNum ->
        Transaction(rs.getInt("TRANSACTION_ID"),
                rs.getInt("CATEGORY_ID"),
                rs.getInt("USER_ID"),
                rs.getDouble("AMOUNT"),
                rs.getString("NOTE"),
                rs.getLong("TRANSACTION_DATE"))
    }

    companion object {
        private const val SQL_FIND_ALL = "SELECT TRANSACTION_ID, CATEGORY_ID, USER_ID, AMOUNT, NOTE, TRANSACTION_DATE FROM ET_TRANSACTIONS WHERE USER_ID = ? AND CATEGORY_ID = ?"
        private const val SQL_FIND_BY_ID = "SELECT TRANSACTION_ID, CATEGORY_ID, USER_ID, AMOUNT, NOTE, TRANSACTION_DATE FROM ET_TRANSACTIONS WHERE USER_ID = ? AND CATEGORY_ID = ? AND TRANSACTION_ID = ?"
        private const val SQL_CREATE = "INSERT INTO ET_TRANSACTIONS (TRANSACTION_ID, CATEGORY_ID, USER_ID, AMOUNT, NOTE, TRANSACTION_DATE) VALUES(NEXTVAL('ET_TRANSACTIONS_SEQ'), ?, ?, ?, ?, ?)"
        private const val SQL_UPDATE = "UPDATE ET_TRANSACTIONS SET AMOUNT = ?, NOTE = ?, TRANSACTION_DATE = ? WHERE USER_ID = ? AND CATEGORY_ID = ? AND TRANSACTION_ID = ?"
        private const val SQL_DELETE = "DELETE FROM ET_TRANSACTIONS WHERE USER_ID = ? AND CATEGORY_ID = ? AND TRANSACTION_ID = ?"
    }
}