package com.example.messanger

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import androidx.appcompat.app.AppCompatActivity
import com.example.messanger.data.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.activity_sign_up.*
import java.util.*


class SignUpActivity : AppCompatActivity() {
    private var handler = false

    private lateinit var bitmap: Bitmap
    private val PICK_ImageRequest = 120


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)




        progressbar_sign_up.visibility = View.GONE

        signUpBtn.setOnClickListener {
            if (handler) {
                performRegister()
            } else {
                Toast.makeText(this, "Пожалуйста выберите какую нибудь фотографию", LENGTH_SHORT)
                    .show()
            }


        }


        next_page_signIp.setOnClickListener {
            finish()
        }

        select_photo_btn.setOnClickListener {
            if (!handler){
                val intent = Intent(Intent.ACTION_PICK)
                intent.type = "image/*"

                startActivityForResult(intent, PICK_ImageRequest)
            }

        }

    }

    private var selectPhotoUri: Uri? = null

    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_ImageRequest && resultCode == Activity.RESULT_OK && data != null) {
            Log.d("RegisterActivity", "photo was selected: ")
            selectPhotoUri = data.data!!





            bitmap = MediaStore.Images.Media.getBitmap(contentResolver, selectPhotoUri)

            selectphoto_image_btn
                .setImageBitmap(bitmap)

            handler = true


        }

    }


    //Sign Un in firebase
    private fun performRegister() {
        val email = email_ln.text.toString()
        val userName = username_ln.text.toString()
        val password = passwordln_signUp.text.toString()

        if (email.isEmpty() || userName.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Пожалуйста заполните поля", LENGTH_SHORT).show()
            return
        }

        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener {

                if (!it.isSuccessful) return@addOnCompleteListener

                Log.d("RegisterActivity", "onCreate: ${it.result.user!!.uid}")

                editTextBlock(email_ln)
                editTextBlock(username_ln)
                editTextBlock(passwordln_signUp)

                progressbar_sign_up.visibility = View.VISIBLE


                uploadImageToFirebaseStorage()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to create user: ${it.message}", Toast.LENGTH_SHORT)
                    .show()
            }
    }

    private fun uploadImageToFirebaseStorage() {
        if (selectPhotoUri == null) return

        val filename = UUID.randomUUID().toString()
        val ref: StorageReference = FirebaseStorage.getInstance().getReference("/images/$filename")




        ref.putFile(selectPhotoUri!!)
            .addOnSuccessListener { it1 ->
                Log.e(
                    "RegisterActivity",
                    "uploadImageToFirebaseStorage: image storage ${it1.metadata?.path}"
                )


                ref.downloadUrl.addOnSuccessListener { imgIt ->
                    Log.e("RegisterActivity", "uploadImageToFirebaseStorage: File location: $imgIt")

                    progressbar_sign_up.visibility = View.GONE


                    saveUserToFirebaseDatabase(imgIt.toString())
                }
            }
            .addOnFailureListener {

            }
    }

    private fun saveUserToFirebaseDatabase(profileImgUrl: String) {
        val password = passwordln_signUp.text.toString()

        Log.e("RegisterActivity", "saveUserToFirebaseDatabase: $profileImgUrl")
        val uid = FirebaseAuth.getInstance().uid ?: ""

        val user =
            User(uid, username_ln.text.toString(), profileImgUrl, password)


        user.let {
            FirebaseDatabase.getInstance().reference
                .child("/users/${it.uid}").setValue(user)


        }

        val intent = Intent(this, UserActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)


    }


}

private fun editTextBlock(editText: EditText) {
    editText.isFocusable = false;
    editText.isEnabled = false;
    editText.isCursorVisible = false;
}



