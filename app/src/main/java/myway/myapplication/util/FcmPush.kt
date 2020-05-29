package myway.myapplication.util

import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull

class FcmPush {
    var JSON = "application/json; charset=utf-8".toMediaTypeOrNull()
    var url ="https://fcm.googleapis.com/fcm/send"
    var serverKey = ""
}