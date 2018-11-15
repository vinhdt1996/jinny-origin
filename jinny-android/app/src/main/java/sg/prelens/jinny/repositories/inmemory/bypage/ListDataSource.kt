/*
 * Copyright (C) 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package sg.prelens.jinny.repositories.inmemory.bypage

import android.arch.paging.PositionalDataSource
import java.util.ArrayList

class ListDataSource<T>(list: List<T>) : PositionalDataSource<T>() {
    private val mList: List<T>

    init {
        mList = ArrayList(list)
    }

    override fun loadInitial(params: LoadInitialParams,
                             callback: LoadInitialCallback<T>) {
        val totalCount = mList.size

        val position = computeInitialLoadPosition(params, totalCount)
        val loadSize = computeInitialLoadSize(params, position, totalCount)

        // for simplicity, we could return everything immediately,
        // but we tile here since it's expected behavior
        val sublist = mList.subList(position, position + loadSize)
        callback.onResult(sublist, position, totalCount)
    }

    override fun loadRange(params: LoadRangeParams,
                           callback: LoadRangeCallback<T>) {
        callback.onResult(mList.subList(params.startPosition,
                params.startPosition + params.loadSize))
    }
}
