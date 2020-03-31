package com.admanager.unseen.notiservice.converters;

import com.admanager.unseen.R;

/**
 * Created by a on 28.03.2017.
 */
public class FBookLiteConverter extends FBookConverter {
    private static final String TAG = "FBookLiteConverter";

    @Override
    public ConverterData getData() {
        return new ConverterData("com.facebook.mlite", "f", "Facebook", R.color.adm_unseen_brand_f);
    }
}
