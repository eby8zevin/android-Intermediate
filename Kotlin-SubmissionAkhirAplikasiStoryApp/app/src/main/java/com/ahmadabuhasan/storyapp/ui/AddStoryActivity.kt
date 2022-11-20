package com.ahmadabuhasan.storyapp.ui

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModelProvider
import com.ahmadabuhasan.storyapp.data.Result
import com.ahmadabuhasan.storyapp.databinding.ActivityAddStoryBinding
import com.ahmadabuhasan.storyapp.utils.SessionManager
import com.ahmadabuhasan.storyapp.viewmodel.StoryViewModel
import com.ahmadabuhasan.storyapp.viewmodel.ViewModelFactory
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.*
import java.text.SimpleDateFormat
import java.util.*

class AddStoryActivity : AppCompatActivity() {

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (!allPermissionsGranted()) {
                Toast.makeText(
                    this,
                    "Tidak mendapatkan permission.",
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            }
        }
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    private lateinit var sharedPref: SessionManager
    private lateinit var viewModel: StoryViewModel
    private lateinit var binding: ActivityAddStoryBinding
    private lateinit var photoPath: String

    private var filePhoto: File? = null

    private var localeID = Locale("in", "ID")
    private val timeStamp: String = SimpleDateFormat(
        FILE_NAME,
        localeID
    ).format(System.currentTimeMillis())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (!allPermissionsGranted()) {
            ActivityCompat.requestPermissions(
                this,
                REQUIRED_PERMISSIONS,
                REQUEST_CODE_PERMISSIONS
            )
        }

        supportActionBar?.title = "Add Story"
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        sharedPref = SessionManager(this)
        setupViewModel()

        binding.btnCamera.setOnClickListener { openCamera() }
        binding.btnGallery.setOnClickListener { openGallery() }
        binding.btnUpload.setOnClickListener { uploadToSever() }
    }

    private fun setupViewModel() {
        val factory: ViewModelFactory = ViewModelFactory.getInstance(this)
        viewModel = ViewModelProvider(this, factory)[StoryViewModel::class.java]
    }

    @SuppressLint("QueryPermissionsNeeded")
    private fun openCamera() {
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

        getPhotoFile(application).also {
            val providerFile = FileProvider.getUriForFile(
                this,
                "com.ahmadabuhasan.androidcamera.fileprovider",
                it
            )
            photoPath = it.absolutePath

            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, providerFile)
            if (cameraIntent.resolveActivity(this.packageManager) != null) {
                startActivityForResult(cameraIntent, REQUEST_CODE)
            } else {
                Toast.makeText(
                    this,
                    "Kamera tidak bisa dibuka, cek permission!",
                    Toast.LENGTH_SHORT
                )
                    .show()
            }
        }
    }

    private fun openGallery() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                val permissions = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
                requestPermissions(permissions, REQUEST_CODE_PERMISSIONS)
            } else {
                chooseImageGallery()
            }
        } else {
            chooseImageGallery()
        }
    }

    private fun chooseImageGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, IMAGE_CHOOSE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            val myFile = File(photoPath)
            filePhoto = myFile
            val resultCamera = BitmapFactory.decodeFile(filePhoto?.path)
            binding.ivPhoto.setImageBitmap(resultCamera)
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }

        if (requestCode == IMAGE_CHOOSE && resultCode == Activity.RESULT_OK) {
            val selectedImg: Uri = data?.data as Uri
            filePhoto = uriToFile(selectedImg, this@AddStoryActivity)
            binding.ivPhoto.setImageURI(selectedImg)
        }
    }

    private fun uploadToSever() {
        val desc = binding.etDesc.text.toString()

        if (filePhoto == null) {
            Toast.makeText(this@AddStoryActivity, "Foto Tidak Boleh Kosong", Toast.LENGTH_SHORT)
                .show()
        } else if (desc.isEmpty()) {
            Toast.makeText(
                this@AddStoryActivity,
                "Deskripsi Tidak Boleh Kosong",
                Toast.LENGTH_SHORT
            ).show()
        } else {

            val description = desc.toRequestBody("text/plain".toMediaType())
            val file = reduceFileImage(filePhoto as File)
            val imageFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
            val bodyMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
                "photo",
                file.name,
                imageFile
            )

            val token = "Bearer ${sharedPref.getToken}"
            viewModel.vmAddNewStory(token, bodyMultipart, description)
                .observe(this@AddStoryActivity) {

                    when (it) {
                        is Result.Loading -> {
                            showLoading(true)
                        }

                        is Result.Success -> {
                            showLoading(false)
                            Toast.makeText(this, it.data.message, Toast.LENGTH_SHORT)
                                .show()
                            val i = Intent(this@AddStoryActivity, MainActivity::class.java)
                            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                            startActivity(i)
                            finish()
                        }

                        is Result.Error -> {
                            showLoading(false)
                            Toast.makeText(this, it.error, Toast.LENGTH_SHORT).show()
                        }
                    }
                }
        }
    }

    private fun getPhotoFile(context: Context): File {
        val directoryStorage = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(timeStamp, ".jpg", directoryStorage)
    }

    private fun uriToFile(selectedImg: Uri, context: Context): File {
        val contentResolver: ContentResolver = context.contentResolver
        val myFile = getPhotoFile(context)

        val inputStream = contentResolver.openInputStream(selectedImg) as InputStream
        val outputStream: OutputStream = FileOutputStream(myFile)
        val buf = ByteArray(1024)
        var len: Int
        while (inputStream.read(buf).also { len = it } > 0) outputStream.write(buf, 0, len)
        outputStream.close()
        inputStream.close()

        return myFile
    }

    private fun reduceFileImage(file: File): File {
        val bitmap = BitmapFactory.decodeFile(file.path)

        var compressQuality = 100
        var streamLength: Int

        do {
            val bmpStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, compressQuality, bmpStream)
            val bmpPicByteArray = bmpStream.toByteArray()
            streamLength = bmpPicByteArray.size
            compressQuality -= 5
        } while (streamLength > 1000000)

        bitmap.compress(Bitmap.CompressFormat.JPEG, compressQuality, FileOutputStream(file))

        return file
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.progressBar.visibility = View.GONE
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }

        return super.onOptionsItemSelected(item)
    }

    companion object {
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
        private const val REQUEST_CODE_PERMISSIONS = 10
        private const val REQUEST_CODE = 200
        private const val IMAGE_CHOOSE = 100

        private const val FILE_NAME = "dd-MMM-yyyy"
    }
}