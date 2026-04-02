package com.sohaib.nestedrecyclerview.interfaces

import com.sohaib.nestedrecyclerview.models.Movie

interface OnMovieItemClickListener {
    fun onItemClick(movie: Movie)
}