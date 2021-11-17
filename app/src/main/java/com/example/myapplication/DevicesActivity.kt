package com.example.myapplication

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlin.system.exitProcess
import android.view.Menu
import android.widget.*
import android.view.MenuItem as MenuItem

interface MyCallback { fun onCallback(value:ArrayList<Device>) }

class DevicesActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    var adapter: DevicesAdapter? = null

    @SuppressLint("WrongConstant")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_devices)

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerview)
        recyclerView.layoutManager = LinearLayoutManager(this, LinearLayout.VERTICAL, false)
        val users = ArrayList<Device>()

        auth = FirebaseAuth.getInstance()
        val uid = auth.currentUser!!.uid
        database = FirebaseDatabase.getInstance().reference.child("Users").child(uid).child("Device")

        fun getUsers(myCall: MyCallback){
            database.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(ds: DatabaseError) {
                    println("error")
                }
                override fun onDataChange(ds: DataSnapshot) {
                    for (snap in ds.children) {
                        val device = snap.getValue(Device::class.java)
                        if (device!!.status == -1) continue
                        users.add(device)
                    }
                    myCall.onCallback(users)
                }
            })
        }

        val context = this
        getUsers(object : MyCallback {
            override fun onCallback(value:ArrayList<Device>) {
                if(value.isEmpty()){
                    goToPairActivity()
                }
                else{
                    adapter = DevicesAdapter(value,context)
                    adapter!!.notifyDataSetChanged()
                    recyclerView.adapter = adapter
                }
            }
        })
    }

    private fun goToPairActivity(){
//        val intent = intent
//        val first = intent.getBooleanExtra("first",false)
//        if(first){
//            moveTaskToBack(true)
//            exitProcess(-1)
//        }
//        else{
            startActivity(Intent(this,PairFirstDeviceActivity::class.java))
//        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.settings,menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId

        if (id == R.id.pairDeviceItem) {
            startActivity(Intent(this,ServersActivity::class.java))
            finish()
            return true
        }
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
