package com.autazcloud.pdv;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.autazcloud.pdv.ui.base.BaseActivity;
import com.autazcloud.pdv.data.remote.service.ApiService;
import com.autazcloud.pdv.domain.constants.AuthAttr;
import com.autazcloud.pdv.domain.interfaces.LoginInterface;
import com.autazcloud.pdv.data.local.PreferencesRepository;
import com.autazcloud.pdv.helpers.defaults.MainThreadBus;
import com.autazcloud.pdv.data.remote.ResultDefault;
import com.autazcloud.pdv.data.remote.subscribers.LoginSubscriber;
import com.autazcloud.pdv.data.remote.subscribers.SubscriberInterface;
import com.autazcloud.pdv.ui.dialog.LoginDialog;
import com.github.pierry.simpletoast.SimpleToast;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import javax.inject.Inject;

import butterknife.ButterKnife;
import rx.schedulers.Schedulers;

public class MainActivity extends BaseActivity implements LoginInterface, SubscriberInterface {

	private final String TAG;

	private int mLayoutSelected = 0;

	public static Keyboard mKeyboard;
	public static KeyboardView mKeyboardView;

	private LoginDialog loginDialog = null;
	private ApiService advService;

	@Inject
	MainThreadBus bus;

	public MainActivity () {
		super();
		this.TAG = getClass().getSimpleName();
	}

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		fullScreen();
		setContentView(R.layout.activity_main);
		ButterKnife.bind(this);
		//presenter.bindView(this);
		//bus.register(this);

		advService = getApp().getApiService();
		
		/*
		String deviceId = Settings.System.getString(getContentResolver(), Secure.ANDROID_ID);
		DeviceUuidFactory device = new DeviceUuidFactory(this);
		Log.i("TAG","android.os.Build.SERIAL: " + deviceId);
		Log.i("TAG","device.getDeviceUuid(): " + device.getDeviceUuid());
		*/
		//Log.v(TAG, "onCreate");
	}

	@Override
	public void onStart() {
		super.onStart();
		//Log.v(TAG, "onStart");

		//((CustomApplication)getApplication()).isFirstAcccess()

		if (PreferencesRepository.isValueEmpty(AuthAttr.USER_API_TOKEN) || PreferencesRepository.isValueEmpty(AuthAttr.ACCOUNT_PUBLIC_TOKEN)) {
			showLoginView();
		} else {
			initialize();
		}
	}
	
	public void initialize() {
		hideLoginView();
		hideDialog();

		mLayoutSelected = PreferencesRepository.getLayout();
		
		Intent intent = getIntent();
		
		switch (mLayoutSelected) {
		case 0 :
			intent = new Intent(MainActivity.this, SalesGridActivity.class);
			break;
		case 1 :
			intent = new Intent(MainActivity.this, SaleControllActivity.class); // TODO implementar abertura na tela de venda
			break;
		default:
			intent = new Intent(MainActivity.this, SalesGridActivity.class);
			break;
		}
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);
	}

	public void showLoginView() {
		((Activity)getContext()).runOnUiThread(new Runnable() {
			public void run() {
				loginDialog = new LoginDialog((MainActivity)getContext());
			}
		});
	}

	public void hideLoginView() {
		if (this.loginDialog != null)
			this.loginDialog.dismiss();
	}

	@Override
	public Context getContext() {
		return this;
	}

	@Override
	public SubscriberInterface getSubscriberInterface() {
		return this;
	}

	@Override
	public boolean onLogin(String username, String password) {
		if(TextUtils.isEmpty(username.trim()) || TextUtils.isEmpty(password.trim()))  {
			return false;
		}

		// Armazenar username em cache
		PreferencesRepository.setValue(AuthAttr.USERNAME, username);

		try {
			advService.authorization(username, password)
					.subscribeOn(Schedulers.io())
					.subscribe(new LoginSubscriber(MainActivity.this));

		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

    @Override
    public void onLoginSuccess(ResultDefault object) {
		((Activity)getContext()).runOnUiThread(new Runnable() {
			public void run() {
				SimpleToast.info((MainActivity)getContext(), getContext().getString(R.string.txt_login_success), "{fa-user}");
			}
		});

		Log.i(TAG, "onLoginSuccess");
        Gson gson = new Gson();
        JsonElement jsonElement = gson.toJsonTree(object.data);
        JsonObject jsonObject = jsonElement.getAsJsonObject();

        PreferencesRepository.setValue(AuthAttr.USER_NAME, jsonObject.get(AuthAttr.USER_NAME).getAsString());
        PreferencesRepository.setValue(AuthAttr.USER_EMAIL, jsonObject.get(AuthAttr.USER_EMAIL).getAsString());
        PreferencesRepository.setValue(AuthAttr.USER_API_TOKEN, jsonObject.get(AuthAttr.USER_API_TOKEN).getAsString());
        PreferencesRepository.setValue(AuthAttr.USER_PUBLIC_TOKEN, jsonObject.get(AuthAttr.PUBLIC_TOKEN).getAsString());

		JsonObject account = jsonObject.getAsJsonObject("account");
		PreferencesRepository.setValue(AuthAttr.ACCOUNT_CLIENT_ID, account.get(AuthAttr.ACCOUNT_CLIENT_ID).getAsString());
		PreferencesRepository.setValue(AuthAttr.ACCOUNT_PUBLIC_TOKEN, account.get(AuthAttr.PUBLIC_TOKEN).getAsString());

		initialize();
    }
	
	@Override
	public void onLoginCancel() {
		System.exit(0);
	}
	
	@Override
	public void onSignUp() {
		// TODO Auto-generated method stub
	}

	@Override
	public void onLogout() {
		// TODO Auto-generated method stub
	}
}
