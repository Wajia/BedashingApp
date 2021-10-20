package com.example.bedashingapp.views.splashscreen

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.bedashingapp.R
import com.example.bedashingapp.views.login.LoginActivity

class SplashActivity : AppCompatActivity() {
    private var threadSplash: Thread? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        startAnimations()
    }

    private fun startAnimations() {
        var anim = AnimationUtils.loadAnimation(this, R.anim.alpha)
        anim.reset()
        val l = findViewById<View>(R.id.lin_lay) as ConstraintLayout
        l.clearAnimation()
        l.startAnimation(anim)
        anim = AnimationUtils.loadAnimation(this, R.anim.translate)
        anim.reset()
        val iv = findViewById<View>(R.id.splash) as ImageView
        iv.clearAnimation()
        iv.startAnimation(anim)
        threadSplash = object : Thread() {
            override fun run() {
                try {
                    var waited = 0
                    // Splash screen pause time
                    while (waited < 3500) {
                        sleep(100)
                        waited += 100
                    }
                    startActivity(Intent(applicationContext, LoginActivity::class.java))
                    finish()
                } catch (e: InterruptedException) {
                    // do nothing
                } finally {
                    finish()
                }
            }
        }
        (threadSplash as Thread).start()
    }
}