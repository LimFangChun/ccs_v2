package my.edu.tarc.communechat_v2.Adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;

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
        TextView textViewRole;
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
        chatRoom.setRole(Objects.requireNonNull(getItem(position)).getRole());

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
            holder.textViewRole = convertView.findViewById(R.id.textView_role);

            //set tag for future use
            convertView.setTag(holder);
        } else {
            //get tag, reuse resource
            holder = (ViewHolder) convertView.getTag();
        }

        StringBuilder date = new StringBuilder();
        date.append(chatRoom.getLast_update().get(Calendar.DAY_OF_MONTH))
                .append("/")
                .append(chatRoom.getLast_update().get(Calendar.MONTH) + 1)
                .append("/")
                .append(chatRoom.getLast_update().get(Calendar.YEAR))
                .append(" ")
                .append(chatRoom.calculateLastUpdate());
        holder.textViewDate.setText(date);
        holder.textViewHeader.setText(chatRoom.getRoom_name());
        holder.textViewContent.setText("");
        holder.textViewRoomID.setText(String.valueOf(chatRoom.getRoom_id()));
        holder.textViewRole.setText(chatRoom.getRole());

        //make default image for each chat room
        //github: https://github.com/amulyakhare/TextDrawable/blob/master/README.md
        ColorGenerator colorGenerator = ColorGenerator.MATERIAL;
        int color;
        TextDrawable drawable;
        if ("".equals(chatRoom.getRoom_name())) {
            color = colorGenerator.getRandomColor();
            drawable = TextDrawable.builder().buildRound(String.valueOf(chatRoom.getRoom_id()), color);
        } else {
            color = colorGenerator.getColor(chatRoom.getRoom_name());
            drawable = TextDrawable.builder().buildRound(chatRoom.getRoom_name().substring(0, 1), color);
        }
        holder.imageViewRoomPicture.setImageDrawable(drawable);

        return convertView;
    }
}
