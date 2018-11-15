package sg.prelens.jinny.repositories.inmemory.bypage

import android.arch.paging.DataSource

/**
 * Created by tommy on 3/15/18.
 */
class ListDataSourceFactory<T>(val list: List<T>):DataSource.Factory<Int, T>{
    override fun create(): DataSource<Int, T> = ListDataSource<T>(list)

}