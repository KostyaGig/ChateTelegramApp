package com.admin.project_kwork.ui.recyclerView.viewHolders

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.admin.project_kwork.R
import com.admin.project_kwork.models.Music
import com.admin.project_kwork.models.User
import com.bumptech.glide.Glide
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MusicViewHolder(view: View, private val onClick: (Music) -> Unit, private val onLongClick: (Music) -> Unit) : RecyclerView.ViewHolder(view) {

    private val musicImage = itemView.findViewById<ImageView>(R.id.music_image)
    private val titleMusicText = itemView.findViewById<TextView>(R.id.title_music_text)
    private val titleAuthorText = itemView.findViewById<TextView>(R.id.title_author_text)
    private val startPlayMusicBtn = itemView.findViewById<FloatingActionButton>(R.id.start_play_music_btn)

    fun bind(currentMusic: Music) {


        Glide.with(itemView.context)
            .load(currentMusic.musicImageUrl)
            .placeholder(R.drawable.ic_headphones)
            .into(musicImage)

        titleMusicText.text = currentMusic.titleMusic
        titleAuthorText.text = currentMusic.titleAuthor

        itemView.setOnClickListener {
            onClick(currentMusic)
        }

        itemView.setOnLongClickListener {
            onLongClick(currentMusic)
            true
        }
    }

}
