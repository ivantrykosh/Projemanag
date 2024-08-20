package com.ivantrykosh.udemy_course.android14.projemanag.domain.model

import android.os.Parcel
import android.os.Parcelable
import org.mockito.ArgumentMatchers
import org.mockito.Mockito.lenient
import org.mockito.invocation.InvocationOnMock
import org.mockito.kotlin.mock

class ParcelFake {

    companion object {
        @JvmStatic
        fun obtain(): Parcel {
            return ParcelFake().mock
        }
    }

    private var position = 0
    private var store = mutableListOf<Any>()
    private var mock = mock<Parcel>()

    init {
        setupWrites()
        setupReads()
        setupOthers()
    }

    private fun setupWrites() {
        val answer = { i: InvocationOnMock ->
            with(store) {
                add(i.arguments[0])
                get(lastIndex)
            }
        }
        lenient().`when`(mock.writeByte(ArgumentMatchers.anyByte())).thenAnswer(answer)
        lenient().`when`(mock.writeInt(ArgumentMatchers.anyInt())).thenAnswer(answer)
        lenient().`when`(mock.writeString(ArgumentMatchers.anyString())).thenAnswer(answer)
        lenient().`when`(mock.writeLong(ArgumentMatchers.anyLong())).thenAnswer(answer)
        lenient().`when`(mock.writeFloat(ArgumentMatchers.anyFloat())).thenAnswer(answer)
        lenient().`when`(mock.writeDouble(ArgumentMatchers.anyDouble())).thenAnswer(answer)
        lenient().`when`(mock.writeStringList(ArgumentMatchers.anyList())).thenAnswer { i ->
            with(store) {
                add(i.arguments[0])
                get(lastIndex)
            }
        }
        lenient().`when`(mock.writeTypedList(ArgumentMatchers.anyList())).thenAnswer { i ->
            with(store) {
                add(i.arguments[0])
                get(lastIndex)
            }
        }
    }

    private fun setupReads() {
        val answer = { _: InvocationOnMock -> store[position++] }
        lenient().`when`(mock.readByte()).thenAnswer(answer)
        lenient().`when`(mock.readInt()).thenAnswer(answer)
        lenient().`when`(mock.readString()).thenAnswer(answer)
        lenient().`when`(mock.readLong()).thenAnswer(answer)
        lenient().`when`(mock.readFloat()).thenAnswer(answer)
        lenient().`when`(mock.readDouble()).thenAnswer(answer)
        lenient().`when`(mock.createStringArrayList()).thenAnswer {
            store[position++] as List<*>
        }
        lenient().`when`(mock.createTypedArrayList(ArgumentMatchers.any(Parcelable.Creator::class.java))).thenAnswer {
            store[position++] as List<*>
        }
    }

    private fun setupOthers() {
        val answer = { i: InvocationOnMock ->
            position = i.arguments[0] as Int
            null
        }
        lenient().`when`(mock.setDataPosition(ArgumentMatchers.anyInt()))
            .thenAnswer(answer)
    }
}