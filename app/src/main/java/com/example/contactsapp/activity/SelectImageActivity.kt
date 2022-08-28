package com.example.contactsapp.activity

import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.canhub.cropper.CropImageView
import com.example.contactsapp.databinding.ActivitySelectImageBinding


class SelectImageActivity : AppCompatActivity(), CropImageView.OnCropImageCompleteListener,
    CropImageView.OnSetImageUriCompleteListener {
    private lateinit var binding: ActivitySelectImageBinding
    private lateinit var pickPhoto: ActivityResultLauncher<String>
    private var isReadPermissionGranted = false
    private var isWritePermissionGranted = false
    private lateinit var permissionLauncher: ActivityResultLauncher<Array<String>>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySelectImageBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()
        setListners()

    }

    private fun init() {

        permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()){ permissions ->

            isReadPermissionGranted = permissions[android.Manifest.permission.READ_EXTERNAL_STORAGE] ?: isReadPermissionGranted
            isWritePermissionGranted = permissions[android.Manifest.permission.WRITE_EXTERNAL_STORAGE] ?: isWritePermissionGranted

        }

        pickPhoto = registerForActivityResult(ActivityResultContracts.GetContent()) {
            Log.d("imagelog", it?.encodedUserInfo.toString())
            binding.cropimageview.setImageUriAsync(it) }


    }

    private fun setListners(){
        binding.btngallery.setOnClickListener {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                pickPhoto.launch("image/*")

            } else {
                requestPermission(this)
                Toast.makeText(this, "Allow File access Permission", Toast.LENGTH_SHORT).show()

            }

        }
        binding.cropimageview.let {
            it.setOnSetImageUriCompleteListener(this)
            it.setOnCropImageCompleteListener(this)
            it.setFixedAspectRatio(true)
            it.scaleX=1.0f
            it.scaleY=1.0f

        }

        /*     cropImageView.setFixedAspectRatio(true);
             cropImageView.setScaleX(1.0f);
             cropImageView.setScaleY(1.0f);*/



        binding.txtdone.setOnClickListener {
            binding.cropimageview.croppedImageAsync(Bitmap.CompressFormat.PNG,40,120,120)
        }
        binding.txtcancel.setOnClickListener {

            finish()
        }

    }

    override fun onCropImageComplete(view: CropImageView, result: CropImageView.CropResult) {
        // saveBitmapToFile(result)
        val returnIntent = intent
        returnIntent.putExtra("result", result.uriContent.toString())
        setResult(RESULT_OK, returnIntent)
        finish()
    }

    override fun onSetImageUriComplete(view: CropImageView, uri: Uri, error: Exception?) {
        binding.cropLayout.visibility= View.VISIBLE
        binding.pickimagelayout.visibility= View.GONE
    }

    /*  fun getDownsizedImageBytes(
          fullBitmap: Bitmap?,
          scaleWidth: Int,
          scaleHeight: Int
      ): ByteArray? {
          val scaledBitmap =
              Bitmap.createScaledBitmap(fullBitmap!!, scaleWidth, scaleHeight, true)

          // 2. Instantiate the downsized image content as a byte[]
          val baos = ByteArrayOutputStream()
          scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
          return baos.toByteArray()
      }*/

    /*fun setdown(){
        if (imageReturnedIntent == null
            || imageReturnedIntent.getData() == null
        ) {
            return
        }

        // aiming for ~500kb max. assumes typical device image size is around 2megs

        // aiming for ~500kb max. assumes typical device image size is around 2megs
        val scaleDivider = 4


        try {

            // 1. Convert uri to bitmap
            val imageUri: Uri = imageReturnedIntent.getData()
            val fullBitmap =
                MediaStore.Images.Media.getBitmap(this.contentResolver, imageUri)

            // 2. Get the downsized image content as a byte[]
            val scaleWidth = fullBitmap.width / scaleDivider
            val scaleHeight = fullBitmap.height / scaleDivider
            val downsizedImageBytes = getDownsizedImageBytes(fullBitmap, scaleWidth, scaleHeight)

            // 3. Upload the byte[]; Eg, if you are using Firebase
            val storageReference = FirebaseStorage.getInstance().getReference("/somepath")
            storageReference.putBytes(downsizedImageBytes!!)
        } catch (ioEx: IOException) {
            ioEx.printStackTrace()
        }
    }*/





    private fun requestPermission(context: Context){


        val isReadPermission = ContextCompat.checkSelfPermission(
            context,
            android.Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED

        val isWritePermission = ContextCompat.checkSelfPermission(context,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED

        val minSdkLevel = Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q

        isReadPermissionGranted = isReadPermission
        isWritePermissionGranted = isWritePermission || minSdkLevel

        Log.d("permission","readpermission: "+ isReadPermissionGranted+"writepermission: "+isWritePermissionGranted)

        val permissionRequest = mutableListOf<String>()
        if (!isWritePermissionGranted){

            permissionRequest.add(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)

        }
        if (!isReadPermissionGranted){

            permissionRequest.add(android.Manifest.permission.READ_EXTERNAL_STORAGE)

        }

        if (permissionRequest.isNotEmpty()) {
            permissionLauncher.launch(permissionRequest.toTypedArray())
        }

    }

}


