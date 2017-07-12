package com.autazcloud.pdv.helpers;

import android.content.Context;

import com.autazcloud.pdv.R;
import com.autazcloud.pdv.domain.constants.DateFormats;

import java.text.DecimalFormat;

public class FormatUtil {

	static volatile Context applicationContext;

	public static synchronized void init(Context context) {
		if (FormatUtil.applicationContext == null) {
			if (context == null) {
				throw new IllegalArgumentException("Non-null context required.");
			}
			FormatUtil.applicationContext = context;
		}
		DateFormats.init(context);
	}


	public static final String getLocaleFormatMoney() {
		return applicationContext.getString(R.string.format_money);
	}
	public static final String getLocaleFormatMoneySymbol() {
		return applicationContext.getString(R.string.format_money);
	}




	public static final String toMoneyFormat(double value) {
		String formattedPrice = new DecimalFormat(getLocaleFormatMoneySymbol()).format(value);
		return (formattedPrice);
	}
	
	public static final String toMoneyFormat(String value) {
		String formattedPrice = new DecimalFormat(getLocaleFormatMoneySymbol()).format(value);
		return (formattedPrice);
	}

	public static final String toMoneyFormat(double value, boolean symbol) {
		String format = (symbol) ? getLocaleFormatMoneySymbol() : getLocaleFormatMoney();
		String formattedPrice = new DecimalFormat(format).format(value);
		return (formattedPrice);
	}

	public static final String toMoneyFormat(String value, boolean symbol) {
		String format = (symbol) ? getLocaleFormatMoneySymbol() : getLocaleFormatMoney();
		String formattedPrice = new DecimalFormat(format).format(value);
		return (formattedPrice);
	}
}