package com.dws.tc.util

import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.ObjectInputStream
import java.io.ObjectOutputStream

fun Any.toByteArray(): ByteArray {
    val byteArrayOutputStream = ByteArrayOutputStream()
    val objectOutputStream= ObjectOutputStream(byteArrayOutputStream)
    objectOutputStream.writeObject(this)
    objectOutputStream.flush()
    val result = byteArrayOutputStream.toByteArray()
    byteArrayOutputStream.close()
    objectOutputStream.close()
    return result
}
@Suppress("UNCHECKED_CAST")
fun fromByteArray(byteArray: ByteArray): Any {
    val byteArrayInputStream = ByteArrayInputStream(byteArray)
    val objectInput= ObjectInputStream(byteArrayInputStream)
    val result = objectInput.readObject()
    objectInput.close()
    byteArrayInputStream.close()
    return result
}