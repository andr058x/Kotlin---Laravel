package com.raj.weathertodo.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.google.gson.Gson
import com.raj.weathertodo.R
import com.raj.weathertodo.home.AddTaskActivity
import com.raj.weathertodo.home.HomeActivity
import com.raj.weathertodo.model.TaskClass
import com.raj.weathertodo.utils.ApiListner
import com.raj.weathertodo.utils.url_todostatus


internal class TaskAdapter(
    private var list: ArrayList<TaskClass>,
    val context: Context
) :
    RecyclerView.Adapter<TaskAdapter.MyViewHolder>() {
    internal inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvTaskTitle = view.findViewById<TextView>(R.id.tvTaskTitle)
        val checkboxTask = view.findViewById<CheckBox>(R.id.checkboxTask)
    }

    @NonNull
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.todo_list_item_card, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = list[position]
        holder.tvTaskTitle.text = item.title

        when (item.status) {
            1 -> {//mark as done
                holder.checkboxTask.isChecked = true
            }

            else -> {
                holder.checkboxTask.isChecked = false
            }

        }

        holder.checkboxTask.setOnClickListener {
            when (item.status) {
                1 -> {//mark as done
                    changeTaskStatus(item.id.toString(), 0, position)
                }

                else -> {
                    changeTaskStatus(item.id.toString(), 1, position)

                }

            }
        }

        holder.itemView.setOnClickListener {
            val str = Gson().toJson(item)
            val i = Intent(context, AddTaskActivity::class.java)
            i.putExtra("task", str)
            i.putExtra("from", "update")
            context.startActivity(i)
        }
    }


    override fun getItemCount(): Int {
        return list.size
    }

    fun changeTaskStatus(id: String, status: Int, position: Int) {
        //todostatus
        //id
        //status

        val map = hashMapOf<String, String>(
            "id" to id,
            "status" to "$status"
        )
        val activity = context as HomeActivity

        activity.pd.show()
        activity.webService.makeApiCall(
            url_todostatus,
            Request.Method.POST,
            map,
            object : ApiListner {
                override fun onResponse(data: String) {
                    activity.pd.dismiss()
                    list[position].status = status
                    notifyDataSetChanged()
                }

                override fun onFailure() {

                }

            })

    }
}