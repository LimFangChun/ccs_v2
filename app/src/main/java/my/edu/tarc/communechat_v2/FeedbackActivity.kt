package my.edu.tarc.communechat_v2

import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import kotlinx.android.synthetic.main.activity_feedback.*
import my.edu.tarc.communechat_v2.MainActivity.mqttHelper
import my.edu.tarc.communechat_v2.internal.MqttHeader
import my.edu.tarc.communechat_v2.internal.MqttHelper
import my.edu.tarc.communechat_v2.model.Feedback
import my.edu.tarc.communechat_v2.model.User
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken
import org.eclipse.paho.client.mqttv3.MqttCallback
import org.eclipse.paho.client.mqttv3.MqttMessage

class FeedbackActivity : AppCompatActivity() {
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.feedback_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        val itemID = item?.itemId
        when (itemID) {
            R.id.nav_submit -> submitFeedback()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_feedback)

        assert(supportActionBar != null)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        setProgressBarVisibility(View.INVISIBLE)

        ratingBar_feedback.setOnRatingBarChangeListener { _, _, _ ->
            if (ratingBar_feedback.rating != 0.0F) {
                textView_rate.error = null
            }
        }
    }

    private fun submitFeedback() {
        val pref = PreferenceManager.getDefaultSharedPreferences(applicationContext)
        setProgressBarVisibility(View.VISIBLE)

        if (ratingBar_feedback.rating == 0.0F) {
            textView_rate.error = "Please rate before submitting feedback"
            return
        }

        val feedback = Feedback()
        feedback.rate = ratingBar_feedback.rating.toDouble()
        feedback.sender_id = pref.getInt(User.COL_USER_ID, -1)
        feedback.message = editText_feedback.text.toString()

        //send to server
        val header = MqttHeader.SEND_FEEDBACK
        val topic = "$header/${feedback.sender_id}"

        mqttHelper.connectPublishSubscribe(this@FeedbackActivity, topic, header, feedback)
        mqttHelper.mqttClient.setCallback(feedbackCallback)
    }

    private val feedbackCallback: MqttCallback = object : MqttCallback {
        override fun messageArrived(topic: String?, message: MqttMessage?) {
            val helper = MqttHelper()
            helper.decode(message.toString())
            if (helper.receivedHeader == MqttHeader.SEND_FEEDBACK_REPLY) {
                mqttHelper.unsubscribe(topic)
                val alertDialog = AlertDialog.Builder(this@FeedbackActivity)
                alertDialog.setNeutralButton(R.string.ok, null)
                alertDialog.setOnDismissListener {
                    finish()
                }

                if (helper.receivedResult == MqttHeader.SUCCESS) {
                    alertDialog.setTitle(R.string.success)
                    alertDialog.setMessage("Your feedback has been recorded. Thank you for your feedback")
                } else {
                    alertDialog.setTitle(R.string.failed)
                    alertDialog.setMessage("Error occurred while recording your feedback. Please try again later")
                }
                alertDialog.show()
                setProgressBarVisibility(View.INVISIBLE)
            }
        }

        override fun connectionLost(cause: Throwable?) {
        }

        override fun deliveryComplete(token: IMqttDeliveryToken?) {
        }
    }

    private fun setProgressBarVisibility(visibility: Int) {
        progressBar_feedback.visibility = visibility
    }
}
