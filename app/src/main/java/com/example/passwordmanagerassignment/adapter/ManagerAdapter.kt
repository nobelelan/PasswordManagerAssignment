package com.example.passwordmanagerassignment.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.View.OnLongClickListener
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.DiffUtil.DiffResult
import androidx.recyclerview.widget.RecyclerView
import com.example.passwordmanagerassignment.databinding.PasswordItemBinding
import com.example.passwordmanagerassignment.model.Password

class ManagerAdapter: RecyclerView.Adapter<ManagerAdapter.ManagerViewHolder>() {

    inner class ManagerViewHolder(val binding: PasswordItemBinding): RecyclerView.ViewHolder(binding.root)

    private val differCallBack = object : DiffUtil.ItemCallback<Password>(){
        override fun areItemsTheSame(oldItem: Password, newItem: Password): Boolean {
            return oldItem.website ==  newItem.website
        }

        override fun areContentsTheSame(oldItem: Password, newItem: Password): Boolean {
            return oldItem ==  newItem
        }
    }

    val differ = AsyncListDiffer(this, differCallBack)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ManagerViewHolder {
        return ManagerViewHolder(PasswordItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ManagerViewHolder, position: Int) {
        val password = differ.currentList[position]
        holder.binding.apply {
            txtWebsite.text = password.website
            txtPassword.text = password.password
        }
        holder.itemView.setOnClickListener {
            onItemClickListener?.let { it(password) }
        }
        holder.itemView.setOnLongClickListener{
            onLongClickListener?.let { it(password) }
            true
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    private var onItemClickListener: ((Password) -> Unit) ?= null
    private var onLongClickListener: ((Password) -> Unit) ?= null

    fun setOnItemClickListener(listener: (Password) -> Unit) {
        onItemClickListener = listener
    }
    fun setOnLongClickListener(listener: (Password) -> Unit) {
        onLongClickListener = listener
    }
}