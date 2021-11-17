package com.example.myapplication

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*
import android.view.KeyEvent.KEYCODE_BACK
import android.content.DialogInterface
import android.util.DisplayMetrics
import android.view.KeyEvent
import android.view.WindowManager


interface MyCallbackServers { fun onCallback(value:ArrayList<Server>) }

class ServersActivity : AppCompatActivity() {

    private lateinit var database: DatabaseReference
    private lateinit var dialog: Dialog
    var adapter: ServersAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_servers)

        dialog = Dialog(this)

        database = FirebaseDatabase.getInstance().reference.child("Users").child("Available Links")
        val servers = ArrayList<Server>()

        val inflater: LayoutInflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view= inflater.inflate(R.layout.servers_popup, null)
        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerviewServers)
        recyclerView.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)

        fun getServers(myCall: MyCallbackServers){
            database.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(ds: DatabaseError) {
                    println("error")
                }
                override fun onDataChange(ds: DataSnapshot) {
                    for (snap in ds.children) {
                        val server = snap.getValue(Server::class.java) ?: break
                        servers.add(server)
                    }
                    myCall.onCallback(servers)
                }
            })
        }

        val context = this
        getServers(object : MyCallbackServers {
            override fun onCallback(value:ArrayList<Server>) {
                if(value.isEmpty()){
                    Toast.makeText(context,"No servers available at the moment. Try again later",Toast.LENGTH_SHORT).show()
                }
                adapter = ServersAdapter(value,context)
                adapter!!.notifyDataSetChanged()
                recyclerView.adapter = adapter
            }
        })


        dialog.setOnKeyListener(object: DialogInterface.OnKeyListener {
            override fun onKey(
                arg0: DialogInterface, keyCode: Int,
                event: KeyEvent
            ): Boolean {
                if (keyCode == KEYCODE_BACK) {
                    finish()
                    goToDevices()
                    dialog.dismiss()
                }
                return true
            }
        })

        chooseServer(view)
    }

    private fun chooseServer(view: View){
        dialog.setCancelable(false)
        view.setBackgroundColor(Color.TRANSPARENT)
        dialog.setContentView(view)
        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        var width = (displayMetrics.widthPixels * 0.9).toInt()
        val height = (displayMetrics.heightPixels * 0.8).toInt()
        dialog.window?.setLayout(width,height)
        dialog.show()
    }

    private fun goToDevices() {
        startActivity(Intent(this,DevicesActivity::class.java))
    }
}
