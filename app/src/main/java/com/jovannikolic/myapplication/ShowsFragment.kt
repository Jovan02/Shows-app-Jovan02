package com.jovannikolic.myapplication

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
import com.jovannikolic.myapplication.databinding.DialogProfileBinding
import com.jovannikolic.myapplication.databinding.FragmentShowsBinding
import files.FileUtil
import java.io.File
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.Request
import okhttp3.RequestBody.Companion.asRequestBody
import models.ShowsListResponse
import models.UpdatePhotoResponse
import models.UserDataResponse
import networking.ApiModule
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ShowsFragment : Fragment() {

    private var _binding: FragmentShowsBinding? = null

    private val binding get() = _binding!!

    private lateinit var adapter: ShowsAdapter

    private lateinit var sharedPreferences: SharedPreferences

    private lateinit var dialog: BottomSheetDialog

    private lateinit var bottomSheetBinding: DialogProfileBinding

    private val viewModel by viewModels<ShowsViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sharedPreferences = requireContext().getSharedPreferences("LoginData", Context.MODE_PRIVATE)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentShowsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initListeners()

        getUserData()
        getShowsList(1, 20)

        viewModel.showsLiveData.observe(viewLifecycleOwner) { list ->
            if (!list.isEmpty()) {
                binding.emptystateimage.setVisibility(View.INVISIBLE)
                binding.emptystatetext.setVisibility(View.INVISIBLE)

                val email = sharedPreferences.getString("email", "non_existing@email.com")

                val tokens = email?.split("@")

                val username = tokens?.getOrNull(0).toString()

                initShowsRecycler(username)
            }
        }

        binding.profileButton.setImageResource(R.drawable.profile_placeholder)
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

        bottomSheetBinding.profileEmail.text = sharedPreferences.getString("email", "non_existing@email.com")

        bottomSheetBinding.changePictureButton.setOnClickListener {
            checkIfPermissionNeeded()
            updateProfilePhoto(sharedPreferences.getString("email", "")!!)
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
            && ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            activateCamera()
        } else {
            requestPermission()
        }
    }

    private fun requestPermission() {
        requestPermissionsLauncher.launch(arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE))
    }

    private val requestPermissionsLauncher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { results ->
        var permissionGiven = false
        results.forEach {
            if (it.value) {
                permissionGiven = true
            } else {
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
            putString("image", file.absolutePath)
        }
        getCameraImage.launch(uri)
    }

    private val getCameraImage = registerForActivityResult(ActivityResultContracts.TakePicture()) { }

    private fun changePicture(bottomSheetBinding: DialogProfileBinding) {
        val path = sharedPreferences.getString("image", "test")
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
            if (!sharedPreferences.getBoolean("remember", false)) {
                findNavController().popBackStack()
            } else {
                sharedPreferences.edit {
                    putBoolean("remember", false)
                }
                val direction = ShowsFragmentDirections.actionLogout()
                findNavController().navigate(direction)
            }
        })
        builder.setNegativeButton("No", null)
        val alert = builder.create()
        alert.show()
    }

    private fun updateProfilePhoto(email: String) {
        val path = sharedPreferences.getString("image", "test")!!
        val requestBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart(
                "image_url", "avatar.jpg",
                File(path).asRequestBody("image_url".toMediaType())
            )
            .build()

        val request = Request.Builder()
            .post(requestBody)
            .build()

        ApiModule.retrofit.updatePhoto(request)
            .enqueue(object : Callback<UpdatePhotoResponse> {
                override fun onResponse(call: Call<UpdatePhotoResponse>, response: Response<UpdatePhotoResponse>) {
                    if (response.isSuccessful)
                        Toast.makeText(requireContext(), "Call Successful.", Toast.LENGTH_SHORT).show()
                    else
                        Toast.makeText(requireContext(), getString(R.string.problems_try_again), Toast.LENGTH_SHORT).show()
                }

                override fun onFailure(call: Call<UpdatePhotoResponse>, t: Throwable) {
                    Toast.makeText(requireContext(), getString(R.string.problems_try_again), Toast.LENGTH_SHORT).show()
                }

            })

    }

    private fun getUserData() {
        ApiModule.retrofit.userData()
            .enqueue(object : retrofit2.Callback<UserDataResponse> {
                override fun onResponse(call: Call<UserDataResponse>, response: Response<UserDataResponse>) {
                    if (response.isSuccessful)
                        Toast.makeText(requireContext(), "Call Successful.", Toast.LENGTH_SHORT).show()
                    else
                        Toast.makeText(requireContext(), "Call Failed OnResponse.", Toast.LENGTH_SHORT).show()
                }

                override fun onFailure(call: Call<UserDataResponse>, t: Throwable) {
                    Toast.makeText(requireContext(), t.message, Toast.LENGTH_LONG).show()
                }

            })
    }

    private fun getShowsList(page: Int, items: Int) {
        ApiModule.retrofit.showsList(page.toString(), items.toString())
            .enqueue(object : retrofit2.Callback<ShowsListResponse> {
                override fun onResponse(call: Call<ShowsListResponse>, response: Response<ShowsListResponse>) {
                    if (response.isSuccessful && response.body() != null) {
                        Toast.makeText(requireContext(), "Call Successful.", Toast.LENGTH_SHORT).show()
                        viewModel.setShowsList(response.body()!!.shows)
                    } else
                        Toast.makeText(requireContext(), "Call Failed OnResponse.", Toast.LENGTH_SHORT).show()
                }

                override fun onFailure(call: Call<ShowsListResponse>, t: Throwable) {
                    Toast.makeText(requireContext(), "Call Failed OnFailure.", Toast.LENGTH_SHORT).show()
                }

            })
    }

}