package com.udacity.asteroidradar.ui.main

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.udacity.asteroidradar.data.Asteroid
import com.udacity.asteroidradar.data.PictureOfDay
import com.udacity.asteroidradar.database.AsteroidRadarDatabase
import com.udacity.asteroidradar.database.getDatabase
import com.udacity.asteroidradar.repository.AsteroidRepository
import com.udacity.asteroidradar.repository.PictureOfDayRepository
import com.udacity.asteroidradar.util.LoadingStatus
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : ViewModel() {

    private val radarDatabase: AsteroidRadarDatabase = getDatabase(application)
    private val pictureOfDayRepository = PictureOfDayRepository()
    private val asteroidRepository = AsteroidRepository(radarDatabase)

    private val _loadingStatus = MutableLiveData<LoadingStatus>()
    val loadingStatus: LiveData<LoadingStatus>
        get() = _loadingStatus

    init {
        _loadingStatus.value = LoadingStatus.LOADING
        viewModelScope.launch {
            try {
                _loadingStatus.value = LoadingStatus.DONE
                pictureOfDayRepository.refreshPictureOfDay()
                asteroidRepository.refreshAsteroids()
            } catch (ex: Throwable) {
                _loadingStatus.value = LoadingStatus.DONE
                ex.printStackTrace()
            }
        }
    }

    val pictureOfDay: LiveData<PictureOfDay> = pictureOfDayRepository.pictureOfDay
    val asteroids: LiveData<List<Asteroid>> = asteroidRepository.asteroids
}