package com.example.demo.services

import com.example.demo.domain.Category
import com.example.demo.exceptions.EtAuthException
import com.example.demo.exceptions.EtBadRequestException
import com.example.demo.exceptions.EtResourceNotFoundException
import kotlin.jvm.Throws

interface CategoryService {

    fun fetchAllCategories(userId: Int): List<Category>

    @Throws(EtResourceNotFoundException::class)
    fun fetchCategoryById(userId: Int, categoryId: Int): Category

    @Throws(EtBadRequestException::class)
    fun addCategory(userId: Int, title: String, description: String): Category

    @Throws(EtBadRequestException::class)
    fun updateCategory(userId: Int, categoryId: Int, category: Category)

    @Throws(EtResourceNotFoundException::class)
    fun removeCategoryWithAllTransactions(userId: Int, categoryId: Int)

}