package Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.example.jejufarmreceiptproject.Activity.MainActivity;
import com.example.jejufarmreceiptproject.R;

import java.text.DecimalFormat;
import java.util.ArrayList;

import Entity.BasketForm;
import Entity.CactusForm;

public class BasketListViewAdapter extends BaseAdapter {
    private ArrayList<BasketForm> list;

    public BasketListViewAdapter() {
        list = new ArrayList<>();
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        final Context context = viewGroup.getContext();
        BasketForm listItem = list.get(i);

        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.control_basketlistview, viewGroup, false);
        }
        DecimalFormat df = new DecimalFormat("#,###");
        TextView title = (TextView) view.findViewById(R.id.titleText);
        TextView count = (TextView) view.findViewById(R.id.countText);
        TextView price = (TextView) view.findViewById(R.id.priceText);
        TextView total = (TextView) view.findViewById(R.id.totalText);
        Button delButton = (Button) view.findViewById(R.id.deleteButton);

        title.setText(String.valueOf(listItem.getTitle()));
        count.setText(df.format(listItem.getCount()));
        price.setText(df.format(listItem.getPrice()));
        total.setText(df.format(listItem.getTotal()));
        delButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View parentRow = (View) v.getParent();
                ListView listView = (ListView) parentRow.getParent();
                final int position = listView.getPositionForView(parentRow);
                list.remove(position);
                notifyDataSetChanged();
                ((MainActivity)MainActivity.mContext).BasketListViewChagned();
            }
        });

        return view;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    public ArrayList<BasketForm> GetInstance() {
        return list;
    }

    @Override
    public Object getItem(int i) {
        return list.get(i).getTitle() + "    " + list.get(i).getPrice();
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    public void clear() {
        list.clear();
        notifyDataSetChanged();
    }

    public void append(String title, int count, int price, int total) {
        list.add(new BasketForm(title, count, price, total));
        notifyDataSetChanged();
    }


}
