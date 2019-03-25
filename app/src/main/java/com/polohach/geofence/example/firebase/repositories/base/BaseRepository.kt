package com.polohach.geofence.example.firebase.repositories.base

import androidx.lifecycle.MutableLiveData
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.polohach.geofence.example.utils.LOG

abstract class BaseRepository<T>(val valueType: Class<T>) {

    companion object {

        private val TAG = BaseRepository::class.java.simpleName
    }

    abstract val reference: DatabaseReference

    protected val listLiveData = MutableLiveData<List<T>>()

    private var eventListener: ValueEventListener? = null

    fun onStart() {
        eventListener = eventListener ?: object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                parseChildData(dataSnapshot)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                LOG.e(TAG, "Load ${valueType.simpleName}:onCancelled", databaseError.toException())
            }
        }
        eventListener?.let {
            reference.addValueEventListener(it)
        }
    }

    fun onStop() {
        eventListener?.let {
            reference.removeEventListener(it)
            eventListener = null
        }
    }

    private fun parseChildData(dataSnapshot: DataSnapshot) {
        listLiveData.value = mutableListOf<T>().apply {
            dataSnapshot.children.forEach { snapshot ->
                snapshot.getValue(valueType)?.let { add(it) }
            }
        }
    }
}
