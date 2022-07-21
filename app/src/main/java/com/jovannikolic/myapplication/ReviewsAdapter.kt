package com.jovannikolic.myapplication

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.jovannikolic.myapplication.databinding.ViewReviewItemBinding
import models.Review

class ReviewsAdapter (
    private var items: List<Review>,
    private val onItemClickCallback: (Review) -> Unit
): RecyclerView.Adapter<ReviewsAdapter.ReviewViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewViewHolder {
        val binding = ViewReviewItemBinding.inflate(LayoutInflater.from(parent.context))
        return ReviewViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ReviewViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount() = items.count()

    fun addReview(review: Review){
        items = items + review
        notifyItemInserted(items.lastIndex)
    }


    inner class ReviewViewHolder(private val binding: ViewReviewItemBinding): RecyclerView.ViewHolder(binding.root){

        fun bind(item: Review){
            binding.apply{
                ratingcomment.text = item.comment
                ratingnumber.text = item.ratingNum.toString()
                ratingcard.setOnClickListener{
                    onItemClickCallback(item)
                }
            }
        }
    }

}