package my.edu.tarc.communechat_v2.Adapter;

import android.annotation.SuppressLint;
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
import my.edu.tarc.communechat_v2.model.FindFriendAdapterClass;

public class FindFriendAdapter extends ArrayAdapter<FindFriendAdapterClass> {
    private Context mContext;
    private int mResource;

    public FindFriendAdapter(@NonNull Context context, int resource, @NonNull ArrayList<FindFriendAdapterClass> objects) {
        super(context, resource, objects);
        mContext = context;
        mResource = resource;
    }

    @SuppressLint("ViewHolder")
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        FindFriendAdapterClass temp = new FindFriendAdapterClass();
        temp.setImageID(Objects.requireNonNull(getItem(position)).getImageID());
        temp.setHeader(Objects.requireNonNull(getItem(position)).getHeader());
        temp.setDescription(Objects.requireNonNull(getItem(position)).getDescription());

        LayoutInflater inflater = LayoutInflater.from(mContext);
        convertView = inflater.inflate(mResource, parent, false);

        ImageView image = (ImageView)convertView.findViewById(R.id.imageView_find);
        switch (temp.getImageID()){
            case 1:
                image.setImageResource(R.drawable.ic_group_work_black_24dp);
                break;
            case 2:
                image.setImageResource(R.drawable.ic_class_black_24dp);
                break;
            case 3:
                image.setImageResource(R.drawable.ic_home_black_24dp);
                break;
            case 4:
                image.setImageResource(R.drawable.ic_cake_black_24dp);
                break;
            case 5:
                image.setImageResource(R.drawable.ic_location_on_black_24dp);
                break;
            case 6:
                image.setImageResource(R.drawable.ic_shuffle_black_24dp);
                break;
            case 7:
                image.setImageResource(R.drawable.ic_search);
                break;
        }

        TextView textViewHeader = convertView.findViewById(R.id.textView_header);
        textViewHeader.setText(temp.getHeader());

        TextView textViewDesc = convertView.findViewById(R.id.textView_description);
        textViewDesc.setText(temp.getDescription());

        return convertView;
    }
}
