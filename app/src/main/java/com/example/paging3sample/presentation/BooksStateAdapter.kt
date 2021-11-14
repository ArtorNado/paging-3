package com.example.paging3sample.presentation

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.paging3sample.databinding.ItemLoadStateBinding

/**
 * [LoadStateAdapter] - адаптер, который получает уведомления об изменении состоянии загрузки.
 * Реализация [LoadStateAdapter] ничем не отличается от обычного ааптера кроме того, что имеет перегрузку с [LoadState]
 *
 * [LoadState] - sealed класс, который имеет 3 реализации NotLoading, Loading, Error
 */
class BooksStateAdapter(
    private val retry: () -> Unit,
) : LoadStateAdapter<BooksStateAdapter.ProgressViewHolder>() {

    override fun onBindViewHolder(holder: ProgressViewHolder, loadState: LoadState) {
        holder.bind(loadState)
    }

    override fun onCreateViewHolder(parent: ViewGroup, loadState: LoadState): ProgressViewHolder {
        return ProgressViewHolder(
            ItemLoadStateBinding.inflate(LayoutInflater.from(parent.context), parent, false),
            retry
        )
    }

    class ProgressViewHolder(
        private val viewBinding: ItemLoadStateBinding,
        private val retry: () -> Unit
    ) : RecyclerView.ViewHolder(viewBinding.root) {

        fun bind(loadState: LoadState) {
            with(viewBinding) {
                progressBar.isVisible = loadState is LoadState.Loading
                retryButton.isVisible = loadState is LoadState.Error
                errorTextView.isVisible = loadState is LoadState.Error

                if (loadState is LoadState.Error) {
                    errorTextView.text = loadState.error.localizedMessage
                }

                retryButton.setOnClickListener { retry() }
            }
        }
    }
}