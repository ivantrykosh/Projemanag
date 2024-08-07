package com.ivantrykosh.udemy_course.android14.projemanag.presenter.main.my_profile

import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.ivantrykosh.udemy_course.android14.projemanag.R
import com.ivantrykosh.udemy_course.android14.projemanag.databinding.FragmentMyProfileBinding
import com.ivantrykosh.udemy_course.android14.projemanag.domain.model.User
import com.ivantrykosh.udemy_course.android14.projemanag.presenter.main.MainActivity
import com.ivantrykosh.udemy_course.android14.projemanag.utils.FirebaseStorageObjects
import dagger.hilt.android.AndroidEntryPoint
import java.io.IOException

@AndroidEntryPoint
class MyProfileFragment : Fragment() {
    private var _binding: FragmentMyProfileBinding? = null
    private val binding get() = _binding!!

    private val myProfileViewModel: MyProfileViewModel by viewModels()

    private lateinit var mainActivity: MainActivity

    private var mSelectedImageFileUri: Uri? = null
    private var mDownloadableUrl: String? = null
    private lateinit var mUserDetails: User

    private val pickProfileImage = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) {
            setImage(uri)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMyProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mainActivity = requireActivity() as MainActivity
        binding.toolbarMyProfile.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
        mainActivity.onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            findNavController().popBackStack()
        }
        getCurrentUser()
        binding.ivUserImage.setOnClickListener {
            pickProfileImage.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }
        binding.btnUpdate.setOnClickListener {
            if (mSelectedImageFileUri != null) {
                uploadProfileImageAndAfterUpdateUserProfile()
            } else {
                updateUserProfile()
            }
        }
    }

    private fun getCurrentUser() {
        mainActivity.showProgressDialog()
        myProfileViewModel.getCurrentUser()
        myProfileViewModel.getCurrentUserState.observe(viewLifecycleOwner) { getUser ->
            when {
                getUser.loading -> { }
                getUser.error.isNotEmpty() -> {
                    mainActivity.hideProgressDialog()
                    mainActivity.showErrorSnackBar(getUser.error)
                }
                else -> {
                    mainActivity.hideProgressDialog()
                    setUserDataInUI(getUser.data!!)
                }
            }
        }
    }

    private fun setUserDataInUI(user: User) {
        mUserDetails = user
        Glide
            .with(this)
            .load(user.image)
            .centerCrop()
            .placeholder(R.drawable.ic_user_place_holder)
            .into(binding.ivUserImage)

        binding.etName.setText(user.name)
        binding.etEmail.setText(user.email)
        if (user.mobile != 0L) {
            binding.etMobile.setText(user.mobile.toString())
        }
    }

    private fun setImage(image: Uri) {
        mSelectedImageFileUri = image
        try {
            Glide
                .with(this)
                .load(mSelectedImageFileUri)
                .centerCrop()
                .placeholder(R.drawable.ic_user_place_holder)
                .into(binding.ivUserImage)
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun updateUserProfile() {
        mainActivity.showProgressDialog()
        val newImageUrl = if (!mDownloadableUrl.isNullOrEmpty() && mDownloadableUrl != mUserDetails.image) mDownloadableUrl!! else ""
        val newName = if (binding.etName.text.toString() != mUserDetails.name && !binding.etName.text.isNullOrEmpty()) binding.etName.text.toString() else ""
        val newMobile = if (binding.etMobile.text.toString() != mUserDetails.mobile.toString() && !binding.etMobile.text.isNullOrEmpty()) binding.etMobile.text.toString().toLong() else 0
        myProfileViewModel.updateUser(newImageUrl, newName, newMobile)
        myProfileViewModel.updateUserState.observe(viewLifecycleOwner) { updateUser ->
            when {
                updateUser.loading -> { }
                updateUser.error.isNotEmpty() -> {
                    mainActivity.hideProgressDialog()
                    mainActivity.showErrorSnackBar(updateUser.error)
                }
                else -> {
                    mainActivity.hideProgressDialog()
                    userUpdatedSuccessfully()
                }
            }
        }
    }

    private fun userUpdatedSuccessfully() {
        Toast.makeText(mainActivity, R.string.user_updated_successfully, Toast.LENGTH_SHORT).show()
        findNavController().popBackStack()
    }

    private fun uploadProfileImageAndAfterUpdateUserProfile() {
        mainActivity.showProgressDialog()
        val imageName = FirebaseStorageObjects.USER_IMAGE + System.currentTimeMillis() + "." + mainActivity.getFileExtension(mSelectedImageFileUri)
        myProfileViewModel.uploadImage(imageName, mSelectedImageFileUri!!)
        myProfileViewModel.uploadImageState.observe(viewLifecycleOwner) { uploadImage ->
            when {
                uploadImage.loading -> { }
                uploadImage.error.isNotEmpty() -> {
                    mainActivity.hideProgressDialog()
                    mainActivity.showErrorSnackBar(uploadImage.error)
                }
                else -> {
                    mainActivity.hideProgressDialog()
                    mDownloadableUrl = uploadImage.data
                    updateUserProfile()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}