package com.example.myapplication

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class SetupRangeActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private lateinit var cameraView: WebView
    private lateinit var deviceName: String
    private lateinit var server: String
    private var auto: Boolean = true
    private val handler = Handler()
    private var runnable = Runnable{}

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setup_range)

        var angleX = 0
        var angleY = 0

        val rangeX1 = findViewById<TextView>(R.id.rangeX1)
        val rangeY1 = findViewById<TextView>(R.id.rangeY1)
        val rangeX2 = findViewById<TextView>(R.id.rangeX2)
        val rangeY2 = findViewById<TextView>(R.id.rangeY2)

        val up = findViewById<ImageView>(R.id.upSetup)
        val down = findViewById<ImageView>(R.id.downSetup)
        val left = findViewById<ImageView>(R.id.leftSetup)
        val right = findViewById<ImageView>(R.id.rightSetup)

        val intent = intent
        deviceName = intent.getStringExtra("device")!!
        auto = intent.getBooleanExtra("auto",true)
        server = intent.getStringExtra("server")!!

        auth = FirebaseAuth.getInstance()
        val uid = auth.currentUser!!.uid
        database = FirebaseDatabase.getInstance().reference.child("Users").child(uid).child("Device").child(deviceName)
        database.child("auto").setValue(0)
        var device: Device?

        database.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(ds: DatabaseError) {
                println("error")
            }
            override fun onDataChange(ds: DataSnapshot) {
                device = ds.getValue(Device::class.java)
                angleX = device!!.angle1.toInt()
                angleY = device!!.angle2.toInt()
                rangeX1.text = device!!.rangeX1; rangeY1.text = device!!.rangeY1
                rangeX2.text = device!!.rangeX2; rangeY2.text = device!!.rangeY2
            }
        })

        cameraView  = findViewById(R.id.cameraView)

        val frameVideo =
            "<html><body><br> <iframe width=\"100%\" height=\"90%\" src=\"https://www.youtube.com/embed/$server?playsinline=1\" frameborder=\"0\" allowfullscreen></iframe></body></html>"

        cameraView.webViewClient = object: WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView ,url: String): Boolean {
                return false
            }
        }
        val webSettings = cameraView.settings
        webSettings.javaScriptEnabled = true
        cameraView.loadData(frameVideo, "text/html", "utf-8")

        val saveButton = findViewById<Button>(R.id.saveButton)
        saveButton.setOnClickListener{
            database.child("rangeX1").setValue(rangeX1.text.toString())
            database.child("rangeX2").setValue(rangeX2.text.toString())
            database.child("rangeY1").setValue(rangeY1.text.toString())
            database.child("rangeY2").setValue(rangeY2.text.toString())
            Toast.makeText(baseContext, "View Range Updated Successfully", Toast.LENGTH_SHORT).show()
            goToCamera()
        }

        val leftRange = findViewById<Button>(R.id.buttonCorner1)
        leftRange.setOnClickListener{
            val x1 = angleX; val y1 = angleY
            val x2 = rangeX2.text.toString().toInt(); val y2 = rangeY2.text.toString().toInt()
            if(x2-x1 <= 70 || y2-y1<=60){
                Toast.makeText(this, "Range smaller than the camera view", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            rangeX1.text = angleX.toString(); rangeY1.text = angleY.toString()
        }

        val rightRange = findViewById<Button>(R.id.buttonCorner2)
        rightRange.setOnClickListener{
            val x2 = angleX; val y2 = angleY
            val x1 = rangeX1.text.toString().toInt(); val y1 = rangeY1.text.toString().toInt()
            if(x2-x1 <= 70 || y2-y1<=60){
                Toast.makeText(this, "Range smaller than the camera view", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            rangeX2.text = angleX.toString(); rangeY2.text = angleY.toString()
        }

        up.setOnClickListener {
            if(angleY == 0) return@setOnClickListener
            angleY -= 10
            database.child("angle2").setValue(angleY.toString())
        }

        up.setOnLongClickListener{
            runnable = Runnable {
                if (!up.isPressed) return@Runnable
                if(angleY == 0) return@Runnable
                angleY -= 10
                database.child("angle2").setValue(angleY.toString())
                handler.postDelayed(runnable, 500)
            }
            handler.postDelayed(runnable, 500)
            true
        }

        down.setOnClickListener {
            if(angleY == 180) return@setOnClickListener
            angleY += 10
            database.child("angle2").setValue(angleY.toString())
        }

        down.setOnLongClickListener{
            runnable = Runnable {
                if (!down.isPressed) return@Runnable
                if(angleY == 180) return@Runnable
                angleY += 10
                database.child("angle2").setValue(angleY.toString())
                handler.postDelayed(runnable, 500)
            }
            handler.postDelayed(runnable, 500)
            true
        }

        left.setOnClickListener {
            if(angleX == 180) return@setOnClickListener
            angleX += 10
            database.child("angle1").setValue(angleX.toString())
        }

        left.setOnLongClickListener{
            runnable = Runnable {
                if (!left.isPressed) return@Runnable
                if(angleX == 180) return@Runnable
                angleX += 10
                database.child("angle1").setValue(angleX.toString())
                handler.postDelayed(runnable, 500)
            }
            handler.postDelayed(runnable, 500)
            true
        }

        right.setOnClickListener {
            if(angleX == 0) return@setOnClickListener
            angleX -= 10
            database.child("angle1").setValue(angleX.toString())
        }

        right.setOnLongClickListener{
            runnable = Runnable {
                if (!right.isPressed) return@Runnable
                if(angleX == 0) return@Runnable
                angleX -= 10
                database.child("angle1").setValue(angleX.toString())
                handler.postDelayed(runnable, 500)
            }
            handler.postDelayed(runnable, 500)
            true
        }
    }

    private fun goToCamera(){
        val a = if(auto){ 1 } else{ 0 }
        database.child("auto").setValue(a)
        val intent = Intent(this, CameraActivity::class.java)
        intent.putExtra("name",deviceName)
        startActivity(intent)
    }

    override fun onBackPressed() {
        goToCamera()
    }
}
