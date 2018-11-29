package my.edu.tarc.communechat_v2.Adapter

import android.content.Context
import android.graphics.BitmapFactory
import android.preference.PreferenceManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.amulyakhare.textdrawable.TextDrawable
import com.amulyakhare.textdrawable.util.ColorGenerator
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.chat_action_row.view.*
import kotlinx.android.synthetic.main.chat_date_row.view.*
import kotlinx.android.synthetic.main.chat_left_image.view.*
import my.edu.tarc.communechat_v2.R
import my.edu.tarc.communechat_v2.model.Message
import my.edu.tarc.communechat_v2.model.User
import java.text.SimpleDateFormat
import java.util.*

class ChatRoomRecyclerAdapter(val context: Context, val messageList: ArrayList<Message>) : RecyclerView.Adapter<ChatRoomRecyclerAdapter.ViewHolder>() {

    companion object {
        const val LEFT_TEXT = 1
        const val LEFT_IMAGE = 2
        const val RIGHT_TEXT = 3
        const val RIGHT_IMAGE = 4
        const val DATE_ROW = 5
        const val ACTION_ROW = 6
        const val TEXT = "Text"
        const val IMAGE = "Image"
        const val DATE = "Date"
        const val ACTION = "Action"
    }

    override fun getItemViewType(position: Int): Int {
        return with(this.messageList[position]) {
            when (message_type) {
                DATE -> DATE_ROW
                ACTION -> ACTION_ROW
                TEXT -> if (isMine(sender_id)) RIGHT_TEXT else LEFT_TEXT
                IMAGE -> if (isMine(sender_id)) RIGHT_IMAGE else LEFT_IMAGE
                else -> -1
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
        val layoutType: Int =
                when (viewType) {
                    LEFT_TEXT -> R.layout.chat_left_text
                    LEFT_IMAGE -> R.layout.chat_left_image
                    RIGHT_TEXT -> R.layout.chat_right_text
                    RIGHT_IMAGE -> R.layout.chat_right_image
                    DATE_ROW -> R.layout.chat_date_row
                    ACTION_ROW -> R.layout.chat_action_row
                    else -> -1
                }


        val view = LayoutInflater.from(context).inflate(layoutType, parent, false)
        return this.ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
        val message = messageList[position]
        holder!!.setData(message, position)
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private var currentPosition: Int = 0

        init {
            //todo
            itemView.setOnLongClickListener {
                Toast.makeText(context, "Clicked item " + messageList[currentPosition].sender_name, Toast.LENGTH_LONG).show()
                true
            }
        }

        fun setData(message: Message?, position: Int) {
            currentPosition = position

            when (message!!.message_type) {
                TEXT -> if (isMine(message.sender_id)) inflateRightText(message) else inflateLeftText(message)
                IMAGE -> if (isMine(message.sender_id)) inflateRightImage(message) else inflateLeftImage(message)
                DATE -> inflateDateRow()
                ACTION -> inflateActionRow(message)
            }

            if (message.message_type == TEXT || message.message_type == IMAGE) {
                //when message is at first
                //or when last message and current message is not sent by same person
                //or when message interval is more than 1 day
                //display time
                val textViewTime = itemView.findViewById<TextView>(R.id.textView_time)
                if (position == 0 ||
                        isDifferentDay(messageList[position], messageList[position - 1]) ||
                        messageList[position].sender_id != messageList[position - 1].sender_id) {
                    textViewTime.visibility = TextView.VISIBLE
                    textViewTime.text = calculateTime(message)
                } else {
                    textViewTime.visibility = TextView.GONE
                }
            }
        }

        private fun inflateLeftText(message: Message) {
            val colorGenerator = ColorGenerator.MATERIAL
            val color = colorGenerator.getColor(message.sender_name)
            val textDrawable = TextDrawable.builder().buildRound(message.sender_name.substring(0, 1), color)

            val imageViewProfilePic = itemView.findViewById<ImageView>(R.id.imageView_profilePic)
            imageViewProfilePic.setImageDrawable(textDrawable)

            val textViewMessage = itemView.findViewById<TextView>(R.id.textView_message)
            textViewMessage.text = message.message

            val textViewName = itemView.findViewById<TextView>(R.id.textView_name)
            if (currentPosition == 0 || messageList[currentPosition].sender_id != messageList[currentPosition - 1].sender_id) {
                textViewName.visibility = TextView.VISIBLE
                textViewName.text = message.sender_name
            } else {
                textViewName.visibility = TextView.GONE
            }
        }

        private fun inflateRightText(message: Message) {
            val textViewMessage = itemView.findViewById<TextView>(R.id.textView_message)
            textViewMessage.text = message.message
        }

        private fun inflateLeftImage(message: Message) {
            val colorGenerator = ColorGenerator.MATERIAL
            val color = colorGenerator.getColor(message.sender_name)
            val textDrawable = TextDrawable.builder().buildRound(message.sender_name.substring(0, 1), color)

            val imageViewProfilePic = itemView.findViewById<ImageView>(R.id.imageView_profilePic)
            imageViewProfilePic.setImageDrawable(textDrawable)

            val bitmap = BitmapFactory.decodeByteArray(message.media, 0, message.media.size)
            itemView.imageView_image.setImageBitmap(bitmap)

            val textViewName = itemView.findViewById<TextView>(R.id.textView_name)
            textViewName.text = message.sender_name
        }

        private fun inflateRightImage(message: Message) {
//            val bitmap = BitmapFactory.decodeByteArray(message.media, 0, message.media.size)
//            val stream = ByteArrayOutputStream()
//            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
//            val resizedBitmap = Bitmap.createScaledBitmap(bitmap, (bitmap.width*0.7).toInt(), (bitmap.height*0.7).toInt(), true)
//            itemView.imageView_image.setImageBitmap(resizedBitmap)

            //if media path has value
            //means this image come from local
            //use Picasso to do it
            if (message.mediaPath != null) {
                Picasso.get().load(message.mediaPath)
                        .centerCrop()
                        .resize(500, 500)
                        .into(itemView.imageView_image)
            } else {
                Picasso.get().load(R.drawable.ic_file_download_black_24dp)
                        .resize(200, 200)
                        .into(itemView.imageView_image)
            }
        }

        private fun inflateDateRow() {
            itemView.textView_date.text = context.getString(R.string.today)
        }

        private fun inflateActionRow(message: Message) {
            val stringBuilder = StringBuilder()
            stringBuilder.append(calculateTime(message)).append("\n").append(message.message)
            itemView.textView_action.text = stringBuilder
        }

        private fun calculateTime(message: Message?): String {
            return with(message!!) {
                when {
                    isToday(date_created) -> {
                        date_created.get(Calendar.HOUR).toString() +
                                ":" +
                                date_created.get(Calendar.MINUTE) +
                                " " +
                                if (date_created.get(Calendar.AM_PM) == 0) "AM" else "PM"
                    }
                    else -> {
                        date_created.get(Calendar.DAY_OF_MONTH).toString() +
                                " " +
                                getMonthName(date_created.get(Calendar.MONTH)) +
                                " " +
                                date_created.get(Calendar.HOUR) +
                                ":" +
                                date_created.get(Calendar.MINUTE) +
                                " " +
                                if (date_created.get(Calendar.AM_PM) == 0) "AM" else "PM"
                    }
                }
            }
        }

        private fun isToday(time: Calendar): Boolean {
            val calendar = Calendar.getInstance()
            return time.get(Calendar.DAY_OF_MONTH) == calendar.get(Calendar.DAY_OF_MONTH)
        }

        private fun getMonthName(month: Int): String {
            val dateFormat = SimpleDateFormat("MMM", Locale.ENGLISH)
            return dateFormat.format(month)
        }

        private fun isDifferentDay(message1: Message, message2: Message): Boolean {
            return (message1.date_created.get(Calendar.DAY_OF_MONTH) != message2.date_created.get(Calendar.DAY_OF_MONTH))
        }
    }

    override fun getItemCount(): Int {
        return messageList.size
    }

    fun getLastIndex(): Int {
        return messageList.size - 1
    }

    private fun isMine(userID: Int): Boolean {
        val pref = PreferenceManager.getDefaultSharedPreferences(context.applicationContext)
        return pref.getInt(User.COL_USER_ID, -1) == userID
    }
}