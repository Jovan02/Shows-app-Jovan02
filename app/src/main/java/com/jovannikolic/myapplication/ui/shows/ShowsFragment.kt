package com.jovannikolic.myapplication.ui.shows

import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.content.edit
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.jovannikolic.myapplication.R
import com.jovannikolic.myapplication.ui.adapter.ShowsAdapter
import com.jovannikolic.myapplication.databinding.DialogProfileBinding
import com.jovannikolic.myapplication.databinding.FragmentShowsBinding
import com.jovannikolic.myapplication.ui.activity.MainApplication
import com.jovannikolic.myapplication.ui.files.FileUtil
import models.Constants.APP
import models.Constants.EMAIL
import models.Constants.IMAGE
import models.Constants.REMEMBER_ME
import models.Show

class ShowsFragment : Fragment() {

    private var _binding: FragmentShowsBinding? = null

    private val binding get() = _binding!!

    private lateinit var adapter: ShowsAdapter

    private lateinit var sharedPreferences: SharedPreferences

    private lateinit var dialog: BottomSheetDialog

    private lateinit var bottomSheetBinding: DialogProfileBinding

    private val viewModel: ShowsViewModel by viewModels{
        ShowsViewModelFactory((activity?.application as MainApplication).database)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sharedPreferences = requireContext().getSharedPreferences(APP, Context.MODE_PRIVATE)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentShowsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initListeners()
        initObservers()

        binding.progressCircular.visibility = View.VISIBLE
        viewModel.getUserData()
        viewModel.getShowsList()

        viewModel.showsLiveData.observe(viewLifecycleOwner) { list ->
            if (!list.isEmpty()) {
                binding.emptystateimage.setVisibility(View.INVISIBLE)
                binding.emptystatetext.setVisibility(View.INVISIBLE)

                val email = sharedPreferences.getString(EMAIL, null)
                val tokens = email?.split("@")
                val username = tokens?.getOrNull(0).toString()

                initShowsRecycler(username)
            }
        }

        binding.profileButton.setImageResource(R.drawable.profile_placeholder)
    }

    private fun initObservers() {
        viewModel.isUpdatedPhoto.observe(viewLifecycleOwner) { isUpdated ->
            if (!isUpdated)
                Toast.makeText(context, R.string.problems_try_again, Toast.LENGTH_SHORT).show()
        }

        viewModel.isGetDataSuccessful.observe(viewLifecycleOwner) { isSuccessful ->
            if (!isSuccessful)
                Toast.makeText(context, R.string.problems_try_again, Toast.LENGTH_SHORT).show()
        }

        viewModel.isGetShowsSuccessful.observe(viewLifecycleOwner) { isSuccessful ->
            if (!isSuccessful) {
                Toast.makeText(context, R.string.problems_try_again, Toast.LENGTH_SHORT).show()
                viewModel.setShowsListFromDatabase().observe(viewLifecycleOwner) { showList ->
                    viewModel.setShowsList(showList.map { show ->
                        Show(show.id, show.average_rating, show.description, show.image_url, show.no_of_reviews, show.title)
                    })
                }
            }
        }

        viewModel.isLoading.observe(viewLifecycleOwner){ isLoading ->
            if(isLoading) {
                binding.progressCircular.visibility = View.VISIBLE
            } else {
                binding.progressCircular.visibility = View.GONE
            }
        }
    }

    private fun initListeners() {
        binding.profileButton.setOnClickListener {
            showBottomSheet()
        }
    }

    private fun initShowsRecycler(user: String) {
        viewModel.showsLiveData.observe(viewLifecycleOwner) { showList ->
            adapter = ShowsAdapter(requireContext(), showList) { show ->
                val direction = ShowsFragmentDirections.toShowDetailsFragment(user, show)
                findNavController().navigate(direction)
            }

            binding.showsrecycler.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            binding.showsrecycler.adapter = adapter
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun showBottomSheet() {

        dialog = BottomSheetDialog(requireContext())

        bottomSheetBinding = DialogProfileBinding.inflate(layoutInflater)
        dialog.setContentView(bottomSheetBinding.root)

        bottomSheetBinding.profileEmail.text = sharedPreferences.getString(EMAIL, "non_existing@email.com")

        bottomSheetBinding.changePictureButton.setOnClickListener {
            checkIfPermissionNeeded()
            viewModel.updateProfilePhoto(sharedPreferences)
            dialog.dismiss()
        }

        bottomSheetBinding.logoutButton.setOnClickListener {
            onLogoutButtonPressed(dialog)
        }
        changePicture(bottomSheetBinding)
        dialog.show()
    }

    private fun checkIfPermissionNeeded() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
            && ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            activateCamera()
        } else {
            requestPermission()
        }
    }

    private fun requestPermission() {
        requestPermissionsLauncher.launch(arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE))
    }

    private val requestPermissionsLauncher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { results ->
        var permissionGiven = true
        results.forEach{
            if(!it.value){
                permissionGiven = false
                return@forEach
            }
        }
        if (permissionGiven) {
            activateCamera()
        }
    }

    private fun activateCamera() {
        val file = FileUtil.createImageFile(requireContext())
        val uri = FileProvider.getUriForFile(requireContext(), "com.jovannikolic.myapplication.fileProvider", file!!)
        sharedPreferences.edit {
            putString(IMAGE, file.absolutePath)
        }
        getCameraImage.launch(uri)
    }

    private val getCameraImage = registerForActivityResult(ActivityResultContracts.TakePicture()) { }

    private fun changePicture(bottomSheetBinding: DialogProfileBinding) {
        val path = sharedPreferences.getString(IMAGE, "test")
        val options = RequestOptions()
            .centerCrop()
            .placeholder(R.drawable.profile_placeholder)
            .error(R.drawable.profile_placeholder)
        Glide.with(this).load(path).apply(options).into(bottomSheetBinding.profilePhoto)
        Glide.with(this).load(path).apply(options).into(binding.profileButton)

    }

    private fun onLogoutButtonPressed(dialog: BottomSheetDialog) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle(R.string.confirm_logout)
        builder.setMessage(R.string.confirm_logout_message)
        builder.setPositiveButton("Yes", DialogInterface.OnClickListener { _, _ ->
            dialog.dismiss()
            sharedPreferences.edit {
                putBoolean(REMEMBER_ME, false)
            }
            val direction = ShowsFragmentDirections.actionLogout()
            findNavController().navigate(direction)

        })
        builder.setNegativeButton("No", null)
        val alert = builder.create()
        alert.show()
    }
}