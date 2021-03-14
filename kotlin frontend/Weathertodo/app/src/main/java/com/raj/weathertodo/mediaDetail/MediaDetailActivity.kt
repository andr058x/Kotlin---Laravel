package com.raj.weathertodo.mediaDetail

import android.annotation.SuppressLint
import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import com.google.gson.Gson
import com.jarvanmo.exoplayerview.media.SimpleMediaSource
import com.raj.weathertodo.MainActivity
import com.raj.weathertodo.R
import com.raj.weathertodo.databinding.ActivityMediaDetailBinding
import com.raj.weathertodo.model.MediaFileCardClass
import com.raj.weathertodo.utils.hideView
import com.raj.weathertodo.utils.makeLog
import com.raj.weathertodo.utils.showView
import com.squareup.picasso.Picasso

class MediaDetailActivity : MainActivity() {

    lateinit var binding: ActivityMediaDetailBinding

    var media: MediaFileCardClass? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMediaDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (intent.extras != null) {
            val str = intent.getStringExtra("media")
            media = Gson().fromJson(str, MediaFileCardClass::class.java)
            supportActionBar?.title = "${media?.title}"
            initview()
        }

    }

    @SuppressLint("SetJavaScriptEnabled")
    fun initview() {
        when (media?.fileType) {
            0 -> {//photo

                //get video url
                storageReference.child("task_data").child(media!!.title)
                    .downloadUrl.addOnSuccessListener { url ->
                        binding.iv.showView()
                        Picasso.get()
                            .load(url)
                            .placeholder(R.drawable.ic_add_photo)
                            .error(R.drawable.ic_add_photo)
                            .into(binding.iv);
                    }


            }
            1 -> { //video
                binding.videoView.showView()
                val mediaSource = SimpleMediaSource(media?.fileUri) //uri also supported
                binding.videoView.play(mediaSource)

                //get video
                storageReference.child("task_data").child(media!!.title)
                    .downloadUrl.addOnSuccessListener {
                        makeLog("uri = $it")

                        binding.videoView.hideView()
                        //val mediaSource = SimpleMediaSource(media?.fileUri) //uri also supported
                        binding.webview.settings.javaScriptEnabled = true

                        binding.webview.webViewClient = object : WebViewClient() {
                            override fun shouldOverrideUrlLoading(
                                view: WebView?,
                                url: String?
                            ): Boolean {
                                view?.loadUrl(url!!)
                                return true
                            }
                        }
                        binding.webview.loadUrl("$it")
                    }
                    .addOnFailureListener {
                        makeLog("error = ${it.message}")
                    }
            }
            2 -> {//audio
                binding.videoView.showView()
                val mediaSource = SimpleMediaSource(media?.fileUri) //uri also supported
                binding.videoView.play(mediaSource)

                //get audio
                storageReference.child("task_data/audio/").child(media!!.title)
                    .downloadUrl.addOnSuccessListener {
                        makeLog("uri = $it")
                        binding.videoView.hideView()
                        //val mediaSource = SimpleMediaSource(media?.fileUri) //uri also supported
                        binding.webview.settings.javaScriptEnabled = true

                        binding.webview.webViewClient = object : WebViewClient() {
                            override fun shouldOverrideUrlLoading(
                                view: WebView?,
                                url: String?
                            ): Boolean {
                                view?.loadUrl(url!!)
                                return true
                            }
                        }
                        binding.webview.loadUrl("$it")
                    }
            }
        }
    }
}