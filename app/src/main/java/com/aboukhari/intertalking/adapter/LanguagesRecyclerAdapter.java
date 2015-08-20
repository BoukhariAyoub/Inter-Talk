package com.aboukhari.intertalking.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.aboukhari.intertalking.R;
import com.aboukhari.intertalking.holder.LanguagesViewHolder;
import com.aboukhari.intertalking.model.Language;

import java.util.ArrayList;

/**
 * Created by aboukhari on 06/08/2015.
 */
public class LanguagesRecyclerAdapter extends RecyclerView.Adapter<LanguagesViewHolder> implements View.OnClickListener {


    Context context;
    private ArrayList<Language> languages;
    ArrayAdapter<Language> mLanguageAdapter;
    LanguagesViewHolder mHolder;


    public LanguagesRecyclerAdapter(Context context, ArrayList<Language> languages, ArrayAdapter<Language> languageAdapter) {
        this.context = context;
        this.languages = languages;
        mLanguageAdapter = languageAdapter;
    }

    @Override
    public LanguagesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_language, parent, false);
        return new LanguagesViewHolder(context, view);
    }

    @Override
    public void onBindViewHolder(LanguagesViewHolder holder, int position) {
        final Language language = languages.get(position);
        mHolder = holder.bindLanguage(language, position);
        holder.getmCrossImageView().setOnClickListener(this);
        holder.getmLevelImageView().setOnClickListener(this);
    }

    @Override
    public int getItemCount() {
        return languages.size();
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    public ArrayList<Language> getLanguages() {
        return languages;
    }

    public void addItem(Language language) {
        int position = 0;
        languages.add(position, language);
        mLanguageAdapter.remove(language);
        mLanguageAdapter.notifyDataSetChanged();
        notifyItemInserted(position);
        notifyItemRangeChanged(position, languages.size());
    }

    public void removeItem(int position) {
        mLanguageAdapter.add(languages.get(position));
        mLanguageAdapter.notifyDataSetChanged();
        languages.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, languages.size());
    }

    public void updateItem(int position) {
        languages.get(position).updateLevel();
        notifyItemChanged(position);
    }


    @Override
    public void onClick(View v) {
        if (v.getId() == mHolder.getmCrossImageView().getId()) {
            int position = (Integer) v.getTag();
            removeItem(position);
        }

        if (v.getId() == mHolder.getmLevelImageView().getId()) {
            int position = (Integer) v.getTag();
            updateItem(position);
        }

    }
}
