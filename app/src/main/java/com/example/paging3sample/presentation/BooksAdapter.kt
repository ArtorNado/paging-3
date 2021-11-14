package com.example.paging3sample.presentation

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.paging3sample.databinding.ItemBookBinding
import com.example.paging3sample.model.BookModel

class BooksAdapter :
    PagingDataAdapter<BookModel, BooksAdapter.ItemBookViewHolder>(BookDiffUtilCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemBookViewHolder {
        val binding = ItemBookBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return ItemBookViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ItemBookViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ItemBookViewHolder(
        private val viewBinding: ItemBookBinding,
    ) : RecyclerView.ViewHolder(viewBinding.root) {

        fun bind(item: BookModel?) {
            viewBinding.bookName.text = item?.name
        }
    }
}

private object BookDiffUtilCallback : DiffUtil.ItemCallback<BookModel>() {
    override fun areItemsTheSame(oldItem: BookModel, newItem: BookModel): Boolean =
        oldItem.name == newItem.name

    override fun areContentsTheSame(oldItem: BookModel, newItem: BookModel): Boolean =
        oldItem == newItem
}