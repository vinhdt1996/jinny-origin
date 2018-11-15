package sg.prelens.jinny.models

import java.io.Serializable

enum class HowToInfo(val status: String?) : Serializable {
    Membership("MEMBERSHIP"),
    CashBack("CASHBACK")
}