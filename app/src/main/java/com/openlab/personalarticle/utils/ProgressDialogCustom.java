package com.openlab.personalarticle.utils;

import android.app.ProgressDialog;
import android.content.Context;

import com.openlab.personalarticle.R;

public class ProgressDialogCustom {

    private ProgressDialog pd;

    public ProgressDialogCustom(Context context)
    {
        pd = new ProgressDialog(context);
    }

    public void show(){
        pd.show();
        pd.setContentView(R.layout.loading_dialog_view);
        pd.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
    }

    public  void dismiss(){
        pd.dismiss();
    }


}
