package com.example.flickrbrowserapp

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.flickrbrowserapp.databinding.ItemRowBinding

class RVAdapter(val main : MainActivity, private val photoList : ArrayList<DataImage>):RecyclerView.Adapter<RVAdapter.ItemViewHolder>() {

    class ItemViewHolder(val binding: ItemRowBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return ItemViewHolder(
            ItemRowBinding.inflate
                (
                LayoutInflater.from
                    (parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val photo = photoList[position]

        holder.binding.apply {
            tvImage.text = photo.title

            Glide.with(main).load(photo.link).into(imageTheme)

            llItemRow.setOnClickListener {
                main.openImage(photo.link)
            }
        }
    }

    override fun getItemCount() = photoList.size
}