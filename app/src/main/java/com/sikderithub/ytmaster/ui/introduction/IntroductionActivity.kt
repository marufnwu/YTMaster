package com.sikderithub.ytmaster.ui.introduction

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.sikderithub.ytmaster.databinding.ActivityIntoductionBinding

class IntroductionActivity : AppCompatActivity() {
    lateinit var binding : ActivityIntoductionBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityIntoductionBinding.inflate(layoutInflater)
        setContentView(binding.root)


    }
}