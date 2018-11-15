package sg.prelens.jinny.repositories.inmemory.bypage.cashbackhistory

import sg.prelens.jinny.models.CashBackHistory
import sg.prelens.jinny.repositories.Listing

interface CashBackHistoryRepository {
    fun fetchHistory(pageSize: Int): Listing<CashBackHistory>
}