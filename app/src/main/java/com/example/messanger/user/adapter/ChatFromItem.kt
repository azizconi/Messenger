package com.example.messanger.user.adapter

import com.example.messanger.R
import com.example.messanger.data.User
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.chat_from_row.view.*


class ChatFromItem(val text: String, val user: User): Item<GroupieViewHolder>() {
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.textView_chat_from_row.text = text


//        val uri = user.profileImgUrl
//        val targetImageView = viewHolder.itemView.imageView_chat_from_row
//        Picasso.get().load(uri).into(targetImageView)
    }

    override fun getLayout(): Int {
       return R.layout.chat_from_row
    }
}