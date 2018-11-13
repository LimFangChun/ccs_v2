package my.edu.tarc.communechat_v2.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.util.ArrayList;
import java.util.Objects;

import my.edu.tarc.communechat_v2.ChatRoomActivity;
import my.edu.tarc.communechat_v2.R;
import my.edu.tarc.communechat_v2.internal.MqttHeader;
import my.edu.tarc.communechat_v2.internal.MqttHelper;
import my.edu.tarc.communechat_v2.internal.RoomSecretHelper;
import my.edu.tarc.communechat_v2.model.Chat_Room;
import my.edu.tarc.communechat_v2.model.Friendship;
import my.edu.tarc.communechat_v2.model.Participant;
import my.edu.tarc.communechat_v2.model.Student;
import my.edu.tarc.communechat_v2.model.User;

import static my.edu.tarc.communechat_v2.MainActivity.mqttHelper;

public class FriendListAdapter extends ArrayAdapter<Student> {
    private Context mContext;
    private int mResource;
    private SharedPreferences pref;

    static class ViewHolder {
        ImageView imageViewProfilePic;
        TextView textViewUserID;
        TextView textViewUsername;
        TextView textViewDescription;
        ImageButton buttonChat;
        ProgressBar progressBarChat;
    }

    public FriendListAdapter(@NonNull Context context, int resource, @NonNull ArrayList<Student> objects) {
        super(context, resource, objects);
        mContext = context;
        mResource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        final ViewHolder holder;
        LayoutInflater inflater = LayoutInflater.from(mContext);
        if (convertView == null) {
            convertView = inflater.inflate(mResource, parent, false);

            holder = new ViewHolder();
            holder.imageViewProfilePic = convertView.findViewById(R.id.imageView_profilePic);
            holder.textViewUserID = convertView.findViewById(R.id.textView_userID);
            holder.textViewDescription = convertView.findViewById(R.id.textView_description);
            holder.textViewUsername = convertView.findViewById(R.id.textView_username);
            holder.buttonChat = convertView.findViewById(R.id.button_chat);
            holder.progressBarChat = convertView.findViewById(R.id.progressBar_chat);
            holder.progressBarChat.setVisibility(View.GONE);

            convertView.setTag(holder);

            pref = PreferenceManager.getDefaultSharedPreferences(getContext().getApplicationContext());
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final Student user = new Student();
        user.setUser_id(Objects.requireNonNull(getItem(position)).getUser_id());
        user.setDisplay_name(Objects.requireNonNull(getItem(position)).getDisplay_name());
        user.setLast_online(Objects.requireNonNull(getItem(position)).getLast_online());
        user.setStatus(Objects.requireNonNull(getItem(position)).getStatus());
        user.setCourse(Objects.requireNonNull(getItem(position)).getCourse());
        user.setAcademic_year(Objects.requireNonNull(getItem(position)).getAcademic_year());
        user.setTutorial_group(Objects.requireNonNull(getItem(position)).getTutorial_group());

        String status;
        if (user.getStatus().equals("Offline")) {
            status = "\uD83D\uDD34";
        } else {
            status = "\uD83D\uDD35";
        }

        //github: https://github.com/amulyakhare/TextDrawable/blob/master/README.md
        ColorGenerator colorGenerator = ColorGenerator.MATERIAL;
        int color = colorGenerator.getColor(user.getDisplay_name());
        TextDrawable drawable = TextDrawable.builder().buildRound(user.getDisplay_name().substring(0, 1), color);
        holder.imageViewProfilePic.setImageDrawable(drawable);

        holder.textViewUserID.setText(String.valueOf(user.getUser_id()));
        holder.textViewUsername.setText(Html.fromHtml(status + user.getDisplay_name()));

        StringBuilder temp = new StringBuilder();
        holder.textViewDescription.setText(temp
                .append(user.getCourse()).append(user.getAcademic_year())
                .append(" ").append("G").append(user.getTutorial_group())
                .append(" - ").append(user.calculateLastOnline()));

        holder.buttonChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getChatRoomID(holder, user);
            }
        });

        return convertView;
    }

    private void getChatRoomID(final ViewHolder holder, final Student user) {
        holder.progressBarChat.setVisibility(View.VISIBLE);
        holder.buttonChat.setEnabled(false);

        String header = MqttHeader.CREATE_CHAT_ROOM;
        String topic = header + "/" + user.getUser_id();
        Friendship friendship = new Friendship();
        friendship.setUser_id(pref.getInt(User.COL_USER_ID, -1));
        friendship.setFriend_id(user.getUser_id());
        mqttHelper.connectPublishSubscribe(getContext(), topic, header, friendship);
        mqttHelper.getMqttClient().setCallback(new MqttCallback() {
            @Override
            public void connectionLost(Throwable cause) {

            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                MqttHelper helper = new MqttHelper();
                helper.decode(message.toString());
                if (helper.getReceivedHeader().equals(MqttHeader.CREATE_CHAT_ROOM_REPLY)) {
                    if (helper.getReceivedResult().equals(MqttHeader.NO_RESULT)) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                        builder.setTitle(R.string.failed);
                        builder.setMessage(R.string.failed_create_chat_room);
                        builder.setNeutralButton(R.string.ok, null);
                        builder.show();
                    } else {
                        Chat_Room chat_room = new Chat_Room();
                        chat_room.setRoom_id(Integer.parseInt(helper.getReceivedResult()));
                        Student user1 = new Student();
                        user1.setUser_id(user.getUser_id());
                        RoomSecretHelper.sendRoomSecret(mContext.getApplicationContext(),user1, chat_room);
                        Intent intent = new Intent(getContext(), ChatRoomActivity.class);
                        intent.putExtra(Chat_Room.COL_ROOM_ID, Integer.parseInt(helper.getReceivedResult()));
                        intent.putExtra(Participant.COL_ROLE, "Admin");
                        getContext().startActivity(intent);
                    }
                    holder.buttonChat.setEnabled(true);
                    holder.progressBarChat.setVisibility(View.GONE);
                    mqttHelper.unsubscribe(topic);
                }
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {

            }
        });
    }
}
