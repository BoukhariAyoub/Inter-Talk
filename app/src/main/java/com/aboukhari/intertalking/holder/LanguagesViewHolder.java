package com.aboukhari.intertalking.holder;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.aboukhari.intertalking.R;
import com.aboukhari.intertalking.model.Language;

/**
 * Created by aboukhari on 06/08/2015.
 */
public class LanguagesViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    Language mLanguage;
    TextView mTextView;
    ImageView mCrossImageView;
    ImageView mLevelImageView;
    Context context;

    int[] mIcons = new int[]{R.mipmap.ic_begginer, R.mipmap.ic_intermediate, R.mipmap.ic_advanced, R.mipmap.ic_native};

    public LanguagesViewHolder(Context context, View itemView,boolean known) {
        super(itemView);

        this.context = context;
        mTextView = (TextView) itemView.findViewById(R.id.tv_language);
        mCrossImageView = (ImageView) itemView.findViewById(R.id.iv_cross);
        mLevelImageView = (ImageView) itemView.findViewById(R.id.iv_level);
        mCrossImageView.setOnClickListener(this);
        mLevelImageView.setOnClickListener(this);
        if(!known){
            mLevelImageView.setVisibility(View.GONE);
        }
    }

    public LanguagesViewHolder bindLanguage(Language language, int position) {
        mLanguage = language;
        mTextView.setText(language.getName());
        mLevelImageView.setImageDrawable(ContextCompat.getDrawable(context, mIcons[language.getLevel()]));
        mCrossImageView.setTag(position);
        mLevelImageView.setTag(position);
        return this;
    }


    @Override
    public void onClick(View v) {
        if (v == mCrossImageView) {
            mCrossImageView.setTag(getAdapterPosition());
        }

        if (v == mLevelImageView) {
            mLevelImageView.setTag(getAdapterPosition());
        }

    }

    public ImageView getmCrossImageView() {
        return mCrossImageView;
    }

    public ImageView getmLevelImageView() {
        return mLevelImageView;
    }
}
