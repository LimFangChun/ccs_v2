package my.edu.tarc.communechat_v2.Utility

import android.content.Intent
import android.os.AsyncTask
import android.util.Base64
import android.widget.ImageView
import android.widget.Toast
import com.squareup.picasso.Picasso
import my.edu.tarc.communechat_v2.ImageFullscreenActivity
import my.edu.tarc.communechat_v2.model.Message
import org.json.JSONArray
import java.io.File
import java.io.FileOutputStream
import java.lang.ref.WeakReference

class StoreImageAsync(val message: String, imageView: ImageView)
    : AsyncTask<Void, Void, Boolean>() {

    private val imageView = WeakReference<ImageView>(imageView)
    private var messageID: Int = 0
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
            filePath = File(MyUtil.getLocalImagePath())
            imagePath = File(MyUtil.getLocalImagePath(), "$messageID.jpg")
            image = Base64.decode(messageJson.getString(Message.COL_IMAGE), Base64.DEFAULT)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

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