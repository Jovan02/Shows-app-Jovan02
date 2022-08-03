package com.jovannikolic.myapplication.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.jovannikolic.myapplication.R
import com.jovannikolic.myapplication.databinding.ViewReviewItemBinding
import models.Review

class ReviewsAdapter(
    private var context: Context,
    private var items: List<Review>,
    private val onItemClickCallback: (Review) -> Unit
) : RecyclerView.Adapter<ReviewsAdapter.ReviewViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewViewHolder {
        val binding = ViewReviewItemBinding.inflate(LayoutInflater.from(parent.context))
        return ReviewViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ReviewViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount() = items.count()

    fun addReview(review: Review) {
        items = items + review
        notifyItemInserted(items.lastIndex)
    }

    inner class ReviewViewHolder(private val binding: ViewReviewItemBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Review) {
            binding.apply {
                val tokens = item.user.email.split("@")
                val username = tokens.getOrNull(0).toString()
                authorcomment.text = username
                ratingcomment.text = item.comment
                ratingnumber.text = item.rating.toString()
                val options = RequestOptions()
                    .centerCrop()
                    .placeholder(R.drawable.profile_placeholder)
                    .error(R.drawable.profile_placeholder)
                Glide.with(context).load(item.user.imageUrl).apply(options).into(profileImage)
                ratingcard.setOnClickListener {
                    onItemClickCallback(item)
                }
            }
        }
    }

}