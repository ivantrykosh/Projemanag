package com.ivantrykosh.udemy_course.android14.projemanag.presenter.main.members

import android.app.Dialog
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.addCallback
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.ivantrykosh.udemy_course.android14.projemanag.R
import com.ivantrykosh.udemy_course.android14.projemanag.adapters.MemberListItemsAdapter
import com.ivantrykosh.udemy_course.android14.projemanag.databinding.FragmentMembersBinding
import com.ivantrykosh.udemy_course.android14.projemanag.domain.model.Board
import com.ivantrykosh.udemy_course.android14.projemanag.domain.model.User
import com.ivantrykosh.udemy_course.android14.projemanag.presenter.main.MainActivity
import com.ivantrykosh.udemy_course.android14.projemanag.utils.Constants
import dagger.hilt.android.AndroidEntryPoint
import org.json.JSONObject
import java.io.BufferedReader
import java.io.DataOutputStream
import java.io.IOException
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.SocketTimeoutException
import java.net.URL

@AndroidEntryPoint
class MembersFragment : Fragment() {
    private var _binding: FragmentMembersBinding? = null
    private val binding get() = _binding!!

    private lateinit var mainActivity: MainActivity

    private val membersViewModel: MembersViewModel by viewModels()

    private lateinit var mAssignedMembersList: ArrayList<User>
    private lateinit var mBoard: Board

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMembersBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mainActivity = requireActivity() as MainActivity
        binding.toolbarMembers.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
        mainActivity.onBackPressedDispatcher.addCallback {
            findNavController().popBackStack()
        }
        binding.toolbarMembers.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.action_add_member -> {
                    dialogSearchMember()
                    return@setOnMenuItemClickListener true
                }
            }
            false
        }
        getBundleData()
        getAssignedMembersDetails()
        observeGetAssignedMembersDetailsState()
        observeGetMemberDetailsState()
        observeAssignMembersToBoard()
    }

    private fun observeGetAssignedMembersDetailsState() {
        membersViewModel.getUsersByIdsState.observe(viewLifecycleOwner) { result ->
            when {
                result.loading -> { }
                result.error.isNotEmpty() -> {
                    mainActivity.hideProgressDialog()
                    mainActivity.showErrorSnackBar(result.error)
                }
                else -> {
                    mainActivity.hideProgressDialog()
                    setupMembersList(result.data!!)
                }
            }
        }
    }

    private fun observeGetMemberDetailsState() {
        membersViewModel.getUserByEmailState.observe(viewLifecycleOwner) { result ->
            when {
                result.loading -> { }
                result.error.isNotEmpty() -> {
                    mainActivity.hideProgressDialog()
                    mainActivity.showErrorSnackBar(result.error)
                }
                else -> {
                    mainActivity.hideProgressDialog()
                    memberDetails(result.data!!)
                }
            }
        }
    }

    private fun observeAssignMembersToBoard() {
        membersViewModel.assignMembersState.observe(viewLifecycleOwner) { result ->
            when {
                result.loading -> { }
                result.error.isNotEmpty() -> {
                    mainActivity.hideProgressDialog()
                    mainActivity.showErrorSnackBar(result.error)
                }
                else -> {
                    mainActivity.hideProgressDialog()
                    memberAssignSuccess()
                }
            }
        }
    }

    private fun dialogSearchMember() {
        val dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.dialog_search_member)
        dialog.findViewById<TextView>(R.id.tv_add).setOnClickListener {
            val email = dialog.findViewById<EditText>(R.id.et_email_search_member).text.toString()
            if (email.isNotEmpty()) {
                dialog.dismiss()
                mainActivity.showProgressDialog()
                membersViewModel.getUserByEmail(email)
            } else {
                Toast.makeText(context, R.string.please_enter_email, Toast.LENGTH_SHORT).show()
            }
        }
        dialog.findViewById<TextView>(R.id.tv_cancel).setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }

    @Suppress("DEPRECATION")
    private fun getBundleData() {
        mBoard = if (Build.VERSION.SDK_INT > Build.VERSION_CODES.TIRAMISU) {
            arguments?.getParcelable(Constants.BOARD_DETAIL, Board::class.java)!!
        } else {
            arguments?.getParcelable(Constants.BOARD_DETAIL)!!
        }
    }

    private fun setupMembersList(members: List<User>) {
        mAssignedMembersList = ArrayList(members)
        binding.rvMembersList.layoutManager = LinearLayoutManager(context)
        binding.rvMembersList.setHasFixedSize(true)
        val adapter = MemberListItemsAdapter(requireContext(), members.toMutableList())
        binding.rvMembersList.adapter = adapter
    }

    private fun getAssignedMembersDetails() {
        mainActivity.showProgressDialog()
        membersViewModel.getUsersByIds(mBoard.assignedTo)
    }

    private fun memberDetails(user: User) {
        mBoard.assignedTo.add(user.id)
        mAssignedMembersList.add(user)
        mainActivity.showProgressDialog()
        membersViewModel.assignMembers(mBoard.documentId, mBoard.assignedTo)
    }

    private fun memberAssignSuccess() {
        setupMembersList(mAssignedMembersList)
        // todo
//        SendNotificationToUser(mBoard.name, mAssignedMembersList.last().fcmToken).execute()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private inner class SendNotificationToUser(val boardName: String, val token: String): AsyncTask<Any, Void, String>() {
        override fun doInBackground(vararg params: Any?): String {
            var result: String
            var connection: HttpURLConnection? = null
            try {
                val url = URL(Constants.FCM_BASE_URL)
                connection = url.openConnection() as HttpURLConnection
                connection.doOutput = true
                connection.doInput = true
                connection.instanceFollowRedirects = false
                connection.requestMethod = "POST"
                connection.setRequestProperty("Content-Type", "application/json")
                connection.setRequestProperty("charset", "utf-8")
                connection.setRequestProperty("Accept", "application/json")
                connection.setRequestProperty(Constants.FCM_AUTHORIZATION, "${Constants.FCM_KEY}=${Constants.FCM_API_KEY}")
                connection.useCaches = false

                val wr = DataOutputStream(connection.outputStream)
                val jsonRequest = JSONObject()
                val dataObject = JSONObject()
                dataObject.put(Constants.FCM_KEY_TITLE, "Assigned to the board ${boardName}}")
                dataObject.put(Constants.FCM_KEY_MESSAGE, "You have been assigned to the Board by ${mAssignedMembersList[0].name}")
                jsonRequest.put(Constants.FCM_KEY_DATA, dataObject)
                jsonRequest.put(Constants.FCM_KEY_TO, token)
                wr.writeBytes(jsonRequest.toString())
                wr.flush()
                wr.close()

                val httpResult = connection.responseCode
                if (httpResult == HttpURLConnection.HTTP_OK) {
                    val inputSteam = connection.inputStream
                    val reader = BufferedReader(InputStreamReader(inputSteam))
                    val sb = StringBuilder()
                    var line: String?
                    try {
                        while (reader.readLine().also { line = it } != null) {
                            sb.append(line + "\n")
                        }
                    } catch (e: IOException) {
                        e.printStackTrace()
                    } finally {
                        try {
                            inputSteam.close()
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }
                    result = sb.toString()
                } else {
                    result = connection.responseMessage
                }
            } catch (e: SocketTimeoutException) {
                result = "Connection timeout"
            } catch (e: Exception) {
                result = "Error: " + e.message
            } finally {
                connection?.disconnect()
            }
            return result
        }

        override fun onPreExecute() {
            super.onPreExecute()
            mainActivity.showProgressDialog()
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            mainActivity.hideProgressDialog()
        }
    }
}