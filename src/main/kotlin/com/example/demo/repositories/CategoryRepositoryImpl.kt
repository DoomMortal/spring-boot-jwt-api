package com.example.demo.repositories

import com.example.demo.domain.Category
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
class CategoryRepositoryImpl : CategoryRepository {

    private val SQL_FIND_ALL = "SELECT C.CATEGORY_ID, C.USER_ID, C.TITLE, C.DESCRIPTION, " +
            "COALESCE(SUM(T.AMOUNT), 0) TOTAL_EXPENSE " +
            "FROM ET_TRANSACTIONS T RIGHT OUTER JOIN ET_CATEGORIES C ON C.CATEGORY_ID = T.CATEGORY_ID " +
            "WHERE C.USER_ID = ? GROUP BY C.CATEGORY_ID"
    private val SQL_FIND_BY_ID = "SELECT C.CATEGORY_ID, C.USER_ID, C.TITLE, C.DESCRIPTION, " +
            "COALESCE(SUM(T.AMOUNT), 0) TOTAL_EXPENSE " +
            "FROM ET_TRANSACTIONS T RIGHT OUTER JOIN ET_CATEGORIES C ON C.CATEGORY_ID = T.CATEGORY_ID " +
            "WHERE C.USER_ID = ? AND C.CATEGORY_ID = ? GROUP BY C.CATEGORY_ID"
    private val SQL_CREATE = "INSERT INTO ET_CATEGORIES (CATEGORY_ID, USER_ID, TITLE, DESCRIPTION) VALUES(NEXTVAL('ET_CATEGORIES_SEQ'), ?, ?, ?)"
    private val SQL_UPDATE = "UPDATE ET_CATEGORIES SET TITLE = ?, DESCRIPTION = ? " +
            "WHERE USER_ID = ? AND CATEGORY_ID = ?"
    private val SQL_DELETE_CATEGORY = "DELETE FROM ET_CATEGORIES WHERE USER_ID = ? AND CATEGORY_ID = ?"
    private val SQL_DELETE_ALL_TRANSACTIONS = "DELETE FROM ET_TRANSACTIONS WHERE CATEGORY_ID = ?"

    @Autowired
    val jdbcTemplate: JdbcTemplate? = null

    override fun findAll(userId: Int): List<Category> {
        return jdbcTemplate!!.query(SQL_FIND_ALL, arrayOf(userId), categoryRowMapper);
    }

    override fun findById(userId: Int, categoryId: Int): Category {
        try {
            return jdbcTemplate!!.queryForObject(SQL_FIND_BY_ID, arrayOf<Any?>(userId, categoryId), categoryRowMapper)!!
        } catch (e: Exception) {
            throw EtResourceNotFoundException("Category not found")
        }
    }

    override fun create(userId: Int, title: String, description: String): Int {
        return try {
            val keyHolder: KeyHolder = GeneratedKeyHolder()
            jdbcTemplate!!.update({ connection: Connection ->
                val ps: PreparedStatement = connection.prepareStatement(SQL_CREATE, Statement.RETURN_GENERATED_KEYS)
                ps.setInt(1, userId)
                ps.setString(2, title)
                ps.setString(3, description)
                ps
            }, keyHolder)
            keyHolder.keys!!["CATEGORY_ID"] as Int
        } catch (e: Exception) {
            throw EtBadRequestException("Invalid request")
        }

    }

    override fun update(userId: Int, categoryId: Int, category: Category) {

        try {

            val keyHolder: KeyHolder = GeneratedKeyHolder()
            jdbcTemplate!!.update({ connection: Connection ->
                val ps: PreparedStatement = connection.prepareStatement(SQL_UPDATE, Statement.RETURN_GENERATED_KEYS)
                ps.setString(1, category.title)
                ps.setString(2, category.description)
                ps.setInt(3, userId)
                ps.setInt(4, categoryId)
                ps
            }, keyHolder)

        } catch (e: Exception) {
            throw EtBadRequestException("Invalid request")
        }
    }

    override fun removeById(userId: Int, categoryId: Int) {
        this.removeAllCatTransactions(categoryId)

        val keyHolder: KeyHolder = GeneratedKeyHolder()
        jdbcTemplate!!.update({ connection: Connection ->
            val ps: PreparedStatement = connection.prepareStatement(SQL_DELETE_CATEGORY, Statement.RETURN_GENERATED_KEYS)
            ps.setInt(1, userId)
            ps.setInt(2, categoryId)
            ps
        }, keyHolder)

        //jdbcTemplate!!.update(SQL_DELETE_CATEGORY, arrayOf(userId, categoryId))
    }

    private fun removeAllCatTransactions(categoryId: Int) {

        val keyHolder: KeyHolder = GeneratedKeyHolder()
        jdbcTemplate!!.update({ connection: Connection ->
            val ps: PreparedStatement = connection.prepareStatement(SQL_DELETE_ALL_TRANSACTIONS, Statement.RETURN_GENERATED_KEYS)
            ps.setInt(1, categoryId)
            ps
        }, keyHolder)

        //jdbcTemplate!!.update(SQL_DELETE_ALL_TRANSACTIONS, arrayOf(categoryId))
    }

    private val categoryRowMapper: RowMapper<Category> = RowMapper<Category> { rs, rowNum ->
        Category(rs.getInt("CATEGORY_ID"),
                rs.getInt("USER_ID"),
                rs.getString("TITLE"),
                rs.getString("DESCRIPTION"),
                rs.getDouble("TOTAL_EXPENSE"))
    }
}