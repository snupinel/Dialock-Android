package com.example.dailysummary.data

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.paging.PagingSource
import androidx.paging.PagingState
import coil.network.HttpException
import com.example.dailysummary.model.CalenderOnePage
import com.example.dailysummary.model.summaryRefinement
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.firstOrNull
import java.io.IOException
import java.time.LocalDate
import javax.inject.Inject

data class YearMonth(
    val year:Int,
    val month:Int
){
    constructor(pageNum:Int) : this(pageNum/12+1970,pageNum%12+1)
    @RequiresApi(Build.VERSION_CODES.O)
    constructor() : this(LocalDate.now().year,LocalDate.now().monthValue)

    fun toPageNum():Int{
        return (year-1970)*12 + month -1
    }

    fun prevMonth():YearMonth{
        return YearMonth(this.toPageNum()-1)
    }

    fun nextMonth():YearMonth{
        return YearMonth(this.toPageNum()+1)
    }
}
@RequiresApi(Build.VERSION_CODES.O)
class CalenderPagingSource @AssistedInject constructor(
    private val summaryRepository: SummaryRepository,
    @Assisted private val yearMonth:YearMonth = YearMonth(),
):PagingSource<YearMonth,CalenderOnePage>() {
    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun load(params: LoadParams<YearMonth>): LoadResult<YearMonth, CalenderOnePage> {
        Log.d("aaaa","load act")
        // Retrofit calls that return the body type throw either IOException for network
        // failures, or HttpException for any non-2xx HTTP status codes. This code reports all
        // errors to the UI, but you can inspect/wrap the exceptions to provide more context.
        return try {
            // Key may be null during a refresh, if no explicit key is passed into Pager
            // construction. Use 0 as default, because our API is indexed started at index 0
            val pageNum = params.key ?: yearMonth
            // Suspending network load via Retrofit. This doesn't need to be wrapped in a
            // withContext(Dispatcher.IO) { ... } block since Retrofit's Coroutine
            // CallAdapter dispatches on a worker thread.
            val summaries = summaryRepository.getSummariesByMonth("%04d-%02d".format(pageNum.year, pageNum.month)).firstOrNull()?: emptyList()
            Log.d("aaaa",summaries.toString())
            val calenderOnePage =summaryRefinement(pageNum.year,pageNum.month,summaries)

            // Since 0 is the lowest page number, return null to signify no more pages should
            // be loaded before it.
            val prevKey = if (pageNum.toPageNum() > 0) pageNum.prevMonth() else null

            // This API defines that it's out of data when a page returns empty. When out of
            // data, we return `null` to signify no more pages should be loaded
            val nextKey = if (pageNum.toPageNum() < 1199) pageNum.nextMonth() else null
            LoadResult.Page(
                data = listOf(calenderOnePage),
                prevKey = prevKey,
                nextKey = nextKey
            )
        } catch (e: IOException) {
            LoadResult.Error(e)
        } catch (e: HttpException) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<YearMonth, CalenderOnePage>): YearMonth? {
        return state.anchorPosition?.let {
            state.closestPageToPosition(it)?.prevKey?.nextMonth()
                ?: state.closestPageToPosition(it)?.nextKey?.prevMonth()
        }
    }



}