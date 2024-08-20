package com.ivantrykosh.udemy_course.android14.projemanag.domain.model

import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class SelectedMembersTest {

    private val testSelectedMembers = SelectedMembers(
        id = "testId",
        image = "image_url"
    )

    @Test
    fun createParcelWithConstructor_success() {
        val parcel = ParcelFake.obtain()
        testSelectedMembers.writeToParcel(parcel, 0)
        parcel.setDataPosition(0)

        val createdSelectedMembers = SelectedMembers(parcel)
        Assert.assertEquals(testSelectedMembers, createdSelectedMembers)
    }

    @Test
    fun createParcelWithCreator_success() {
        val parcel = ParcelFake.obtain()
        testSelectedMembers.writeToParcel(parcel, 0)
        parcel.setDataPosition(0)

        val createdSelectedMembers = SelectedMembers.CREATOR.createFromParcel(parcel)
        Assert.assertEquals(testSelectedMembers, createdSelectedMembers)
    }

    @Test
    fun createArrayOfSelectedMembers_success() {
        val size = 4
        val array = SelectedMembers.CREATOR.newArray(size)

        Assert.assertEquals(size, array.size)
        array.forEach { item ->
            Assert.assertNull(item)
        }
    }

    @Test
    fun describeContents_success() {
        val value = SelectedMembers().describeContents()
        Assert.assertEquals(0, value)
    }
}