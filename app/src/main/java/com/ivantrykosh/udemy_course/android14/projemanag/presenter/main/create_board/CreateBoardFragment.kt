package com.ivantrykosh.udemy_course.android14.projemanag.presenter.main.create_board

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
import com.ivantrykosh.udemy_course.android14.projemanag.databinding.FragmentCreateBoardBinding
import com.ivantrykosh.udemy_course.android14.projemanag.domain.model.User
import com.ivantrykosh.udemy_course.android14.projemanag.presenter.main.MainActivity
import com.ivantrykosh.udemy_course.android14.projemanag.utils.FirebaseStorageObjects
import dagger.hilt.android.AndroidEntryPoint
import java.io.IOException

@AndroidEntryPoint
class CreateBoardFragment : Fragment() {
    private var _binding: FragmentCreateBoardBinding? = null
    private val binding get() = _binding!!

    private val createBoardViewModel: CreateBoardViewModel by viewModels()

    private lateinit var mainActivity: MainActivity

    private var mSelectedImageFileUri: Uri? = null
    private var mBoardImageUrl: String? = null
    private lateinit var mUsername: String
    private lateinit var mUserId: String

    private val boardImagePicker = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) {
            setImage(uri)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCreateBoardBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mainActivity = requireActivity() as MainActivity
        mUserId = arguments?.getString(User.FIELDS.ID) ?: ""
        mUsername = arguments?.getString(User.FIELDS.NAME) ?: ""
        binding.toolbarCreateBoard.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
        mainActivity.onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            findNavController().popBackStack()
        }
        binding.ivBoardImage.setOnClickListener {
            boardImagePicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }
        binding.btnCreate.setOnClickListener {
            if (mSelectedImageFileUri != null) {
                uploadBoardImageAndAfterCreateBoard()
            } else {
                createBoard()
            }
        }
        observeUploadImageState()
        observeCreateBoardState()
    }

    private fun observeUploadImageState() {
        createBoardViewModel.uploadImageState.observe(viewLifecycleOwner) { uploadImage ->
            when {
                uploadImage.loading -> { }
                uploadImage.error.isNotEmpty() -> {
                    mainActivity.hideProgressDialog()
                    mainActivity.showErrorSnackBar(uploadImage.error)
                }
                else -> {
                    mainActivity.hideProgressDialog()
                    mBoardImageUrl = uploadImage.data
                    createBoard()
                }
            }
        }
    }

    private fun observeCreateBoardState() {
        createBoardViewModel.createBoardState.observe(viewLifecycleOwner) { result ->
            when {
                result.loading -> { }
                result.error.isNotEmpty() -> {
                    mainActivity.hideProgressDialog()
                    mainActivity.showErrorSnackBar(result.error)
                }
                else -> {
                    mainActivity.hideProgressDialog()
                    boardCreatedSuccessfully()
                }
            }
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
                .into(binding.ivBoardImage)
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun uploadBoardImageAndAfterCreateBoard() {
        mainActivity.showProgressDialog()
        val imageName = FirebaseStorageObjects.BOARD_IMAGE + System.currentTimeMillis() + "." + mainActivity.getFileExtension(mSelectedImageFileUri)
        createBoardViewModel.uploadImage(imageName, mSelectedImageFileUri!!)
    }

    private fun createBoard() {
        mainActivity.showProgressDialog()
        val assignedUsersList = ArrayList<String>()
        assignedUsersList.add(mUserId)
        val name = binding.etBoardName.text?.toString()
        if (name.isNullOrEmpty()) {
            Toast.makeText(context, R.string.please_enter_name, Toast.LENGTH_SHORT).show()
        } else {
            createBoardViewModel.createBoard(name, mBoardImageUrl ?: "", mUsername, assignedUsersList)
        }
    }

    private fun boardCreatedSuccessfully() {
        findNavController().popBackStack()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}