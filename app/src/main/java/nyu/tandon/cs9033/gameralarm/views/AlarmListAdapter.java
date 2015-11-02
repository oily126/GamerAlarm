package nyu.tandon.cs9033.gameralarm.views;

import android.content.Context;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import nyu.tandon.cs9033.gameralarm.R;

/**
 * Created by oily on 10/30/2015.
 */
public class AlarmListAdapter extends BaseAdapter {
    private Context context;
    private List<Map<String, Object>> listItems;
    private LayoutInflater listContainer;
    public final class AlarmItemView {
        public TextView alarmTime;
        public TextView alarmWeek;
        public TextView alarmMode;
        public SwitchCompat enableAlarm;
    }

    public AlarmListAdapter(Context context, List<Map<String, Object>> listItems) {
        this.context = context;
        listContainer = LayoutInflater.from(this.context);
        this.listItems = listItems;
    }

    @Override
    public int getCount() {
            return listItems.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final int selectId = position;
        AlarmItemView alarmItems = null;
        if (convertView == null) {
            alarmItems = new AlarmItemView();
            convertView = listContainer.inflate(R.layout.alarm_listitem, null);
            alarmItems.alarmTime = (TextView) convertView.findViewById(R.id.alarmTime);
            alarmItems.alarmWeek = (TextView) convertView.findViewById(R.id.alarmWeek);
            alarmItems.alarmMode = (TextView) convertView.findViewById(R.id.alarmMode);
            alarmItems.enableAlarm = (SwitchCompat) convertView.findViewById(R.id.enableAlarm);
            convertView.setTag(alarmItems);
        } else {
            alarmItems = (AlarmItemView) convertView.getTag();
        }

        alarmItems.alarmTime.setText((String) listItems.get(position).get("alarmTime"));
        alarmItems.alarmWeek.setText((String) listItems.get(position).get("alarmWeek"));
        alarmItems.alarmMode.setText((String) listItems.get(position).get("alarmMode"));
        alarmItems.enableAlarm.setChecked((Boolean) listItems.get(position).get("enableAlarm"));

        alarmItems.enableAlarm.setOnCheckedChangeListener(new Switch.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            //TODO enable or disable the corresponding alarm
            }
        });

        return convertView;
    }
}
