package my.edu.tarc.communechat_v2.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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

import my.edu.tarc.communechat_v2.R;
import my.edu.tarc.communechat_v2.internal.MqttHeader;
import my.edu.tarc.communechat_v2.internal.MqttHelper;
import my.edu.tarc.communechat_v2.internal.RoomSecretHelper;
import my.edu.tarc.communechat_v2.model.Chat_Room;
import my.edu.tarc.communechat_v2.model.Participant;
import my.edu.tarc.communechat_v2.model.User;

import static my.edu.tarc.communechat_v2.MainActivity.mqttHelper;

public class AddPeopleAdapter extends ArrayAdapter<User> {
    private Context mContext;
    private int mResource;
    private ArrayList<User> mObject;
    private Participant chat_room;

    static class ViewHolder {
        ImageView imageViewProfilePic;
        TextView textViewDisplayName;
        TextView textViewLastOnline;
        TextView textViewUserID;
        ImageButton buttonAdd;
        ProgressBar progressBarAdd;
    }

    public AddPeopleAdapter(@NonNull Context context, int resource, @NonNull ArrayList<User> objects, Participant participant) {
        super(context, resource, objects);
        mContext = context;
        mResource = resource;
        mObject = objects;
        this.chat_room = participant;
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        final ViewHolder holder;

        if (convertView == null) {
            convertView = inflater.inflate(mResource, parent, false);
            holder = new ViewHolder();
            holder.imageViewProfilePic = convertView.findViewById(R.id.imageView_profilePic);
            holder.textViewDisplayName = convertView.findViewById(R.id.textView_displayName);
            holder.textViewLastOnline = convertView.findViewById(R.id.textView_lastOnline);
            holder.textViewUserID = convertView.findViewById(R.id.textView_userID);
            holder.buttonAdd = convertView.findViewById(R.id.button_add);
            holder.progressBarAdd = convertView.findViewById(R.id.progressBar_addPeople);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final User user = new User();
        user.setUser_id(Objects.requireNonNull(getItem(position)).getUser_id());
        user.setDisplay_name(Objects.requireNonNull(getItem(position)).getDisplay_name());
        user.setLast_online(Objects.requireNonNull(getItem(position)).getLast_online());
        user.setStatus(Objects.requireNonNull(getItem(position)).getStatus());

        holder.textViewUserID.setText(String.valueOf(user.getUser_id()));
        holder.textViewDisplayName.setText(String.format("%s %s", user.getStatusInUnicode(), user.getDisplay_name()));
        holder.textViewLastOnline.setText(user.calculateLastOnline());

        ColorGenerator colorGenerator = ColorGenerator.MATERIAL;
        int color = colorGenerator.getColor(user.getDisplay_name());
        TextDrawable drawable = TextDrawable.builder().buildRound(user.getDisplay_name().substring(0, 1), color);
        holder.imageViewProfilePic.setImageDrawable(drawable);

        holder.progressBarAdd.setVisibility(View.GONE);
        holder.buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addUserToGroup(user, holder, position);
            }
        });
        return convertView;
    }

    private void addUserToGroup(final User user, final ViewHolder holder, final int position) {
        holder.progressBarAdd.setVisibility(View.VISIBLE);
        holder.buttonAdd.setEnabled(false);
        String header = MqttHeader.ADD_PEOPLE_TO_GROUP;
        String topic = header + "/" + user.getUser_id();

         Participant participant = new Participant();
        participant.setRoom_id(chat_room.getRoom_id());
        participant.setUser_id(user.getUser_id());

        //for sending secret key
        final Participant participant1 = new Participant();
        participant1.setRoom_id(chat_room.getRoom_id());
        mqttHelper.connectPublishSubscribe(getContext(), topic, header, participant);
        mqttHelper.getMqttClient().setCallback(new MqttCallback() {
            @Override
            public void connectionLost(Throwable cause) {

            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                MqttHelper helper = new MqttHelper();
                helper.decode(message.toString());
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());
                if (helper.getReceivedHeader().equals(MqttHeader.ADD_PEOPLE_TO_GROUP_REPLY)) {
                    if (helper.getReceivedResult().equals(MqttHeader.NO_RESULT)) {
                        alertDialog.setTitle(R.string.failed);
                        alertDialog.setMessage("Failed to add " + user.getDisplay_name() + " to the group chat");
                    } else {
                        Chat_Room chat_room1 = new Chat_Room();
                        chat_room.setRoom_id(participant1.getRoom_id());
                        RoomSecretHelper.sendRoomSecret(mContext, user, chat_room1);

                        alertDialog.setTitle(R.string.success);
                        alertDialog.setMessage("Added " + user.getDisplay_name() + " to the group chat");
                        alertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                            @Override
                            public void onDismiss(DialogInterface dialogInterface) {
                                mObject.remove(position);
                                notifyDataSetChanged();
                            }
                        });
                    }
                    alertDialog.setNeutralButton(R.string.ok, null);
                    alertDialog.show();
                    mqttHelper.unsubscribe(topic);
                    holder.progressBarAdd.setVisibility(View.GONE);
                    holder.buttonAdd.setEnabled(true);
                }
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {

            }
        });
    }
}
