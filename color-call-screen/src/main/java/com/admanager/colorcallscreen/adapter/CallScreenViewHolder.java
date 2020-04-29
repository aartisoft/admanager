package com.admanager.colorcallscreen.adapter;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.admanager.colorcallscreen.ColorCallScreenApp;
import com.admanager.colorcallscreen.R;
import com.admanager.colorcallscreen.activities.CCSInCallActivity;
import com.admanager.colorcallscreen.activities.ColorCallScreenActivity;
import com.admanager.colorcallscreen.api.BgModel;
import com.admanager.colorcallscreen.fragment.BgDetailsFragment;
import com.admanager.colorcallscreen.model.ContactBean;
import com.admanager.colorcallscreen.service.GsmCall;
import com.admanager.colorcallscreen.utils.Prefs;
import com.admanager.colorcallscreen.view.FullScreenVideoView;
import com.admanager.core.AdmUtils;
import com.admanager.recyclerview.BindableViewHolder;

import java.util.Random;

public class CallScreenViewHolder extends BindableViewHolder<BgModel> {

    private TextView textDuration;
    private LinearLayout buttonHangup;
    private LinearLayout buttonAnswer;
    private LinearLayout call_buttons;
    private TextView textStatus;
    private TextView textDisplayName;
    private TextView textNumber;
    private ImageView bg_image;
    private FullScreenVideoView bg_video;
    private ImageView iv_portrait;
    private View imageContainer;
    private View selected;
    private View selectedForUser;
    private View root;

    public CallScreenViewHolder(@NonNull View itemView) {
        super(itemView);
        imageContainer = itemView.findViewById(R.id.imageContainer);
        selected = itemView.findViewById(R.id.selected);
        selectedForUser = itemView.findViewById(R.id.selectedForUser);
        iv_portrait = itemView.findViewById(R.id.iv_portrait);
        bg_image = itemView.findViewById(R.id.bg_image);
        bg_video = itemView.findViewById(R.id.bg_video);
        textDuration = itemView.findViewById(R.id.text_duration);
        textStatus = itemView.findViewById(R.id.text_status);
        textDisplayName = itemView.findViewById(R.id.text_display_name);
        textNumber = itemView.findViewById(R.id.text_number);
        buttonHangup = itemView.findViewById(R.id.button_hangup);
        buttonAnswer = itemView.findViewById(R.id.button_answer);
        call_buttons = itemView.findViewById(R.id.call_buttons);
        root = itemView.findViewById(R.id.root);

        int p = (int) AdmUtils.dpToPx(itemView.getContext(), 60);
        reSize(imageContainer, p);
        textDisplayName.setTextSize(13);
        textNumber.setTextSize(10);
        itemView.getLayoutParams().height = (int) AdmUtils.dpToPx(itemView.getContext(), 320);
    }

    public static int randomIndex() {
        return new Random().nextInt(ColorCallScreenApp.NAMES.length);
    }

    public static String randomName(int i) {
        return ColorCallScreenApp.NAMES[i % ColorCallScreenApp.NAMES.length];
    }

    public static Uri randomUri(Activity activity, int i) {
        return getPortraitUri(activity, i);
    }

    private static void reSize(View v, int l) {
        if (v.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
            p.height = l;
            p.width = l;
            v.requestLayout();
        }
    }

    private static Uri getPortraitUri(Activity activity, int i) {
        return Uri.parse("android.resource://" + activity.getPackageName() + "/" + (ColorCallScreenApp.IMAGES[i % ColorCallScreenApp.IMAGES.length]));
    }

    @Override
    public void bindTo(final Activity activity, final BgModel bgModel, int position) {

        boolean isSelected = Prefs.with(activity).isSelectedBg(bgModel);
        boolean isSelectedForUser = Prefs.with(activity).isSelectedBgForUser(null, bgModel);

        selected.setVisibility(isSelected ? View.VISIBLE : View.GONE);
        selectedForUser.setVisibility(isSelectedForUser ? View.VISIBLE : View.GONE);
        final String name = ColorCallScreenApp.NAMES[position % ColorCallScreenApp.NAMES.length];

        final Uri uri = getPortraitUri(activity, position);
        ContactBean contactBean = new ContactBean(null, name, ColorCallScreenApp.NUMBER, uri.toString());

        GsmCall gsmCall = new GsmCall(GsmCall.Status.RINGING, contactBean);
        CCSInCallActivity.updateView(gsmCall, textStatus, textDuration, buttonHangup, buttonAnswer, call_buttons, textDisplayName, textNumber, iv_portrait);

        CCSInCallActivity.loadBgImage(itemView.getContext(), bgModel.thumbnail, bg_image);

        buttonHangup.setVisibility(isSelected ? View.VISIBLE : View.GONE);
        buttonAnswer.setVisibility(isSelected ? View.VISIBLE : View.GONE);
        textStatus.setVisibility(View.GONE);

        root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (activity instanceof ColorCallScreenActivity) {
                    Bundle bundle = BgDetailsFragment.createBgDetailsBundle(name, uri, bgModel, ColorCallScreenApp.NUMBER, false);
                    ((ColorCallScreenActivity) activity).navController.navigate(R.id.bgDetailsFragment, bundle);
                }
            }
        });
    }


}
