package com.mytrawlbenstest.home

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.mytrawlbenstest.databinding.ActivityHomeBinding
import com.mytrawlbenstest.detail.DetailMovieActivity
import com.test.core.R
import com.test.core.data.Resource
import com.test.core.ui.MovieAdapter
import com.test.core.utils.Config
import com.test.core.utils.Utils.updateUI
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomeBinding
    private val viewModel: HomeViewModel by viewModels()
    private lateinit var nowPlayingAdapter: MovieAdapter
    private lateinit var populerAdapter: MovieAdapter
    private lateinit var topRatedAdapter: MovieAdapter
    private lateinit var upcomingAdapter: MovieAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding  = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        nowPlayingAdapter = MovieAdapter()
        populerAdapter = MovieAdapter()
        topRatedAdapter = MovieAdapter()
        upcomingAdapter = MovieAdapter()
        menu()
        getMovies(Config.NOW_PLAYING_MOVIES)
        getMovies(Config.POPULAR_MOVIES)
        getMovies(Config.TOP_RATED_MOVIES)
        getMovies(Config.UPCOMING_MOVIES)
        viewModel.getGenresMovie.observe(this){}
        seeMore()
    }


    private fun getMovies(type:String) {
        viewModel.getMovies(type, 1)

        val resultValue = when(type) {
            Config.NOW_PLAYING_MOVIES -> viewModel.resultValueNowPlaying
            Config.POPULAR_MOVIES -> viewModel.resultValuePopuler
            Config.TOP_RATED_MOVIES -> viewModel.resultValueTopRated
            else -> viewModel.resultValueUpcoming
        }


        val movieAdapter = when (type){
            Config.NOW_PLAYING_MOVIES -> nowPlayingAdapter
            Config.POPULAR_MOVIES -> populerAdapter
            Config.TOP_RATED_MOVIES -> topRatedAdapter
            else -> upcomingAdapter
        }

        val recylerview = when (type){
            Config.POPULAR_MOVIES -> binding.rvPopuler
            Config.TOP_RATED_MOVIES -> binding.rvTopRated
            else -> binding.rvUpcoming
        }

        val progressBar = when (type){
            Config.NOW_PLAYING_MOVIES -> binding.progressBar
            Config.POPULAR_MOVIES -> binding.progressBar1
            Config.TOP_RATED_MOVIES -> binding.progressBar2
            else -> binding.progressBar3
        }

        resultValue?.observe(this){result ->
            when (result) {
                is Resource.Loading -> updateUI(true, progressBar)
                is Resource.Success -> {
                    updateUI(false, progressBar)
                    movieAdapter.setData(result.data)
                }
                is Resource.Error -> {
                    updateUI(false, progressBar)
                }
                else -> {
                    updateUI(false, progressBar)
                }
            }
        }

        movieAdapter.onItemClick =  {data ->
            val intent = Intent(this, DetailMovieActivity::class.java)
            intent.putExtra(DetailMovieActivity.EXTRA_DATA, data)
            startActivity(intent)
        }

        when (type) {
            Config.NOW_PLAYING_MOVIES ->{
                binding.rvNowPlaying.adapter = nowPlayingAdapter
                binding.rvNowPlaying.apply {
                    set3DItem(true)
                    setInfinite(true)
                }
            }
            else -> {
                with(recylerview) {
                    layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                    setHasFixedSize(true)
                    adapter = movieAdapter
                }
            }
        }
    }

    private fun seeMore() {
        binding.apply {
            titleNowPlaying.setOnClickListener { intentToMovieList(Config.NOW_PLAYING_MOVIES) }
            titlePopular.setOnClickListener { intentToMovieList(Config.POPULAR_MOVIES) }
            titleTopRated.setOnClickListener { intentToMovieList(Config.TOP_RATED_MOVIES) }
            titleUpcoming.setOnClickListener {intentToMovieList(Config.UPCOMING_MOVIES) }
        }
    }

    private fun intentToMovieList(type: String){
        val intent = Intent(this@HomeActivity, MovieListActivity::class.java)
        intent.putExtra(MovieListActivity.TYPE_VALUE, type)
        startActivity(intent)
    }

    private  fun menu(){
        val menuHost: MenuHost = this
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menu.clear()
                menuInflater.inflate(R.menu.menu_home, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.action_fav -> {
                        intentToMovieList(Config.FAVORITE_MOVIE)
                        return true
                    }
                    else -> false
                }
            }
        }, this , Lifecycle.State.RESUMED)
    }
}