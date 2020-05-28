package myway.myapplication

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.company.howl.howlstagram.model.ContentDTO
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_add_photo.*
import java.text.SimpleDateFormat
import java.util.*

class AddPhotoActivity : AppCompatActivity() {

    var PICK_IMAGE_FROM_ALBOM = 0
    var storage: FirebaseStorage? = null
    var photoUri: Uri? = null
    var firestore: FirebaseFirestore? = null
    private var auth: FirebaseAuth? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_photo)


        // Firebase storage
        storage = FirebaseStorage.getInstance()
        // Firebase Database
        firestore = FirebaseFirestore.getInstance()
        // Firebase Auth
        auth = FirebaseAuth.getInstance()

        //Open the album
        var photoPickerIntent = Intent(Intent.ACTION_PICK)
        photoPickerIntent.type = "image/*"
        startActivityForResult(photoPickerIntent, PICK_IMAGE_FROM_ALBOM)


        //add image upload event
        addphoto_btn_upload.setOnClickListener {
            contentUpload()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_FROM_ALBOM) {
            if (resultCode == Activity.RESULT_OK) {
                //This is path to the selected image
                photoUri = data?.data
                addphoto_image.setImageURI(photoUri)

            } else {
                //Exit the addPhotoActivity if you leave the album without selecting it
                finish()
            }
        }
    }

    private fun contentUpload() {
//Make file name
        var timestamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        var imageFileName = "IMAGE" + timestamp + "_.png"

        var storageRef = storage?.reference?.child("images")?.child(imageFileName)

        //FileUpload  Callback method / Promise method
        storageRef?.putFile(photoUri!!)?.addOnSuccessListener {
            storageRef.downloadUrl.addOnSuccessListener { uri ->
                var contentDTO = ContentDTO()

                //Insert download Uir of image
                contentDTO = ContentDTO()
                //이Insert uid of user미지 주소
                contentDTO.imageUrl = uri!!.toString()
                //insert uid of user
                contentDTO.uid = auth?.currentUser?.uid
                //insert explain of content
                contentDTO.explain = addphoto_edit_explain.text.toString()
                //Insert userid
                contentDTO.userId = auth?.currentUser?.email
                //Insert itmstamp
                contentDTO.timestamp = System.currentTimeMillis()

                //
                firestore?.collection("images")?.document()?.set(contentDTO)

                setResult(Activity.RESULT_OK)
                finish()
            }

          /*  storageRef?.putFile(photoUri!!)?.addOnSuccessListener {
                storageRef.downloadUrl.addOnSuccessListener { uri ->
                    var contentDTO = ContentDTO()

                    //Insert download Uir of image
                    contentDTO = ContentDTO()
                    //이Insert uid of user미지 주소
                    contentDTO.imageUrl = uri!!.toString()
                    //insert uid of user
                    contentDTO.uid = auth?.currentUser?.uid
                    //insert explain of content
                    contentDTO.explain = addphoto_edit_explain.text.toString()
                    //Insert userid
                    contentDTO.userId = auth?.currentUser?.email
                    //Insert itmstamp
                    contentDTO.timestamp = System.currentTimeMillis()

                    //
                    firestore?.collection("images")?.document()?.set(contentDTO)

                    setResult(Activity.RESULT_OK)
                    finish()*/
        }
            ?.addOnFailureListener {
                progress_bar.visibility = View.GONE

                Toast.makeText(
                    this, getString(R.string.upload_fail),
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

}

