package com.example.demo.resources

import com.example.demo.domain.Category
import com.example.demo.services.CategoryService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*
import javax.servlet.http.HttpServletRequest


@RestController
@RequestMapping("/api/categories")
class CategoryResource {

    @Autowired
    val categoryService: CategoryService? = null

    @GetMapping("")
    fun getAllCategories(request: HttpServletRequest): ResponseEntity<List<Category>> {
        val userId = request.getAttribute("userId") as Int
        val categories = categoryService!!.fetchAllCategories(userId)
        return ResponseEntity(categories, HttpStatus.OK)
    }

    @GetMapping("/{categoryId}")
    fun getCategoryById(request: HttpServletRequest,
                        @PathVariable("categoryId") categoryId: Int?): ResponseEntity<Category?>? {
        val userId = request.getAttribute("userId") as Int
        val category = categoryService!!.fetchCategoryById(userId, categoryId!!)
        return ResponseEntity(category, HttpStatus.OK)
    }

    @PostMapping("")
    fun addCategory(request: HttpServletRequest,
                    @RequestBody categoryMap: Map<String, Any>): ResponseEntity<Category> {
        val userId = request.getAttribute("userId") as Int
        val title = categoryMap["title"] as String?
        val description = categoryMap["description"] as String?

        val category = categoryService!!.addCategory(userId, title!!, description!!)
        return ResponseEntity(category, HttpStatus.CREATED)
    }

    @PutMapping("/{categoryId}")
    fun updateCategory(request: HttpServletRequest,
                       @PathVariable("categoryId") categoryId: Int?,
                       @RequestBody category: Category?): ResponseEntity<Map<String, Boolean>>? {

        val userId = request.getAttribute("userId") as Int
        categoryService!!.updateCategory(userId, categoryId!!, category!!)
        val map: MutableMap<String, Boolean> = HashMap()
        map["success"] = true
        return ResponseEntity(map, HttpStatus.OK)
    }

    @DeleteMapping("/{categoryId}")
    fun deleteCategory(request: HttpServletRequest,
                       @PathVariable("categoryId") categoryId: Int): ResponseEntity<Map<String, Boolean>>? {
        val userId = request.getAttribute("userId") as Int
        categoryService!!.removeCategoryWithAllTransactions(userId, categoryId)
        val map: MutableMap<String, Boolean> = HashMap()
        map["success"] = true
        return ResponseEntity(map, HttpStatus.OK)
    }

}