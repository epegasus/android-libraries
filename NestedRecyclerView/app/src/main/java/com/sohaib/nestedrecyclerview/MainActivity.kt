package com.sohaib.nestedrecyclerview

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.sohaib.nestedrecyclerview.databinding.ActivityMainBinding
import com.sohaib.nestedrecyclerview.adapters.AdapterCategories
import com.sohaib.nestedrecyclerview.dataProviders.DpCategories
import com.sohaib.nestedrecyclerview.interfaces.OnCategoryItemClickListener
import com.sohaib.nestedrecyclerview.interfaces.OnMovieItemClickListener
import com.sohaib.nestedrecyclerview.models.Category
import com.sohaib.nestedrecyclerview.models.Movie

class MainActivity : AppCompatActivity() {

    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    private val adapterCategories by lazy { AdapterCategories(categoryItemClick, movieItemClick) }

    private val dpCategories by lazy { DpCategories() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        initRecyclerView()
        fetchData()

        binding.srlRefreshMain.setOnRefreshListener { fetchData() }
    }

    private fun initRecyclerView() {
        binding.rvCategoriesMain.adapter = adapterCategories
    }

    private fun fetchData() {
        binding.srlRefreshMain.isRefreshing = false
        adapterCategories.submitList(dpCategories.categoriesList)
    }

    private val categoryItemClick = object : OnCategoryItemClickListener {
        override fun onReadMoreClick(category: Category) {
            Toast.makeText(this@MainActivity, category.title, Toast.LENGTH_SHORT).show()
        }
    }

    private val movieItemClick = object : OnMovieItemClickListener {
        override fun onItemClick(movie: Movie) {
            Toast.makeText(this@MainActivity, movie.title, Toast.LENGTH_SHORT).show()
        }
    }
}