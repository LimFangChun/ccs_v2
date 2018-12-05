package my.edu.tarc.communechat_v2

import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_image_fullscreen.*
import java.io.File

class ImageFullscreenActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image_fullscreen)

        assert(supportActionBar != null)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        val imagePath = intent.getStringExtra("ImagePath")
        val imageUri = Uri.fromFile(File(imagePath))
        Picasso.get().load(imageUri).into(imageView_image)
    }
}
