package com.ivantrykosh.udemy_course.android14.projemanag.utils

import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import android.webkit.MimeTypeMap
import com.ivantrykosh.udemy_course.android14.projemanag.BuildConfig
import com.ivantrykosh.udemy_course.android14.projemanag.presenter.BaseActivity

object Constants {
    const val FCM_TOKEN_UPDATED = "fcm_token_updated"

    const val BOARD_DETAIL = "board_detail"
    const val TASK_LIST_ITEM_POSITION = "task_list_item_position"
    const val CARD_LIST_ITEM_POSITION = "card_list_item_position"
    const val BOARD_MEMBERS_LIST = "board_members_list"
    const val SELECT = "select"
    const val UNSELECT = "unselect"

    const val PROJEMANAG = "projemanag_prefs"

    const val READ_STORAGE_PERMISSION_CODE = 1
    const val PICK_IMAGE_REQUEST_CODE = 2

    const val FCM_BASE_URL = "https://fcm.googleapis.com/fcm/send"
    const val FCM_AUTHORIZATION = "authorization"
    const val FCM_KEY = "key"
    const val FCM_KEY_TITLE = "title"
    const val FCM_KEY_MESSAGE = "message"
    const val FCM_KEY_DATA = "data"
    const val FCM_KEY_TO = "to"

    const val FCM_API_KEY = BuildConfig.FCM_API_KEY

    fun showImageChooser(activity: BaseActivity) {
        val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        activity.startActivityForResult(galleryIntent, PICK_IMAGE_REQUEST_CODE)
    }

    fun getFileExtension(uri: Uri?, activity: BaseActivity): String? {
        return MimeTypeMap.getSingleton().getExtensionFromMimeType(activity.contentResolver.getType(uri!!))
    }
}