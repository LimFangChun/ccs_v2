package my.edu.tarc.communechat_v2.Utility

import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.util.Base64
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import com.squareup.picasso.Picasso
import my.edu.tarc.communechat_v2.ImageFullscreenActivity
import my.edu.tarc.communechat_v2.model.Message
import org.json.JSONArray
import java.io.File
import java.io.FileOutputStream
import java.lang.ref.WeakReference

class StoreImageAsync(val message: String, imageView: ImageView, context: Context)
    : AsyncTask<Void, Void, Boolean>() {

    private val imageView = WeakReference<ImageView>(imageView)
    private var messageID: Int = 0
    private val context: WeakReference<Context> = WeakReference(context)
    private lateinit var filePath: File
    private lateinit var imagePath: File
    private lateinit var image: ByteArray

    override fun onPreExecute() {
        super.onPreExecute()
        try {
            val messageArray = JSONArray(message)
            val messageJson = messageArray.getJSONObject(0)
            messageID = messageJson.getInt(Message.COL_MESSAGE_ID)

            //construct a path
            filePath = File(MyUtil.getLocalImagePath(context.get()!!.applicationContext))
            imagePath = File(MyUtil.getLocalImagePath(context.get()!!.applicationContext), "$messageID.jpg")
            image = Base64.decode(messageJson.getString(Message.COL_IMAGE), Base64.DEFAULT)
            //image = Base64.decode("/9j/4AAQSkZJRgABAQAAAQABAAD/2wBDAA0JCgsKCA0LCgsODg0PEyAVExISEyccHhcgLikxMC4p\\nLSwzOko+MzZGNywtQFdBRkxOUlNSMj5aYVpQYEpRUk//2wBDAQ4ODhMREyYVFSZPNS01T09PT09P\\nT09PT09PT09PT09PT09PT09PT09PT09PT09PT09PT09PT09PT09PT09PT0//wAARCAFVAZADASIA\\nAhEBAxEB/8QAHAABAAIDAQEBAAAAAAAAAAAAAAYHAwQFAQII/8QAVhAAAQMDAQQFBgcLCAYLAQAA\\nAQACAwQFEQYSITFBBxNRYZEUFyJxgdEWMjZUVaGxFSMzQlJicnOTwdJDRXSCg5SywzRTVmOS8CQm\\nZYSipLPC4eLxNf/EABoBAQEAAwEBAAAAAAAAAAAAAAABAwQFAgb/xAAsEQEAAgIBAgQFAwUAAAAA\\nAAAAAQIDEQQFEhMxQVEhMkJxgSJhoVKRwdHh/9oADAMBAAIRAxEAPwCzkREBERAREQEREBERAREQ\\nEREBERAREQEREBERAREQEREBERAREQEREBERAREQEREBERAREQEREBERAREQEREBERAREQEREBER\\nAREQEREBERAREQEREBERAREQEREBERAREQEREBERAREQEREBERAREQEREBERAREQEREBERAREQER\\nEBERAREQEREBERAREQEREBERAREQEREBERAREQEREBERAREQEREBERAREQEREBERAREQEREBERAR\\nEQEREBERAREQEXi9QEREBERAREQEREBERAREQEREBERAREQEREBERAREQEREBERAREQEREBERARE\\nQEREBERAREQEREBEXI1Je4bJbHVEhG0dzB3oPL7qO32SImrl++YyGA7yq3u/SVcKrrI6KNsMZ3A8\\n1FL3dai73B9TUPLieC5yDu/Cy9h5Plz8nnkrrW7pHvVGwNmEdS0cnnChiILo070hUN1e2Gsa2mmc\\nd2/0VMwQ4Ag5B3gr8ytcWuDmnBHAqy9I6+jpaUUV4duj3NkzvKC0EVfVvSMZZOrslvlqTnBcRkZW\\nuKvXN0YTTNFODxBGMKbFjSyxxML5XtY0cycLl1eprLSNzLcIfUHZUOj0VqKvjIuV4czJ3hpK6dF0\\nb2eGMeVOkqH8y47kGzP0hWGJpLZZJcfkNytcdI1se3MdJVu/qLvU2m7NSxCOK3wAAfkrajtdBH8S\\n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\\nXT1BWU8sz", Base64.DEFAULT)
            Log.i(TAG, "filePath: " + filePath.toString())
            Log.i(TAG, "imagePath: " + imagePath.toString())
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private val TAG = "[StoreImageAsync]"

    override fun doInBackground(vararg p0: Void?): Boolean? {
        return try {
            if (filePath.exists() && !imagePath.exists()) {
                val fileOutputStream = FileOutputStream(imagePath.path)
                fileOutputStream.write(image)
                fileOutputStream.close()
            }
            true
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
            false
        }
    }

    override fun onPostExecute(result: Boolean?) {
        if (result!!) {
            try {
                Picasso.get().load(imagePath).into(imageView.get())
                imageView.get()!!.setOnClickListener {
                    val intent = Intent(imageView.get()?.context, ImageFullscreenActivity::class.java)
                    intent.putExtra("ImagePath", imagePath.absolutePath)
                    imageView.get()?.context?.startActivity(intent)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        } else {
            Toast.makeText(imageView.get()!!.context, "Failed to download image", Toast.LENGTH_LONG).show()
        }
    }
}