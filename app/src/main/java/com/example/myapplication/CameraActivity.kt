package com.example.myapplication

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.media.MediaMetadataRetriever
import android.media.ThumbnailUtils
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.util.DisplayMetrics
import android.util.Log
import android.view.*
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.io.File
import java.io.FileOutputStream

interface MyCallbackCode { fun onCallback(value:String) }

class CameraActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private lateinit var cameraView: WebView
    private lateinit var name: String
    private lateinit var server: String
    private lateinit var frameVideo: String
    private lateinit var link: String
    private lateinit var auto: Switch
    private lateinit var up: ImageView
    private lateinit var down: ImageView
    private lateinit var left: ImageView
    private lateinit var right: ImageView
    private val handler = Handler()
    private var runnable = Runnable{}

    @SuppressLint("WrongViewCast", "ClickableViewAccessibility", "SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera)

        up = findViewById<ImageView>(R.id.up)
        down = findViewById<ImageView>(R.id.down)
        left = findViewById<ImageView>(R.id.left)
        right = findViewById<ImageView>(R.id.right)

        val angle1Text = findViewById<TextView>(R.id.angle1Text)
        val angle2Text = findViewById<TextView>(R.id.angle2Text)
        auto = findViewById(R.id.automatic)
        cameraView  = findViewById(R.id.cameraView)

        val intent = intent
        name = intent.getStringExtra("name")!!

        auth = FirebaseAuth.getInstance()
        val uid = auth.currentUser!!.uid
        database = FirebaseDatabase.getInstance().reference.child("Users").child(uid).child("Device").child(name)
        var device: Device?

        fun getCode(myCall: MyCallbackCode){
            database.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(ds: DatabaseError) {
                    println("error")
                }
                override fun onDataChange(ds: DataSnapshot) {
                    device = ds.getValue(Device::class.java)
                    angle1Text.text = device!!.angle1
                    angle2Text.text = device!!.angle2
                    auto.isChecked = device!!.auto == 1
                    server = device!!.code
                    myCall.onCallback(server)
                }
            })
        }

        getCode(object : MyCallbackCode {
            override fun onCallback(value:String) {
                frameVideo =
                    "<html><body><br> <iframe width=\"100%\" height=\"90%\" src=\"https://www.youtube.com/embed/$server?playsinline=1\" frameborder=\"0\" allowfullscreen></iframe></body></html>"
                link = "https://www.youtube.com/embed/$server"
                cameraView.webViewClient = object: WebViewClient() {
                    override fun shouldOverrideUrlLoading(view: WebView ,url: String): Boolean {
                        return false
                    }
                }

                val webSettings = cameraView.settings
                webSettings.javaScriptEnabled = true
                cameraView.loadData(frameVideo, "text/html", "utf-8")
            }
        })

        auto.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                database.child("auto").setValue(1)
            } else {
                database.child("auto").setValue(0)
            }
        }

        val zoomIn = findViewById<ImageView>(R.id.zoomIn)
        zoomIn.setOnClickListener{ cameraView.zoomIn() }

        val zoomOut = findViewById<ImageView>(R.id.zoomOut)
        zoomOut.setOnClickListener { cameraView.zoomOut() }

        val capture = findViewById<ImageView>(R.id.capture)
        capture.setOnClickListener {
            saveImage(ss(cameraView),"MDP16-" + System.currentTimeMillis().toString()+".png")
        }

        left.setOnClickListener {
            if(auto.isChecked){
                Toast.makeText(this, "Disable auto mode to control the camera", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val angle = angle1Text.text.toString().toInt()
            if(angle == 180) return@setOnClickListener
            val newAngle = angle + 10
            angle1Text.text = newAngle.toString()
            database.child("angle1").setValue(newAngle.toString())
        }

        left.setOnLongClickListener{
            if(auto.isChecked){
                Toast.makeText(this, "Disable auto mode to control the camera", Toast.LENGTH_SHORT).show()
                return@setOnLongClickListener true
            }
            runnable = Runnable {
                if (!left.isPressed) return@Runnable
                val angle = angle1Text.text.toString().toInt()
                if(angle == 180) return@Runnable
                val newAngle = angle + 10
                angle1Text.text = newAngle.toString()
                database.child("angle1").setValue(newAngle.toString())
                handler.postDelayed(runnable, 500)
            }
            handler.postDelayed(runnable, 500)
            true
        }

        right.setOnClickListener {
            if(auto.isChecked){
                Toast.makeText(this, "Disable auto mode to control the camera", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val angle = angle1Text.text.toString().toInt()
            if(angle == 0) return@setOnClickListener
            val newAngle = angle - 10
            angle1Text.text = newAngle.toString()
            database.child("angle1").setValue(newAngle.toString())
        }

        right.setOnLongClickListener{
            if(auto.isChecked){
                Toast.makeText(this, "Disable auto mode to control the camera", Toast.LENGTH_SHORT).show()
                return@setOnLongClickListener true
            }
            runnable = Runnable {
                if (!right.isPressed) return@Runnable
                val angle = angle1Text.text.toString().toInt()
                if(angle == 0) return@Runnable
                val newAngle = angle - 10
                angle1Text.text = newAngle.toString()
                database.child("angle1").setValue(newAngle.toString())
                handler.postDelayed(runnable, 500)
            }
            handler.postDelayed(runnable, 500)
            true
        }

        up.setOnClickListener {
            if(auto.isChecked){
                Toast.makeText(this, "Disable auto mode to control the camera", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val angle = angle2Text.text.toString().toInt()
            if(angle == 0) return@setOnClickListener
            val newAngle = angle - 10
            angle2Text.text = newAngle.toString()
            database.child("angle2").setValue(newAngle.toString())
        }

        up.setOnLongClickListener{
            if(auto.isChecked){
                Toast.makeText(this, "Disable auto mode to control the camera", Toast.LENGTH_SHORT).show()
                return@setOnLongClickListener true
            }
            runnable= Runnable {
                if (!up.isPressed) return@Runnable
                val angle = angle2Text.text.toString().toInt()
                if(angle == 0) return@Runnable
                val newAngle = angle - 10
                angle2Text.text = newAngle.toString()
                database.child("angle2").setValue(newAngle.toString())
                handler.postDelayed(runnable, 500)
            }
            handler.postDelayed(runnable, 500)
            true
        }

        down.setOnClickListener {
            if(auto.isChecked){
                Toast.makeText(this, "Disable auto mode to control the camera", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val angle = angle2Text.text.toString().toInt()
            if(angle == 180) return@setOnClickListener
            val newAngle = angle + 10
            angle2Text.text = newAngle.toString()
            database.child("angle2").setValue(newAngle.toString())
        }

        down.setOnLongClickListener{
            if(auto.isChecked){
                Toast.makeText(this, "Disable auto mode to control the camera", Toast.LENGTH_SHORT).show()
                return@setOnLongClickListener true
            }
            runnable= Runnable {
                if (!down.isPressed) return@Runnable
                val angle = angle2Text.text.toString().toInt()
                if(angle == 180) return@Runnable
                val newAngle = angle + 10
                angle2Text.text = newAngle.toString()
                database.child("angle2").setValue(newAngle.toString())
                handler.postDelayed(runnable, 500)
            }
            handler.postDelayed(runnable, 500)
            true
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.camera_settings,menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == R.id.setupAngles) {
            goToSetup()
            finish()
            return true
        }
        if(id == R.id.recenter){
            database.child("recenter").setValue(1)
            database.addChildEventListener(object : ChildEventListener {
                override fun onChildMoved(p0: DataSnapshot, p1: String?) {}
                override fun onCancelled(p0: DatabaseError) {}
                override fun onChildRemoved(dataSnapshot: DataSnapshot) {}
                override fun onChildChanged(dataSnapshot: DataSnapshot, p1: String?) {
                    database.removeEventListener(this)
                    showFeedbackDialog()
                }
                override fun onChildAdded(dataSnapshot: DataSnapshot, p1: String?) {}
            })
            return true
        }
        if(id == R.id.scan){
            showConfirmDialog()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun takeScreenshot(view: WebView): Bitmap{
        val bitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        view.draw(canvas)
        return bitmap
    }

    private fun ss(view: WebView): Bitmap
    {
        try {
            //val link = "https://www.youtube.com/embed/jH2akHswY8M"
            Log.e("myTag","Here1")
            val mediametadataretriever = MediaMetadataRetriever()
            Log.e("myTag","Here1.0")
            mediametadataretriever.setDataSource(view.url)
            Log.e("myTag","Here2")
            val bitmap = mediametadataretriever.getFrameAtTime(-1L)
            Log.e("myTag","Here3")
            if(null != bitmap)
            {
                Log.e("myTag","Here4")
                return ThumbnailUtils.extractThumbnail(bitmap, view.width, view.height, 2)
            }
            Log.e("myTag","Here5")
            mediametadataretriever.release()
            return bitmap
        } catch (t: Throwable) {
            Log.e("myTag",t.toString())
            return Bitmap.createBitmap(view.width, view.height , Bitmap.Config.ARGB_8888)
        }
    }

    private fun saveImage(bitmap: Bitmap, name: String){
        Log.e("myTag","Here7")
        val root = getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!
        try{
            val out = FileOutputStream(File(root, name))
            bitmap.compress(Bitmap.CompressFormat.PNG,100,out)
            out.flush()
            out.close()
            Toast.makeText(applicationContext, "Image saved to $root",Toast.LENGTH_SHORT).show()
        } catch(e: Exception){
            Toast.makeText(applicationContext,"IMAGE NOT SAVED",Toast.LENGTH_SHORT).show()
            e.printStackTrace()
        }
    }

    private fun showFeedbackDialog(){
        val dialog = Dialog(this)
        dialog.setCancelable(false)
        val inflater: LayoutInflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view= inflater.inflate(R.layout.feedback_popup, null)
        view.setBackgroundColor(Color.TRANSPARENT)
        dialog.setContentView(view)
        val no = dialog.findViewById<Button>(R.id.no)
        no.setOnClickListener{
            dialog.dismiss()
            goToSetup()
        }
        val yes = dialog.findViewById<Button>(R.id.yes)
        yes.setOnClickListener{
            dialog.dismiss()
            Toast.makeText(baseContext, "Camera Recentered", Toast.LENGTH_SHORT).show()
        }
        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        val width = (displayMetrics.widthPixels * 0.9).toInt()
        val height = (displayMetrics.heightPixels * 0.25).toInt()
        dialog.window?.setLayout(width,height)
        dialog.show()
        dialog.setOnKeyListener { _, keyCode, _ ->
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                finish()
                dialog.dismiss()
                goToSetup()
            }
            true
        }
    }

    private fun showConfirmDialog(){
        val dialog = Dialog(this)
        dialog.setCancelable(false)
        val inflater: LayoutInflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view= inflater.inflate(R.layout.confirm_popup, null)
        view.setBackgroundColor(Color.TRANSPARENT)
        dialog.setContentView(view)
        val no = dialog.findViewById<Button>(R.id.cancelScan)
        no.setOnClickListener{
            dialog.dismiss()
        }
        val yes = dialog.findViewById<Button>(R.id.startScan)
        yes.setOnClickListener{
            dialog.dismiss()
            scan()
        }
        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        val width = (displayMetrics.widthPixels * 0.9).toInt()
        val height = (displayMetrics.heightPixels * 0.30).toInt()
        dialog.window?.setLayout(width,height)
        dialog.show()
        dialog.setOnKeyListener { _, keyCode, _ ->
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                dialog.dismiss()
            }
            true
        }
    }

    private fun scan(){
        database.child("scan").setValue(1)
        up.visibility = View.INVISIBLE; down.visibility = View.INVISIBLE; left.visibility = View.INVISIBLE; right.visibility = View.INVISIBLE
        database.addChildEventListener(object : ChildEventListener {
            override fun onChildMoved(p0: DataSnapshot, p1: String?) {}
            override fun onCancelled(p0: DatabaseError) {}
            override fun onChildRemoved(dataSnapshot: DataSnapshot) {}
            override fun onChildChanged(dataSnapshot: DataSnapshot, p1: String?) {
                database.removeEventListener(this)
                Toast.makeText(applicationContext, "Scan Complete",Toast.LENGTH_SHORT).show()
                up.visibility = View.VISIBLE; down.visibility = View.VISIBLE; left.visibility = View.VISIBLE; right.visibility = View.VISIBLE
            }
            override fun onChildAdded(dataSnapshot: DataSnapshot, p1: String?) {}
        })
    }

    private fun goToSetup(){
        val device = name
        val intent = Intent(this, SetupRangeActivity::class.java)
        intent.putExtra("device",device)
        intent.putExtra("server",server)
        intent.putExtra("auto",auto.isChecked)
        startActivity(intent)
    }

    override fun onBackPressed() {
        startActivity(Intent(this,DevicesActivity::class.java))
    }
}
