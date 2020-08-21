package com.ricki.test.Adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ricki.test.Model.UserItemModel
import com.ricki.test.R
import com.ricki.test.databinding.ItemListviewUserBinding
import kotlin.collections.ArrayList

class UserAdapter(var listOfUser: ArrayList<UserItemModel>) : RecyclerView.Adapter<UserAdapter.UserViewHolder>() {

    class UserViewHolder(val itemLayoutArticlesBinding: ItemListviewUserBinding) :
        RecyclerView.ViewHolder(itemLayoutArticlesBinding.root)

    fun updateData(_listOfUser: ArrayList<UserItemModel>, isClear: Boolean) {
        if(isClear){
            listOfUser.clear()
        }
        listOfUser.addAll(_listOfUser)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = UserViewHolder(
        DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.item_listview_user,
            parent,
            false
        )
    )

    override fun getItemCount(): Int {
        return listOfUser.size
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        var item = listOfUser[position]
        holder.itemLayoutArticlesBinding.textViewName.text = item.login
        Glide
            .with(holder.itemView.context)
            .load(item.avatar_url)
            .centerCrop()
            .into(holder.itemLayoutArticlesBinding.imageViewUser)
    }
}