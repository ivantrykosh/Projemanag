package com.ivantrykosh.udemy_course.android14.projemanag.domain.model

import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class CardTest {

    private val testCard = Card(
        name = "card name",
        createdBy = "user1Id",
        assignedTo = ArrayList(listOf("user1Id", "user2Id")),
        labelColor = "#FFFFFF",
        dueDate = System.currentTimeMillis()
    )

    @Test
    fun createParcelWithConstructor_success() {
        val parcel = ParcelFake.obtain()
        testCard.writeToParcel(parcel, 0)
        parcel.setDataPosition(0)

        val createdCard = Card(parcel)
        Assert.assertEquals(testCard, createdCard)
    }

    @Test
    fun createParcelWithCreator_success() {
        val parcel = ParcelFake.obtain()
        testCard.writeToParcel(parcel, 0)
        parcel.setDataPosition(0)

        val createdCard = Card.CREATOR.createFromParcel(parcel)
        Assert.assertEquals(testCard, createdCard)
    }

    @Test
    fun createArrayOfCard_success() {
        val size = 4
        val array = Card.CREATOR.newArray(size)

        Assert.assertEquals(size, array.size)
        array.forEach { item ->
            Assert.assertNull(item)
        }
    }

    @Test
    fun describeContents_success() {
        val value = Card().describeContents()
        Assert.assertEquals(0, value)
    }

    @Test
    fun fieldsNamesEquals_success() {
        Assert.assertEquals(Card.FIELDS.NAME, Card::name.name)
        Assert.assertEquals(Card.FIELDS.CREATED_BY, Card::createdBy.name)
        Assert.assertEquals(Card.FIELDS.ASSIGNED_TO, Card::assignedTo.name)
        Assert.assertEquals(Card.FIELDS.LABEL_COLOR, Card::labelColor.name)
        Assert.assertEquals(Card.FIELDS.DUE_DATE, Card::dueDate.name)
    }
}