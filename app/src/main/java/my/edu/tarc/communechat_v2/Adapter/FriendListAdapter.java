package my.edu.tarc.communechat_v2.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Objects;

import my.edu.tarc.communechat_v2.R;
import my.edu.tarc.communechat_v2.model.User;

public class FriendListAdapter extends ArrayAdapter<User> {
    private Context mContext;
    private int mResource;

    static class ViewHolder {
        ImageView imageViewProfilePic;
        TextView textViewUserID;
        TextView textViewUsername;
        TextView textViewDescription;
    }

    public FriendListAdapter(@NonNull Context context, int resource, @NonNull ArrayList<User> objects) {
        super(context, resource, objects);
        mContext = context;
        mResource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ViewHolder holder;
        LayoutInflater inflater = LayoutInflater.from(mContext);
        if(convertView == null){
            convertView = inflater.inflate(mResource, parent, false);

            holder = new ViewHolder();
            holder.imageViewProfilePic = convertView.findViewById(R.id.imageView_profilePic);
            holder.textViewUserID = convertView.findViewById(R.id.textView_userID);
            holder.textViewDescription = convertView.findViewById(R.id.textView_description);
            holder.textViewUsername = convertView.findViewById(R.id.textView_username);

            convertView.setTag(holder);
        }else {
            holder = (ViewHolder)convertView.getTag();
        }

        User user = new User();
        user.setUser_id(Objects.requireNonNull(getItem(position)).getUser_id());
        user.setUsername(Objects.requireNonNull(getItem(position)).getUsername());
        user.setLast_online(Objects.requireNonNull(getItem(position)).getLast_online());
        user.setStatus(Objects.requireNonNull(getItem(position)).getStatus());

        long lastOnlineAgo = user.getLast_online().getTime() - System.currentTimeMillis();
        StringBuilder temp = new StringBuilder();
        if (lastOnlineAgo / 1000 / 60 / 60 / 24 / 30 != 0) {
            temp.append(lastOnlineAgo / 1000 / 60 / 60 / 24 / 30).append(" month(s) ago");
        } else if (lastOnlineAgo / 1000 / 60 / 60 / 24 != 0) {
            temp.append(lastOnlineAgo / 1000 / 60 / 60 / 24).append(" day(s) ago");
        } else if (lastOnlineAgo / 1000 / 60 / 60 != 0) {
            temp.append(lastOnlineAgo / 1000 / 60 / 60 / 24).append(" hour(s) ago");
        } else if (lastOnlineAgo / 1000 / 60 / 60 / 24 != 0) {
            temp.append(lastOnlineAgo / 1000 / 60).append(" minute(s) ago");
        }else {
            temp.append(lastOnlineAgo / 1000).append(" second(s) ago");
        }

        holder.textViewUserID.setText(user.getUser_id());
        holder.textViewUsername.setText(user.getUsername());
        holder.textViewDescription.setText(temp.toString());

        return convertView;
    }
}
