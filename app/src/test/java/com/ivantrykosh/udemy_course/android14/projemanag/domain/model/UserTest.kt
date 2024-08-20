package com.ivantrykosh.udemy_course.android14.projemanag.domain.model

import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class UserTest {

    private val testUser = User(
        id = "testId",
        name = "test name",
        email = "test@email.com",
        image = "image_url",
        mobile = 338811L,
        fcmToken = "testToken"
    )

    @Test
    fun createParcelWithConstructor_success() {
        val parcel = ParcelFake.obtain()
        testUser.writeToParcel(parcel, 0)
        parcel.setDataPosition(0)

        val createdUser = User(parcel)
        Assert.assertEquals(testUser, createdUser)
    }

    @Test
    fun createParcelWithCreator_success() {
        val parcel = ParcelFake.obtain()
        testUser.writeToParcel(parcel, 0)
        parcel.setDataPosition(0)

        val createdUser = User.CREATOR.createFromParcel(parcel)
        Assert.assertEquals(testUser, createdUser)
    }

    @Test
    fun createArrayOfUser_success() {
        val size = 4
        val array = User.CREATOR.newArray(size)

        Assert.assertEquals(size, array.size)
        array.forEach { item ->
            Assert.assertNull(item)
        }
    }

    @Test
    fun describeContents_success() {
        val value = User().describeContents()
        Assert.assertEquals(0, value)
    }

    @Test
    fun fieldsNamesEquals_success() {
        Assert.assertEquals(User.FIELDS.ID, User::id.name)
        Assert.assertEquals(User.FIELDS.NAME, User::name.name)
        Assert.assertEquals(User.FIELDS.EMAIL, User::email.name)
        Assert.assertEquals(User.FIELDS.IMAGE, User::image.name)
        Assert.assertEquals(User.FIELDS.MOBILE, User::mobile.name)
        Assert.assertEquals(User.FIELDS.FCM_TOKEN, User::fcmToken.name)
        Assert.assertEquals(User.FIELDS.SELECTED, User::selected.name)
    }
}