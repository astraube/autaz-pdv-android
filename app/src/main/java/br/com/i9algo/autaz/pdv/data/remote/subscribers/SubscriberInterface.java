package br.com.i9algo.autaz.pdv.data.remote.subscribers;

import android.content.Context;
import android.support.annotation.NonNull;

import br.com.i9algo.autaz.pdv.data.remote.service.ApiService;
import cn.pedant.SweetAlert.SweetAlertDialog;


/**
 * Created by aStraube on 08/07/2017.
 */

public interface SubscriberInterface {
    public Context getContext();
    public ApiService getApiService();

    public void onSubscriberCompleted();
    public void onSubscriberError(@NonNull Throwable e, final String title, final String msg);
    public void onSubscriberNext(Object t);

    public SweetAlertDialog getSweetDialog();
    public void setSweetDialog(SweetAlertDialog dialog);
    public void setSweetProgress(String message);
    public void setSweetProgress(String message, String title);
}
