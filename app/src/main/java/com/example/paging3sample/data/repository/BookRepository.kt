package com.example.paging3sample.data.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.example.paging3sample.api.BookService
import com.example.paging3sample.data.paging.BookRemoteMediator
import com.example.paging3sample.dp.AppDatabase
import com.example.paging3sample.dp.BookLocal
import com.example.paging3sample.model.BookModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * [Pager] - Занимается тем, что получает данные, подкачивает страницы и объединяет это все в
 * [PagingData] (Контейнер для данных, возвращаемых из [PagingSource] или [RemoteMediator])
 */
class BookRepository(
    private val bookService: BookService,
    private val appDatabase: AppDatabase
) {

    /**
     * [PagingConfig] - Устанавливает параметры касаемые того, как загружать данные.
     *
     * [pageSize] - желаемое кол-во загружаемых элементов.
     *
     * [prefetchDistance] - за сколько элементов до границы загруженного контента нужно запускать подгрузку.
     *
     * [enablePlaceholders] - могут ли показываться null для еще незагруженных элементов
     * они устроены таким образом, что в наш адаптер будут прилетать null элементы и мы
     * можем их обрабатывать.
     *
     * [initialLoadSize] - кол-во загружаемых элементов при первой загрузке. По умолчанию размер страницы * 3
     *
     * [maxSize] - максимальное кол-во элементов, которое может быть загружено в PagedList перед тем,
     * как страницы будут удаляться. По умолчанию установлен  MAX_SIZE_UNBOUNDED - страницы никогда не бдут сбрасываться
     *
     * [jumpThreshold] - кол-во элементов, которое нужно проскроллить от загруженных данных, чтобы остановить
     * загрузку следующей страницы и начать с текущей позици
     */

    @OptIn(ExperimentalPagingApi::class)
    fun booksFlow(): Flow<PagingData<BookLocal>> {
        return Pager(
            config = PagingConfig(pageSize = PAGE_SIZE, enablePlaceholders = false),
            remoteMediator = BookRemoteMediator(
                bookService,
                appDatabase,
            ),
            pagingSourceFactory = { appDatabase.bookDao().booksPaging() }
        ).flow
    }

    /**
     * [Pager.flow] - Flow.
     * [Pager.liveData] - livedata.
     * [Pager.flowable] - RxJava Flowable, [Pager.observable] - Rxjava observable
     */

    companion object {
        const val PAGE_SIZE = 50
    }
}