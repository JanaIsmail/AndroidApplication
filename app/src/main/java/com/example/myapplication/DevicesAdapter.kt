package com.example.myapplication

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class DevicesAdapter(private val userList: ArrayList<Device>, val context: Context) : RecyclerView.Adapter<DevicesAdapter.ViewHolder>(){

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int){
        val user: Device = userList[position]
        holder.viewImage.setImageResource(R.drawable.camera)
        holder.textViewName.text = user.name

        when(user.status){
            1 -> { holder.textViewState.text = "Online" ; holder.textViewState.setTextColor(Color.GREEN) }
            0 -> { holder.textViewState.text = "Offline" ; holder.textViewState.setTextColor(Color.RED) }
        }

          holder.itemView.setOnClickListener{
              val name = holder.textViewName.text.toString()
              val intent = Intent(context, CameraActivity::class.java)
              intent.putExtra("name",name)
              context.startActivity(intent)
          }
    }

    override fun getItemCount() : Int{ return userList.size }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder{
        val v = LayoutInflater.from(parent.context).inflate(R.layout.card_devices, parent, false)
        return ViewHolder(v,context)
    }

    class ViewHolder(itemView: View, val context: Context) : RecyclerView.ViewHolder(itemView){
        val viewImage: ImageView = itemView.findViewById(R.id.ViewImage)
        val textViewName: TextView = itemView.findViewById(R.id.textViewName)
        val textViewState: TextView = itemView.findViewById(R.id.textViewState)
    }
}