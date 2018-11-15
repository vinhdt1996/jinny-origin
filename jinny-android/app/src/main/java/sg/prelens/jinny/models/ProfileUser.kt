package sg.prelens.jinny.models

class ProfileUser() {
    var id: String? = null
    var email: String? = null
    var dob: String? = null
    var gender: String? = null
    var token: String? = null
    var guest: Boolean? = false
    var isFirstTime_CashBack: Boolean = false
    var isFirstTime_Membership: Boolean = false

    var residential_region: Region? = null

    constructor(id: String?,
                email: String?,
                dob: String?,
                gender: String?,
                token: String?,
                residentialRegion: Region?) : this() {
        this.id = id
        this.email = email
        this.dob = dob
        this.gender = gender
        this.token = token
        this.residential_region = residentialRegion
    }

    constructor(email: String?,
                dob: String?,
                gender: String?,
                token: String?,
                residentialRegion: Region?) : this() {
        this.email = email
        this.dob = dob
        this.gender = gender
        this.token = token
        this.residential_region = residentialRegion
    }

    constructor(email: String?,
                dob: String?,
                gender: String?,
                residentialRegion: Region?) : this() {
        this.email = email
        this.dob = dob
        this.gender = gender
        this.residential_region = residentialRegion
    }

    constructor(id: String?,
                email: String?,
                dob: String?,
                gender: String?,
                token: String?, isFirstTime_CashBack: Boolean, isFirsTime_Membership: Boolean) : this() {
        this.id = id
        this.email = email
        this.dob = dob
        this.gender = gender
        this.token = token
        this.isFirstTime_CashBack = isFirstTime_CashBack
        this.isFirstTime_Membership = isFirsTime_Membership
    }

    constructor(id: String?,
                email: String?,
                dob: String?,
                gender: String?,
                token: String?, isFirstTime_CashBack: Boolean, isFirsTime_Membership: Boolean, guest: Boolean?) : this() {
        this.id = id
        this.email = email
        this.dob = dob
        this.gender = gender
        this.token = token
        this.isFirstTime_CashBack = isFirstTime_CashBack
        this.isFirstTime_Membership = isFirsTime_Membership
        this.guest = guest
    }
}