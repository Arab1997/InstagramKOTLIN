package myway.myapplication.navigation


import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Message
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.graphics.convertTo
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.Glide.init
import com.bumptech.glide.request.RequestOptions
import com.google.api.Billing
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_detail.view.*
import kotlinx.android.synthetic.main.item_detail.view.*
import myway.myapplication.MainActivity
import myway.myapplication.R
import myway.myapplication.model.AlarmDTO
import myway.myapplication.model.ContentDTO
import myway.myapplication.navigation.CommentActivity
import okhttp3.*
import org.w3c.dom.Comment
import java.util.*
import kotlin.collections.ArrayList


class DetailViewFragment : Fragment() {

    //var mainView: View? = null
    var imagesSnapshot: ListenerRegistration? = null
    var firestore: FirebaseFirestore? = null
    var uid: String? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var view = inflater.inflate(R.layout.fragment_detail, container, false)

        firestore = FirebaseFirestore.getInstance()
        uid = FirebaseAuth.getInstance().currentUser?.uid


        view.detailViewFragment_recyclerview.adapter = DetailViewRecyclerViewAdapter()
        view.detailViewFragment_recyclerview.layoutManager = LinearLayoutManager(activity)
        return view
    }

    inner class DetailViewRecyclerViewAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
        var contentDTOs: ArrayList<ContentDTO> = arrayListOf()
        var contentUidList: ArrayList<String> = arrayListOf()


        init {
            firestore?.collection("images")?.orderBy("timestamp")
                ?.addSnapshotListener { querySnapshot, _ ->
                    contentDTOs.clear()
                    contentUidList.clear()

                    //Sometimes this Code return null of querySnapshot  when it signout
                    if (querySnapshot == null) return@addSnapshotListener

                    for (snapshot in querySnapshot.documents) {
                        var items = snapshot.toObject(ContentDTO::class.java)
                        contentDTOs.add(items!!)
                        contentUidList.add(snapshot.id)
                    }
                    notifyDataSetChanged()
                }
        }

        override fun onCreateViewHolder(p0: ViewGroup, p1: Int): RecyclerView.ViewHolder {
            var view = LayoutInflater.from(p0.context).inflate(R.layout.item_detail, p0, false)
            return CustomViewHolder(view)
        }

        inner class CustomViewHolder(view: View) : RecyclerView.ViewHolder(view)

        override fun getItemCount(): Int {
            return contentDTOs.size
        }

        @SuppressLint("SetTextI18n")
        override fun onBindViewHolder(p0: RecyclerView.ViewHolder, position: Int) {
            var viewHolder = (p0 as CustomViewHolder).itemView

            //User id
            viewHolder.detailviewitem_profile_textview.text = contentDTOs[position].userId
            //image
            Glide.with(p0.itemView.context).load(contentDTOs[position].imageUrl)
                .into(viewHolder.detailviewitem_imageview_content)
            //Explain of content
            viewHolder.detailviewitem_explain_textview.text = contentDTOs[position].explain
            //likes
            viewHolder.detailviewitem_favoritecounter_textview.text =
                "Likes " + contentDTOs[position].favoriteCount

            //This code is when  the button is clicked
            viewHolder.detailviewitem_favorite_imageview.setOnClickListener {
                favouriteEvent(position)
            }

            // This code is when  the  page  is   loaded
            if (contentDTOs[position].favorites.containsKey(uid)) {
                //this is like  status
                viewHolder.detailviewitem_favorite_imageview.setImageResource(R.drawable.ic_favourite)
            } else {
                //this is unlike status
                viewHolder.detailviewitem_favorite_imageview.setImageResource(R.drawable.ic_favourite_border)
            }
            //This  code is when  the profile  image  is  clicked
            viewHolder.detailviewitem_profile_textview.setOnClickListener {
                var fragment = UserFragment()
                var bundle = Bundle()
                bundle.putString("destinationUid", contentDTOs[position].uid)
                bundle.putString("userId", contentDTOs[position].userId)
                fragment.arguments = bundle
                activity?.supportFragmentManager?.beginTransaction()
                    ?.replace(R.id.main_content, fragment)?.commit()
            }
            viewHolder.detailviewitem_comment_imageview.setOnClickListener { v ->

                var intent = Intent(v.context, CommentActivity::class.java)
                intent.putExtra("contentUid", contentUidList[position])
                intent.putExtra("destinationUid", contentDTOs[position].uid)
                startActivity(intent)

            }

            viewHolder.detailviewitem_comment_imageview.setOnClickListener { v ->
                var intent  = Intent(v.context, CommentActivity::class.java)
                intent.putExtra("contentUid",contentUidList[position] )
                startActivity(intent)
            }
        }


        fun favouriteEvent(position: Int) {
            var tsDoc = firestore?.collection("images")?.document(contentUidList[position])
            firestore?.runTransaction { transaction ->


                var contentDTO = transaction.get(tsDoc!!).toObject(ContentDTO::class.java)

                if (contentDTO!!.favorites.containsKey(uid)) {
                    //when the button is clicked
                    contentDTO.favoriteCount = contentDTO.favoriteCount - 1
                    contentDTO.favorites.remove(uid)
                } else {
                    //when the button is not clicked
                    contentDTO.favoriteCount = contentDTO.favoriteCount + 1
                    contentDTO.favorites[uid!!] = true
                    favouriteAlarm(contentDTOs[position].uid!!)
                }
                transaction.set(tsDoc, contentDTO)
            }


        }

        fun favouriteAlarm(destinationUid: String) {
            var alarmDTO = AlarmDTO()
            alarmDTO.destinationUid = destinationUid
            alarmDTO.userId = FirebaseAuth.getInstance().currentUser?.email
            alarmDTO.uid = FirebaseAuth.getInstance().currentUser?.uid
            alarmDTO.kind = 0
            alarmDTO.timestamp = System.currentTimeMillis()
            FirebaseFirestore.getInstance().collection("alarms").document().set(alarmDTO)
        }
    }

}
