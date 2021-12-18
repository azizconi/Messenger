package com.example.messanger.user.chatLog

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.messanger.R
import com.example.messanger.UserActivity
import com.example.messanger.data.ChatMessage
import com.example.messanger.data.User
import com.example.messanger.user.NewMessageActivity
import com.example.messanger.user.adapter.ChatFromItem
import com.example.messanger.user.adapter.ChatToItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import kotlinx.android.synthetic.main.activity_chat_log.*

class ChatLogActivity : AppCompatActivity() {

    private val adapter = GroupAdapter<GroupieViewHolder>()

    private var toUser: User? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_log)




        recyclerview_chat_log.adapter = adapter



        toUser = intent.getParcelableExtra<User>(NewMessageActivity.USER_KEY)
        supportActionBar?.title = toUser?.username




        listenForMessages()

        send_message_chat_log.setOnClickListener {
            if (edittext_chat_log.text.toString() != ""){
                performSendMessage()
            }


        }

    }

    private fun listenForMessages() {
        val fromId = FirebaseAuth.getInstance().uid
        val toId = toUser?.uid

        val ref = FirebaseDatabase.getInstance().reference.child("/user-messages/$fromId/$toId")




        ref.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val chatMessage = snapshot.getValue(ChatMessage::class.java)
                if (chatMessage != null) {

                    if (chatMessage.fromId == FirebaseAuth.getInstance().uid) {

                        val currentUser = UserActivity.currentUser ?: return
                        adapter.add(ChatToItem(chatMessage.text, currentUser))


                    } else {
                        adapter.add(ChatFromItem(chatMessage.text, toUser!!))
                    }
                }

                recyclerview_chat_log.scrollToPosition(adapter.itemCount - 1)

            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                TODO("Not yet implemented")
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                TODO("Not yet implemented")
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                TODO("Not yet implemented")
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    private fun performSendMessage() {

        val text = edittext_chat_log.text.toString()

        val fromId = FirebaseAuth.getInstance().uid
        val user = intent.getParcelableExtra<User>(NewMessageActivity.USER_KEY)
        val toId = user!!.uid


        if (fromId == null) return
//        val reference = FirebaseDatabase.getInstance().getReference("messages").push()
        val reference =
            FirebaseDatabase.getInstance().getReference("/user-messages/$fromId/$toId").push()

        val toReference =
            FirebaseDatabase.getInstance().getReference("/user-messages/$toId/$fromId")



        val chatMessage =
            ChatMessage(reference.key!!, text, fromId, toId, System.currentTimeMillis() / 1000)


        chatMessage.let {
            FirebaseDatabase.getInstance().reference
                .child("/user-messages/$fromId/$toId").push().setValue(chatMessage)

            Log.d("Tag", "Saved our chat: ${reference.key}")

            edittext_chat_log.text.clear()
            recyclerview_chat_log.scrollToPosition(adapter.itemCount - 1)
        }

        toReference.setValue(chatMessage)

        val latestMessageRef =
            FirebaseDatabase.getInstance().getReference("/latest-messages/$fromId/$toId")
        latestMessageRef.setValue(chatMessage)

        val latestMessageToRef =
            FirebaseDatabase.getInstance().getReference("/latest-messages/$toId/$fromId")
        latestMessageRef.setValue(chatMessage)

    }


}