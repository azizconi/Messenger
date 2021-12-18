package com.example.messanger.user.adapter

import com.example.messanger.R
import com.example.messanger.data.User
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.user_row_new_message.view.*

class UserItem(val user: User) : Item<GroupieViewHolder>() {
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.username_textview_new_message.text = user.username

        Picasso.get().load(user.profileImgUrl).into(viewHolder.itemView.imageView_new_message)


    }

    override fun getLayout(): Int {
        return R.layout.user_row_new_message
    }

}