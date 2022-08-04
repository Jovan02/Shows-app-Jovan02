package com.jovannikolic.myapplication

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.jovannikolic.myapplication.databinding.DialogAddReviewBinding
import com.jovannikolic.myapplication.databinding.FragmentShowDetailsBinding
import models.Review
import java.text.DecimalFormat
import models.AddReviewRequest
import models.AddReviewResponse
import models.GetReviewsResponse
import models.ShowDetailsResponse
import networking.ApiModule
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ShowDetailsFragment : Fragment() {

    private var _binding: FragmentShowDetailsBinding? = null

    private val binding get() = _binding!!

    private val args by navArgs<ShowDetailsFragmentArgs>()

    private val viewModel: ShowDetailsViewModel by viewModels{
        ShowDetailsViewModelFactory((activity?.application as MainApplication).database)
    }

    private lateinit var sharedPreferences: SharedPreferences

    private lateinit var adapter: ReviewsAdapter

    private lateinit var show_id: String

    var firstInit = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPreferences = requireContext().getSharedPreferences("LoginData", Context.MODE_PRIVATE)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentShowDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.initListeners(requireContext(), viewLifecycleOwner, binding, this, layoutInflater)

        binding.toolbar.setNavigationIcon(R.drawable.ic_back_button)
        binding.collapsingToolbar.setExpandedTitleTextAppearance(R.style.toolbarTitle)

        viewModel.averageReviewsLiveData.observe(viewLifecycleOwner) { average ->
            viewModel.numberOfReviewsLiveData.observe(viewLifecycleOwner) { number ->
                binding.averageratingtext.text = getString(R.string.reviews_average, number.toString(), average.toString())
            }
            binding.averageratingbar.rating = average
        }

        viewModel.currentShow.observe(viewLifecycleOwner){ show ->
            binding.collapsingToolbar.title = show.title
            binding.showtext.text = show.description
            val options = RequestOptions()
                .centerCrop()
                .placeholder(R.drawable.family_guy)
                .error(R.drawable.family_guy)
            Glide.with(this).load(show.image_url).apply(options).into(binding.showimg)
        }

        viewModel.getShowData(requireContext(), viewLifecycleOwner, this, binding, args.show.id)
    }

}