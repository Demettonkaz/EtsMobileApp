package com.demettonkaz.etsmobileapp;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.viethoa.RecyclerViewFastScroller;
import com.viethoa.models.AlphabetItem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

import static io.realm.Realm.getApplicationContext;

public class FragmentPersonList extends Fragment implements IClickListener {

    private FloatingActionButton fab;
    private EditText etSearch;
    private RecyclerView rvPersonList;
    private Realm realm;
    private PersonAdapter personAdapter;
    private RealmResults<PersonModel> personModelList;
    private ArrayList<PersonModel> rvList = new ArrayList<>();
    private FragmentPersonAdd fragmentPersonAdd = new FragmentPersonAdd();

    private RecyclerViewFastScroller fastScroller;
    private List<AlphabetItem> mAlphabetItems;
    private List<String> mDataArray = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_person_list, container, false);

        Realm.init(getActivity().getApplicationContext());
        realm = Realm.getDefaultInstance();
        personModelList = realm.where(PersonModel.class).findAll();

        Init(view);
        searchPerson();
        enableSwipeToDeleteAndUndo();

        rvList.addAll(personModelList);
        Collections.sort(rvList, new FirstNameSorter());

        initialiseData();
        initialiseUI();

        fab.setOnClickListener(v -> addNewPerson());

        return view;
    }

    public void Init(View view) {
        fab = view.findViewById(R.id.fab);
        etSearch = view.findViewById(R.id.etSearch);
        rvPersonList = view.findViewById(R.id.rvPersonList);
        fastScroller = view.findViewById(R.id.fastScroller);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        rvPersonList.setLayoutManager(mLayoutManager);
        rvPersonList.setItemAnimator(new DefaultItemAnimator());
    }

    public void searchPerson() {
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                personAdapter.filter(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    public void addNewPerson() {
        rvList.clear();

        Bundle bundlePosition = new Bundle();
        bundlePosition.putInt("position", -1);
        fragmentPersonAdd.setArguments(bundlePosition);

        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.frameLayout, fragmentPersonAdd, "FragmentPersonAdd");
        ft.addToBackStack(null);
        ft.commit();
    }

    @Override
    public void onItemClick(View view, int position) {
        rvList.clear();
        personModelList = realm.where(PersonModel.class).findAll();

        Bundle bundlePosition = new Bundle();
        bundlePosition.putInt("position", position);
        fragmentPersonAdd.setArguments(bundlePosition);

        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.frameLayout, fragmentPersonAdd, "FragmentPersonAdd");
        ft.addToBackStack(null);
        ft.commit();
    }

    private void enableSwipeToDeleteAndUndo() {
        SwipeToDeleteCallback swipeToDeleteCallback = new SwipeToDeleteCallback(getContext()) {
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
                final int position = viewHolder.getAdapterPosition();
                personAdapter.removeItem(position);
            }
        };
        ItemTouchHelper itemTouchhelper = new ItemTouchHelper(swipeToDeleteCallback);
        itemTouchhelper.attachToRecyclerView(rvPersonList);
    }

    protected void initialiseData() {
        mDataArray = DataHelper.getAlphabetData();

        mAlphabetItems = new ArrayList<>();
        List<String> strAlphabets = new ArrayList<>();
        for (int i = 0; i < mDataArray.size(); i++) {
            String name = mDataArray.get(i);
            if (name == null || name.trim().isEmpty())
                continue;

            String word = name.substring(0, 1);
            if (!strAlphabets.contains(word)) {
                strAlphabets.add(word);
                mAlphabetItems.add(new AlphabetItem(i, word, false));
            }
        }
    }

    protected void initialiseUI() {
        personAdapter = new PersonAdapter(getApplicationContext(), rvList, FragmentPersonList.this);
        rvPersonList.setAdapter(personAdapter);

        fastScroller.setRecyclerView(rvPersonList);
        fastScroller.setUpAlphabet(mAlphabetItems);
    }
}
