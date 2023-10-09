package com.mytrawlbenstest.detail

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.mytrawlbenstest.databinding.ActivityDetailMovieBinding
import com.mytrawlbenstest.home.HomeViewModel
import com.test.core.R
import com.test.core.data.Resource
import com.test.core.domain.model.MovieModel
import com.test.core.ui.ReviewMovieAdapter
import com.test.core.utils.Utils
import com.test.core.utils.Utils.showNotif
import com.test.core.utils.Utils.toList
import com.test.core.utils.Utils.updateUI
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class DetailMovieActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailMovieBinding
    private val viewModel: HomeViewModel by viewModels()
    private lateinit var reviewMovieAdapter: ReviewMovieAdapter
    private var isEmptyReview = false
    private var _updateFav = MutableLiveData<Boolean>()
    private val updateFav: LiveData<Boolean> = _updateFav

    companion object {
        const val EXTRA_DATA = "extra_data"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailMovieBinding.inflate(layoutInflater)
        setContentView(binding.root)
        title = ""
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val data = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent?.getParcelableExtra(EXTRA_DATA, MovieModel::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent?.getParcelableExtra(EXTRA_DATA)
        }

        if (data != null) {
            reviewMovieAdapter = ReviewMovieAdapter()
            getReviewsMovie(data.id)
            showData(data)
            menu(data)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }

    private  fun menu(data: MovieModel){
        val menuHost: MenuHost = this
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menu.clear()
                menuInflater.inflate(R.menu.menu_home, menu)
                val menuFav = menu.findItem(R.id.action_fav)
                val currentStatusFav = data.isFavorite
                var drawFav =  if (currentStatusFav == true) R.drawable.ic_favorite  else   R.drawable.ic_favorite_border
                menuFav.icon = ContextCompat.getDrawable(this@DetailMovieActivity,drawFav)
                updateFav.observe(this@DetailMovieActivity) { isFav ->
                    drawFav =  if (isFav) R.drawable.ic_favorite  else R.drawable.ic_favorite_border
                    menuFav.icon = ContextCompat.getDrawable(this@DetailMovieActivity,drawFav)
                }
            }

            @SuppressLint("NullSafeMutableLiveData")
            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.action_fav -> {
                        var statusFav = data.isFavorite
                        statusFav = !statusFav!!
                        data.isFavorite = statusFav
                        viewModel.setFavoriteMovie(data, statusFav)
                        _updateFav.postValue(statusFav)
                        val messageId = if(statusFav) R.string.info_add_favorite else R.string.info_remove_favorite
                        showNotif(this@DetailMovieActivity, getString(R.string.app_name), getString(messageId, data.title))
                        return true
                    }
                    else -> false
                }
            }
        }, this , Lifecycle.State.RESUMED)
    }

    @SuppressLint("SetTextI18n")
    private fun showData(data:MovieModel) {
        with(binding){
            Glide.with(this@DetailMovieActivity)
                .load(getString(R.string.baseUrlImage, data.poster_path))
                .into(poster)
            Glide.with(this@DetailMovieActivity)
                .load(getString(R.string.baseUrlImage, data.backdrop_path))
                .into(backDrop)
            tvTitle.text = data.title
            tvRating.text = "${data.vote_average} / ${data.vote_count}"
            tvOverview.text = data.overview
            tvReleaseDate.text = "Release Date: ${data.release_date}"
            getGenreMovie(data)
        }
    }

    private fun getGenreMovie(data: MovieModel){
        val listGenreId = toList(data.genreIds)
        val listGenre = ArrayList<String>()
        if (listGenreId != null) {
            viewModel.getGenreMovieById(listGenreId)
            viewModel.resultGenreMovie?.observe(this)
            {
                it.data?.map { listGenre.add(it.name.toString()) }
                Timber.tag("GENRE-NAME").d(listGenre.toString())
                binding.tvGenres.text = "Genre: ${listGenre.joinToString(", ")}"
            }
        }
    }


    private fun getReviewsMovie(movieId: Int) {
        viewModel.getReviewsMovieByMovieId(movieId)
        viewModel.resultReviewsMovie?.observe(this){result ->
            when (result) {
                is Resource.Loading -> updateUI(true, binding.progressBar)
                is Resource.Success -> {
                    isEmptyReview = result.data?.size == reviewMovieAdapter.itemCount
                    updateUI(false, binding.progressBar)
                    reviewMovieAdapter.setData(result.data)

                }
                is Resource.Error -> {
                    updateUI(false, binding.progressBar)
                }
                else -> {
                    updateUI(false, binding.progressBar)
                }
            }
        }
        with(binding.rvReview) {
            layoutManager =  GridLayoutManager(this@DetailMovieActivity,2,GridLayoutManager.HORIZONTAL, false)
            setHasFixedSize(true)
            adapter = reviewMovieAdapter
        }
    }
}