package com.autazcloud.pdv.data.remote.repositoryes;

import com.autazcloud.pdv.data.local.PreferencesRepository;
import com.autazcloud.pdv.data.remote.subscribers.AuthValidateSubscriber;
import com.autazcloud.pdv.data.remote.subscribers.LoginSubscriber;
import com.autazcloud.pdv.data.remote.subscribers.SubscriberInterface;
import com.autazcloud.pdv.domain.constants.AuthAttr;

import rx.schedulers.Schedulers;

/**
 * Created by aStraube on 12/07/2017.
 */

public class AuthRepository {

    private final SubscriberInterface _owner;

    public AuthRepository(final SubscriberInterface owner) {
        this._owner = owner;
    }

    public void onLogin(String username, String password) throws Exception {
        // Armazenar username em cache
        PreferencesRepository.setValue(AuthAttr.USERNAME, username);

        _owner.getApiService().authorization(username, password)
                    .subscribeOn(Schedulers.io())
                    .subscribe(new LoginSubscriber(_owner));
    }

    public void onValidateCredentialsUser(final SubscriberInterface owner) {
        String apiToken = PreferencesRepository.getValue(AuthAttr.USER_API_TOKEN);
        String publicToken = PreferencesRepository.getValue(AuthAttr.USER_PUBLIC_TOKEN);

        _owner.getApiService().authorizationValidate(apiToken, publicToken)
                .subscribeOn(Schedulers.io())
                .subscribe(new AuthValidateSubscriber(_owner));
    }
}
