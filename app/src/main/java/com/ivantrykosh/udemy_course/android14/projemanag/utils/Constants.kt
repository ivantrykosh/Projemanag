package com.ivantrykosh.udemy_course.android14.projemanag.utils

import com.ivantrykosh.udemy_course.android14.projemanag.BuildConfig

object Constants {
    const val BOARD_DETAIL = "board_detail"
    const val TASK_LIST_ITEM_POSITION = "task_list_item_position"
    const val CARD_LIST_ITEM_POSITION = "card_list_item_position"
    const val BOARD_MEMBERS_LIST = "board_members_list"
    const val SELECT = "select"
    const val UNSELECT = "unselect"

    const val PROJEMANAG = "projemanag_prefs"

    const val FCM_BASE_URL = "https://fcm.googleapis.com/fcm/send"
    const val FCM_AUTHORIZATION = "authorization"
    const val FCM_KEY = "key"
    const val FCM_KEY_TITLE = "title"
    const val FCM_KEY_MESSAGE = "message"
    const val FCM_KEY_DATA = "data"
    const val FCM_KEY_TO = "to"

    const val FCM_API_KEY = BuildConfig.FCM_API_KEY
}