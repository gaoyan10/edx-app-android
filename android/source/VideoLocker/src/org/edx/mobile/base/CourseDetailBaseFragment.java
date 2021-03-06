package org.edx.mobile.base;

import android.app.ActionBar;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import org.edx.mobile.R;
import org.edx.mobile.model.api.ProfileModel;
import org.edx.mobile.module.analytics.SegmentTracker;
import org.edx.mobile.module.prefs.PrefManager;
import org.edx.mobile.view.CourseDetailTabActivity;
import org.edx.mobile.module.analytics.ISegment;
import org.edx.mobile.module.analytics.SegmentFactory;
import org.edx.mobile.module.db.IDatabase;
import org.edx.mobile.module.db.impl.DatabaseFactory;
import org.edx.mobile.module.prefs.UserPrefs;
import org.edx.mobile.module.storage.IStorage;
import org.edx.mobile.module.storage.Storage;
import org.edx.mobile.util.BrowserUtil;
import org.edx.mobile.util.LogUtil;


public class CourseDetailBaseFragment extends Fragment {

    public CourseDetailTabActivity mActivity;
    protected IDatabase db;
    protected IStorage storage;
    protected ISegment segIO;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = (CourseDetailTabActivity) this.getActivity();
        initDB();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    public boolean onBackPressed(){
        return false;
    }

    public void showOpenInBrowserPanel(final String url) {
        try {
            final StringBuffer urlStringBuffer = new StringBuffer();
            if (!url.contains("http://") && !url.contains("https://")){
                urlStringBuffer.append("http://");
                urlStringBuffer.append(url);
            }else{
                urlStringBuffer.append(url);
            }
            
            if(getView()!=null){
                getView().findViewById(R.id.open_in_browser_panel).setVisibility(
                        View.VISIBLE);
                TextView openInBrowserTv = (TextView) getView().findViewById
                        (R.id.open_in_browser_btn);
                openInBrowserTv.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        BrowserUtil.open(getActivity(), 
                                urlStringBuffer.toString());
                    }
                });
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            LogUtil.log(getClass().getName(), "error in hiding Open in Browser Panel");
        }
    }

    public void hideOpenInBrowserPanel() {
        try {
            if(getView()!=null){
                if(getView().findViewById(R.id.open_in_browser_panel)!=null){
                    getView().findViewById(R.id.open_in_browser_panel).setVisibility(
                            View.GONE);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            LogUtil.log(getClass().getName(), "error in showing player");
        }
    }
    
    private void initDB() {
        storage = new Storage(getActivity());

        UserPrefs userprefs = new UserPrefs(getActivity());
        String username = null;
        if (userprefs != null) {
            ProfileModel profile = userprefs.getProfile();
            if(profile!=null){
                username =profile.username;
            }
        }
        db = DatabaseFactory.getInstance(getActivity(), 
                DatabaseFactory.TYPE_DATABASE_NATIVE, username);

        segIO = SegmentFactory.getInstance(getActivity(), 
                new SegmentTracker(getActivity()));
    }

    /**
     * Returns user's profile.
     * @return
     */
    protected ProfileModel getProfile() {
        PrefManager prefManager = new PrefManager(getActivity(), PrefManager.Pref.LOGIN);
        return prefManager.getCurrentUserProfile();
    }
}
