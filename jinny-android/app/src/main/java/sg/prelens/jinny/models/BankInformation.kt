package sg.prelens.jinny.models

data class BankInformation(
        val id: Int?,
        val holder_name: String?,
        val bank_name: String?,
        val account_number: String?,
        val account_number_last_4: String?
)