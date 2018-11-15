package sg.prelens.jinny.models

data class WithdrawalHistory(val updated_at: String,
                             val id: String,
                             val bank_info: String,
                             val amount: String,
                             val status: String,
                             val title: String,
                             val description: String)