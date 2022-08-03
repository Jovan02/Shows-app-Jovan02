package com.jovannikolic.myapplication.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.jovannikolic.myapplication.R
import com.jovannikolic.myapplication.databinding.ViewShowItemBinding
import com.jovannikolic.myapplication.ui.models.Show

class ShowsAdapter(
    private val context: Context,
    private var items: List<Show>,
    private val onItemClickCallback: (Show) -> Unit
) : RecyclerView.Adapter<ShowsAdapter.ShowViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShowViewHolder {
        val binding = ViewShowItemBinding.inflate(LayoutInflater.from(parent.context))
        return ShowViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ShowViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount() = items.count()

    inner class ShowViewHolder(private val binding: ViewShowItemBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Show) {
            binding.showname.text = item.title
            binding.showdescription.text = item.description
            val options = RequestOptions()
                .centerCrop()
                .placeholder(R.drawable.family_guy)
                .error(R.drawable.family_guy)
            Glide.with(context).load(item.image_url).apply(options).into(binding.showImage)
            binding.cardcontainer.setOnClickListener {
                onItemClickCallback(item)
            }
        }
    }

}