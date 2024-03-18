package com.example.lightpay;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;


public class settingsFragment extends Fragment {
    private SwitchCompat privateAccountSwitch;

    private final boolean isNightMode = false;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        ImageButton backArrowBtn = view.findViewById(R.id.back_set_arrow);
        ImageView nightModeImageView = view.findViewById(R.id.night_mode);
        ImageView notificationImageView = view.findViewById(R.id.setting_notification);
        ImageView privateAccountImageView = view.findViewById(R.id.private_account);
        ImageView securityImageView = view.findViewById(R.id.security);
        ImageView textSizeImageView = view.findViewById(R.id.textSizeIcon);
        ImageView languageImageView = view.findViewById(R.id.languageIcon);
        ImageView messaegImageView = view.findViewById(R.id.messageIcon);
        ImageView faqImageView = view.findViewById(R.id.faq_Icon);
        ImageView logoutImageView = view.findViewById(R.id.icon_logout);

        SwitchCompat nightModeSwitch = view.findViewById(R.id.night_mode_switch);
        SwitchCompat notificationSwitch = view.findViewById(R.id.notification_switch);
        SwitchCompat privateAccountSwitch = view.findViewById(R.id.private_switch);

        backArrowBtn.setOnClickListener(v -> {
            //implement method here
        });
        nightModeImageView.setOnClickListener(v -> {
            //implement method here
        });
        notificationImageView.setOnClickListener(v -> {
            //implement method here
        });
        privateAccountImageView.setOnClickListener(v -> {
            //implement method here
        });
        securityImageView.setOnClickListener(v -> {
            //implement method here
        });
        textSizeImageView.setOnClickListener(v -> {
            //implement method here
        });
        languageImageView.setOnClickListener(v -> {
            //implement method here
        });
        messaegImageView.setOnClickListener(v -> {
            //implement method here
        });
        faqImageView.setOnClickListener(v -> {
            //implement method here
        });
        logoutImageView.setOnClickListener(v -> {
            //implement method here
        });

        return view;
    }
}