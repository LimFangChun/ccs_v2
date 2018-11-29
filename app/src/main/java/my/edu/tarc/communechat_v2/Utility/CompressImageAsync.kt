package my.edu.tarc.communechat_v2.Utility

import android.content.Context
import android.net.Uri
import android.os.AsyncTask
import android.support.v7.widget.RecyclerView
import my.edu.tarc.communechat_v2.Adapter.ChatRoomRecyclerAdapter
import my.edu.tarc.communechat_v2.MainActivity
import my.edu.tarc.communechat_v2.internal.MqttHeader
import my.edu.tarc.communechat_v2.model.Message
import java.lang.ref.WeakReference

class CompressImageAsync(context: Context,
                         val message: Message,
                         val messageArrayList: ArrayList<Message>,
                         val adapter: ChatRoomRecyclerAdapter,
                         recyclerView: RecyclerView,
                         val topic: String?,
                         val filePath: Uri)
    : AsyncTask<Void, Boolean, Boolean>() {
    val recyclerView: WeakReference<RecyclerView> = WeakReference(recyclerView)
    private val weakReference: WeakReference<Context> = WeakReference(context)

    override fun doInBackground(vararg p0: Void?): Boolean {
        //compress image
        message.media = myUtil.getByteArray(weakReference.get()!!, filePath)

        return true
    }

    override fun onPostExecute(result: Boolean?) {
        super.onPostExecute(result)

        val header = MqttHeader.SEND_ROOM_IMAGE
        MainActivity.mqttHelper.connectPublish(weakReference.get(), topic, header, message)

        messageArrayList.add(message)
        adapter.notifyItemInserted(adapter.getLastIndex())
        recyclerView.get()!!.smoothScrollToPosition(adapter.getLastIndex())
    }
}