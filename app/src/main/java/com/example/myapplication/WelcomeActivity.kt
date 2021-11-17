package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.github.paolorotolo.appintro.AppIntro
import kotlin.system.exitProcess
import android.annotation.SuppressLint
import android.view.WindowManager
import android.os.Build
import android.view.View

class WelcomeActivity : AppIntro()  {

    @SuppressLint("ResourceType", "ObsoleteSdkInt")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        supportActionBar?.hide()

        if (Build.VERSION.SDK_INT < 16) {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        } else {
            val decorView = window.decorView
            val uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN
            decorView.systemUiVisibility = uiOptions
        }

        addSlide(FirstFragment())
        addSlide(SecondFragment())
        addSlide(ThirdFragment())
        addSlide(FourthFragment())
    }

    override fun onSkipPressed(currentFragment: Fragment?) {
        super.onSkipPressed(currentFragment)
        goToMain()
    }
    override fun onDonePressed(currentFragment: Fragment?) {
        super.onDonePressed(currentFragment)
        goToMain()
    }
    //override fun onSlideChanged(oldFragment: Fragment?, newFragment: Fragment?) =  super.onSlideChanged(oldFragment, newFragment)

    override fun onBackPressed() {
        moveTaskToBack(true)
        exitProcess(-1)
    }

    private fun goToMain(){
        val first = false
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("first",first)
        startActivity(intent)
    }
}
