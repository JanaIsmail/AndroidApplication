package com.example.myapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.journeyapps.barcodescanner.BarcodeEncoder

class PairDeviceActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private lateinit var databaseServers: DatabaseReference
    private lateinit var barcodeImage: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pair_device)

        auth = FirebaseAuth.getInstance()
        val uid = auth.currentUser!!.uid
        database = FirebaseDatabase.getInstance().reference.child("Users").child(uid).child("Device")
        databaseServers = FirebaseDatabase.getInstance().reference.child("Users").child("Available Links")

        val pairDeviceButton = findViewById<Button>(R.id.pairDeviceButton)
        val deviceName = findViewById<TextView>(R.id.deviceName)
        val instruction = findViewById<TextView>(R.id.instruction)
        barcodeImage = findViewById(R.id.barcodeImage)
        val code = intent.getStringExtra("code")!!
        val secret = intent.getStringExtra("secret")!!

        pairDeviceButton.setOnClickListener{
            val name = deviceName.text.toString()
            if(name.isEmpty()) {
                deviceName.error = "Enter device name"
                return@setOnClickListener
            }
            pairDeviceButton.visibility = View.INVISIBLE
            deviceName.visibility = View.INVISIBLE
            instruction.visibility = View.VISIBLE
            barcodeImage.visibility = View.VISIBLE
            val newDevice = Device()
            newDevice.angle1 = "90"; newDevice.angle2 = "90"
            newDevice.name = name; newDevice.code = code; newDevice.secret = secret
            newDevice.status = -1
            newDevice.auto = 0; newDevice.scan = 0; newDevice.recenter = 0
            newDevice.rangeX1 = "10"; newDevice.rangeX2 = "170"
            newDevice.rangeY1 = "10"; newDevice.rangeY2 = "170"
            pair(name,code,secret)
            val newChild = database.child(name)
            newChild.setValue(newDevice)
            newChild.addChildEventListener(object : ChildEventListener {
                override fun onChildMoved(p0: DataSnapshot, p1: String?) { TODO("not implemented") }
                override fun onCancelled(p0: DatabaseError) { TODO("not implemented") }
                override fun onChildRemoved(dataSnapshot: DataSnapshot) {}
                override fun onChildChanged(dataSnapshot: DataSnapshot, p1: String?) {
                    databaseServers.child(code).removeValue()
                    goToDevices()
                    newChild.removeEventListener(this)
                }
                override fun onChildAdded(dataSnapshot: DataSnapshot, p1: String?) {}
            })
        }
//        val later = findViewById<TextView>(R.id.later)
//        later.setOnClickListener(){
//            startActivity(Intent(this,PairFirstDeviceActivity::class.java))
//        }
    }

    private fun goToDevices() { startActivity(Intent(this, DevicesActivity::class.java)) }

    private fun pair(name: String, code: String, secret: String){
        val text = auth.currentUser!!.uid  + ";;" + name + ";;" + code + ";;" + secret
        val multiFormatWriter = MultiFormatWriter()
        val bitMatrix = multiFormatWriter.encode(text, BarcodeFormat.QR_CODE,200,200)
        val barcodeEncoder = BarcodeEncoder()
        val bitmap = barcodeEncoder.createBitmap(bitMatrix)
        barcodeImage.setImageBitmap(bitmap)

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.sign_out,menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == R.id.signOutScanItem) {
            auth.signOut()
            startActivity(Intent(this,MainActivity::class.java))
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
//        val first = true
//        val intent = Intent(this, DevicesActivity::class.java)
//        intent.putExtra("first",first)
//        startActivity(intent)
        startActivity(Intent(this,ServersActivity::class.java))
    }
}
