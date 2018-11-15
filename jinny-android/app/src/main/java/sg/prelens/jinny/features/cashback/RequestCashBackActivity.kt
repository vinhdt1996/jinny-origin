package sg.prelens.jinny.features.cashback

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_cashback.*
import kotlinx.android.synthetic.main.layout_toolbar_detail_normal.*
import org.jetbrains.anko.startActivity
import sg.prelens.jinny.R
import sg.prelens.jinny.features.cashbackresult.CashBackResultActivity
import sg.prelens.jinny.utils.FileUtil
import android.os.Environment.DIRECTORY_PICTURES
import android.os.Environment.getExternalStoragePublicDirectory
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.R.attr.data
import java.util.*


class RequestCashBackActivity : AppCompatActivity() {
    companion object {
        private const val REQUEST_IMAGE = 1
        private const val PERMISSION_READ_STORAGE = 123
    }

    private var id: String? = null
    private var usersVoucherId: Int? = null
    private var name: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cashback)

        id = intent.extras["id"] as? String?
        if (id == null) {
            throw IllegalStateException("id could not be null")
        }
        usersVoucherId = intent.extras["usersVoucherId"] as? Int?
        if (usersVoucherId == null) {
            throw IllegalStateException("usersVoucherId could not be null")
        }

        name = intent.extras["name"] as? String?

        tvTitle?.text = getString(R.string.cash_back_request)
        savedInstanceState ?: supportFragmentManager.beginTransaction()
                .replace(R.id.container, CameraKitFragment.newInstance(id
                        ?: return, usersVoucherId ?: return))
                .commit()

        tvSelectAlbum?.setOnClickListener {
            if (ActivityCompat.checkSelfPermission(Objects.requireNonNull(this@RequestCashBackActivity), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(Objects.requireNonNull(this@RequestCashBackActivity), arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE), REQUEST_IMAGE)
            } else {
                getImage()
            }
        }
        ivBack?.setOnClickListener {
            onBackPressed()
        }
    }

    private fun getImage() {
//        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
//        intent.addCategory(Intent.CATEGORY_OPENABLE)
//        intent.type = "image/*"
//        if (intent.resolveActivity(packageManager) != null) {
//            startActivityForResult(intent, REQUEST_IMAGE)
//        }
        val galleryIntent = Intent(Intent.ACTION_PICK)
        val imageFile = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
        val strImage = imageFile.getPath()
        val data = Uri.parse(strImage)
        galleryIntent.setDataAndType(data, "image/*")
        startActivityForResult(galleryIntent, REQUEST_IMAGE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE && resultCode == RESULT_OK) {
            val fileUtil = FileUtil()
            this.startActivity<CashBackResultActivity>(
                    "file" to fileUtil.getPath(this, data?.data ?: return)
                    , "usersVoucherId" to usersVoucherId
                    , "id" to id, "name" to name)
        }
    }

//    private fun checkPermissionReadStorage(
//            context: Context): Boolean {
//        val currentAPIVersion = Build.VERSION.SDK_INT
//        if (currentAPIVersion >= android.os.Build.VERSION_CODES.M) {
//            if (ContextCompat.checkSelfPermission(context,
//                            Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
//                if (ActivityCompat.shouldShowRequestPermissionRationale(
//                                context as Activity,
//                                Manifest.permission.READ_EXTERNAL_STORAGE)) {
//                    showDialog("External storage", context,
//                            Manifest.permission.READ_EXTERNAL_STORAGE)
//                } else {
//                    ActivityCompat
//                            .requestPermissions(
//                                    context,
//                                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
//                                    PERMISSION_READ_STORAGE)
//                }
//                return false
//            } else {
//                return true
//            }
//        } else {
//            return true
//        }
//    }

    private fun showDialog(msg: String, context: Context,
                           permission: String) {
        val alertBuilder = AlertDialog.Builder(context)
        alertBuilder.setCancelable(true)
        alertBuilder.setTitle("Permission necessary")
        alertBuilder.setMessage("$msg permission is necessary")
        alertBuilder.setPositiveButton(android.R.string.yes,
                { _, _ ->
                    ActivityCompat.requestPermissions(context as Activity,
                            arrayOf(permission),
                            PERMISSION_READ_STORAGE)
                })
        val alert = alertBuilder.create()
        alert.show()
    }

//    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
//        when (requestCode) {
//            PERMISSION_READ_STORAGE ->
//                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    getImage()
//                }
//            else -> super.onRequestPermissionsResult(requestCode, permissions, grantResults)
//        }
//    }
}