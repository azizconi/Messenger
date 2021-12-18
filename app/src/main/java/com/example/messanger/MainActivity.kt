package com.example.messanger

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        main_activity_progressbar.visibility = View.GONE



        signInBtn.setOnClickListener {

            val userName = usernameln.text.toString()
            val password = passwordln.text.toString()


            if (userName.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Пожалуйста заполните поля", Toast.LENGTH_SHORT).show()

            } else {
                FirebaseAuth.getInstance().signInWithEmailAndPassword(userName, password)
                    .addOnSuccessListener {
                        Log.d("TAG", "showSignInDialog: kor kad")
                        val intent = Intent(this, UserActivity::class.java)
                        startActivity(intent)
                        main_activity_progressbar.visibility = View.GONE
                        finish()
                    }
                    .addOnFailureListener {
                        main_activity_progressbar.visibility = View.GONE
                        Toast.makeText(this, "Неправильный логин или пароль", Toast.LENGTH_SHORT)
                            .show()
                    }
            }


        }


        next_page_signUp.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }

    }


    override fun onStart() {
        if (FirebaseAuth.getInstance().currentUser != null) {
            val intent = Intent(this, UserActivity::class.java)
            startActivity(intent)
            finish()
        }

        super.onStart()
    }


}