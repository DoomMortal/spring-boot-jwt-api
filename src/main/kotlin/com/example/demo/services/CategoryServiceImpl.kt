package com.example.demo.services

import com.example.demo.domain.Category
import com.example.demo.repositories.CategoryRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class CategoryServiceImpl : CategoryService {

    @Autowired
    var categoryRepository: CategoryRepository? = null

    override fun fetchAllCategories(userId: Int): List<Category> {
        return categoryRepository!!.findAll(userId)
    }

    override fun fetchCategoryById(userId: Int, categoryId: Int): Category {
        return categoryRepository!!.findById(userId, categoryId)
    }

    override fun addCategory(userId: Int, title: String, description: String): Category {
        val categoryId = categoryRepository?.create(userId, title, description)
        return categoryRepository!!.findById(userId, categoryId!!.toInt())
    }

    override fun updateCategory(userId: Int, categoryId: Int, category: Category) {
        categoryRepository!!.update(userId, categoryId, category)
    }

    override fun removeCategoryWithAllTransactions(userId: Int, categoryId: Int) {
        this.fetchCategoryById(userId, categoryId)
        categoryRepository!!.removeById(userId, categoryId)
    }
}