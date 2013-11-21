package com.example.home1;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.google.android.gms.auth.GoogleAuthUtil;

public class Login extends Activity implements OnClickListener{
	private AccountManager accountManager;
	private String[] accounts;
	private Spinner accountSpinner;
	private Button loginButton;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {		
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.login);
		loginButton = (Button)findViewById(R.id.bLogin);
		loginButton.setOnClickListener(this);
		
		Connection.initialize(Login.this);
		
        accounts = getAccountNames();		
        accountSpinner = initializeSpinner(R.id.accountSpinner, accounts);	
	}

	@Override
	public void onClick(View v) {		
		if (accounts.length != 0) {			
			Connection.login(accountSpinner.getSelectedItem().toString());
		}	
	}
	
	//vrati array svih google accounta na mobitelu
    private String[] getAccountNames() {
    	accountManager = AccountManager.get(this);
        Account[] accounts = accountManager.getAccountsByType(GoogleAuthUtil.GOOGLE_ACCOUNT_TYPE);
        String[] names = new String[accounts.length];

        for (int i = 0; i < names.length; i++)
            names[i] = accounts[i].name;            
        
        return names;
    }
    
    //stavi sve google accounte u spinner za korisnika da odabere
    private Spinner initializeSpinner(int id, String[] values) {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(Login.this, android.R.layout.simple_spinner_item, values);
        Spinner spinner = (Spinner) findViewById(id);
        spinner.setAdapter(adapter);
        return spinner;
    }
}
