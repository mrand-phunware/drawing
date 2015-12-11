package com.codemolly.drawing;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.wearable.view.CircledImageView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by mrand on 10/1/15.
 */
public class OptionsActivity extends Activity {
    private ViewPager mViewPager;
    public static final int RESULT_CODE_SHARE = 101;
    public static final int RESULT_CODE_CLEAR = 102;
    public static final int RESULT_CODE_CLOSE = 103;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_option);
        mViewPager = (ViewPager)findViewById(R.id.viewPager);
        mViewPager.setAdapter(new SettingViewPagerAdapter(this));
    }

    class SettingViewPagerAdapter extends PagerAdapter {
        private Context context;
        private LayoutInflater inflater;

        public SettingViewPagerAdapter(Context context) {
            this.context = context;
            this.inflater = LayoutInflater.from(context);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View view = null;
            if (position == 0) {
                view = buildSendView();
            } else if (position == 1) {
                view = buildClearView();
            } else if (position == 2) {
                view = buildCloseView();
            }
            container.addView(view);
            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return object == view;
        }

        private View buildSendView() {
            View view = inflater.inflate(R.layout.action_button, null);
            TextView title = (TextView) view.findViewById(R.id.action_title);
            title.setText("Send");
            final CircledImageView button = (CircledImageView) view.findViewById(R.id.action_image);
            button.setImageResource(R.drawable.ic_send_white_36dp);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    setResult(RESULT_CODE_SHARE, null);
                    finish();
                }
            });
            return view;
        }

        private View buildClearView() {
            View view = inflater.inflate(R.layout.action_button, null);
            TextView title = (TextView) view.findViewById(R.id.action_title);
            title.setText("Clear");
            final CircledImageView button = (CircledImageView) view.findViewById(R.id.action_image);
            button.setImageResource(R.drawable.ic_clear_white_36dp);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    setResult(RESULT_CODE_CLEAR, null);
                    finish();
                }
            });
            return view;
        }

        private View buildCloseView() {
            View view = inflater.inflate(R.layout.action_button, null);
            TextView title = (TextView) view.findViewById(R.id.action_title);
            title.setText("Back");
            final CircledImageView button = (CircledImageView) view.findViewById(R.id.action_image);
            button.setImageResource(R.drawable.ic_create_white_36dp);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    finish();
                }
            });
            return view;
        }
    }
}
