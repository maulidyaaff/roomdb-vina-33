package com.example.room_vina_33

import androidx.room.*

@Dao
interface MovieDao {

    @Insert
    suspend fun addMovie(Movie: Movie)

    @Update
    suspend fun updateMovie(Movie: Movie)

    @Delete
    suspend fun deleteMovie(Movie: Movie)

    @Query ("SELECT * FROM movie")
    suspend fun getMovie():List<Movie>

    @Query ("SELECT * FROM movie WHERE id=:movie_id")
    suspend fun getMovies(movie_id: Int):List<Movie>
}