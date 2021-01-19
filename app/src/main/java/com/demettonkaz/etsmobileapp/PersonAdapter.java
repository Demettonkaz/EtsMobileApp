package com.demettonkaz.etsmobileapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.viethoa.RecyclerViewFastScroller;

import java.util.ArrayList;
import java.util.Locale;

public class PersonAdapter extends RecyclerView.Adapter<PersonAdapter.MyViewHolder> implements RecyclerViewFastScroller.BubbleTextGetter {

    private Context mContext;
    private ArrayList<PersonModel> filteredList = new ArrayList<>();
    private ArrayList<PersonModel> personModelList;
    private IClickListener itemClickListener;

    public PersonAdapter(Context mContext, ArrayList<PersonModel> dataSet, IClickListener itemClickListener) {
        this.mContext = mContext;
        this.personModelList = dataSet;
        filteredList.addAll(personModelList);
        this.itemClickListener = itemClickListener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_design, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        PersonModel personModel = personModelList.get(position);

        holder.nameTv.setText(personModel.getPersonName() + " " + personModel.getPersonSurname());
        holder.birthdayTv.setText(personModel.getPersonDateOfBirth());
        holder.emailTv.setText(personModel.getPersonEmail());
        holder.phoneNumberTv.setText(personModel.getPersonPhoneNumber());

        if ((personModel.getPersonNote()).isEmpty()) {
            holder.noteLayout.setVisibility(View.GONE);
        } else {
            holder.noteTv.setText(personModel.getPersonNote());
        }
    }

    @Override
    public int getItemCount() {
        return personModelList.size();
    }

    public void filter(String charText) {
        personModelList.clear();
        charText = charText.toLowerCase(Locale.getDefault());

        if (charText.length() == 0) {
            personModelList.addAll(filteredList);
        } else {
            for (PersonModel pm : filteredList) {
                if (pm.getPersonName().toLowerCase().contains(charText)) {
                    personModelList.add(pm);
                }
            }
        }
        notifyDataSetChanged();
    }

    public void removeItem(int position) {
        personModelList.remove(position);
        notifyItemRemoved(position);
    }

    public ArrayList<PersonModel> getData() {
        return personModelList;
    }

    @Override
    public String getTextToShowInBubble(int pos) {
        if (pos < 0 || pos >= personModelList.size())
            return null;

        String name = personModelList.get(pos).getPersonName();
        if (name == null || name.length() < 1)
            return null;
        return personModelList.get(pos).getPersonName().substring(0, 1);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView nameTv, birthdayTv, emailTv, phoneNumberTv, noteTv;
        public LinearLayout noteLayout;

        public MyViewHolder(View view) {
            super(view);

            nameTv = view.findViewById(R.id.nameTv);
            birthdayTv = view.findViewById(R.id.birthdayTv);
            emailTv = view.findViewById(R.id.emailTv);
            phoneNumberTv = view.findViewById(R.id.phoneNumberTv);
            noteTv = view.findViewById(R.id.noteTv);
            noteLayout = view.findViewById(R.id.noteLayout);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    itemClickListener.onItemClick(v, getAdapterPosition());
                }
            });
        }
    }
}
