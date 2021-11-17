package com.example.paging3sample.data.paging

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.example.paging3sample.api.BookService
import com.example.paging3sample.data.mapper.mapBookResponseToBookLocal
import com.example.paging3sample.dp.AppDatabase
import com.example.paging3sample.dp.BookLocal
import com.example.paging3sample.dp.BookRemoteKeysLocal
import com.example.paging3sample.model.BookResponse

@ExperimentalPagingApi
class BookRemoteMediator(
    private val service: BookService,
    private val appDatabase: AppDatabase
) : RemoteMediator<Int, BookLocal>() {

    /**
     * [LAUNCH_INITIAL_REFRESH] - При инициализации нужно сразу запросить новые данные из сети ([REFRESH]).
     * [SKIP_INITIAL_REFRESH] - При инициализации не нужно запрашивать новые данные из сети,
     * а вовзращаем закэшированные. След обновление будет только после того, как пользователь сам
     * его запросит ([REFRESH], [APPEND], [PREPEND])
     */
    override suspend fun initialize(): InitializeAction {
        return InitializeAction.LAUNCH_INITIAL_REFRESH
    }

    /**
     * [load] вызывается, когда нам нужно подгрузить больше данных из сети
     *
     * [LoadType] - говорит нам, куда нужно добавить данные на странице.
     * [LoadType.APPEND] - загрузить новые данные в конце страницы.
     * [LoadType.PREPEND] - загрузить новые данные в начале страницы.
     * [LoadType.REFRESH] - Означает первую загрузку данных или вызов [PagingDataAdapter.refresh]
     *
     * [MediatorResult] может быть двух типов:
     * [MediatorResult.Error] - ошибка при запросе данных из сети.
     * [MediatorResult.Success] - Успешно получили данные из сети. Должны указать, достигли ли конца списка
     */

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, BookLocal>
    ): MediatorResult {
        val page = when (loadType) {
            LoadType.REFRESH -> {
                // Если это первая загрузка, то anchorPosition == null.
                // Когда вызывается PagingDataAdapter.refresh(), anchorPosition является первой
                // видимой позицией в списке, поэтому нам нужно загрузить страницу содержащую ее
                val remoteKeys = getRemoteKeyClosestToCurrentPosition(state)
                remoteKeys?.nextKey?.minus(1) ?: INITIAL_PAGE_NUMBER
            }
            LoadType.APPEND -> {
                val remoteKeys = getRemoteKeyForLastItem(state)
                // Если remoteKeys == null, то результата REFRESH еще нет в базе данных.
                // В таком случае мы может вернуть `endOfPaginationReached = false` чтобы Paging
                // снова вызвал этот метод. Иначе, если у нас remoteKeys != null, но nextKey == null,
                // то следующей страницы нет и возвращаем endOfPaginationReached = true
                val nextKey = remoteKeys?.nextKey
                if (nextKey == null) {
                    return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
                }

                nextKey
            }
            LoadType.PREPEND -> {
                val remoteKeys = getRemoteKeyForFirstItem(state)
                val prevKey = remoteKeys?.prevKey

                if (prevKey == null) {
                    return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
                }

                prevKey
            }
        }

        return try {
            val response = service.getBooksPager(page, state.config.pageSize)
            // достигли конца пагинации, если список пустой
            val endOfPaginationReached = response.isEmpty()

            appDatabase.withTransaction {
                // если LoadType Refresh - очищаем данные
                if (loadType == LoadType.REFRESH) {
                    deleteBookKeysCache()
                    clearBooksCache()
                }

                val prevKey = if (page == INITIAL_PAGE_NUMBER) null else page - 1
                val nextKey = if (endOfPaginationReached) null else page + 1
                val keys = response.map {
                    BookRemoteKeysLocal(bookName = it.name, prevKey = prevKey, nextKey = nextKey)
                }

                // сохраняем данные
                saveBookKeysCache(keys)
                saveBooksCache(response, page)
            }
            MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
        } catch (exception: Exception) {
            MediatorResult.Error(exception)
        }
    }

    private suspend fun getRemoteKeyForLastItem(state: PagingState<Int, BookLocal>): BookRemoteKeysLocal? {
        // state.pages.lastOrNull - получаем последнюю страницу, которая содержит элементы
        // из нее получаем последний элемент
        return state.pages.lastOrNull { it.data.isNotEmpty() }?.data?.lastOrNull()
            ?.let { bookLocal ->
                // Получаем remote keys элемента
                appDatabase.bookRemoteKeysDao().remoteKeysBooks(bookLocal.name)
            }
    }

    private suspend fun getRemoteKeyForFirstItem(state: PagingState<Int, BookLocal>): BookRemoteKeysLocal? {
        // state.pages.firstOrNull - получаем первую страницу, которая содержит элементы
        // из нее получаем первый элемент
        return state.pages.firstOrNull { it.data.isNotEmpty() }?.data?.firstOrNull()
            ?.let { bookLocal ->
                // Получаем remote keys элемента
                appDatabase.bookRemoteKeysDao().remoteKeysBooks(bookLocal.name)
            }
    }

    private suspend fun getRemoteKeyClosestToCurrentPosition(
        state: PagingState<Int, BookLocal>
    ): BookRemoteKeysLocal? {
        // Пытаемся загрузить данные после anchor position по элементу, который последний на странице,
        // где находится anchor position
        return state.anchorPosition?.let { position ->
            state.closestItemToPosition(position)?.name?.let { bookName ->
                appDatabase.bookRemoteKeysDao().remoteKeysBooks(bookName)
            }
        }
    }

    private suspend fun clearBooksCache() {
        appDatabase.bookDao().clearBooks()
    }

    private suspend fun deleteBookKeysCache() {
        appDatabase.bookRemoteKeysDao().clearRemoteKeys()
    }

    private suspend fun saveBooksCache(books: List<BookResponse>, pageNumber: Int) {
        appDatabase.bookDao()
            .insertAll(books.map { mapBookResponseToBookLocal(it, pageNumber) })
    }

    private suspend fun saveBookKeysCache(keys: List<BookRemoteKeysLocal>) {
        appDatabase.bookRemoteKeysDao().insertAll(keys)
    }

    private companion object {

        private const val INITIAL_PAGE_NUMBER = 1
    }
}