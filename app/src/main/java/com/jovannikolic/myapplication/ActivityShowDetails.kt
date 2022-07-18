package com.jovannikolic.myapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.jovannikolic.myapplication.databinding.ActivityShowDetailsBinding
import com.jovannikolic.myapplication.databinding.DialogAddReviewBinding

class ActivityShowDetails : AppCompatActivity() {

    lateinit var binding: ActivityShowDetailsBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityShowDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getData()

        clickReviewButton()

    }

    private fun showBottomSheet(){
        val dialog = BottomSheetDialog(this)

        val bottomSheetBinding = DialogAddReviewBinding.inflate(layoutInflater)
        dialog.setContentView(bottomSheetBinding.root)

        dialog.show()
    }

    private fun clickReviewButton(){
        binding.reviewbutton.setOnClickListener{
            showBottomSheet()
        }
    }

    private fun getData(){
        binding.maintitle.text = intent.extras?.getString("title")
        binding.showimg.setImageResource(intent.getIntExtra("imageResource", 0))
        binding.showtext.text = intent.extras?.getString("description")
    }

}