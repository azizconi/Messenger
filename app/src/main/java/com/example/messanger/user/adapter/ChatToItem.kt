package com.example.messanger.user.adapter

import com.example.messanger.R
import com.example.messanger.data.User
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.chat_to_row.view.*


class ChatToItem(val text: String, val user: User): Item<GroupieViewHolder>() {
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.textView_chat_to_row.text = text

//        val uri = user.profileImgUrl
//        val targetImageView = viewHolder.itemView.imageView_chat_to_row
//        Picasso.get().load(uri).into(targetImageView)

    }

    override fun getLayout(): Int {
       return R.layout.chat_to_row
    }
}