package circlebinder.common.checklist;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import circlebinder.R;

public final class ChecklistItemViewHolder {

    private final View container;
    private final TextView labelName;
    private final ImageView checklist;
    private final List<TextView> circles;

    public ChecklistItemViewHolder(View convertView) {
        container = convertView;
        labelName = (TextView) convertView.findViewById(R.id.common_checklist_item_label_name);
        checklist = (ImageView) convertView.findViewById(R.id.common_checklist_item_label_icon);
        circles = new CopyOnWriteArrayList<>();
        int circleItemLabelId = R.id.common_checklist_item_circle_label;
        circles.add((TextView)
                convertView.findViewById(R.id.common_checklist_item_circle_label1).findViewById(circleItemLabelId)
        );
    }

    public View getContainer() {
        return container;
    }

    public TextView getLabelName() {
        return labelName;
    }

    public ImageView getChecklist() {
        return checklist;
    }

    public List<TextView> getCircles() {
        return circles;
    }
}
