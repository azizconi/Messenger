package com.example.messanger.user


import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.messanger.R
import com.example.messanger.data.User
import com.example.messanger.user.adapter.UserItem
import com.example.messanger.user.chatLog.ChatLogActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import kotlinx.android.synthetic.main.activity_new_message.*


class NewMessageActivity : AppCompatActivity() {




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_message)



        supportActionBar?.title = "Выберите пользователя"


        fetchUsers()

    }

    companion object {
        const val USER_KEY = "USER_KEY"
    }


    private fun fetchUsers() {
        val ref = FirebaseDatabase.getInstance().getReference("/users/")

        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val adapter = GroupAdapter<GroupieViewHolder>()


                snapshot.children.forEach {
                    Log.d("NewMessage", it.toString())
                    val user = it.getValue(User::class.java)
//                    /////////


                    if (user != null) {

                        if (user.uid != FirebaseAuth.getInstance().uid) {
                            adapter.add(UserItem(user = user))


                        }

                    }

                }

                adapter.setOnItemClickListener { item, view ->

                    val userItem = item as UserItem

                    val intent = Intent(view.context, ChatLogActivity::class.java)

                    intent.putExtra(USER_KEY, userItem.user)
                    startActivity(intent)
                    finish()
                }

                recyclerview_new_messenger.adapter = adapter
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }
}