package br.com.i9algo.autaz.pdv;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

//import com.crashlytics.android.Crashlytics;
import com.github.pierry.simpletoast.SimpleToast;
import com.mixpanel.android.mpmetrics.MixpanelAPI;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

import javax.inject.Inject;

import br.com.i9algo.autaz.pdv.controllers.printer2.PrinterEpson;
import br.com.i9algo.autaz.pdv.data.local.AccountRealmRepository;
import br.com.i9algo.autaz.pdv.data.local.PreferencesRepository;
import br.com.i9algo.autaz.pdv.data.local.UserRealmRepository;
import br.com.i9algo.autaz.pdv.data.remote.repositoryes.AuthRepository;
import br.com.i9algo.autaz.pdv.data.remote.service.ApiService;
import br.com.i9algo.autaz.pdv.data.remote.subscribers.SubscriberInterface;
import br.com.i9algo.autaz.pdv.domain.constants.Constants;
import br.com.i9algo.autaz.pdv.domain.interfaces.LoginInterface;
import br.com.i9algo.autaz.pdv.domain.models.Account;
import br.com.i9algo.autaz.pdv.domain.models.User;
import br.com.i9algo.autaz.pdv.domain.models.inbound.ResultStatusDefault;
import br.com.i9algo.autaz.pdv.domain.models.inbound.UserWrapper;
import br.com.i9algo.autaz.pdv.helpers.IDManagement;
import br.com.i9algo.autaz.pdv.helpers.Logger;
import br.com.i9algo.autaz.pdv.helpers.defaults.MainThreadBus;
import br.com.i9algo.autaz.pdv.ui.base.BaseActivity;
import br.com.i9algo.autaz.pdv.ui.dialog.LoginDialog;
import butterknife.ButterKnife;

public class MainActivity extends BaseActivity implements LoginInterface, SubscriberInterface {

	private final String LOG_TAG = getClass().getSimpleName();

	private int mLayoutSelected = 0;

	public static Keyboard mKeyboard;
	public static KeyboardView mKeyboardView;

	private LoginDialog loginDialog = null;
	private ApiService advService;

	private AuthRepository mAuthRepo;

	@Inject
	MainThreadBus bus;


	public static Intent createIntent(Context context) {
		return new Intent(context, MainActivity.class)
				.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	}
	public static void startActivityIfDiff(Activity activity) {
		if (!activity.getClass().getSimpleName().equals(MainActivity.class.getSimpleName())){
			activity.startActivity(createIntent(activity));
		}
	}


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        startMixPanelApi(this);
		fullScreen();
		setContentView(R.layout.activity_main);
		ButterKnife.bind(this);

		advService = getApp().getApiService();

		if (BuildConfig.BACKEND_STATUS) {
			this.mAuthRepo = new AuthRepository(this);
		} else {
			startActivity( SalesGridActivity.createIntent(MainActivity.this) );
		}

		// TODO verificar atualizacao por enquanto no servidor
		//new UpdateRunnable(this, new Handler()).start();


		/*
		GPSTracker gps = GPSTracker.getInstance(mContext);
		// checa se o GPS esta habilitado
		if(gps.canGetLocation()){
			double latitude = gps.getLatitude();
			double longitude = gps.getLongitude();

			infos.setLatitude(latitude);
			infos.setLongitude(longitude);
		}*/
	}

	@Override
	public void onStart() {
		super.onStart();
		Logger.v(LOG_TAG, "onStart");

		if (BuildConfig.BACKEND_STATUS) {
			User model = UserRealmRepository.getFirst();
			if (model != null && !StringUtils.isEmpty(model.getPublicToken()) && !StringUtils.isEmpty(model.getApiToken())) {
				verifyCredentials();
			} else {
				showLoginView();
			}
		}
	}

	private void verifyCredentials() {
		// API WEB - Repository "Auth"
		this.mAuthRepo = new AuthRepository(this);

		// API WEB - Verifica as credenciais do usuario
		this.mAuthRepo.onValidateCredentialsUser(this);
	}

	public void initialize() {
        User user = UserRealmRepository.getFirst();
        Account account = AccountRealmRepository.getFirst();

		// Cadastra Device
		/* TODO - desabilidade temporariamente, esta com porblemas para editar no API WEB
		DeviceRepository dRepo = new DeviceRepository(this);
		try {
			Device d = DeviceRealmRepository.getFirst();
			if (d == null || StringUtils.isEmpty(d.getPublicToken()))
                d = new Device(this);

			dRepo.onDeviceStore(d);

		} catch (Exception e) {
			e.printStackTrace();
		}*/

        //String idUniq = IDManagement.getDeviceUuid().toString(); // TODO - enviar UUID para API WEB

		/**
		 * Crashlytics
		 */
		//Crashlytics.setUserIdentifier(user.getPublicToken());
		//Crashlytics.setUserEmail(user.getEmail());
		//Crashlytics.setUserName(user.getName());

		/****************************************************************
		 * Mixpanel
		 ****************************************************************/
		final MixpanelAPI.People people = getMixpanel().getPeople();

		people.set("$first_name", user.getName());
		people.set("$last_name", "");
		people.set("$email", user.getEmail());
		people.set("$created", user.getCreatedAt());
		people.set("$last_login", new Date()); // Ultimo login

		if (account != null && account.getSignaturePlan() != null) {
			people.set("expire_at", account.getSignaturePlan().getExpireAt());
		}
        //people.set("credits", name);
        //people.set("gender", "Male");
        //people.increment("Update Count", 1L);// Acompanhar quantas vezes o usuario atualizou seu perfil

        try {
            final JSONObject domainProperty = new JSONObject();
            domainProperty.put("user domain", domainFromEmailAddress(user.getEmail()));
			domainProperty.put("user_public_token", user.getPublicToken());
			domainProperty.put("device_uuid", IDManagement.getDeviceUuid().toString());
            getMixpanel().registerSuperProperties(domainProperty);

        } catch (JSONException e) {
            throw new RuntimeException("Could not encode hour first viewed as JSON");
        }
        // New Line Activity Feed
		//getMixpanel().track("sessao iniciada", null);

		/****************************************************************
		 * End Mixpanel
		 ****************************************************************/

		hideLoginView();
		hideDialog();

		mLayoutSelected = PreferencesRepository.getLayout();

		Intent intent;

		if (mLayoutSelected == 1) {
			intent = SaleControllActivity.createIntent(MainActivity.this);// TODO implementar abertura na tela de venda
		} else {
			intent = SalesGridActivity.createIntent(MainActivity.this);
		}
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
	public void onLogin(String username, String password) {
		if(TextUtils.isEmpty(username.trim()) || TextUtils.isEmpty(password.trim()))  {
			onSubscriberError(null, getContext().getString(R.string.err_oops), getContext().getString(R.string.txt_form_fill));
			return;
		}

		try {
			this.mAuthRepo.onLogin(username, password);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onLoginSuccess(UserWrapper object) {
		Logger.i(LOG_TAG, "API WEB - onLoginSuccess - UserWrapper");

		// Adicionar o UUID do device + o token do usuario como DistinctId no mixpanel
		final String uuid = IDManagement.getDeviceUuid().toString();
		setMixpanelTrackingDistinctId(uuid + "_" + object.getModel().getPublicToken());

		((Activity)getContext()).runOnUiThread(new Runnable() {
			public void run() {
				SimpleToast.info((MainActivity)getContext(), getContext().getString(R.string.txt_login_success), "{fa-user}");
			}
		});

		initialize();
	}

	@Override
	public void onLoginError(ResultStatusDefault resultStatus) {
		Logger.e(LOG_TAG, "API WEB - onLoginError - UserWrapper");

		showLoginView();
		onSubscriberError(resultStatus.error, resultStatus.title, resultStatus.message);
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
