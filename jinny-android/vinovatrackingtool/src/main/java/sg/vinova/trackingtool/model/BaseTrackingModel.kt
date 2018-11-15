package sg.vinova.trackingtool.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


class BaseTrackingModel(
        @SerializedName("event_type")
        @Expose
        var eventType: Int,
        @SerializedName("event_name")
        @Expose
        var eventName: String,
        @SerializedName("event_description")
        @Expose
        var eventDescription: String?,
        @SerializedName("device_id")
        @Expose
        var deviceId: String = "",
        @SerializedName("time_stamp")
        @Expose
        var timeStamp: String = "",
        @SerializedName("user_id")
        @Expose
        var userId: String= ""
): Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readInt(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString()) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(eventType)
        parcel.writeString(eventName)
        parcel.writeString(eventDescription)
        parcel.writeString(deviceId)
        parcel.writeString(timeStamp)
        parcel.writeString(userId)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<BaseTrackingModel> {
        override fun createFromParcel(parcel: Parcel): BaseTrackingModel {
            return BaseTrackingModel(parcel)
        }

        override fun newArray(size: Int): Array<BaseTrackingModel?> {
            return arrayOfNulls(size)
        }
    }
}
