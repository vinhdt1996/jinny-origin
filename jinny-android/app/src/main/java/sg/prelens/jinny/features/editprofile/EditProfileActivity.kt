package sg.prelens.jinny.features.editprofile

import android.app.DatePickerDialog
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.content.DialogInterface
import android.content.res.Resources
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.activity_edit_profile.*
import kotlinx.android.synthetic.main.layout_toolbar.*
import org.jetbrains.anko.toast
import sg.prelens.jinny.R
import sg.prelens.jinny.api.ServiceLocator
import sg.prelens.jinny.base.BaseActivity
import sg.prelens.jinny.exts.*
import sg.prelens.jinny.models.ProfileUser
import sg.prelens.jinny.models.Region
import java.util.*
import android.os.Build
import android.view.WindowManager
import android.view.Gravity
import android.widget.*
import androidx.work.State
import com.example.roomdbAnalytics.dao.RoomDB
import com.example.roomdbAnalytics.model.DefaultAnalytics
import kotlinx.android.synthetic.main.layout_datepicker.*
import kotlinx.android.synthetic.main.layout_datepicker.view.*
import kotlinx.android.synthetic.main.layout_dialog_vertical.view.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.customView
import sg.prelens.jinny.constant.AnalyticConst
import sg.prelens.jinny.service.tracking.TrackingHelper
import sg.prelens.jinny.utils.FirebaseAnalyticsUtil
import sg.vinova.trackingtool.model.EventType
import kotlin.collections.ArrayList

@Suppress("CAST_NEVER_SUCCEEDS")
class EditProfileActivity : BaseActivity(), View.OnClickListener {
    private lateinit var regionViewModel: RegionViewModel
    private lateinit var profileUserViewModel: UpdateProfileViewModel
    private lateinit var myProfileViewModel: MyProfileViewModel
    private var year: Int = 0
    private var month: Int = 0
    private var day: Int = 0
    private var gender: String? = null
    private var region: Region? = null
    private lateinit var dialogInterface: DialogInterface
    override fun getLayoutId(): Int {
        return R.layout.activity_edit_profile
    }

    override fun replaceFragmentId(): Int {
        return 0
    }

    override fun isFullScreen(): Boolean {
        return false
    }

    override fun isBackPressed(): Boolean {
        return true
    }

    private var roomDB: RoomDB? = null

    override fun init() {
        super.init()
        setTitle(getString(R.string.edit_profile), 0)
        setBackgroundToolbar(0)
        ivBack?.setOnClickListener(this)
        btnSave?.setOnClickListener(this)
        initSpinnerGender()
        initSpinnerRegion()
        initDatePicker()
        initUpdateProfile()
        setUpHideSoftKeyboard(rlContainer)
        roomDB = RoomDB.newInstance(this)
        if (!TrackingHelper.hasConnection(this)) {
            val default = DefaultAnalytics(EventType.PAGE_VIEW, AnalyticConst.user_edit_profile, description = null)
            TrackingHelper.setDB(default, roomDB ?: return, this)
        }
        TrackingHelper.sendEvent(EventType.PAGE_VIEW, AnalyticConst.user_edit_profile, "", this)?.observe(this, Observer {
            if (it?.state == State.FAILED) {
                val default = DefaultAnalytics(EventType.PAGE_VIEW, AnalyticConst.user_edit_profile, description = null)
                TrackingHelper.setDB(default, roomDB ?: return@Observer, this)
            }
        })
    }

    private fun initUpdateProfile() {
        profileUserViewModel = updateProfileModel()
        profileUserViewModel.updateProfileLiveData.observe(this, Observer {
            it?.let {
                //                toast(getString(R.string.success_update_profile))
                TrackingHelper.sendEvent(EventType.ACTION, AnalyticConst.user_edit_profile, "", this)?.observe(this, Observer {
                    if (it?.state == State.FAILED) {
                        val default = DefaultAnalytics(EventType.ACTION, AnalyticConst.user_edit_profile, description = null)
                        TrackingHelper.setDB(default, roomDB ?: return@Observer, this)
                    }
                })
                showDialog(getString(R.string.success_update_profile), 0)
            }
        })
        profileUserViewModel.errorLiveData.observe(this, Observer {
            if (it != null) {
                it?.parseMessage().let {
                    //                toast(getString(R.string.error_update_profile))
                    showDialog(getString(R.string.error_update_profile))
                }
            }
        })
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.ivBack -> {
                onBackPressed()
            }
            R.id.btnSave -> {
//                if (edtName.text.isNullOrEmpty()) {
//                    this.toast(getString(R.string.err_name_profile_empty))
//                    return@onClick
//                }
                if (edtEmail.text.isNullOrEmpty()) {
//                    this.toast(getString(R.string.err_email_profile_empty))
                    showDialog(getString(R.string.err_email_profile_empty))
                    return@onClick
                }
                if (!edtEmail?.text.toString().isValidEmail()) {
//                    this.toast(R.string.err_email_invalid)
                    showDialog(getString(R.string.err_email_invalid))
                    return@onClick
                }
                if (edtDate.text.isNullOrEmpty()) {
//                    this.toast(getString(R.string.err_date_profile_empty))
                    showDialog(getString(R.string.err_date_profile_empty))
                    return@onClick
                }
                if (gender?.equals(getString(R.string.select), true) == true) {
                    gender = null
                }
                if (region?.id == -1) {
                    region = null
                }
                val profileUser = ProfileUser(edtEmail?.text.toString(), edtDate?.text.toString(), gender?.toLowerCase(), region)
                this.hideKeyboard()
                profileUserViewModel.setUpdateProfile(profileUser)
            }
        }
    }

    private lateinit var customDatePickerDiaLog: DialogInterface

    private fun initDatePicker() {
        val androidVersion = android.os.Build.VERSION.SDK_INT
        val calendar = Calendar.getInstance()
//        var datePickerDialog: DatePickerDialog
        year = calendar.get(Calendar.YEAR)
        month = calendar.get(Calendar.MONTH)
        day = calendar.get(Calendar.DAY_OF_MONTH)

        edtDate.setOnClickListener {
            //            datePickerDialog.show()
            val monthDate: Int
            val yearDate: Int
            if (edtDate?.text?.contains(getString(R.string.mm_yy)) == false) {
                val dateList = edtDate?.text?.split("/")
                monthDate = dateList?.get(0)?.toInt() ?: 1
                yearDate = dateList?.get(1)?.toInt() ?: 2018
            } else {
                monthDate = month + 1
                yearDate = year
            }
            customDatePickerDiaLog = alert {
                customView {
                    val view = layoutInflater.inflate(R.layout.layout_datepicker, null)
                    addView(view, null)
                    view.apply {
                        //Custom DatePicker for android 7
                        npMonth?.minValue = 1
                        npMonth?.maxValue = 12
                        npMonth?.value = monthDate
                        npMonth?.descendantFocusability = NumberPicker.FOCUS_BLOCK_DESCENDANTS
                        npMonth?.wrapSelectorWheel = true

                        npYear?.minValue = 1900
                        npYear?.maxValue = year
                        npYear?.value = yearDate
                        npYear?.descendantFocusability = NumberPicker.FOCUS_BLOCK_DESCENDANTS
                        npYear?.wrapSelectorWheel = true
                        tvCancel?.setOnClickListener {
                            customDatePickerDiaLog.cancel()
                        }
                        tvOk?.setOnClickListener {
                            edtDate?.setText(npMonth?.value?.toString() + "/" + npYear?.value?.toString())
                            customDatePickerDiaLog.cancel()
                        }
                    }
                }
            }.show()
        }
    }


    private fun initSpinnerGender() {
        val adapterGender = object : ArrayAdapter<String>(this@EditProfileActivity, android.R.layout.simple_spinner_dropdown_item) {
            override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
                val view = super.getView(position, convertView, parent)
                if (position == count) {
                    (view as TextView).text = ""
                    view.hint = getItem(count)
                }
                return view
            }

            override fun getCount(): Int {
                return super.getCount() - 1
            }
        }
        (adapterGender as ArrayAdapter<String>).setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        (adapterGender as ArrayAdapter<String>).add(getString(R.string.male))
        (adapterGender as ArrayAdapter<String>).add(getString(R.string.female))
        (adapterGender as ArrayAdapter<String>).add(getString(R.string.select))
        spnGender.adapter = adapterGender
        spnGender.setSelection((adapterGender as ArrayAdapter<String>).count)
        spnGender.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                gender = parent?.getItemAtPosition(position).toString()
            }
        }
    }

    private fun initMyProfile() {
        myProfileViewModel = myProfileModel()
        myProfileViewModel.myProfileLiveData.observe(this, Observer {
            it?.let {
                edtEmail.setText(it.result.email)
                if (it.result.dob.isNullOrEmpty()) {
                    edtDate.setText(getString(R.string.mm_yy))
                } else {
                    edtDate.setText(it.result.dob)
                }
                if (it.result.gender != null) {
                    spnGender.setSelection((spnGender.adapter as ArrayAdapter<String>).getPosition(it.result.gender?.capitalize()))
                }
                if (it.result.residential_region != null) {
                    spnRegion.setSelection((spnRegion.adapter as ArrayAdapter<Region>).getPosition(it.result.residential_region))
                }
            }
        })
        myProfileViewModel.errorLiveData.observe(this, Observer {
            it?.let {
                log("error")
            }
        })
    }

    private fun initSpinnerRegion() {
        regionViewModel = regionModel()
        regionViewModel.regionLiveData.observe(this, Observer {
            it?.let {
                initMyProfile()
                val listRegion = mutableListOf<Region>()
                for (region in it.results) {
                    listRegion.add(Region(region.id, region.name))
                }
                listRegion.add(Region(-1, getString(R.string.select)))
                val adapter = object : ArrayAdapter<Region>(this@EditProfileActivity, android.R.layout.simple_spinner_dropdown_item, listRegion) {
                    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
                        val view = super.getView(position, convertView, parent)
                        if (position == count) {
                            (view as TextView).text = ""
                            view.hint = getItem(count).name
                        }
                        return view
                    }

                    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup?): View {
                        val view = super.getDropDownView(position, convertView, parent)
                        val textView = view as TextView
                        textView.text = listRegion[position].name
                        return view
                    }

                    override fun getCount(): Int {
                        return super.getCount() - 1
                    }
                }
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spnRegion.adapter = adapter
                spnRegion.setSelection(adapter.count)
                spnRegion.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onNothingSelected(parent: AdapterView<*>?) {
                    }

                    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                        if (listRegion[position].id != -1) {
                            val textView = view as TextView
                            textView.text = listRegion[position].name
                        }
                        region = listRegion[position]
                    }
                }
            }
        })
        regionViewModel.errorLiveData.observe(this, Observer {
            if (it != null) {
                it?.parseMessage()?.let {
                    //                toast(it)
                    showDialog(it)
                }
            }
        })
    }

    fun showDialog(message: String?, flag: Int?) {
        dialogInterface = alert {
            customView {
                val view = layoutInflater.inflate(R.layout.layout_dialog_vertical, null)
                addView(view, null)
                view.btnCancel?.visibility = View.GONE
                view.btnOk?.setOnClickListener {
                    if (flag == null) {
                        hideDialogCustom()
                    } else {
                        hideDialogCustom()
                        this@EditProfileActivity.finish()
                    }
                }
                isCancelable = false
                view.tvContent?.text = message
            }
        }.show()
    }

    private fun hideDialogCustom() {
        dialogInterface.cancel()
    }

    private fun regionModel(): RegionViewModel =
            ViewModelProviders.of(this, object : ViewModelProvider.Factory {
                override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                    val repo = ServiceLocator.instance(this@EditProfileActivity).getRegionRepository()
                    @Suppress("UNCHECKED_CAST")
                    return RegionViewModel(repo) as T
                }
            })[RegionViewModel::class.java]

    private fun updateProfileModel(): UpdateProfileViewModel =
            ViewModelProviders.of(this, object : ViewModelProvider.Factory {
                override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                    val repo = ServiceLocator.instance(this@EditProfileActivity).updateProfileRepository()
                    @Suppress("UNCHECKED_CAST")
                    return UpdateProfileViewModel(repo) as T
                }
            })[UpdateProfileViewModel::class.java]

    private fun myProfileModel(): MyProfileViewModel =
            ViewModelProviders.of(this, object : ViewModelProvider.Factory {
                override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                    val repo = ServiceLocator.instance(this@EditProfileActivity).getMyProfile()
                    @Suppress("UNCHECKED_CAST")
                    return MyProfileViewModel(repo) as T
                }
            })[MyProfileViewModel::class.java]
}