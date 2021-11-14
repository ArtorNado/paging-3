package com.example.paging3sample.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.paging3sample.api.BookService
import com.example.paging3sample.model.BookModel
import com.example.paging3sample.data.paging.BookPagingSource
import kotlinx.coroutines.flow.Flow

/**
 * [Pager] - Занимается тем, что получает данные, подкачивает страницы и объединяет это все в
 * [PagingData] (Контейнер для данных, возвращаемых из [PagingSource] или [RemoteMediator])
 */
class BookRepository(
    private val bookService: BookService,
) {

    /**
     * [PagingConfig] - Устанавливает параметры касаемые того, как загружать данные.
     *
     * [pageSize] - желаемое кол-во загружаемых элементов.
     *
     * [prefetchDistance] - за сколько элементов до граници загруженного контента нужно запускать подгрузку.
     *
     * [enablePlaceholders] - могут ли показываться null для еще незагруженных элементов
     * они устроены таким образом, что в наш адаптер будут прилетать null элементы и мы
     * можем их обрабатывать.
     *
     * [initialLoadSize] - кол-во загружаемых элементов при первой загрузке. По умолчанию кол-во страниц * 3
     *
     * [maxSize] - максимальное кол-во элементов, которое может быть загружено в PagedList перед тем,
     * как страницы будут удаляться. По умолчанию установлен  MAX_SIZE_UNBOUNDED - страницы никогда не бдут сбрасываться
     *
     * [jumpThreshold] - кол-во элементов, которое нужно проскроллить от загруженных данных, чтобы остановить
     * загрузку следующей страницы и начать с текущей позици
     */

    fun booksFlow(): Flow<PagingData<BookModel>> {
        return Pager(
            config = PagingConfig(
                pageSize = PAGE_SIZE,
                enablePlaceholders = false,
            ),
            pagingSourceFactory = { BookPagingSource(bookService) }
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