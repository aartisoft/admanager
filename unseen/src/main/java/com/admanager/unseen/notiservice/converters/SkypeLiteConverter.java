package com.admanager.unseen.notiservice.converters;

import com.admanager.unseen.R;

/**
 * Created by a on 28.03.2017.
 */
public class SkypeLiteConverter extends SkypeConverter {
    private static final String TAG = "SkypeLiteConverter";

    @Override
    public ConverterData getData() {
        return new ConverterData("com.skype.m2", "s", "Skype", R.color.adm_unseen_brand_s);
    }


}
