package com.example.myapplication

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.RadioButton
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat.getSystemService
import androidx.recyclerview.widget.RecyclerView

class ServersAdapter(private val serverList: ArrayList<Server>, val context: Context) : RecyclerView.Adapter<ServersAdapter.ViewHolder>() {

    private var lastSelectedPosition = -1

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val server  = serverList[position]
        holder.serverName.text = server.code
        holder.selectServer.isChecked = lastSelectedPosition == position

        holder.itemView.setOnClickListener{
            lastSelectedPosition = position
            notifyDataSetChanged()
            val code = server.code
            val secret = server.secret
            val intent = Intent(context, PairDeviceActivity::class.java)
            intent.putExtra("code",code)
            intent.putExtra("secret",secret)
            context.startActivity(intent)
        }

        holder.selectServer.setOnClickListener{
            lastSelectedPosition = position
            notifyDataSetChanged()
            val code = server.code
            val secret = server.secret
            val intent = Intent(context, PairDeviceActivity::class.java)
            intent.putExtra("code",code)
            intent.putExtra("secret",secret)
            context.startActivity(intent)
        }
    }
    override fun getItemCount() : Int{ return serverList.size }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.card_servers, parent, false)
        return ViewHolder(v,context)
    }

    class ViewHolder(itemView: View,val context: Context) : RecyclerView.ViewHolder(itemView){
        val serverName: TextView = itemView.findViewById(R.id.serverName)
        val selectServer = itemView.findViewById<RadioButton>(R.id.selectServer)
    }

}