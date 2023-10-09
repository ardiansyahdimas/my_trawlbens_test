package com.mytrawlbenstest.home

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.test.core.R
import com.mytrawlbenstest.detail.DetailMovieActivity
import com.mytrawlbenstest.databinding.ActivityMovieListBinding
import com.test.core.data.Resource
import com.test.core.ui.MovieListAdapter
import com.test.core.utils.Config
import com.test.core.utils.Utils.updateUI
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MovieListActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMovieListBinding
    private lateinit var movieAdapter: MovieListAdapter
    private val viewModel: HomeViewModel by viewModels()
    private var currentPage = 1
    private var isSearch = false

    companion object {
        const val TYPE_VALUE = "value_type"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMovieListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        movieAdapter = MovieListAdapter()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val type = intent?.getStringExtra(TYPE_VALUE)

        if (type != null) {
            title = when(type) {
                Config.NOW_PLAYING_MOVIES -> getString(R.string.now_playing)
                Config.POPULAR_MOVIES -> getString(R.string.popular)
                Config.TOP_RATED_MOVIES -> getString(R.string.top_rated)
                Config.UPCOMING_MOVIES -> getString(R.string.upcoming)
                else -> getString(R.string.favorite_movie)
            }

            if (type == Config.FAVORITE_MOVIE) {
                getFavoriteMovie()
            } else {
                getMovies(type, currentPage)
                setupRecyclerView(type)
            }
        }
        menu()
    }

    private  fun menu() {
        val menuHost: MenuHost = this
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                // Add menu items here
                menu.clear()
                menuInflater.inflate(R.menu.menu_search, menu)
                val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
                val searchView = menu.findItem(R.id.action_search)?.actionView as SearchView
                searchView.setSearchableInfo(searchManager.getSearchableInfo(componentName))
                searchView.queryHint = resources.getString(R.string.cari_disini)
                searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                    override fun onQueryTextSubmit(query: String): Boolean {
                        isSearch = query.isNotEmpty()
                        if (query.isNotEmpty()) {
                            movieAdapter.filter.filter(query)
                        }
                        return true
                    }

                    override fun onQueryTextChange(newText: String): Boolean {
                        isSearch = newText.isNotEmpty()
                        movieAdapter.filter.filter(newText)
                        return false
                    }
                })
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                // Handle the menu selection
                return when (menuItem.itemId) {
                    else -> false
                }
            }
        }, this , Lifecycle.State.RESUMED)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }

    private fun getFavoriteMovie(){
        viewModel.getFavoriteMovies.observe(this){data ->
            updateUI(false , binding.progressBar)
            binding.tvEmptyData.isVisible = data.isEmpty()
            movieAdapter.setData(data)
        }

        movieAdapter.onItemClick =  {data ->
            val intent = Intent(this, DetailMovieActivity::class.java)
            intent.putExtra(DetailMovieActivity.EXTRA_DATA, data)
            startActivity(intent)
        }

        with(binding.rvMovie) {
            layoutManager = GridLayoutManager(this@MovieListActivity, 2)
            setHasFixedSize(true)
            adapter = movieAdapter
        }
    }

    private fun getMovies(type:String, page:Int) {
        viewModel.getMovies(type, page)
        val resultValue = when(type) {
            Config.NOW_PLAYING_MOVIES -> viewModel.resultValueNowPlaying
            Config.POPULAR_MOVIES -> viewModel.resultValuePopuler
            Config.TOP_RATED_MOVIES -> viewModel.resultValueTopRated
            else -> viewModel.resultValueUpcoming
        }

        resultValue?.observe(this){result ->
            when (result) {
                is Resource.Loading -> {
                    updateUI(true, binding.progressBar)
                }
                is Resource.Success -> {
                    updateUI(false, binding.progressBar)
                    if (page > 1) {
                        result.data?.forEach {
                            movieAdapter.addItem(it)
                        }
                    } else {
                        movieAdapter.setData(result.data)
                    }
                }
                is Resource.Error -> {
                    updateUI(false, binding.progressBar)
                }
                else -> {
                    updateUI(false, binding.progressBar)
                }
            }
        }

        movieAdapter.onItemClick =  {data ->
            val intent = Intent(this, DetailMovieActivity::class.java)
            intent.putExtra(DetailMovieActivity.EXTRA_DATA, data)
            startActivity(intent)
        }

        if (page < 2) {
            with(binding.rvMovie) {
                layoutManager = GridLayoutManager(this@MovieListActivity, 2)
                setHasFixedSize(true)
                adapter = movieAdapter
            }
        }
    }

    private fun setupRecyclerView(type: String) {
        binding.rvMovie.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val layoutManager = recyclerView.layoutManager as GridLayoutManager
                val visibleItemCount = layoutManager.childCount
                val totalItemCount = layoutManager.itemCount
                val firstVisibleItem = layoutManager.findFirstVisibleItemPosition()

                if ((visibleItemCount + firstVisibleItem) >= totalItemCount && !isSearch) {
                    currentPage++
                    getMovies(type, currentPage)
                }
            }
        })
    }
}