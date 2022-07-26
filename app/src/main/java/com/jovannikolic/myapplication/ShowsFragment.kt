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
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.jovannikolic.myapplication.databinding.DialogProfileBinding
import com.jovannikolic.myapplication.databinding.FragmentShowsBinding
import files.FileUtil
import models.Show

class ShowsFragment : Fragment() {

    private val shows = listOf(
        Show(
            0,
            "Family Guy",
            "Family Guy is an American adult animated sitcom created by Seth MacFarlane. The series centers on the Griffins, a family consisting of parents Peter and Lois; their children, Meg, Chris, and Stewie; and their anthropomorphic pet dog, Brian. Set in the fictional city of Quahog, Rhode Island, the show exhibits much of its humor in the form of metafictional cutaway gags that often lampoon American culture.",
            R.drawable.family_guy
        ),
        Show(
            1,
            "Simpsons",
            "The Simpsons is an American animated sitcom created by Matt Groening. The series is a satirical depiction of American life, epitomized by the Simpson family, which consists of Homer, Marge, Bart, Lisa, and Maggie. The show is set in the fictional town of Springfield and parodies American culture and society, television, and the human condition.",
            R.drawable.simpsons
        ),
        Show(
            2,
            "Prison Break",
            "Prison Break is an American serial drama television series created by Paul Scheuring. The series revolves around two brothers, Lincoln Burrows and Michael Scofield. Burrows has been sentenced to death for a crime he did not commit, and Scofield devises an elaborate plan to help his brother escape prison and clear his name.",
            R.drawable.prison_break
        ),
        Show(
            3,
            "Sherlock",
            "Sherlock is a British mystery crime drama television series based on Sir Arthur Conan Doyle's Sherlock Holmes detective stories. Sherlock depicts \"consulting detective\" Sherlock Holmes solving various mysteries in modern-day London. Holmes is assisted by his flatmate and friend, Dr John Watson , who has returned from military service in Afghanistan with the Royal Army Medical Corps.",
            R.drawable.sherlock
        ),
        Show(
            4,
            "Witcher",
            "The Witcher is a Polish-American fantasy drama television series created by Lauren Schmidt Hissrich, based on the book series of the same name by Polish writer Andrzej Sapkowski. Set on a fictional, medieval-inspired landmass known as \"the Continent\", The Witcher explores the legend of Geralt of Rivia and Princess Ciri, who are linked to each other by destiny.",
            R.drawable.witcher
        ),
        Show(
            5,
            "Game of Thrones",
            "Game of Thrones is an American fantasy drama television series created by David Benioff and D. B. Weiss. Set on the fictional continents of Westeros and Essos, Game of Thrones has a large ensemble cast and follows several story arcs throughout the course of the show. The first major arc concerns the Iron Throne of the Seven Kingdoms of Westeros through a web of political conflicts among the noble families either vying to claim the throne or fighting for independence from whoever sits on it. ",
            R.drawable.game_of_thrones
        ),
    )

    private var _binding: FragmentShowsBinding? = null

    private val binding get() = _binding!!

    private lateinit var adapter: ShowsAdapter

    private lateinit var sharedPreferences: SharedPreferences

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

        adapter = ShowsAdapter(shows) { show ->
            val direction = ShowsFragmentDirections.toShowDetailsFragment(user, show)
            findNavController().navigate(direction)
        }

        binding.showsrecycler.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

        binding.showsrecycler.adapter = adapter
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
            // TODO: Open camera and take a picture
            checkIfPermissionNeeded()
            dialog?.dismiss()
        }

        bottomSheetBinding.logoutButton.setOnClickListener {
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

}