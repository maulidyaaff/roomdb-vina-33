package com.example.room_vina_33

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {
    val db by lazy { MovieDb(this) }
    lateinit var movieAdapter: MovieAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setupRecyclerView()

        val add = findViewById<Button>(R.id.add_movie)
        add.setOnClickListener {
            intentEdit(0,Constant.TYPE_CREATE)
        }

    }

    override fun onStart(){
        super.onStart()
        loadMovie()
    }

    private fun loadMovie() {
        CoroutineScope(Dispatchers.IO).launch {
            val movie = db.movieDao().getMovie()
            Log.d("MainActivity", "dbresponse: $movie")
            withContext(Dispatchers.Main){
                movieAdapter.setData(movie)
            }
        }
    }



    private fun setupRecyclerView() {
        movieAdapter = MovieAdapter(arrayListOf(), object : MovieAdapter.OnAdapterListener{
            override fun onClick(movie: Movie) {
                intentEdit(movie.id,Constant.TYPE_READ)
            }

            override fun onUpdate(movie: Movie) {
                intentEdit(movie.id,Constant.TYPE_UPDATE)
            }

            override fun onDelete(movie: Movie) {
                deleteDialog(movie)
            }

        })
        rv_movie.apply {
            layoutManager = LinearLayoutManager(applicationContext)
            addItemDecoration(
                DividerItemDecoration(
                    baseContext,
                    (layoutManager as LinearLayoutManager).orientation
                )
            )
            adapter = movieAdapter
        }
    }

    fun intentEdit(movieId: Int, intentType: Int){
        startActivity(
            Intent(applicationContext, AddActivity::class.java)
                .putExtra("intent_id", movieId)
                .putExtra("intent_type", intentType)
        )
    }

    private fun deleteDialog(movie: Movie){
        val alertDialog = AlertDialog.Builder(this)
        alertDialog.apply {
            setTitle("Konfirmasi")
            setMessage("yakin hapus ${movie.title}?")
            setNegativeButton("Batal"){ dialogInterface, i->
                dialogInterface.dismiss()
            }
            setPositiveButton("Hapus"){ dialogInterface, i->
                dialogInterface.dismiss()
                CoroutineScope(Dispatchers.IO).launch {
                    db.movieDao().deleteMovie(movie)
                    loadMovie()
                }
            }
        }
        alertDialog.show()
    }
}