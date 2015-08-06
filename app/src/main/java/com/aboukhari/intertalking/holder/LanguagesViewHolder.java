package com.aboukhari.intertalking.holder;

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

    public LanguagesViewHolder(View itemView) {
        super(itemView);

        mTextView =(TextView) itemView.findViewById(R.id.tv_language);
        mCrossImageView = (ImageView) itemView.findViewById(R.id.iv_cross);
        mCrossImageView.setOnClickListener(this);
    }

    public void bindLanguage(Language language,int position) {
        mLanguage = language;
        mTextView.setText(language.getName());
        mCrossImageView.setTag(position);
    }

    public ImageView getmCrossImageView() {
        return mCrossImageView;
    }

    @Override
    public void onClick(View v) {
        if(v==mCrossImageView){

        }
    }
}
