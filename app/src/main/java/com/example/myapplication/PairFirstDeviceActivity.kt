package com.example.myapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import com.google.firebase.auth.FirebaseAuth
import kotlin.system.exitProcess

class PairFirstDeviceActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pair_first_device)

        auth = FirebaseAuth.getInstance()
        val pairFirstDevice = findViewById<ImageView>(R.id.pairFirstDevice)
        pairFirstDevice.setOnClickListener { startActivity(Intent(this,ServersActivity::class.java)) }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.sign_out,menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == R.id.signOutItem) {
            auth.signOut()
            startActivity(Intent(this,MainActivity::class.java))
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        moveTaskToBack(true)
        exitProcess(-1)
    }
}
