package circlebinder.creation.circle;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import com.dmitriy.tarasov.android.intents.IntentUtils;

import net.ichigotake.common.app.ActivityTripper;
import net.ichigotake.common.app.FragmentFactory;
import net.ichigotake.common.app.OnPageChangeListener;
import net.ichigotake.common.os.BundleMerger;

import circlebinder.common.Legacy;
import circlebinder.common.checklist.ChecklistColor;
import circlebinder.common.checklist.ChecklistPopupSelector;
import circlebinder.common.circle.WebViewContainer;
import circlebinder.common.event.Circle;
import circlebinder.common.event.CircleBuilder;

import net.ichigotake.common.view.ActionProvider;
import net.ichigotake.common.view.ReloadActionProvider;

import circlebinder.creation.app.BaseFragment;
import circlebinder.R;
import circlebinder.creation.event.CircleTable;

public final class CircleDetailFragment extends BaseFragment
        implements OnPageChangeListener, Legacy {

    public static FragmentFactory<CircleDetailFragment> factory(Circle circle) {
        return new CircleDetailFragmentFactory(circle);
    }

    private static class CircleDetailFragmentFactory implements FragmentFactory<CircleDetailFragment> {

        private final Circle circle;

        public CircleDetailFragmentFactory(Circle circle) {
            this.circle = circle;
        }

        @Override
        public CircleDetailFragment create() {
            return newInstance(circle);
        }
    }

    private static CircleDetailFragment newInstance(Circle circle) {
        CircleDetailFragment fragment = new CircleDetailFragment();
        Bundle args = new Bundle();
        args.putParcelable(KEY_CIRCLE, circle);
        fragment.setArguments(args);
        return fragment;
    }

    private static final String KEY_CIRCLE = "circle";
    private Circle circle;
    private WebViewContainer webContainer;
    private View checklistView;
    private String currentUrl;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        circle = BundleMerger.merge(getArguments(), savedInstanceState).getParcelable(KEY_CIRCLE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.circle_detail, parent, false);
        WebView webView = (WebView)view.findViewById(R.id.circle_detail_web_view);
        webContainer = new WebViewContainer(webView);
        webContainer.setOnUrlLoadListener((url) -> this.currentUrl = url);
        webContainer.load(getLink(circle));
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.circle_web, menu);
        inflater.inflate(R.menu.open_browser, menu);
        MenuItem shareItem = menu.findItem(R.id.menu_circle_web_share);
        shareItem.setActionProvider(new ActionProvider(getActivity(),() ->
                    new ActivityTripper(
                            getActivity(),
                            IntentUtils.shareText(circle.getName(), currentUrl)
                    ).trip()
                ));
        menu.findItem(R.id.menu_open_browser)
                .setActionProvider(new ActionProvider(getActivity(), () ->
                        new ActivityTripper(getActivity(), IntentUtils.openLink(currentUrl)).trip()
                ));
        inflater.inflate(R.menu.reload, menu);
        menu.findItem(R.id.menu_reload)
                .setActionProvider(new ReloadActionProvider(getActivity(), webContainer));
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        checklistView = getView().findViewById(R.id.circle_detail_checklist);
        final ChecklistPopupSelector checklistSelector =
                new ChecklistPopupSelector(getActivity(), checklistView);
        checklistView.setBackgroundResource(circle.getChecklistColor().getDrawableResource());
        checklistSelector.setOnItemClickListener(item -> {
            updateChecklist(item);
            checklistSelector.dismiss();
        });
        checklistView.setOnClickListener(v -> {
            if (checklistSelector.isShowing()) {
                checklistSelector.dismiss();
            } else {
                checklistSelector.show();
            }
        });

    }

    private String getLink(Circle circle) {
        if (circle.getLinks().isEmpty()) {
            return "https://google.co.jp/search?q="
                    + "\"" + circle.getPenName() + "\""
                    + "%20"
                    + "\"" + circle.getName() + "\"";
        }
        return circle.getLinks().get(0).getUri().toString();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(KEY_CIRCLE, circle);
    }

    private void updateChecklist(ChecklistColor checklistColor) {
        checklistView.setBackgroundResource(checklistColor.getDrawableResource());
        CircleTable.setChecklist(circle, checklistColor);
        circle = new CircleBuilder(circle)
                .setChecklistColor(checklistColor)
                .build();
    }

    @Override
    public void active() {
        webContainer.reload();
        restoreActionBar();
    }

    private void restoreActionBar() {
        if (getActivity() == null) {
            return;
        }
        getActivity().invalidateOptionsMenu();
        if (getActivity() instanceof OnCirclePageChangeListener) {
            ((OnCirclePageChangeListener)getActivity()).onCirclePageChanged(circle);
        }
    }

    @Override
    public void onDestroy() {
        if (webContainer != null) {
            webContainer.onDestroy();
        }
        super.onDestroy();
    }

}
