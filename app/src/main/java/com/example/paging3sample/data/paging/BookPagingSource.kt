package com.example.paging3sample.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.paging3sample.api.BookService
import com.example.paging3sample.data.mapper.mapBookResponseToBookModel
import com.example.paging3sample.model.BookModel
import java.lang.Exception

/**
 * [PagingSource] - отвечает за подгрузку данных из сети.
 * Основная суть - организовать запрос данных для текущей страницы и получить их.
 */
// наследуемся от PagingSource и указываем тип для ключа (Int) и для значения (BookModel)
class BookPagingSource(
    private val service: BookService
) : PagingSource<Int, BookModel>() {

    /**
     * [load] асинхронно вызывается, когда нам нужно подгрузить больше данных.
     *
     * [LoadParams] - содержит информацию о загрузке.
     * [LoadParams.key] - ключ загружаемой страницы. LoadParams.loadSize - размер страницы.
     *
     * [LoadResult] - результат загрузки.
     * [LoadResult.Page] - успешный результат загрузки
     * [LoadResult.Error] - произошла ошибка при загрузке
     */

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, BookModel> {
        val page = params.key ?: 1
        val pageSize = params.loadSize

        return try {
            val books =
                service.getBooksPager(page, pageSize)
                    .map { mapBookResponseToBookModel(it) }

            val nextKey = if (books.size < pageSize) null else page.plus(1)
            val prevKey = if (page == 1) null else page.minus(1)

            LoadResult.Page(
                data = books,
                prevKey = prevKey,
                nextKey = nextKey,
            )
        } catch (exception: Exception) {
            LoadResult.Error(exception)
        }
    }

    /**
     * [getRefreshKey] нужен для получения ключа, который позволит нам загрузить текущие данные при обновлении списка
     * Т.е. используется, когда Paging библиотека хочет подгрузить новые данные для замены текущего
     * списка при свайп рефреше, изменении конфигурации итд
     *
     * [PagingState] - содержит информацию о последней просматриваемой позиции, предыдущих
     * загруженных страницах и настройках конфигурации
     *
     * [anchorPosition] - последняя позиция, которая была видна пользователю
     *
     * [closestPageToPosition] - ближайшая страница, которая была загружена к переданной [anchorPosition]
     */
    override fun getRefreshKey(state: PagingState<Int, BookModel>): Int? {
        val anchorPosition = state.anchorPosition ?: return null
        val page = state.closestPageToPosition(anchorPosition) ?: return null

        return page.prevKey?.plus(1) ?: page.nextKey?.minus(1)
    }
}