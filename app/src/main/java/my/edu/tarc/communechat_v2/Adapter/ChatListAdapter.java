package my.edu.tarc.communechat_v2.Adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Objects;

import my.edu.tarc.communechat_v2.R;
import my.edu.tarc.communechat_v2.model.Chat_Room;

public class ChatListAdapter extends ArrayAdapter<Chat_Room> {

    private Context mContext;
    private int mResource;
    private ArrayList<Chat_Room> mObject;
    private SharedPreferences pref;

    static class ViewHolder {
        ImageView imageViewRoomPicture;
        TextView textViewHeader;
        TextView textViewDate;
        TextView textViewContent;
        TextView textViewRoomID;
    }

    public ChatListAdapter(@NonNull Context context, int resource, @NonNull ArrayList<Chat_Room> objects) {
        super(context, resource, objects);
        mContext = context;
        mResource = resource;
        mObject = objects;
        pref = PreferenceManager.getDefaultSharedPreferences(getContext());
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        final Chat_Room chatRoom = new Chat_Room();
        chatRoom.setRoom_id(Objects.requireNonNull(getItem(position)).getRoom_id());
        chatRoom.setOwner_id(Objects.requireNonNull(getItem(position)).getOwner_id());
        chatRoom.setLast_update(Objects.requireNonNull(getItem(position)).getLast_update());
        chatRoom.setRoom_name(Objects.requireNonNull(getItem(position)).getRoom_name());

        final ViewHolder holder;

        if (convertView == null) {
            //init convertView
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(mResource, parent, false);

            //init views
            holder = new ViewHolder();
            holder.imageViewRoomPicture = convertView.findViewById(R.id.imageView_roomPicture);
            holder.textViewHeader = convertView.findViewById(R.id.textView_header);
            holder.textViewContent = convertView.findViewById(R.id.textView_content);
            holder.textViewDate = convertView.findViewById(R.id.textView_lastDate);
            holder.textViewRoomID = convertView.findViewById(R.id.textView_roomID);

            //set tag for future use
            convertView.setTag(holder);

            //animations, optional
            Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.list_item_load);
            animation.setStartOffset(position * 100);
            convertView.setAnimation(animation);
        } else {
            //get tag, reuse resource
            holder = (ViewHolder) convertView.getTag();
        }

        StringBuilder date = new StringBuilder();
        date.append(chatRoom.getDate_created().get(Calendar.DAY_OF_MONTH))
                .append("/")
                .append(chatRoom.getDate_created().get(Calendar.MONTH))
                .append("/")
                .append(chatRoom.getDate_created().get(Calendar.YEAR))
                .append(" ")
                .append(chatRoom.calculateLastUpdate());
        holder.textViewDate.setText(date);
        holder.textViewHeader.setText(chatRoom.getRoom_name());
        holder.textViewContent.setText("");
        holder.textViewRoomID.setText(String.valueOf(chatRoom.getRoom_id()));

        return convertView;
    }
}
