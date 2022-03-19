package com.dharma.shopinar;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.viewpager.widget.PagerAdapter;

import com.airbnb.lottie.LottieAnimationView;

import java.util.List;

public class WelcomeViewPagerAdapter extends PagerAdapter {

   Context mContext ;
   List<ScreenItem> mListScreen;

    public WelcomeViewPagerAdapter(Context mContext, List<ScreenItem> mListScreen) {
        this.mContext = mContext;
        this.mListScreen = mListScreen;
    }


    @Override
    public Object instantiateItem(ViewGroup container, int position) {

        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layoutScreen = inflater.inflate(R.layout.layout_screen,null);

        LottieAnimationView anim = (LottieAnimationView) layoutScreen.findViewById(R.id.welcome_slide_anim);
        anim.setAnimation(mListScreen.get(position).getWelcomeSlideAnim());
//        ImageView imgSlide = layoutScreen.findViewById(R.id.intro_img);
        TextView title = layoutScreen.findViewById(R.id.intro_title);
        TextView description = layoutScreen.findViewById(R.id.intro_description);

        title.setText(mListScreen.get(position).getTitle());
        description.setText(mListScreen.get(position).getDescription());
//        imgSlide.setImageResource(mListScreen.get(position).getScreenImg());

        container.addView(layoutScreen);

        return layoutScreen;





    }

    @Override
    public int getCount() {
        return mListScreen.size();
    }

    @Override
    public boolean isViewFromObject( View view,  Object o) {
        return view == o;
    }

    @Override
    public void destroyItem( ViewGroup container, int position,  Object object) {

        container.removeView((View)object);

    }
}
