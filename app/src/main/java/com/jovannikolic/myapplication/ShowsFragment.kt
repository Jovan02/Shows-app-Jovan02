package com.jovannikolic.myapplication

import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.content.edit
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.jovannikolic.myapplication.databinding.DialogProfileBinding
import com.jovannikolic.myapplication.databinding.FragmentShowsBinding
import files.FileUtil
import models.Show

class ShowsFragment : Fragment() {

    private var _binding: FragmentShowsBinding? = null

    private val binding get() = _binding!!

    private lateinit var adapter: ShowsAdapter

    private lateinit var sharedPreferences: SharedPreferences

    private val viewModel by viewModels<ShowsViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sharedPreferences = requireContext().getSharedPreferences("LoginData", Context.MODE_PRIVATE)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentShowsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initListeners()

    }

    private fun initListeners() {
        binding.showbutton.setOnClickListener {
            binding.emptystateimage.setVisibility(View.INVISIBLE)
            binding.emptystatetext.setVisibility(View.INVISIBLE)
            binding.showbutton.setVisibility(View.GONE)

            val email = sharedPreferences.getString("email", "non_existing@email.com")

            val tokens = email?.split("@")

            val username = tokens?.getOrNull(0).toString()

            initShowsRecycler(username)
        }

        binding.profileButton.setOnClickListener {
            showBottomSheet()
        }

    }

    private fun initShowsRecycler(user: String) {

        viewModel.showsLiveData.observe(viewLifecycleOwner) { showList ->
            adapter = ShowsAdapter(showList) { show ->
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
        val dialog = context?.let { BottomSheetDialog(it) }

        val bottomSheetBinding = DialogProfileBinding.inflate(layoutInflater)
        dialog?.setContentView(bottomSheetBinding.root)


        bottomSheetBinding.profileEmail.text = sharedPreferences.getString("email", "non_existing@email.com")

        bottomSheetBinding.changePictureButton.setOnClickListener {
            checkIfPermissionNeeded()
            dialog?.dismiss()
        }

        bottomSheetBinding.logoutButton.setOnClickListener {
            onLogoutButtonPressed(dialog)
        }
        changePicture(bottomSheetBinding)
        dialog?.show()
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
        var permissionGiven = false
        results.forEach{
            if(it.value){
                permissionGiven = true
            }else{
                permissionGiven = false
                return@forEach
            }
        }
        if(permissionGiven){
            activateCamera()
        }
    }

    private fun activateCamera(){
        val file = FileUtil.createImageFile(requireContext())
        val uri = FileProvider.getUriForFile(requireContext(), "com.jovannikolic.myapplication.fileProvider", file!!)
        sharedPreferences.edit{
            putString("image", file.absolutePath)
        }
        getCameraImage.launch(uri)
    }

    private val getCameraImage = registerForActivityResult(ActivityResultContracts.TakePicture()){ _ ->

    }

    private fun changePicture(bottomSheetBinding: DialogProfileBinding) {
        val path = sharedPreferences.getString("image", "test")
        bottomSheetBinding.profilePhoto.setImageBitmap(BitmapFactory.decodeFile(path))
    }

    private fun onLogoutButtonPressed(dialog: BottomSheetDialog?){
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Confirm logout")
        builder.setMessage("Are you sure you want to logout?")
        builder.setPositiveButton("Yes", DialogInterface.OnClickListener { _, _ ->
            dialog?.dismiss()
            if (!sharedPreferences.getBoolean("remember", false)) {
                findNavController().popBackStack()
            } else {
                sharedPreferences.edit {
                    putBoolean("remember", false)
                }
                findNavController().navigate(R.id.actionLogout)
            }
        })
        builder.setNegativeButton("No", DialogInterface.OnClickListener { _, _ -> })
        val alert = builder.create()
        alert.show()
    }

}