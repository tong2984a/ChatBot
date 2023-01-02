package com.codepalace.chatbot.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.codepalace.chatbot.R
import com.codepalace.chatbot.data.Message
import com.codepalace.chatbot.utils.BotResponse
import com.codepalace.chatbot.utils.Constants.OPEN_GOOGLE
import com.codepalace.chatbot.utils.Constants.OPEN_SEARCH
import com.codepalace.chatbot.utils.Constants.RECEIVE_ID
import com.codepalace.chatbot.utils.Constants.SEND_ID
import com.codepalace.chatbot.utils.Constants.SPEAK_MAID
import com.codepalace.chatbot.utils.Constants.SPEAK_MARRY
import com.codepalace.chatbot.utils.Constants.SPEAK_MEET
import com.codepalace.chatbot.utils.Constants.SPEAK_NG_INLAW_MARRY
import com.codepalace.chatbot.utils.Constants.SPEAK_NG_INLAW_MEET
import com.codepalace.chatbot.utils.Constants.SPEAK_NG_MAID
import com.codepalace.chatbot.utils.Constants.SPEAK_NG_MARRY
import com.codepalace.chatbot.utils.Constants.SPEAK_NG_STICKY_RICE_WINE
import com.codepalace.chatbot.utils.Constants.SPEAK_WHAT
import com.codepalace.chatbot.utils.Constants.SPEAK_WHERE
import com.codepalace.chatbot.utils.Constants.SPEAK_WHOM
import com.codepalace.chatbot.utils.Constants.SPEAK_PO_CEMETERY
import com.codepalace.chatbot.utils.Constants.SPEAK_PO_DESSERT
import com.codepalace.chatbot.utils.Constants.SPEAK_PO_DESSERT_SHORT
import com.codepalace.chatbot.utils.Constants.SPEAK_PO_FARM
import com.codepalace.chatbot.utils.Constants.SPEAK_PO_FARM_SHORT
import com.codepalace.chatbot.utils.Constants.SPEAK_PO_MOM
import com.codepalace.chatbot.utils.Constants.SPEAK_PO_MOM_SHORT
import com.codepalace.chatbot.utils.Constants.SPEAK_PO_PIG
import com.codepalace.chatbot.utils.Constants.SPEAK_PO_PIG_SHORT
import com.codepalace.chatbot.utils.Constants.SPEAK_WEDDING
import com.codepalace.chatbot.utils.Time
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*
import java.io.File
import java.io.IOException


class MainActivity : AppCompatActivity() {
    private val TAG = "MainActivity"

    //You can ignore this messageList if you're coming from the tutorial,
    // it was used only for my personal debugging
    var messagesList = mutableListOf<Message>()
    var mMediaPlayer: MediaPlayer? = null
    private var mPlayer = MediaPlayer().apply {
        setOnPreparedListener { start() }
        setOnCompletionListener { reset() }
    }
    private lateinit var adapter: MessagingAdapter
    private val botList = listOf("Peter", "Francesca", "阿婆", "阿爺", "阿公", "阿嫲")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerView()

        clickEvents()

        val random = (0..5).random()
        customBotMessage("Hello! I am ${botList[random]}. Let's chat!")
        // Check permissions
        if (CheckPermissions()) {
            Log.i("POPO", "has permission")
        } else {
            // Request permissions
            RequestPermissions()
        }
    }

    private fun clickEvents() {

        //Send a message
        btn_send.setOnClickListener {
            sendMessage()
        }

        //Scroll back to correct position when user clicks on text view
        et_message.setOnClickListener {
            GlobalScope.launch {
                delay(100)

                withContext(Dispatchers.Main) {
                    rv_messages.scrollToPosition(adapter.itemCount - 1)

                }
            }
        }
    }

    private fun recyclerView() {
        adapter = MessagingAdapter()
        rv_messages.adapter = adapter
        rv_messages.layoutManager = LinearLayoutManager(applicationContext)

    }

    override fun onStart() {
        super.onStart()
        //In case there are messages, scroll to bottom when re-opening app
        GlobalScope.launch {
            delay(100)
            withContext(Dispatchers.Main) {
                rv_messages.scrollToPosition(adapter.itemCount - 1)
            }
        }
    }

    // 4. Destroys the MediaPlayer instance when the app is closed
    override fun onStop() {
        super.onStop()
        if (mMediaPlayer != null) {
            mMediaPlayer!!.release()
            mMediaPlayer = null
        }
    }

    private fun sendMessage() {
        val message = et_message.text.toString()
        val timeStamp = Time.timeStamp()

        if (message.isNotEmpty()) {
            //Adds it to our local list
            messagesList.add(Message(message, SEND_ID, timeStamp))
            et_message.setText("")

            adapter.insertMessage(Message(message, SEND_ID, timeStamp))
            rv_messages.scrollToPosition(adapter.itemCount - 1)

            botResponse(message)
        }
    }

    private fun playContentUri(resid: Int) {
        try {
            if (mMediaPlayer != null) {
                mMediaPlayer!!.stop()
                mMediaPlayer!!.reset()
                mMediaPlayer = null
            }
            if (mMediaPlayer == null) {
                mMediaPlayer = MediaPlayer.create(applicationContext, resid)
                mMediaPlayer!!.start()
            } else mMediaPlayer!!.start()
        } catch (exception: IOException) {
            Log.i(TAG, "MediaPlayer exception: " + exception)
            mMediaPlayer?.release()
            mMediaPlayer = null
        }
    }

    private fun queryAllFiles() {
        val projection = arrayOf(MediaStore.Audio.Media._ID, MediaStore.Audio.Media.DISPLAY_NAME)
        val selection = "${MediaStore.Audio.Media.DISPLAY_NAME} = \"Yahoo - Snip Snap.m4a\""
        //val selectionArgs = arrayOf(fileName)
        applicationContext.contentResolver.query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            projection,
            null,
            null,
            null
        )?.use { cursor ->
            while (cursor.moveToNext()) {
                val id = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID))
                val displayName = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME))
                Log.i("POPO", "id:$id")
                val contentUri = Uri.withAppendedPath(
                    MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, displayName
                )
                Log.i("POPO", "contentUri:$contentUri")
            }
        }
    }

    private fun playRecording(fileName: String) {
        //queryAllFiles()
        val projection = arrayOf(MediaStore.Audio.Media._ID, MediaStore.Audio.Media.DISPLAY_NAME)
        val selection = "${MediaStore.Audio.Media.DISPLAY_NAME} = ?"
        val selectionArgs = arrayOf(fileName)
        applicationContext.contentResolver.query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            projection,
            selection,
            selectionArgs,
            null
        )?.use { cursor ->
            while (cursor.moveToNext()) {
                val id = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID))
                val displayName = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME))
                //Log.i("POPO", "id:"  + id.toString())
                //Log.i("POPO", "displayName:"  + displayName)
                val contentUri = Uri.withAppendedPath(
                    MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, id.toString()
                )
                //Log.i("POPO", "contentUri:"  + contentUri.toString())
                val contentUri2 = Uri.withAppendedPath(
                        MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, displayName
                )
                Log.i("POPO", "contentUri2:$contentUri2")
                //val assetFileDescriptor = applicationContext.contentResolver.openAssetFileDescriptor(contentUri2, "r")
                mPlayer.run {
                    reset()
                    //setDataSource(assetFileDescriptor!!.fileDescriptor, assetFileDescriptor.startOffset,
                    //    assetFileDescriptor.declaredLength)
                    setDataSource("/storage/emulated/0/Music/$displayName")
                    prepareAsync()
                }
            }
        }
    }

    private fun botResponse(message: String) {
        val timeStamp = Time.timeStamp()

        GlobalScope.launch {
            //Fake response delay
            delay(1000)

            withContext(Dispatchers.Main) {
                //Gets the response
                val response = BotResponse.basicResponses(message)

                //Adds it to our local list
                messagesList.add(Message(response, RECEIVE_ID, timeStamp))

                //Inserts our message into the adapter
                adapter.insertMessage(Message(response, RECEIVE_ID, timeStamp))

                //Scrolls us to the position of the latest message
                rv_messages.scrollToPosition(adapter.itemCount - 1)

                //Starts Google
                when (response) {
                    OPEN_GOOGLE -> {
                        val site = Intent(Intent.ACTION_VIEW)
                        site.data = Uri.parse("https://www.google.com/")
                        startActivity(site)
                    }
                    OPEN_SEARCH -> {
                        val site = Intent(Intent.ACTION_VIEW)
                        val searchTerm: String? = message.substringAfterLast("search")
                        site.data = Uri.parse("https://www.google.com/search?&q=$searchTerm")
                        startActivity(site)
                    }

                    SPEAK_NG_INLAW_MARRY -> {
                        playContentUri(R.raw.ng_inlaw_marry)
                    }
                    SPEAK_NG_MAID -> {
                        playContentUri(R.raw.ng_maid)
                    }
                    SPEAK_NG_MARRY -> {
                        playContentUri(R.raw.ng_marry)
                    }
                    SPEAK_NG_INLAW_MEET -> {
                        playContentUri(R.raw.ng_inlaw_meet)
                    }
                    SPEAK_NG_STICKY_RICE_WINE -> {
                        playContentUri(R.raw.ng_wine)
                    }

                    SPEAK_PO_CEMETERY -> {
                        playContentUri(R.raw.po_cemetary)
                    }
                    SPEAK_PO_DESSERT_SHORT -> {
                        playContentUri(R.raw.po_dessert_short)
                    }
                    SPEAK_PO_FARM_SHORT -> {
                        playContentUri(R.raw.po_farm_short)
                    }
                    SPEAK_PO_MOM_SHORT -> {
                        playContentUri(R.raw.po_mom_short)
                    }
                    SPEAK_PO_PIG_SHORT -> {
                        playContentUri(R.raw.po_pig_short)
                    }
                    SPEAK_PO_DESSERT -> {
                        playContentUri(R.raw.po_dessert)
                    }
                    SPEAK_PO_FARM -> {
                        playContentUri(R.raw.po_farm)
                    }
                    SPEAK_PO_MOM -> {
                        playContentUri(R.raw.po_mom)
                    }
                    SPEAK_PO_PIG -> {
                        playContentUri(R.raw.po_pig)
                    }

                    SPEAK_MAID -> {
                        playContentUri(R.raw.maid)
                    }
                    SPEAK_MARRY -> {
                        playContentUri(R.raw.marry)
                    }
                    SPEAK_MEET -> {
                        playContentUri(R.raw.meet)
                    }
                    SPEAK_WEDDING -> {
                        playContentUri(R.raw.wedding)
                    }
                    SPEAK_WHERE -> {
                        playRecording("where.ogg")
                    }
                    SPEAK_WHOM -> {
                        playRecording("whom.ogg")
                    }
                    SPEAK_WHAT -> {
                        playRecording("what.ogg")
                    }
                }
            }
        }
    }

    private fun customBotMessage(message: String) {

        GlobalScope.launch {
            delay(1000)
            withContext(Dispatchers.Main) {
                val timeStamp = Time.timeStamp()
                messagesList.add(Message(message, RECEIVE_ID, timeStamp))
                adapter.insertMessage(Message(message, RECEIVE_ID, timeStamp))

                rv_messages.scrollToPosition(adapter.itemCount - 1)
            }
        }
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        // If permissions accepted ->
        when (requestCode) {
            REQUEST_AUDIO_PERMISSION_CODE -> if (grantResults.size > 0) {
                val permissionToRecord = grantResults[0] == PackageManager.PERMISSION_GRANTED
                val permissionToStore = grantResults[1] == PackageManager.PERMISSION_GRANTED
                if (permissionToRecord && permissionToStore) {

                    // Message
                    Toast.makeText(applicationContext, "Permission Granted", Toast.LENGTH_LONG).show()

                } else {

                    // Message
                    Toast.makeText(applicationContext, "Permission Denied", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    fun CheckPermissions(): Boolean {

        // Check permissions
        val result =
            ContextCompat.checkSelfPermission(applicationContext, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        val result1 = ContextCompat.checkSelfPermission(applicationContext, Manifest.permission.RECORD_AUDIO)
        return result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED
    }

    private fun RequestPermissions() {

        // Request permissions
        ActivityCompat.requestPermissions(this,
            arrayOf(Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE),
            REQUEST_AUDIO_PERMISSION_CODE)
    }

    companion object {
        const val REQUEST_AUDIO_PERMISSION_CODE = 1
    }
}