package com.polohach.geofence.example.utils

import android.text.Editable
import android.text.TextWatcher

open class SimpleTextWatcher : TextWatcher {

    override fun afterTextChanged(s: Editable?) {
        // override to implement
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        // override to implement
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        // override to implement
    }
}
