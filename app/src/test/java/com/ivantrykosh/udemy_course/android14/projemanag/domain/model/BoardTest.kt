package com.ivantrykosh.udemy_course.android14.projemanag.domain.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class BoardTest {

    private val testBoard = Board(
        documentId = "doc123",
        name = "Project Board",
        image = "image_url",
        createdBy = "user123",
        assignedTo = arrayListOf("user1", "user2"),
        taskList = arrayListOf(Task("task1"), Task("task2"))
    )

    @Test
    fun createParcelWithConstructor_success() {
        val parcel = ParcelFake.obtain()
        testBoard.writeToParcel(parcel, 0)
        parcel.setDataPosition(0)

        val createdBoard = Board(parcel)
        assertEquals(testBoard, createdBoard)
    }

    @Test
    fun createParcelWithCreator_success() {
        val parcel = ParcelFake.obtain()
        testBoard.writeToParcel(parcel, 0)
        parcel.setDataPosition(0)

        val createdBoard = Board.CREATOR.createFromParcel(parcel)
        assertEquals(testBoard, createdBoard)
    }

    @Test
    fun createArrayOfBoard_success() {
        val size = 4
        val array = Board.CREATOR.newArray(size)

        assertEquals(size, array.size)
        array.forEach { item ->
            assertNull(item)
        }
    }

    @Test
    fun describeContents_success() {
        val value = Board().describeContents()
        assertEquals(0, value)
    }

    @Test
    fun fieldsNamesEquals_success() {
        assertEquals(Board.FIELDS.DOCUMENT_ID, Board::documentId.name)
        assertEquals(Board.FIELDS.NAME, Board::name.name)
        assertEquals(Board.FIELDS.IMAGE, Board::image.name)
        assertEquals(Board.FIELDS.CREATED_BY, Board::createdBy.name)
        assertEquals(Board.FIELDS.ASSIGNED_TO, Board::assignedTo.name)
        assertEquals(Board.FIELDS.TASK_LIST, Board::taskList.name)
    }
}