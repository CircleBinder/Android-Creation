package circlebinder.common.circle;

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
import net.ichigotake.common.content.OnBeforeLoadingListener;
import net.ichigotake.common.os.BundleMerger;

import circlebinder.common.Legacy;
import circlebinder.common.web.WebViewClient;
import circlebinder.common.web.WebViewContainer;
import circlebinder.common.event.Circle;

import net.ichigotake.common.view.ActionProvider;
import net.ichigotake.common.view.MenuPresenter;
import net.ichigotake.common.view.ReloadActionProvider;

import circlebinder.common.app.BaseFragment;
import circlebinder.R;
import circlebinder.common.checklist.ChecklistSelectActionProvider;

public final class CircleDetailFragment extends BaseFragment implements Legacy {

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
    private String currentUrl;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        circle = BundleMerger.merge(getArguments(), savedInstanceState).getParcelable(KEY_CIRCLE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.common_circle_detail, parent, false);
        WebView webView = (WebView)view.findViewById(R.id.common_circle_detail_web_view);
        WebViewClient client = new WebViewClient(webView);
        client.setOnBeforeLoadingListener(new OnBeforeLoadingListener() {
            @Override
            public void onBeforeLoading(String url) {
                currentUrl = url;
                getActivity().invalidateOptionsMenu();
            }
        });
        webView.setWebViewClient(client);
        webContainer = new WebViewContainer(webView);
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        MenuPresenter presenter = new MenuPresenter(menu, inflater);
        presenter.inflate(R.menu.share, R.id.menu_share)
                .setActionProvider(new ActionProvider(getActivity(), new ActionProvider.OnClickListener() {
                    @Override
                    public void onClick() {
                        new ActivityTripper(
                                getActivity(),
                                IntentUtils.shareText(circle.getName(), currentUrl)
                        ).trip();
                    }
                }))
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);
        presenter.inflate(R.menu.open_browser, R.id.menu_open_browser)
                .setActionProvider(new ActionProvider(getActivity(), new ActionProvider.OnClickListener() {
                    @Override
                    public void onClick() {
                        new ActivityTripper(getActivity(), IntentUtils.openLink(currentUrl)).trip();
                    }
                }))
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);
        presenter.inflate(R.menu.reload, R.id.menu_reload)
                .setActionProvider(new ReloadActionProvider(getActivity(), webContainer))
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);
        presenter.inflate(R.menu.checklist_selector, R.id.menu_checklist_selector)
                .setActionProvider(new ChecklistSelectActionProvider(getActivity(), circle));
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        webContainer.load(getLink(circle));
        postEvent();
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

    private void postEvent() {
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
