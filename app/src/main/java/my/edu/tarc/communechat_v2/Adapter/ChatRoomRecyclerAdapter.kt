package my.edu.tarc.communechat_v2.Adapter

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.TransitionDrawable
import android.preference.PreferenceManager
import android.support.v7.widget.RecyclerView
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.amulyakhare.textdrawable.TextDrawable
import com.amulyakhare.textdrawable.util.ColorGenerator
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.chat_action_row.view.*
import kotlinx.android.synthetic.main.chat_date_row.view.*
import kotlinx.android.synthetic.main.chat_left_image.view.*
import kotlinx.android.synthetic.main.chat_warning_row.view.*
import my.edu.tarc.communechat_v2.ImageFullscreenActivity
import my.edu.tarc.communechat_v2.MainActivity.mqttHelper
import my.edu.tarc.communechat_v2.R
import my.edu.tarc.communechat_v2.Utility.MyUtil
import my.edu.tarc.communechat_v2.Utility.StoreImageAsync
import my.edu.tarc.communechat_v2.internal.MqttHeader
import my.edu.tarc.communechat_v2.internal.MqttHelper
import my.edu.tarc.communechat_v2.model.Message
import my.edu.tarc.communechat_v2.model.User
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken
import org.eclipse.paho.client.mqttv3.MqttCallback
import org.eclipse.paho.client.mqttv3.MqttMessage
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class ChatRoomRecyclerAdapter(val context: Context, val messageList: ArrayList<Message>, val role: String) :
        RecyclerView.Adapter<ChatRoomRecyclerAdapter.ViewHolder>() {

    companion object {
        const val LEFT_TEXT = 1
        const val LEFT_IMAGE = 2
        const val RIGHT_TEXT = 3
        const val RIGHT_IMAGE = 4
        const val DATE_ROW = 5
        const val ACTION_ROW = 6
        const val WARNING_ROW = 7
        const val TEXT = "Text"
        const val IMAGE = "Image"
        const val DATE = "Date"
        const val ACTION = "Action"
        const val WARNING = "Warning"
        const val PIN_MESSAGE = "Pin message"
        const val UNPIN_MESSAGE = "Unpin message"
        const val DELETE_MESSAGE = "Delete message"
    }

    override fun getItemViewType(position: Int): Int {
        return with(this.messageList[position]) {
            when (message_type) {
                DATE -> DATE_ROW
                ACTION -> ACTION_ROW
                TEXT -> if (isMine(sender_id)) RIGHT_TEXT else LEFT_TEXT
                IMAGE -> if (isMine(sender_id)) RIGHT_IMAGE else LEFT_IMAGE
                WARNING -> WARNING_ROW
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
                    WARNING_ROW -> R.layout.chat_warning_row
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

        fun setData(message: Message?, position: Int) {
            currentPosition = position

            if (role == "Admin" && message?.message_type != DATE && message?.message_type != ACTION) {
                initItemListener(message)
            }

            when (message!!.message_type) {
                TEXT -> if (isMine(message.sender_id)) inflateRightText(message) else inflateLeftText(message)
                IMAGE -> if (isMine(message.sender_id)) inflateRightImage(message) else inflateLeftImage(message)
                DATE -> inflateDateRow()
                ACTION -> inflateActionRow(message)
                WARNING -> inflateWarningRow(message)
            }

            if (message.message_type == TEXT || message.message_type == IMAGE) {
                //when message is at first
                //or when last message and current message is not sent by same person
                //or when message interval is more than 1 day
                //display time
                val textViewTime = itemView.findViewById<TextView>(R.id.textView_time)
                if (position == 0 ||
                        isDifferentDay(messageList[position], messageList[position - 1]) ||
                        messageList[position - 1].message_type == ACTION ||
                        messageList[position - 1].message_type == DATE ||
                        messageList[position].sender_id != messageList[position - 1].sender_id ||
                        MyUtil.isSameHour(messageList[position].date_created)) {
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

            val textViewName = itemView.findViewById<TextView>(R.id.textView_name)
            textViewName.text = message.sender_name

            val localImageFile = File(MyUtil.getLocalImagePath(), "${message.message_id}.jpg")
            when {
                message.mediaPath != null -> {
                    Picasso.get().load(message.mediaPath)
                            .centerCrop()
                            .resize(500, 500)
                            .into(itemView.imageView_image)
                    itemView.imageView_image.setOnClickListener {
                        val intent = Intent(context, ImageFullscreenActivity::class.java)
                        intent.putExtra("ImagePath", localImageFile.absolutePath)
                        context.startActivity(intent)
                    }
                }
                localImageFile.exists() -> {
                    Picasso.get().load(localImageFile)
                            .centerCrop()
                            .resize(500, 500)
                            .into(itemView.imageView_image)
                    itemView.imageView_image.setOnClickListener {
                        val intent = Intent(context, ImageFullscreenActivity::class.java)
                        intent.putExtra("ImagePath", localImageFile.absolutePath)
                        context.startActivity(intent)
                    }
                }
                message.media != null -> {
                    //todo test this
                    val messageArray = JSONArray()
                    val messageObject = JSONObject()
                    messageObject.put(Message.COL_MESSAGE_ID, message.message_id)
                    messageObject.put(Message.COL_IMAGE, Base64.encode(message.media, Base64.DEFAULT))
                    messageArray.put(messageObject)
                    StoreImageAsync(messageArray.toString(), itemView.imageView_image).execute()
                }
                else -> {
                    itemView.imageView_image.setImageResource(R.drawable.ic_file_download_black_24dp)
                    itemView.imageView_image.setOnClickListener {
                        downloadImage(message, itemView.imageView_image)
                    }
                }
            }
        }

        private fun inflateRightImage(message: Message) {
            //if media path has value
            //means this image come from local
            //use Picasso to do it
            val localImageFile = File(MyUtil.getLocalImagePath(), "${message.message_id}.jpg")
            when {
                message.mediaPath != null -> {
                    Picasso.get().load(message.mediaPath)
                            .centerCrop()
                            .resize(500, 500)
                            .into(itemView.imageView_image)
                    itemView.imageView_image.setOnClickListener {
                        val intent = Intent(context, ImageFullscreenActivity::class.java)
                        intent.putExtra("ImagePath", localImageFile.absolutePath)
                        context.startActivity(intent)
                    }
                }
                localImageFile.exists() -> {
                    Picasso.get().load(localImageFile)
                            .centerCrop()
                            .resize(500, 500)
                            .into(itemView.imageView_image)
                    itemView.imageView_image.setOnClickListener {
                        val intent = Intent(context, ImageFullscreenActivity::class.java)
                        intent.putExtra("ImagePath", localImageFile.absolutePath)
                        context.startActivity(intent)
                    }
                }
                else -> {
                    itemView.imageView_image.setImageResource(R.drawable.ic_file_download_black_24dp)
                    itemView.imageView_image.setOnClickListener {
                        downloadImage(message, itemView.imageView_image)
                    }
                }
            }
        }

        private fun inflateDateRow() {
            itemView.textView_date.text = context.getString(R.string.today)
        }

        private fun inflateActionRow(message: Message) {
            itemView.textView_action.text = message.message
        }

        private fun inflateWarningRow(message: Message){
            itemView.textView_warning.text = message.message

            //Animation
            val backgrounds = arrayOf(ColorDrawable(Color.RED), ColorDrawable(Color.WHITE))
            val crossfader = TransitionDrawable(backgrounds)
            itemView.layout_warning.background = crossfader
            crossfader.startTransition(1000)
        }

        private fun calculateTime(message: Message?): String {
            var hour = message!!.date_created.get(Calendar.HOUR)
            //**NOTE** In calendar class 0 represents Noon 12 or Midnight 12
            //Its strange I know, we need manual checking
            if (hour == 0) hour = 12
            return with(message) {
                when {
                    MyUtil.isToday(date_created) -> {
                        String.format("%02d", hour) +
                                ":" +
                                String.format("%02d", date_created.get(Calendar.MINUTE)) +
                                " " +
                                if (date_created.get(Calendar.AM_PM) == 0) "AM" else "PM"
                    }
                    else -> {
                        date_created.get(Calendar.DAY_OF_MONTH).toString() +
                                " " +
                                getMonthName(date_created.get(Calendar.MONTH)) +
                                " " +
                                String.format("%02d", hour) +
                                ":" +
                                String.format("%02d", date_created.get(Calendar.MINUTE)) +
                                " " +
                                if (date_created.get(Calendar.AM_PM) == 0) "AM" else "PM"
                    }
                }
            }
        }

        private fun getMonthName(month: Int): String {
            val dateFormat = SimpleDateFormat("MMM", Locale.ENGLISH)
            return dateFormat.format(month)
        }

        private fun isDifferentDay(message1: Message, message2: Message): Boolean {
            return (message1.date_created.get(Calendar.DAY_OF_MONTH) != message2.date_created.get(Calendar.DAY_OF_MONTH))
        }

        private fun downloadImage(message: Message, imageView: ImageView) {
            val topic = "downloadImage/${message.message_id}"
            val header = MqttHeader.DOWNLOAD_IMAGE

            mqttHelper.connectPublishSubscribe(context, topic, header, message)
            mqttHelper.mqttClient.setCallback(object : MqttCallback {
                override fun messageArrived(topic: String?, message: MqttMessage?) {
                    val helper = MqttHelper()
                    helper.decode(message.toString())
                    if (helper.receivedHeader == MqttHeader.DOWNLOAD_IMAGE_REPLY) {
                        mqttHelper.unsubscribe(topic)
                        StoreImageAsync(helper.receivedResult, imageView).execute()
                    }
                }

                override fun connectionLost(cause: Throwable?) {
                }

                override fun deliveryComplete(token: IMqttDeliveryToken?) {
                }
            })
        }

        private fun initItemListener(message: Message?) {
            itemView.setOnLongClickListener {
                val dialogBuilder = AlertDialog.Builder(context)
                var items = arrayOf<CharSequence>()

                items += if (message!!.status == "Pinned") {
                    (UNPIN_MESSAGE)
                } else {
                    (PIN_MESSAGE)
                }

                items += (DELETE_MESSAGE)

                dialogBuilder.setTitle("Select an action")
                dialogBuilder.setItems(items) { _, position ->
                    pinMessage(message, position, items)
                }
                dialogBuilder.show()
                true
            }
        }

        private fun pinMessage(message: Message, position: Int, items: Array<CharSequence>) {
            Log.d("RecyclerAdapter", "Item clicked: ${message.message_id}, position: ${items[position]}")
            val topic = "updateMessage/${message.message_id}"
            val header: String = when (items[position]) {
                PIN_MESSAGE -> MqttHeader.PIN_MESSAGE
                UNPIN_MESSAGE -> MqttHeader.UNPIN_MESSAGE
                DELETE_MESSAGE -> MqttHeader.DELETE_MESSAGE
                else -> ""
            }

            mqttHelper.connectPublishSubscribe(context, topic, header, message)
            mqttHelper.mqttClient.setCallback(object : MqttCallback {
                override fun messageArrived(topic: String?, message: MqttMessage?) {
                    processReceivedMessage(topic, message, position)
                }

                override fun connectionLost(cause: Throwable?) {
                }

                override fun deliveryComplete(token: IMqttDeliveryToken?) {
                }
            })
        }

        private fun processReceivedMessage(topic: String?, message: MqttMessage?, position: Int) {
            mqttHelper.unsubscribe(topic)
            val dialogBuilder = AlertDialog.Builder(context)
            dialogBuilder.setNeutralButton(R.string.ok, null)
            val helper = MqttHelper()
            helper.decode(message.toString())
            if (helper.receivedResult == MqttHeader.SUCCESS) {
                dialogBuilder.setTitle(R.string.success)
                when (helper.receivedHeader) {
                    MqttHeader.PIN_MESSAGE_REPLY -> {
                        dialogBuilder.setMessage("Message has been pinned")
                        messageList[position].status = "Pinned"
                        initItemListener(messageList[position])
                    }
                    MqttHeader.UNPIN_MESSAGE_REPLY -> {
                        dialogBuilder.setMessage("Message has been unpinned")
                        messageList[position].status = "Unpinned"
                        initItemListener(messageList[position])
                    }
                    MqttHeader.DELETE_MESSAGE_REPLY -> {
                        dialogBuilder.setMessage("Message has been deleted")
                        messageList.removeAt(adapterPosition)
                        notifyItemRemoved(adapterPosition)
                        notifyItemRangeChanged(adapterPosition, messageList.size)
                    }
                }
            } else {
                dialogBuilder.setTitle(R.string.failed)
                dialogBuilder.setMessage("Failed to perform action")
            }
            dialogBuilder.show()
        }
    }

    override fun getItemCount(): Int {
        return messageList.size
    }

    fun getLastIndex(): Int {
        return if (messageList.isEmpty()) 0 else messageList.size - 1
    }

    fun addMessage(message: Message) {
        messageList.add(message)
        notifyItemInserted(getLastIndex())
    }

    private fun isMine(userID: Int): Boolean {
        val pref = PreferenceManager.getDefaultSharedPreferences(context.applicationContext)
        return pref.getInt(User.COL_USER_ID, -1) == userID
    }
}