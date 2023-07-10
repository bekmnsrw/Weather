package com.example.android1.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.android1.R
import com.example.android1.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private var viewBinding: ActivityMainBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityMainBinding.inflate(layoutInflater).also {
            setContentView(it.root)
        }

        viewBinding?.run {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.container, MainFragment(), MAIN_FRAGMENT_TAG)
                .commit()
        }
    }

    companion object {
        const val MAIN_FRAGMENT_TAG = "MAIN_FRAGMENT_TAG"
    }
}
