package com.example.myapplication

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.ActionMode
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlin.system.exitProcess
import android.view.Menu
import android.widget.*
import android.view.MenuItem as MenuItem


interface ViewHolderClickListener {
    fun onLongTap(index: Int)
    fun onTap(index: Int)
}

interface MainInterface {
    fun mainInterface(size: Int)
}

interface MyCallbackWithSelect { fun onCallback(value:ArrayList<Device>) }

class DevicesWithSelectActivity : AppCompatActivity(), MainInterface {

    var actionMode: ActionMode? = null
    var adapterWithSelect: DevicesAdapterWithSelect? = null

    companion object{
        var isMultiSelectOn = false
        //const val TAG = "Device"
    }

    override fun mainInterface(size: Int){
        if(actionMode == null) actionMode = startActionMode(ActionModeCallback())
        if(size > 0) actionMode?.title = "$size"
        else actionMode?.finish()
    }

    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference

    @SuppressLint("WrongConstant")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_devices_with_select)

        isMultiSelectOn = false
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerviewSelect)
        recyclerView.layoutManager = LinearLayoutManager(this, LinearLayout.VERTICAL, false)
        val users = ArrayList<Device>()

        auth = FirebaseAuth.getInstance()
        val uid = auth.currentUser!!.uid
        database = FirebaseDatabase.getInstance().reference.child("Users").child(uid).child("Device")

        fun getUsers(myCall: MyCallbackWithSelect){
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

        val context1 = this as Context
        val context2 = this as MainInterface

        getUsers(object : MyCallbackWithSelect {
            override fun onCallback(value:ArrayList<Device>) {
                if(value.isEmpty()){
                    goToPairActivity()
                }
                else{
                    adapterWithSelect = DevicesAdapterWithSelect(value,context1,context2)
                    adapterWithSelect!!.notifyDataSetChanged()
                    recyclerView.adapter = adapterWithSelect
                }
            }
        })
    }

    inner class ActionModeCallback: ActionMode.Callback {
        private var shouldResetRecyclerView = true
        override fun onActionItemClicked(mode: ActionMode?, item: MenuItem?): Boolean{
            when(item?.itemId){
                R.id.removeDeviceItem -> {
                    shouldResetRecyclerView = true
                    deleteSelectedIds(adapterWithSelect!!)
                    actionMode?.title = ""
                    actionMode?.finish()
                    Toast.makeText(baseContext, adapterWithSelect!!.selectedIds.size.toString() + "devices deleted", Toast.LENGTH_SHORT).show()
                    return true
                }
            }
            return false
        }

        override fun onCreateActionMode(mode: ActionMode?, menu: Menu?): Boolean {
            val inflater = mode?.menuInflater
            inflater?.inflate(R.menu.devicesettings, menu)
            return true
        }

        override fun onPrepareActionMode(mode: ActionMode?, menu: Menu?): Boolean {
            menu?.findItem(R.id.removeDeviceItem)?.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)
            return true
        }

        override fun onDestroyActionMode(mode: ActionMode?) {
            if(shouldResetRecyclerView){
                adapterWithSelect?.selectedIds?.clear()
                adapterWithSelect?.notifyDataSetChanged()
            }
            isMultiSelectOn = false
            actionMode = null
            shouldResetRecyclerView = true
        }
    }

    private fun deleteSelectedIds(myAdapterWithSelect: DevicesAdapterWithSelect){
        if(myAdapterWithSelect.selectedIds.size < 1) return
        val selectedIdIteration = myAdapterWithSelect.selectedIds.listIterator()
        while(selectedIdIteration.hasNext()){
            val selectedItemID = selectedIdIteration.next()
            database.child(selectedItemID).removeValue()
            isMultiSelectOn = false
        }
        finish()
        startActivity(Intent())
    }


    private fun goToPairActivity(){
        val intent = intent
        val first = intent.getBooleanExtra("first",false)
        if(first){
            moveTaskToBack(true)
            exitProcess(-1)
        }
        else{
            startActivity(Intent(this,PairDeviceActivity::class.java))
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
            menuInflater.inflate(R.menu.settings,menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId

        if (id == R.id.pairDeviceItem) {
            startActivity(Intent(this,PairDeviceActivity::class.java))
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
        if(isMultiSelectOn){
            for( s in adapterWithSelect!!.selectedIds) {
                val i = adapterWithSelect!!.selectedIds.indexOf(s)
                adapterWithSelect!!.addIDIntoSelectedIDs(i)
            }
        }
        else {
            moveTaskToBack(true)
            exitProcess(-1)
        }
    }
}
