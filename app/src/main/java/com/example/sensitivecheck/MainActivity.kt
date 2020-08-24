package com.example.sensitivecheck

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tttt.setOnTouchListener { it, event ->
            it.requestLayout()
            false
        }
//        dsdsds.setOnClickListener { v -> println("ds") }
    }
}