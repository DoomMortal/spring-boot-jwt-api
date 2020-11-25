package com.example.demo.repositories

import com.example.demo.domain.Category
import com.example.demo.exceptions.EtBadRequestException
import com.example.demo.exceptions.EtResourceNotFoundException
import kotlin.jvm.Throws

interface CategoryRepository {

    @Throws(EtResourceNotFoundException::class)
    fun findAll(userId: Int) : List<Category>

    @Throws(EtResourceNotFoundException::class)
    fun findById(userId: Int, categoryId: Int) : Category

    @Throws(EtBadRequestException::class)
    fun create(userId: Int, title:String, description:String) : Int

    @Throws(EtBadRequestException::class)
    fun update(userId: Int, categoryId: Int, category: Category)

    fun removeById(userId: Int, categoryId: Int)

}