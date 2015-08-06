package com.aboukhari.intertalking.adapter;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.aboukhari.intertalking.R;
import com.aboukhari.intertalking.holder.LanguagesViewHolder;
import com.aboukhari.intertalking.model.Language;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by aboukhari on 06/08/2015.
 */
public class LanguagesRecyclerAdapter extends RecyclerView.Adapter<LanguagesViewHolder> implements View.OnClickListener {


    private List<Language> languages;

    public LanguagesRecyclerAdapter(ArrayList<Language> languages) {
        this.languages = languages;
    }

    @Override
    public LanguagesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_language, parent, false);
        return new LanguagesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(LanguagesViewHolder holder, int position) {
        final Language language = languages.get(position);
        holder.bindLanguage(language, position);

        holder.getmCrossImageView().setOnClickListener(this);


    }

    @Override
    public int getItemCount() {
        return languages.size();
    }

    public void addItem(int position, Language language) {
        languages.add(position, language);
        notifyItemInserted(position);
    }

    public void removeItem(int position) {
        languages.remove(position);
        notifyItemRemoved(position);
    }

    @Override
    public void onClick(View v) {
        Log.d("natija", "view = " + v);
        if (v instanceof ImageView) {
            int position = (Integer) v.getTag();
            languages.remove(position);
            notifyItemRemoved(position);
        }
    }
}
