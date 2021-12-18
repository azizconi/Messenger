package com.example.messanger

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import com.example.messanger.data.ChatMessage
import com.example.messanger.data.User
import com.example.messanger.user.NewMessageActivity
import com.example.messanger.user.adapter.LatestMessageRow
import com.example.messanger.user.chatLog.ChatLogActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import kotlinx.android.synthetic.main.activity_user.*

class UserActivity : AppCompatActivity() {


    companion object {
        var currentUser: User? = null
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user)




        recyclerview_latest_messages.adapter = adapter
        recyclerview_latest_messages.addItemDecoration(
            DividerItemDecoration(
                this,
                DividerItemDecoration.VERTICAL
            )
        )

        adapter.setOnItemClickListener{ item, view->
            val intent = Intent(this, ChatLogActivity::class.java)


            val row = item as LatestMessageRow

            intent.putExtra(NewMessageActivity.USER_KEY, row.chatPartnerUser)
            startActivity(intent)
        }


//        setupMessagesRow()
        listenForLatestMessages()

        fetchCurrentUser()


    }

    val latestMessageMap = HashMap<String, ChatMessage>()


    private fun refreshRecyclerViewMessages() {
        adapter.clear()
        latestMessageMap.values.forEach {
            adapter.add(LatestMessageRow(it))
        }
    }

    private fun listenForLatestMessages() {
        val fromId = FirebaseAuth.getInstance().uid

        val ref = FirebaseDatabase.getInstance().getReference("/latest-messages/$fromId")
        ref.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {

                val chatMessage = snapshot.getValue(ChatMessage::class.java) ?: return
                latestMessageMap[snapshot.key!!] = chatMessage
                refreshRecyclerViewMessages()




                Log.e("TAG", "onChildAdded: кор кад $adapter")

            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                val chatMessage = snapshot.getValue(ChatMessage::class.java) ?: return
                latestMessageMap[snapshot.key!!] = chatMessage
                refreshRecyclerViewMessages()


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


    override fun onStart() {
        if (FirebaseAuth.getInstance().currentUser == null) {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        super.onStart()
    }


    val adapter = GroupAdapter<GroupieViewHolder>()


    private fun fetchCurrentUser() {
        val uid = FirebaseAuth.getInstance().uid
        val ref = FirebaseDatabase.getInstance().reference


        ref.child("/users/$uid").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                currentUser = snapshot.getValue(User::class.java)
                Log.e("TAG", "onDataChange: ${currentUser?.username}")

            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_new_message -> {
                val intent = Intent(this, NewMessageActivity::class.java)
                startActivity(intent)
            }

            R.id.menu_sign_out -> {
                FirebaseAuth.getInstance().signOut()
                val intent = Intent(this, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }

        }

        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.nav_menu, menu)

        return super.onCreateOptionsMenu(menu)
    }
}