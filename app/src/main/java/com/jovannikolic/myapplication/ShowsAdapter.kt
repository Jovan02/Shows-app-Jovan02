package com.jovannikolic.myapplication

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.jovannikolic.myapplication.databinding.ViewShowItemBinding
import layout.Show
import javax.security.auth.callback.Callback

class ShowsAdapter (
    private var items: List<Show>,
    private val onItemClickCallback: (Show) -> Unit
): RecyclerView.Adapter<ShowsAdapter.ShowViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShowViewHolder {
        val binding = ViewShowItemBinding.inflate(LayoutInflater.from(parent.context))
        return ShowViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ShowViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount() = items.count()

    inner class ShowViewHolder(private val binding: ViewShowItemBinding): RecyclerView.ViewHolder(binding.root){

        fun bind(item: Show){
            binding.showname.text = item.name
            binding.showdescription.text = item.description
            binding.showImage.setImageResource(item.imageResourceId)
            binding.cardcontainer.setOnClickListener{
                onItemClickCallback(item)
            }
        }
    }

}