package com.ivantrykosh.udemy_course.android14.projemanag.domain.model

import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class TaskTest {

    private val testTask = Task(
        title = "test title",
        createdBy = "testUserId",
        cards = ArrayList(listOf(Card(name = "card name", createdBy = "testUserId")))
    )

    @Test
    fun createParcelWithConstructor_success() {
        val parcel = ParcelFake.obtain()
        testTask.writeToParcel(parcel, 0)
        parcel.setDataPosition(0)

        val createdTask = Task(parcel)
        Assert.assertEquals(testTask, createdTask)
    }

    @Test
    fun createParcelWithCreator_success() {
        val parcel = ParcelFake.obtain()
        testTask.writeToParcel(parcel, 0)
        parcel.setDataPosition(0)

        val createdTask = Task.CREATOR.createFromParcel(parcel)
        Assert.assertEquals(testTask, createdTask)
    }

    @Test
    fun createArrayOfTask_success() {
        val size = 4
        val array = Task.CREATOR.newArray(size)

        Assert.assertEquals(size, array.size)
        array.forEach { item ->
            Assert.assertNull(item)
        }
    }

    @Test
    fun describeContents_success() {
        val value = Task().describeContents()
        Assert.assertEquals(0, value)
    }

    @Test
    fun fieldsNamesEquals_success() {
        Assert.assertEquals(Task.FIELDS.TITLE, Task::title.name)
        Assert.assertEquals(Task.FIELDS.CREATED_BY, Task::createdBy.name)
        Assert.assertEquals(Task.FIELDS.CARDS, Task::cards.name)
    }
}