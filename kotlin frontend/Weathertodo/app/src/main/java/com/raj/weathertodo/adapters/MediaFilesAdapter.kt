package com.raj.weathertodo.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.raj.weathertodo.R
import com.raj.weathertodo.mediaDetail.MediaDetailActivity
import com.raj.weathertodo.model.MediaFileCardClass


class MediaFilesAdapter(
    private var list: ArrayList<MediaFileCardClass>,
    val context: Context
) :
    RecyclerView.Adapter<MediaFilesAdapter.MyViewHolder>() {
    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvTitle = view.findViewById<TextView>(R.id.tv_media_title)
        val ivImg = view.findViewById<ImageView>(R.id.iv_media)
        val ivDelete = view.findViewById<ImageView>(R.id.iv_media_delete)
    }

    @NonNull
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.lv_card_mediafiles_item, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = list[position]
        holder.apply {
            tvTitle.setText("${item.title}")

            when (item.fileType) {
                0 -> { //photo
                    ivImg.setImageDrawable(context.getDrawable(R.drawable.ic_add_photo))
                }
                1 -> { //video
                    ivImg.setImageDrawable(context.getDrawable(R.drawable.ic_add_video))

                }
                else -> {//audio
                    ivImg.setImageDrawable(context.getDrawable(R.drawable.ic_add_audio))
                }
            }

            ivDelete.setOnClickListener {
                list.removeAt(position)
                notifyDataSetChanged()
            }

        }

        holder.itemView.setOnClickListener {
            val i = Intent(context, MediaDetailActivity::class.java)
            i.putExtra("media", Gson().toJson(item))
            context.startActivity(i)
        }

    }


    override fun getItemCount(): Int {
        return list.size
    }

}