package com.raj.weathertodo.home

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaRecorder
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.awesomedialog.*
import com.google.firebase.storage.StorageMetadata
import com.google.gson.Gson
import com.raj.weathertodo.MainActivity
import com.raj.weathertodo.R
import com.raj.weathertodo.adapters.MediaFilesAdapter
import com.raj.weathertodo.databinding.ActivityAddTaskBinding
import com.raj.weathertodo.mediaDetail.MediaDetailActivity
import com.raj.weathertodo.model.MediaFileCardClass
import com.raj.weathertodo.model.TaskClass
import com.raj.weathertodo.model.UserClass
import com.raj.weathertodo.utils.*
import com.sandrios.sandriosCamera.internal.SandriosCamera
import com.sandrios.sandriosCamera.internal.configuration.CameraConfiguration
import com.sandrios.sandriosCamera.internal.ui.model.Media
import org.json.JSONObject
import java.io.File
import java.io.IOException
import java.util.*


class AddTaskActivity : MainActivity() {
    lateinit var binding: ActivityAddTaskBinding


    val PICK_Camera_IMAGE = 4
    val PICK_Camera_Video = 1003

    //data
    var user: UserClass? = null
    var title = ""
    var description = ""
    var image_uri = ""
    var video_uri = ""
    var audio_uri = ""


    var selected_image_uri = ""
    var selected_video_uri = ""
    var selected_audio_uri = ""

    var task: TaskClass? = null

    var isRecording = 0 // 0 = not, already running 1

    lateinit var mediaRecorder: MediaRecorder
    lateinit var output: String
    var fileName: String = ""
    var state = false
    var recordingStopped = false

    lateinit var adapter: MediaFilesAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddTaskBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = "Add New Task"

        if (intent.extras != null) {
            supportActionBar?.title = "Update Task"
            val str = intent.getStringExtra("task")
            task = Gson().fromJson(str, TaskClass::class.java)
            binding.apply {
                etTask.setText(task?.title)
                etNotes.setText(task?.description)
                btnDelete.showView()
                btnDelete.setOnClickListener {
                    deleteTask()
                }

                image_uri = task?.image.toString()
                video_uri = task?.video.toString()
                audio_uri = task?.audio.toString()


                if (image_uri != "null") {
                    manageAddUi(
                        MediaFileCardClass(image_uri, image_uri, 0),
                        "image"
                    )
                }

                if (video_uri != "null") {
                    manageAddUi(
                        MediaFileCardClass(video_uri, video_uri, 1),
                        "video"
                    )
                }

                if (audio_uri != "null") {
                    manageAddUi(
                        MediaFileCardClass(audio_uri, audio_uri, 2),
                        "audio"
                    )
                }

            }
        }

        val userStr = sharedPRef.getData(sp_user_data)
        user = Gson().fromJson(userStr, UserClass::class.java)

        binding.btOpenCamera.setOnClickListener {

            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.CAMERA
                ) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.CAMERA
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                val permissions = arrayOf(
                    Manifest.permission.CAMERA
                )
                ActivityCompat.requestPermissions(this, permissions, 0)
            } else {
                openCamra()
            }
        }

        binding.btOpenCameraforPhoto.setOnClickListener {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.CAMERA
                ) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.CAMERA
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                val permissions = arrayOf(
                    Manifest.permission.CAMERA
                )
                ActivityCompat.requestPermissions(this, permissions, 0)
            } else {
                openCamraForPhoto()

            }
        }

        mediaRecorder = MediaRecorder()
        binding.btUploadAudio.setOnClickListener {

            recordAudio()
        }

        binding.btnAddTask.setOnClickListener {
            title = binding.etTask.text.toString().trim()
            description = binding.etNotes.text.toString().trim()

            if (title.isEmpty()) {
                binding.etTask.setError("Add Task First")
                return@setOnClickListener
            }

            savTaskToDb()

        }


    }

    fun recordAudio() {

        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.RECORD_AUDIO
            ) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            val permissions = arrayOf(
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
            ActivityCompat.requestPermissions(this, permissions, 0)
        } else {

            when (isRecording) {
                0 -> {
                    fileName = "${System.currentTimeMillis()}.mp3"
                    //   output = Environment.getExternalStorageDirectory().absolutePath + "/$fileName"
                    output = "${externalCacheDir?.absolutePath}/$fileName"
                    mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC)
                    mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
                    mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
                    mediaRecorder.setOutputFile(output)

                    startRecording()
                    isRecording = 1
                    binding.btUploadAudio.text = "Stop"
                }

                else -> {
                    stopRecording()
                    isRecording = 0
                    binding.btUploadAudio.text = "Record"
                }
            }

        }

    }

    //media recorder function s
    private fun startRecording() {
        try {
            mediaRecorder?.prepare()
            mediaRecorder?.start()
            state = true
            makeToast("\"Recording started!\"")
        } catch (e: IllegalStateException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    /* private fun pauseRecording() {
         if (state) {
             if (!recordingStopped) {
                 makeToast("Stopped!")
                 mediaRecorder.pause()
                 recordingStopped = true

             } else {
                 resumeRecording()
             }
         }
     }

     private fun resumeRecording() {
         makeToast("Resume!")
         mediaRecorder?.resume()

         recordingStopped = false
     }*/

    private fun stopRecording() {
        if (state) {
            mediaRecorder?.stop()
            mediaRecorder?.release()
            state = false

            //for selected audio
            audio_uri = output
            val f = File(output)
            upload_audio_firebase(Uri.parse(output), f)

            //uploadImages(Uri.fromFile(f), 2)
            // manageAddUi(MediaFileCardClass(fileName, audio_uri, 2), "audio")
            makeToast("Stopped!")

        } else {
            makeToast("You are not recording right now!")
        }
    }

    fun deleteTask() {
        AwesomeDialog.build(this@AddTaskActivity)
            .title("Delete Task ?")
            .body("Are you sure you want to delete this Task. This can not Undone.")
            .onPositive("Delete", buttonBackgroundColor = R.drawable.button_bg) {

                val map = hashMapOf(
                    "id" to "${task?.id}"
                )
                webService.makeApiCall(url_tododelete, webService.POST, map, object : ApiListner {
                    override fun onResponse(data: String) {
                        pd.dismiss()
                        makeLog("add task : $data")

                        val obj = JSONObject(data)
                        val success = obj.getInt("success")
                        val message = obj.getString("message")
                        // val data = obj.getString("data")

                        when (success) {
                            1 -> {
                                makeToast("Task Deleted to list.")
                                finish()
                            }

                            else -> {
                                AwesomeDialog.build(this@AddTaskActivity)
                                    .title("Task Adding Failed.")
                                    .body("Something Went Wrong Tryu Agian")
                                    .onPositive("Ok") {

                                    }
                            }
                        }

                    }

                    override fun onFailure() {
                    }

                })


            }
            .onNegative("Cancel") {

            }

    }

    fun savTaskToDb() {

        val map = hashMapOf<String, String>(
            "id" to if (intent.extras != null) {
                task!!.id.toString()
            } else "",
            "user_id" to "${user?.id}",
            "title" to "$title",
            "description" to "$description",
            "image" to "$selected_image_uri",
            "video" to "$selected_video_uri",
            "audio" to "$selected_audio_uri",
        )

        pd.show()
        val url = if (intent.extras != null) {
            url_updatetodo
        } else url_addtodo

        webService.makeApiCall(url, webService.POST, map, object : ApiListner {
            override fun onResponse(data: String) {
                pd.dismiss()
                makeLog("add task : $data")

                val obj = JSONObject(data)
                val success = obj.getInt("success")
                val message = obj.getString("message")
                // val data = obj.getString("data")

                when (success) {
                    1 -> {

                        makeToast("Task Added to list.")
                        finish()
                    }

                    else -> {
                        AwesomeDialog.build(this@AddTaskActivity)
                            .title("Task Adding Failed.")
                            .body("Something Went Wrong Tryu Agian")
                            .onPositive("Ok") {
                                finish()
                            }
                    }
                }

            }

            override fun onFailure() {
            }
        })

    }

    fun openCamra() {
        /* val takePictureIntent = Intent(MediaStore.ACTION_VIDEO_CAPTURE)
         startActivityForResult(takePictureIntent, PICK_Camera_Video)*/
        Intent(MediaStore.ACTION_VIDEO_CAPTURE).also { takeVideoIntent ->
            takeVideoIntent.resolveActivity(packageManager)?.also {
                startActivityForResult(takeVideoIntent, PICK_Camera_Video)
            }
        }
    }

    fun openCamraForPhoto() {

        SandriosCamera
            .with()
            .setVideoFileSize(20)
            .setMediaAction(CameraConfiguration.MEDIA_ACTION_PHOTO)
            .enableImageCropping(true)
            .launchCamera(this);

        /* val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
         makeLog("button camre")
         startActivityForResult(takePictureIntent, PICK_Camera_IMAGE)*/


        /* val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
         try {
             startActivityForResult(takePictureIntent, PICK_Camera_IMAGE)
         } catch (e: ActivityNotFoundException) {
             // display error state to the user
         }*/
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)


        if (resultCode === RESULT_OK && requestCode === SandriosCamera.RESULT_CODE && data != null) {
            if (data.getSerializableExtra(SandriosCamera.MEDIA) is Media) {
                val media = data.getSerializableExtra(SandriosCamera.MEDIA) as Media
                Log.e("File", "" + media.path)
                Log.e("Type", "" + media.type)
                Toast.makeText(applicationContext, "Media captured.", Toast.LENGTH_SHORT).show()
                if (image_uri.isEmpty()) {

                    val f = File(media.path)
                    uploadImages(Uri.fromFile(f), 0)
                    binding.btOpenCameraforPhoto.isEnabled = false
                }
            }
        }

        when (requestCode) {
            PICK_Camera_Video -> { //select video
                if (resultCode == Activity.RESULT_OK && data != null && data.data != null) {
                    if (video_uri.isEmpty()) {

                        uploadImages(data.data!!, 1)
                        binding.btOpenCamera.isEnabled = false
                    }

                } else {
                    Toast.makeText(this, "Video not selected", Toast.LENGTH_SHORT).show()
                }
            }

            PICK_Camera_IMAGE -> {
                makeLog("cam_req : ${data!!.data}")
                if (resultCode == Activity.RESULT_OK && data.data != null) {
                    if (image_uri.isEmpty()) {
                        uploadImages(data.data!!, 0)
                        binding.btOpenCameraforPhoto.isEnabled = false
                    }

                } else {
                    Toast.makeText(this, "Photo not selected", Toast.LENGTH_SHORT).show()
                }
            }
        }

    }

    //function to upload all images
    fun uploadImages(uri: Uri, type: Int): String {
        var fileName = "${System.currentTimeMillis()}.${getExtension(uri)}"

        if (type == 0) {
            fileName = "${System.currentTimeMillis()}.jpg"
        }

        if (type == 2) {
            fileName = "${System.currentTimeMillis()}.mp3"
        }

        pd.show()
        val uploadImgRef = storageReference.child("task_data").child(fileName)
        uploadImgRef.putFile(uri)
            .addOnSuccessListener {
                pd.dismiss()
                when (type) {
                    0 -> {//photo
                        image_uri = uri.toString()
                        selected_image_uri = fileName
                        manageAddUi(
                            MediaFileCardClass(fileName, image_uri, 0),
                            "image"
                        )
                    }

                    1 -> {//video
                        video_uri = uri.toString()
                        selected_video_uri = fileName
                        manageAddUi(
                            MediaFileCardClass(fileName, video_uri, 1),
                            "video"
                        )
                    }
                    else -> {//audio
                        audio_uri = uri.toString()
                        selected_audio_uri = fileName
                        manageAddUi(
                            MediaFileCardClass(fileName, audio_uri, 2),
                            "audio"
                        )
                    }
                }
                makeLog("file : $fileName")

            }
            .addOnFailureListener {
                pd.dismiss()
                makeToast("Something went Wrong")
            }

        return fileName
    }

    fun getExtension(uri: Uri): String? {
        val cR = contentResolver
        val mime = MimeTypeMap.getSingleton()
        return mime.getExtensionFromMimeType(cR.getType(uri))
    }

    fun upload_audio_firebase(uri: Uri, file: File) {
        // Create the file metadata
        // Create the file metadata
        binding.btUploadAudio.isEnabled = false
        makeLog("uri audio : $uri")
        val metadata = StorageMetadata.Builder()
            .setContentType("audio/mpeg")
            .build()

        pd.show()
        val uploadTask =
            storageReference.child("task_data").child("audio/" + file.name).putFile(
                Uri.fromFile(file), metadata
            );


        uploadTask
            .addOnCanceledListener {
                makeLog("audio : ")
                pd.dismiss()
            }
            .addOnSuccessListener {
                pd.dismiss()
                audio_uri = uri.toString()
                selected_audio_uri = file.name
                manageAddUi(
                    MediaFileCardClass(selected_audio_uri, audio_uri, 2),
                    "audio"
                )

                binding.btUploadAudio.isEnabled = false

            }
    }

    fun manageAddUi(md: MediaFileCardClass, type: String) {
        when (type) {
            "image" -> {
                binding.cardImage.showView()
                binding.tvImageTitle.text = md.title
                binding.ivImageDelete.setOnClickListener {
                    binding.cardImage.hideView()
                    image_uri = ""
                }
                binding.cardImage.setOnClickListener {
                    showFileDetailsView(md)
                }
            }

            "video" -> {
                binding.cardVideo.showView()
                binding.tvVideoTitle.text = md.title
                binding.ivVideoDelete.setOnClickListener {
                    binding.cardVideo.hideView()
                    video_uri = ""

                }
                binding.cardVideo.setOnClickListener {
                    showFileDetailsView(md)
                }
            }
            else -> {
                binding.cardAudio.showView()
                binding.tvAudioTitle.text = md.title
                binding.ivAudioDelete.setOnClickListener {
                    binding.cardAudio.hideView()

                    audio_uri = ""

                }

                binding.cardAudio.setOnClickListener {
                    showFileDetailsView(md)
                }
            }
        }
    }

    fun showFileDetailsView(item: MediaFileCardClass) {
        val i = Intent(this, MediaDetailActivity::class.java)
        i.putExtra("media", Gson().toJson(item))
        startActivity(i)
    }
}
