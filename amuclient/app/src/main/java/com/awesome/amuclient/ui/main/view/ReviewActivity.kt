package com.awesome.amuclient.ui.main.view

import AddReviewService
import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.awesome.amuclient.R
import com.awesome.amuclient.data.model.Constants
import com.awesome.amuclient.data.api.response.DefaultResponse
import com.awesome.amuclient.data.model.ReserveList
import com.awesome.amuclient.data.model.Review
import com.awesome.amuclient.ui.main.viewmodel.*
import com.awesome.amuclient.util.FirebaseUtils
import com.google.firebase.storage.FirebaseStorage
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.activity_review.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.ByteArrayOutputStream

class ReviewActivity : AppCompatActivity() {

    private lateinit var firebaseViewModel : FirebaseViewModel
    private lateinit var reviewViewModel: ReviewViewModel

    private var reserveList : ReserveList? = null
    private var clientId : String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_review)

        reserveList = intent.getParcelableExtra("reserveList")

        firebaseViewModel = ViewModelProvider(this).get(FirebaseViewModel::class.java)
        clientId = firebaseViewModel.getUid()
        var factory = ReviewViewModelFactory(clientId.toString())
        reviewViewModel = ViewModelProvider(this, factory).get(ReviewViewModel::class.java)



        initLayout()
        initListener()

        observe()


    }

    private fun observe() {
        firebaseViewModel.taskToString.observe(this, Observer<String> {
            val review: Review = Review(null, it,
                    review_comment.text.toString(),
                    null,
                    reserveList!!.store.id.toString(),
                    firebaseViewModel.getUid(),
                    rbPoint.text.toString(),
                    reserveList!!.reserve.id.toString())
            reviewViewModel.addReview(review)
        })

        reviewViewModel.status.observe(this, Observer<Int> {
            if(it == 200) {
                finish()
            }
        })

    }

    private fun initLayout() {
        store_name.setText(reserveList!!.store.name)

    }

    private fun initListener() {
        add_image.setOnClickListener {
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if(checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) ==
                    PackageManager.PERMISSION_DENIED) {
                    val permissions = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
                    requestPermissions(permissions,
                        PERMISSION_CODE
                    )
                }
                else {
                    pickImageFromGallery()
                }
            }
            else {
                pickImageFromGallery()
            }
        }

        rb.setOnRatingBarChangeListener { _, rating, _ ->
            rbPoint.setText(rating.toString())
        }

        submit_review.setOnClickListener {
            firebaseViewModel.uploadTask(add_image.drawable as BitmapDrawable, reserveList!!.reserve.id.toString() +"_review")
        }
    }

//    private fun addReview() {
//        val bitmap = (add_image.drawable as BitmapDrawable).bitmap
//        val baos = ByteArrayOutputStream()
//        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
//        val data = baos.toByteArray()
//
//        var task = FirebaseStorage.getInstance().getReference()
//                .child(firebaseViewModel.getUid() + storeName + "_review")
//        val uploadTask = task.putBytes(data)
//
//        uploadTask.addOnFailureListener {
//            Toast.makeText(this, "업로드실패", Toast.LENGTH_LONG).show()
//
//        }
//                .addOnSuccessListener {
//                    Toast.makeText(this, "업로드성공", Toast.LENGTH_LONG).show()
//
//                    task.downloadUrl.addOnCompleteListener { task ->
//                        val review: Review = Review(null, task.result.toString(),
//                                review_comment.text.toString(), null, storeId, firebaseViewModel.getUid(), rbPoint.text.toString(), reserveId)
//
//                        val gson = GsonBuilder().setLenient().create()
//                        val retrofit = Retrofit.Builder()
//                                .baseUrl(Constants.serverUrl)
//                                .addConverterFactory(GsonConverterFactory.create(gson))
//                                .build()
//
//                        val joinApi = retrofit.create(AddReviewService::class.java)
//                        joinApi.addReview(review)
//                                .enqueue(object : Callback<DefaultResponse> {
//
//                                    override fun onFailure(call: Call<DefaultResponse>, t: Throwable) {
//                                        Log.e("retrofit add review", "실패")
//                                        Log.e("Check", t.toString())
//                                    }
//
//                                    override fun onResponse(
//                                            call: Call<DefaultResponse>,
//                                            response: Response<DefaultResponse>
//                                    )  {
//                                        if (response.isSuccessful && response.body() != null && response.body()!!.code == 200) {
//                                            Log.e("ReviewActivity", "success")
//                                            finish()
//
//                                        } else {
//                                            Log.e("ReviewActivity", "실패")
//                                        }
//                                    }
//                                })
//                    }
//                }
//    }
    companion object {
        //image pick code
        private val IMAGE_PICK_CODE = 1000;
        //Permission code
        val PERMISSION_CODE = 1001;
    }

    private fun pickImageFromGallery() {
        //Intent to pick image
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent,
                IMAGE_PICK_CODE
        )
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when(requestCode){
            PERMISSION_CODE -> {
                if (grantResults.size > 0 && grantResults[0] ==
                    PackageManager.PERMISSION_GRANTED){
                    //permission from popup granted
                    pickImageFromGallery()
                }
                else{
                    //permission from popup denied
                    Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    //handle result of picked image
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == IMAGE_PICK_CODE){
            add_image.setImageURI(data?.data)
        }
    }
}