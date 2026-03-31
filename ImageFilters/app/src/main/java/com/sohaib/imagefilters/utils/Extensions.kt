package com.sohaib.imagefilters.utils

import android.content.Context
import android.util.Log
import android.widget.Toast

/**
 * @Author: SOHAIB AHMED
 * @Date: 8/30/2023
 * @Accounts
 *      -> https://github.com/epegasus
 *      -> https://stackoverflow.com/users/20440272/sohaib-ahmed
 */

object Extensions {

    fun showLog(message: Any) {
        Log.d("MyTag", "showLog: $message")
    }

    fun Context.showToast(message: Any) {
        Toast.makeText(this, message.toString(), Toast.LENGTH_SHORT).show()
    }

}