package com.example.playlistmaker.ui.media.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.playlistmaker.databinding.ActivityMediaBinding
import com.google.android.material.tabs.TabLayoutMediator
import org.koin.core.parameter.parametersOf
import org.koin.java.KoinJavaComponent.getKoin

class MediaActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMediaBinding

    private lateinit var tabMediator: TabLayoutMediator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMediaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.viewPager.adapter =
            getKoin().get { parametersOf(supportFragmentManager, lifecycle) } as MediaPagerAdapter

        tabMediator = getKoin().get { parametersOf(binding.tabLayout, binding.viewPager) }

        tabMediator.attach()

        binding.back.setOnClickListener { backToPreviousScreen() }
    }

    override fun onDestroy() {
        super.onDestroy()

        tabMediator.detach()
    }

    private fun backToPreviousScreen() {
        this.onBackPressedDispatcher.onBackPressed()
    }
}