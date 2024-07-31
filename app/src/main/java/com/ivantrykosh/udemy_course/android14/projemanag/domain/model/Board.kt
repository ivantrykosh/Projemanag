package com.ivantrykosh.udemy_course.android14.projemanag.domain.model

import android.os.Parcel
import android.os.Parcelable

data class Board(
    var documentId: String = "",
    val name: String = "",
    val image: String = "",
    val createdBy: String = "",
    val assignedTo: ArrayList<String> = ArrayList(),
    val taskList: ArrayList<Task> = ArrayList()
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.createStringArrayList()!!,
        parcel.createTypedArrayList(Task.CREATOR)!!
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(documentId)
        parcel.writeString(name)
        parcel.writeString(image)
        parcel.writeString(createdBy)
        parcel.writeStringList(assignedTo)
        parcel.writeTypedList(taskList)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Board> {
        override fun createFromParcel(parcel: Parcel): Board {
            return Board(parcel)
        }

        override fun newArray(size: Int): Array<Board?> {
            return arrayOfNulls(size)
        }
    }

    object FIELDS {
        const val DOCUMENT_ID = "documentId"
        const val NAME = "name"
        const val IMAGE = "image"
        const val CREATED_BY = "createdBy"
        const val ASSIGNED_TO = "assignedTo"
        const val TASK_LIST = "taskList"
    }
}
