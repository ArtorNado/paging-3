package com.example.paging3sample.presentation

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.paging.LoadState
import com.example.paging3.observe
import com.example.paging3sample.Injection
import com.example.paging3sample.databinding.ActivityMainBinding
import com.google.android.material.snackbar.Snackbar
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

        viewModel = ViewModelProvider(this, Injection.provideViewModelFactory(this))
            .get(BooksListViewModel::class.java)

        initViews()
        observe()
    }

    private fun initViews() {
        initRecyclerView()
        initClickListeners()
//        initLoadStateObserver()
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
        viewBinding.booksRecyclerView.adapter = booksAdapter.withLoadStateHeaderAndFooter(
            header = BooksStateAdapter(booksAdapter::retry),
            footer = BooksStateAdapter(booksAdapter::retry)
        )
    }

    /**
     * [loadStateFlow] - flow, который излучает [CombinedLoadStates] каждый раз, когда происходит
     * изменение состояния загрузки PagingData
     *
     * [CombinedLoadStates] - коллекция [LoadState] для [PagingSource] и [RemoteMediator].
     * Содержит [LoadState] для [LoadTypes] (REFRESH, APPEND, PREPEND)
     * [CombinedLoadStates.mediator] - возвращает [LoadState] для [RemoteMediator],
     * [CombinedLoadStates.source] для [PagingSource] в случае с медиатором вернет null, если его нет
     */

    private fun initLoadStateObserver() {
        booksAdapter.loadStateFlow.observe(this) { combinedLoadStates ->
            with(viewBinding) {
                // если у медиатора REFRESH - NotLoading и и авдаптере нет элементов, то список пустой
                val isAdapterEmpty = booksAdapter.itemCount == 0
                val refreshState = combinedLoadStates.mediator?.refresh

                // Если REFRESH находится в состоянии загрузки, и данных нет, то это первая загрузка
                // и выводим progress на весь экран, иначе, если данные есть, выводим linear p.i.
                progressBar.isVisible = refreshState is LoadState.Loading && isAdapterEmpty
                linearProgressIndicator.isVisible =
                    refreshState is LoadState.Loading && !isAdapterEmpty

                val errorState = refreshState as? LoadState.Error
                    ?: refreshState as? LoadState.Error
                    ?: refreshState as? LoadState.Error

                errorState?.let {
                    Snackbar.make(
                        root,
                        it.error.localizedMessage ?: "",
                        Snackbar.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }
}