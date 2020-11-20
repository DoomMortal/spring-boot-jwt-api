package com.example.demo.repositories

import com.example.demo.domain.User
import com.example.demo.exceptions.EtAuthException
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.RowMapper
import org.springframework.jdbc.support.GeneratedKeyHolder
import org.springframework.jdbc.support.KeyHolder
import org.springframework.stereotype.Repository
import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.Statement

import org.springframework.dao.EmptyResultDataAccessException

@Repository
class UserRepositoryImpl : UserRepository {

    private val SQL_CREATE = "INSERT INTO ET_USERS(USER_ID, FIRST_NAME, LAST_NAME, EMAIL, PASSWORD) VALUES(NEXTVAL('ET_USERS_SEQ'), ?, ?, ?, ?)"
    private val SQL_COUNT_BY_EMAIL = "SELECT COUNT(*) FROM ET_USERS WHERE EMAIL = ?"
    private val SQL_FIND_BY_ID = "SELECT USER_ID, FIRST_NAME, LAST_NAME, EMAIL, PASSWORD " +
            "FROM ET_USERS WHERE USER_ID = ?"
    private val SQL_FIND_BY_EMAIL = "SELECT USER_ID, FIRST_NAME, LAST_NAME, EMAIL, PASSWORD " +
            "FROM ET_USERS WHERE EMAIL = ?"

    @Autowired
    var jdbcTemplate: JdbcTemplate? = null

    override fun create(firstName: String, lastName: String, email: String, password: String): Int {
        val hashedPassword: String = BCrypt.hashpw(password, BCrypt.gensalt(10))
        return try {
            val keyHolder: KeyHolder = GeneratedKeyHolder()
            jdbcTemplate!!.update({ connection: Connection ->
                val ps: PreparedStatement = connection.prepareStatement(SQL_CREATE, Statement.RETURN_GENERATED_KEYS)
                ps.setString(1, firstName)
                ps.setString(2, lastName)
                ps.setString(3, email)
                ps.setString(4, hashedPassword)
                ps
            }, keyHolder)
            keyHolder.keys!!["USER_ID"] as Int
        } catch (e: Exception) {
            throw EtAuthException("Invalid details. Failed to create account")
        }
    }

    override fun findByEmailAndPassword(email: String?, password: String?): User? {
        try {
            val user: User? = jdbcTemplate!!.queryForObject<com.example.demo.domain.User?>(SQL_FIND_BY_EMAIL, arrayOf<kotlin.Any?>(email), userRowMapper)
            if (!BCrypt.checkpw(password, user!!.password)) throw EtAuthException("Invalid email/password")
            return user
        } catch (e: EmptyResultDataAccessException) {
            throw EtAuthException("Invalid email/password")
        }
    }

    override fun getCountByEmail(email: String): Int? {
        return jdbcTemplate!!.queryForObject(SQL_COUNT_BY_EMAIL, arrayOf<Any>(email), Int::class.java)
    }

    override fun findById(userId: Int): User? {
        return jdbcTemplate!!.queryForObject(SQL_FIND_BY_ID, arrayOf<Any>(userId), userRowMapper)
    }

    private val userRowMapper: RowMapper<User> = RowMapper<User> { rs, rowNum ->
        User(rs.getInt("USER_ID"),
                rs.getString("FIRST_NAME"),
                rs.getString("LAST_NAME"),
                rs.getString("EMAIL"),
                rs.getString("PASSWORD"))
    }

}