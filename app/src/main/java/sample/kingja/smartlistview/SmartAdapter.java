package sample.kingja.smartlistview;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Description:TODO
 * Create Time:2019/4/14 14:46
 * Author:KingJA
 * Email:kingjavip@gmail.com
 */
public class SmartAdapter extends BaseAdapter {
    private Context context;
    private List<String> list;

    public SmartAdapter(Context context, List<String> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int i) {
        return list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = View
                    .inflate(context, R.layout.item_names, null);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        String name = list.get(position);
        viewHolder.tv_name.setText(name);
        return convertView;
    }

    public class ViewHolder {
        public final View root;
        public TextView tv_name;

        public ViewHolder(View root) {
            this.root = root;
            tv_name = root.findViewById(R.id.tv_name);
        }
    }
}
