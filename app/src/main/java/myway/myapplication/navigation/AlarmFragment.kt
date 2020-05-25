package com.company.howl.howlstagram.navigation


import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.android.synthetic.main.activity_main.*
import myway.myapplication.MainActivity
import myway.myapplication.R
import okhttp3.*
import java.util.*


class AlarmFragment : Fragment() {

    var mainView: View? = null
    var imagesSnapshot  : ListenerRegistration? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mainView = inflater.inflate(R.layout.fragment_alarm, container, false)

        return mainView
    }

    inner class CustomViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}
