package com.example.playlistmaker.ui.main

import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.playlistmaker.R
import com.google.android.material.bottomnavigation.BottomNavigationView

class RootActivity : AppCompatActivity() {
    //Находим на данном экране BottomNavigationView
    private val bottomNavigationView: BottomNavigationView by lazy{ findViewById(R.id.bottomNavigationView) }
    private val viewTopLine: View by lazy{ findViewById(R.id.vTopLine) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_root)


        //Связываем BottomNavigationView и NavController
        //Получим экземпляр NavContoller
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.container_view) as NavHostFragment
        val navController = navHostFragment.navController

        //Передаем BottomNavigationView значение NavController
        bottomNavigationView.setupWithNavController(navController)

        //Слушатель изменений текущего Destination
        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.playlistAddFragment, R.id.playerFragment -> {
                    setMenuVisibility(false)
                    setInputAdjustResize()
                }

                R.id.searchFragment -> {
                    setInputAdjustPan()
                    setMenuVisibility(true)
                }

                else -> {
                    setInputAdjustResize()
                    setMenuVisibility(true)
                }
            }
        }
    }

    private fun setMenuVisibility(isVisible: Boolean){
        bottomNavigationView.isVisible = isVisible
        viewTopLine.isVisible = isVisible
    }

    private fun setInputAdjustPan() {
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
    }

    private fun setInputAdjustResize() {
        @Suppress("DEPRECATION")
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
    }
}