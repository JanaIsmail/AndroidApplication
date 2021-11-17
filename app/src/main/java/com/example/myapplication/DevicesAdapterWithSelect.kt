package com.example.myapplication

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView

class DevicesAdapterWithSelect(private val userList: ArrayList<Device>, val context: Context, private val mainInterface: MainInterface) : RecyclerView.Adapter<DevicesAdapterWithSelect.ViewHolder>(), ViewHolderClickListener{

    @SuppressLint("SetTextI18n")

    var selectedIds: MutableList<String> = ArrayList()

    override fun onLongTap(index: Int){
        if(!DevicesWithSelectActivity.isMultiSelectOn){
            DevicesWithSelectActivity.isMultiSelectOn = true
        }
        addIDIntoSelectedIDs(index)
    }

    override fun onTap(index: Int){
        addIDIntoSelectedIDs(index)
    }

    fun addIDIntoSelectedIDs(index: Int){
        val id = userList[index].name
        if(selectedIds.contains(id)){
            selectedIds.remove(id)
        }
        else{
            selectedIds.add(id)
        }
        notifyItemChanged(index)
        if(selectedIds.size < 1) DevicesWithSelectActivity.isMultiSelectOn = false
        mainInterface.mainInterface(selectedIds.size)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int){
        val user: Device = userList[position]
        holder.viewImage.setImageResource(R.drawable.camera)
        holder.textViewName.text = user.name

        when(user.status){
            1 -> { holder.textViewState.text = "Online" ; holder.textViewState.setTextColor(Color.GREEN) }
            0 -> { holder.textViewState.text = "Offline" ; holder.textViewState.setTextColor(Color.RED) }
        }

//          holder.itemView.setOnClickListener{ context.startActivity(Intent(context,CameraActivity::class.java)) }
//        holder.itemView.setOnLongClickListener(){
//            holder.itemView
//            val hold = true
//            val intent = Intent(context, DevicesWithSelectActivity::class.java)
//            intent.putExtra("hold",hold)
//            return@setOnLongClickListener true
//        }

        val id = userList[position].name

        if(selectedIds.contains(id)){
            holder.constraintLayout.foreground = ColorDrawable(ContextCompat.getColor(context, R.color.colorControlActivated))
        }
        else{
            holder.constraintLayout.foreground = ColorDrawable(ContextCompat.getColor(context, android.R.color.transparent))
        }

    }

    override fun getItemCount() : Int{ return userList.size }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder{
        val v = LayoutInflater.from(parent.context).inflate(R.layout.card_devices, parent, false)
        return ViewHolder(v,this,context)
    }

    class ViewHolder(itemView: View, private val r_tap: ViewHolderClickListener,  val context: Context) :
        RecyclerView.ViewHolder(itemView),View.OnLongClickListener,View.OnClickListener{
        val viewImage: ImageView = itemView.findViewById(R.id.ViewImage)
        val textViewName: TextView = itemView.findViewById(R.id.textViewName)
        val textViewState: TextView = itemView.findViewById(R.id.textViewState)
        val constraintLayout: ConstraintLayout = itemView.findViewById(R.id.constraintLayout)

        init{
            constraintLayout.setOnClickListener(this)
            constraintLayout.setOnLongClickListener(this)
        }
        override fun onClick(v: View?){
            if(DevicesWithSelectActivity.isMultiSelectOn) {
                r_tap.onTap(adapterPosition)
            }
            else{
                val name = textViewName.text.toString()
                val intent = Intent(context, CameraActivity::class.java)
                intent.putExtra("name",name)
                context.startActivity(intent)
            }
        }
        override fun onLongClick(v: View?): Boolean{
            r_tap.onLongTap(adapterPosition)
            return true
        }
    }
}