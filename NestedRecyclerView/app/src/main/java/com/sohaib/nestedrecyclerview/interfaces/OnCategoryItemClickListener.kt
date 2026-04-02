package com.sohaib.nestedrecyclerview.interfaces

import com.sohaib.nestedrecyclerview.models.Category

interface OnCategoryItemClickListener {
    fun onReadMoreClick(category: Category)
}