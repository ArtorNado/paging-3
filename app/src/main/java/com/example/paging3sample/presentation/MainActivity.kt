package com.example.paging3sample.presentation

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.paging3sample.Injection
import com.example.paging3sample.databinding.ActivityMainBinding
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private var _viewBinding: ActivityMainBinding? = null
    private val viewBinding get() = _viewBinding!!

    private lateinit var viewModel: BooksListViewModel

    private val booksAdapter = BooksAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _viewBinding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(viewBinding.root)

        viewModel = ViewModelProvider(this, Injection.provideViewModelFactory())
            .get(BooksListViewModel::class.java)

        initViews()
        observe()
    }

    private fun initViews() {
        initRecyclerView()
        initClickListeners()
    }

    private fun observe() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.getBooks()
                    .collectLatest(booksAdapter::submitData)
            }
        }
    }

    /**
     * [PagingDataAdapter.refresh] нужен для обновления данных.
     * Для PagingSource запускает создание нового [PagingData] с новым экземпляром [PagingSource].
     * Если используется [RemoteMediator], вызывает [RemoteMediator.load] с [LoadType.REFRESH]
     */
    private fun initClickListeners() {
        viewBinding.refreshButton.setOnClickListener {
            booksAdapter.refresh()
        }
    }

    /**
     * [PagingDataAdapter.withLoadStateHeader] - если мы хотим отображать состояние загрузки только в верхнем колонтитуле
     *
     * [PagingDataAdapter.withLoadStateFooter] - если мы хотим отображать состояние загрузки только в нижнем колонтитуле
     *
     * [PagingDataAdapter.withLoadStateHeaderAndFooter] - если мы хотим отобразить состояние загрузки вверху и внизу
     *
     * [PagingDataAdapter.retry] - повторяет неудачный запрос загрузки данных.
     * Для PagingSource, в отличии от [refresh], не создает новый [PagingSource],
     * а просто повторяет запрос для того же [PagingData]
     */
    private fun initRecyclerView() {
        viewBinding.booksRecyclerView.adapter = booksAdapter
//        viewBinding.booksRecyclerView.adapter = booksAdapter.withLoadStateHeaderAndFooter(
//            header = BooksStateAdapter(booksAdapter::retry),
//            footer = BooksStateAdapter(booksAdapter::retry)
//        )
    }
}