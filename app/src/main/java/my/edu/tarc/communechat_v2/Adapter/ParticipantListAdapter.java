package my.edu.tarc.communechat_v2.Adapter;

import android.content.Context;
import android.graphics.Color;
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
import my.edu.tarc.communechat_v2.model.Participant;

public class ParticipantListAdapter extends ArrayAdapter<Participant> {
    private Context mContext;
    private int mResource;

    public ParticipantListAdapter(@NonNull Context context, int resource, @NonNull ArrayList<Participant> objects) {
        super(context, resource, objects);
        mContext = context;
        mResource = resource;
    }

    static class ViewHolder {
        ImageView imageViewProfilePic;
        TextView textViewUserID;
        TextView textViewDisplayName;
        TextView textViewLastOnline;
        TextView textViewRole;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ViewHolder holder;
        LayoutInflater inflater = LayoutInflater.from(mContext);

        if (convertView == null) {
            convertView = inflater.inflate(mResource, parent, false);

            //init views
            holder = new ViewHolder();
            holder.imageViewProfilePic = convertView.findViewById(R.id.imageView_profilePic);
            holder.textViewUserID = convertView.findViewById(R.id.textView_userID);
            holder.textViewDisplayName = convertView.findViewById(R.id.textView_displayName);
            holder.textViewLastOnline = convertView.findViewById(R.id.textView_lastOnline);
            holder.textViewRole = convertView.findViewById(R.id.textView_role);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final Participant participant = new Participant();
        participant.setUser_id(Objects.requireNonNull(getItem(position)).getUser_id());
        participant.setDisplay_name(Objects.requireNonNull(getItem(position)).getDisplay_name());
        participant.setRole(Objects.requireNonNull(getItem(position)).getRole());
        participant.setLast_online(Objects.requireNonNull(getItem(position)).getLast_online());
        participant.setStatus(Objects.requireNonNull(getItem(position)).getStatus());

        //put values into views
        ColorGenerator colorGenerator = ColorGenerator.MATERIAL;
        int color = colorGenerator.getColor(participant.getDisplay_name());
        TextDrawable drawable = TextDrawable.builder().buildRound(participant.getDisplay_name().substring(0, 1), color);
        holder.imageViewProfilePic.setImageDrawable(drawable);

        holder.textViewUserID.setText(String.valueOf(participant.getUser_id()));
        holder.textViewDisplayName.setText(String.format("%s %s", participant.getStatusInUnicode(), participant.getDisplay_name()));
        if (participant.getRole().equals("Admin")) {
            holder.textViewRole.setTextColor(Color.RED);
        }
        holder.textViewRole.setText(participant.getRole());

        String lastOnline = "Last online: " +
                participant.getLast_online().get(Calendar.DAY_OF_MONTH) +
                "/" +
                participant.getLast_online().get(Calendar.MONTH) +
                "/" +
                participant.getLast_online().get(Calendar.YEAR) +
                "\n" +
                participant.calculateLastOnline();
        holder.textViewLastOnline.setText(lastOnline);

        return convertView;
    }
}
