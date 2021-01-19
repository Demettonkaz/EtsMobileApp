package com.demettonkaz.etsmobileapp;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;

import io.realm.Realm;
import io.realm.RealmResults;

public class FragmentPersonAdd extends Fragment {

    private Realm realm;
    private EditText etName, etSurname, etPhoneNumber, etEmail, etNote, etDateOfBirth;
    private TextView nameError, surnameError, phoneNumberError, dateOfBirthError, emailError;
    private FloatingActionButton fab;
    private Button btnSaveUpdate;
    private Spinner spinner;
    private String[] countryCode = {"+90", "+1"};
    private ArrayAdapter<String> dataAdapter;
    private ArrayList<PersonModel> rvList = new ArrayList<>();
    private int updatePersonPosition;
    private String nameStr, surnameStr, phoneNumberStr, countryCodeStr, fullPhoneNumberStr,
            emailStr, dateStr = "", noteStr, tmpBirthDayStr, birthDayStr;
    private LinearLayout nameLayout, surnameLayout, dateOfBirthLayout, phoneNumberLayout, emailLayout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_person_add, container, false);

        Realm.init(getActivity().getApplicationContext());
        realm = Realm.getDefaultInstance();
        updatePersonPosition = getArguments().getInt("position");

        Init(view);

        etName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s != null) {
                    nameError.setVisibility(view.INVISIBLE);
                    nameLayout.setBackgroundResource(R.drawable.layout_design);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        etSurname.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s != null) {
                    surnameError.setVisibility(view.INVISIBLE);
                    surnameLayout.setBackgroundResource(R.drawable.layout_design);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        etDateOfBirth.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s != null) {
                    dateOfBirthError.setVisibility(view.INVISIBLE);
                    dateOfBirthLayout.setBackgroundResource(R.drawable.layout_design);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        etPhoneNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s != null) {
                    phoneNumberError.setVisibility(view.INVISIBLE);
                    phoneNumberLayout.setBackgroundResource(R.drawable.layout_design);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        etEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s != null) {
                    emailError.setVisibility(view.INVISIBLE);
                    emailLayout.setBackgroundResource(R.drawable.layout_design);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });


        dataAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, countryCode);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(dataAdapter);

        if (updatePersonPosition != -1) {
            RealmResults<PersonModel> realmResults = realm.where(PersonModel.class).findAll();
            rvList.addAll(realmResults);

            Collections.sort(rvList, new FirstNameSorter());

            final PersonModel updateTable = rvList.get(updatePersonPosition);
            tmpBirthDayStr = updateTable.getPersonDateOfBirth();
            fillAllFields(updateTable.getPersonName(), updateTable.getPersonSurname(), updateTable.getPersonDateOfBirth(),
                    updateTable.getPersonPhoneNumber(), updateTable.getPersonEmail(), updateTable.getPersonNote());
        }

        etDateOfBirth.setOnClickListener(v -> selectedDateOfBirth());

        btnSaveUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                getStr();

                if (updatePersonPosition == -1) {
                    if (!checkFields(v)) {
                    } else {
                        realm.executeTransactionAsync(new Realm.Transaction() {
                            @Override
                            public void execute(Realm bgRealm) {
                                Number maxId = bgRealm.where(PersonModel.class).max("id");
                                int nextId = (maxId == null) ? 1 : maxId.intValue() + 1;
                                PersonModel personModel = bgRealm.createObject(PersonModel.class, nextId);

                                personModel.setPersonName(nameStr);
                                personModel.setPersonSurname(surnameStr);
                                personModel.setPersonDateOfBirth(birthDayStr);
                                personModel.setPersonPhoneNumber(fullPhoneNumberStr);
                                personModel.setPersonEmail(emailStr);
                                personModel.setPersonNote(noteStr);
                            }
                        }, new Realm.Transaction.OnSuccess() {
                            @Override
                            public void onSuccess() {
                                View design = getLayoutInflater().inflate(R.layout.alertview_design, container, false);
                                AlertDialog.Builder ad = new AlertDialog.Builder(getContext());
                                ad.setView(design);
                                ad.create().show();
                            }
                        }, new Realm.Transaction.OnError() {
                            @Override
                            public void onError(Throwable error) {
                            }
                        });
                    }
                } else {
                    if (birthDayStr == "") {
                        birthDayStr = tmpBirthDayStr;
                    }
                    if (!checkFields(v)) {
                    } else {
                        realm.executeTransactionAsync(bgRealm -> {
                            RealmResults<PersonModel> realmResults = Realm.getDefaultInstance().where(PersonModel.class).findAll();
                            final PersonModel updatePerson = realmResults.get(updatePersonPosition);

                            updatePerson.setPersonName(nameStr);
                            updatePerson.setPersonSurname(surnameStr);
                            updatePerson.setPersonDateOfBirth(birthDayStr);
                            updatePerson.setPersonPhoneNumber(fullPhoneNumberStr);
                            updatePerson.setPersonEmail(emailStr);
                            updatePerson.setPersonNote(noteStr);
                        }, new Realm.Transaction.OnSuccess() {
                            @Override
                            public void onSuccess() {
                                View design = getLayoutInflater().inflate(R.layout.alertview_design, container, false);
                                AlertDialog.Builder ad = new AlertDialog.Builder(getContext());
                                ad.setView(design);
                                ad.create().show();
                            }
                        }, new Realm.Transaction.OnError() {
                            @Override
                            public void onError(Throwable error) {
                            }
                        });
                    }
                }
            }
        });

        fab.setOnClickListener(v -> goToList());

        return view;
    }

    public void Init(View view) {
        fab = view.findViewById(R.id.fab);
        etName = view.findViewById(R.id.etName);
        etSurname = view.findViewById(R.id.etSurname);
        etDateOfBirth = view.findViewById(R.id.etDateOfBirth);
        etPhoneNumber = view.findViewById(R.id.etPhoneNumber);
        etEmail = view.findViewById(R.id.etEmail);
        etNote = view.findViewById(R.id.etNote);
        btnSaveUpdate = view.findViewById(R.id.btnSaveUpdate);
        spinner = view.findViewById(R.id.spinner);

        nameError = view.findViewById(R.id.nameError);
        surnameError = view.findViewById(R.id.surnameError);
        dateOfBirthError = view.findViewById(R.id.dateOfBirthError);
        phoneNumberError = view.findViewById(R.id.phoneNumberError);
        emailError = view.findViewById(R.id.emailError);

        nameLayout = view.findViewById(R.id.nameLayout);
        surnameLayout = view.findViewById(R.id.surnameLayout);
        dateOfBirthLayout = view.findViewById(R.id.dateOfBirthLayout);
        phoneNumberLayout = view.findViewById(R.id.phoneNumberLayout);
        emailLayout = view.findViewById(R.id.emailLayout);
    }

    public void goToList() {
        FragmentPersonList fragmentPersonList = new FragmentPersonList();
        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.frameLayout, fragmentPersonList, "FragPersonList");
        ft.addToBackStack(null);
        ft.commit();
    }

    public void selectedDateOfBirth() {
        final Calendar calendar = Calendar.getInstance();
        final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        final DatePickerDialog dpd = new DatePickerDialog(getContext(),
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        month += 1;
                        dateStr = (dayOfMonth + "/" + month + "/" + year);
                        etDateOfBirth.setText(dateStr);
                    }
                }, year, month, day);

        dpd.setButton(DatePickerDialog.BUTTON_POSITIVE, "Ayarla", dpd);
        dpd.setButton(DatePickerDialog.BUTTON_NEGATIVE, "İptal", dpd);
        dpd.show();
    }

    public void getStr() {
        nameStr = etName.getText().toString();
        surnameStr = etSurname.getText().toString();
        phoneNumberStr = etPhoneNumber.getText().toString();
        countryCodeStr = spinner.getSelectedItem().toString();
        fullPhoneNumberStr = countryCodeStr + phoneNumberStr;
        emailStr = etEmail.getText().toString();
        birthDayStr = dateStr;

        if ((etNote.getText().toString().isEmpty())) {
            noteStr = "";
        } else {
            noteStr = etNote.getText().toString();
        }
    }

    public boolean checkFields(View view) {
        if ((nameStr.length() < 2 || nameStr.length() > 20) || (surnameStr.length() < 2 || surnameStr.length() > 20)
                || birthDayStr.length() <= 0 || phoneNumberStr.length() <= 0 || emailStr.length() <= 0) {
            if ((nameStr.length() < 2 || nameStr.length() > 20)) {
                nameError.setVisibility(view.VISIBLE);
                nameLayout.setBackgroundResource(R.drawable.layout_error_design);
            }
            if ((surnameStr.length() < 2 || surnameStr.length() > 20)) {
                surnameError.setVisibility(view.VISIBLE);
                surnameLayout.setBackgroundResource(R.drawable.layout_error_design);
            }
            if (birthDayStr.length() <= 0) {
                dateOfBirthError.setVisibility(view.VISIBLE);
                dateOfBirthLayout.setBackgroundResource(R.drawable.layout_error_design);
            }
            if (phoneNumberStr.length() <= 0) {
                phoneNumberError.setVisibility(view.VISIBLE);
                phoneNumberLayout.setBackgroundResource(R.drawable.layout_error_design);
            }
            if (emailStr.length() <= 0) {
                emailError.setVisibility(view.VISIBLE);
                emailLayout.setBackgroundResource(R.drawable.layout_error_design);
            }
            return false;
        }
        return true;
    }

    private void fillAllFields(String name, String surname, String dateOfBirth, String phoneNumber, String email, String note) {
        etName.setText(name);
        etSurname.setText(surname);
        etDateOfBirth.setText(dateOfBirth);
        String tr = "+9";
        if ((phoneNumber.substring(0, 2)).equals(tr)) {
            etPhoneNumber.setText(phoneNumber.substring(3));
        } else {
            etPhoneNumber.setText(phoneNumber.substring(2));
        }
        etEmail.setText(email);
        etNote.setText(note);
    }
}
