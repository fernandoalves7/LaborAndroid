package com.rco.labor.activities;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.AppCompatCheckBox;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.rco.labor.R;
import com.rco.labor.adapters.ServersListAdapter;
import com.rco.labor.businesslogic.BusinessRules;
import com.rco.labor.businesslogic.ServerUrl;
import com.rco.labor.businesslogic.labor.Labor;
import com.rco.labor.businesslogic.rms.User;
import com.rco.labor.utils.UiUtils;

import java.util.ArrayList;

/**
 * Created by Fernando on 8/24/2018.
 */

public class LoginActivity extends Activity implements AdapterView.OnItemClickListener {
    private BusinessRules rules = BusinessRules.instance();
    private ServersListAdapter adapter;
    private boolean isProcessing = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Button loginButton = findViewById(R.id.login_button);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (isProcessing)
                        return;

                    EditText username = findViewById(R.id.username);
                    EditText password = findViewById(R.id.password);

                    if (UiUtils.isNullOrWhitespacesAny(username, password)) {
                        UiUtils.showToast(LoginActivity.this, "Please provide a username / password and try again.");
                        return;
                    }

                    LoginUserTask loginUserTask = new LoginUserTask();
                    loginUserTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, username.getText().toString(), password.getText().toString());
                } catch (Throwable t) {
                    if (t != null)
                        t.printStackTrace();
                }
            }
        });

        EditText usernameField = findViewById(R.id.username);
        usernameField.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int keyCode, KeyEvent keyEvent) {
                try {
                    if ((keyEvent.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                        EditText password = findViewById(R.id.password);
                        password.requestFocus();

                        return true;
                    }
                } catch (Throwable t) {
                    if (t != null)
                        t.printStackTrace();
                }

                return false;
            }
        });

        EditText password = findViewById(R.id.password);
        password.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int keyCode, KeyEvent keyEvent) {
                try {
                    if ((keyEvent.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                        Button loginButton = findViewById(R.id.login_button);
                        loginButton.callOnClick();

                        return true;
                    }

                    if (keyCode == KeyEvent.KEYCODE_ENTER)
                        return true;
                } catch (Throwable t) {
                    if (t != null)
                        t.printStackTrace();
                }

                return false;
            }
        });

        AppCompatCheckBox rememberPassword = findViewById(R.id.remember_password);
        rememberPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rules.switchRememberUserPassword();
            }
        });

        rememberPassword.setChecked(rules.rememberUserPassword());
        usernameField.requestFocus();

        if (rules.existsLastLoggedInUsername()) {
            usernameField.setText(rules.getLastLoggedInUsername());

            if (rules.rememberUserPassword())
                password.setText(rules.getLastLoggedInUserPassword());

            password.requestFocus();
        }

        loadServersList();

        if (rules.isProductionMode()) {
            ListView serversList = findViewById(R.id.servers_list);
            serversList.setVisibility(View.INVISIBLE);
            rules.setSelectedServerUrl("lion");
        } else {
            rules.setSelectedServerUrl("fox");
        }
    }

    private void loadServersList() {
        ArrayList<ServerUrl> serverUrls = rules.getServerUrls();

        adapter = new ServersListAdapter(this, serverUrls);
        ListView serversList = findViewById(R.id.servers_list);

        if (serversList != null) {
            serversList.setAdapter(adapter);
            serversList.setOnItemClickListener(this);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        rules.setSelectedServerUrl(i);
        loadServersList();
    }

    public class LoginUserTask extends AsyncTask<String, Integer, Integer> {
        private static final int INVALID_CREDENTIALS = 1;

        private static final int UNABLE_TO_COMM = 2;
        private static final int UNABLE_TO_AUTHENTICATE = 3;
        private static final int UNABLE_TO_GET_USER_DATA = 4;

        private static final int AUTHENTICATING = 7;
        private static final int AUTHENTICATION_DONE = 5;
        private static final int SYNC_DONE = 5;

        @Override
        protected void onPreExecute() {
            setProcessingState(true);
        }

        @Override
        protected Integer doInBackground(String... params) {
            try {
                publishProgress(AUTHENTICATING);

                String username = params[0];
                String password = params[1];

                rules.authenticate(username, password);
                rules.setUsernamePasswordIdentifier(username, password);

                ArrayList<Labor> labor = rules.syncLabor();
                ArrayList<User> staff = rules.syncUsers("staff");

                rules.clearUsers();
                rules.storeLabor(labor);
                rules.storeUsers(staff);

                ArrayList<String> userRights = rules.syncUserRights();
                rules.setUserRights(userRights);

                /*
                if (user == null)
                    return INVALID_CREDENTIALS;

                rules.setUser(user);*/

                return BusinessRules.OK;
            /*} catch (ParseException ex) {
                if (ex.getMessage() != null && ex.getMessage().indexOf("authorization") != -1)
                    return INVALID_CREDENTIALS;

                return UNABLE_TO_GET_USER_DATA;*/
            } catch (Throwable t) {
                if (t != null)
                    t.printStackTrace();

                return BusinessRules.UNABLE_TO_SYNC;
            }
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            if (values[0] == AUTHENTICATING)
                setProcessingMessage("Authenticating...");
            else if (values[0] == AUTHENTICATION_DONE)
                setProcessingMessage("Synchronizing...");
        }

        @Override
        protected void onPostExecute(Integer result) {
            try {
                switch (result) {
                    case BusinessRules.UNABLE_TO_SYNC:
                        setProcessingState(false, false);

                        UiUtils.showExclamationDialog(LoginActivity.this, getString(R.string.app_name), getString(R.string.error_unable_to_login),
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        try {
                                            dialog.cancel();
                                            //LoginActivity.this.finish();
                                        } catch (Throwable t) {
                                            t.printStackTrace();
                                        }
                                    }
                                });
                        break;

                    case BusinessRules.OK:
                        EditText username = findViewById(R.id.username);
                        rules.setLastLoggedInUsername(username.getText().toString());

                        if (rules.rememberUserPassword()) {
                            EditText password = findViewById(R.id.password);
                            rules.setLastLoggedInUserPassword(password.getText().toString());
                        } else
                            rules.clearLastLoggedInUserPassword();

                        startActivity(new Intent(LoginActivity.this, MainMenuActivity.class));
                            (new Handler()).postDelayed(new Runnable() { public void run() {
                                setProcessingState(false);
                                finish();
                            } }, 3000);
                        break;

                    default:
                        setProcessingState(false, false);
                        UiUtils.showToast(LoginActivity.this, "Invalid username and/or password.");
                        break;
                }
            } catch (Throwable t) {
                if (t != null)
                    t.printStackTrace();
            }
        }
    }

    private void setProcessingMessage(String msg) {
        UiUtils.setTextView(LoginActivity.this, R.id.authenticating_feedback_text, msg);
    }

    private void setProcessingState(boolean isProcessing) {
        setProcessingState(isProcessing, true);
    }

    private void setProcessingState(boolean isProcessing, boolean clearCredentials) {
        this.isProcessing = isProcessing;

        EditText username = findViewById(R.id.username);
        EditText password = findViewById(R.id.password);

        if (isProcessing) {
            username.setEnabled(false);
            password.setEnabled(false);

            findViewById(R.id.login_button_panel).setEnabled(false);
            findViewById(R.id.authenticating_panel).setVisibility(View.VISIBLE);
        } else {
            if (clearCredentials) {
                username.setText("");
                password.setText("");
            }

            username.setEnabled(true);
            password.setEnabled(true);

            if (clearCredentials)
                username.requestFocus();

            findViewById(R.id.login_button_panel).setEnabled(true);
            findViewById(R.id.authenticating_panel).setVisibility(View.GONE);
        }
    }

    private void setDummyCredentials() {
        ((EditText) findViewById(R.id.username)).setText("hc7");
        ((EditText) findViewById(R.id.password)).setText("hc7");

        findViewById(R.id.password).requestFocus();
    }

    private EditText.OnEditorActionListener getOnEditorEnterKeyActionListener(final OnFieldEnterKeyPressListener onClickListener) {
        return new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {
                try {
                    if (event == null) {
                        if (actionId != EditorInfo.IME_ACTION_NEXT && actionId != EditorInfo.IME_ACTION_DONE)
                            return false; // Let system handle everything except soft enters in singleLine EditTexts
                    } else if (actionId == EditorInfo.IME_NULL) {
                        // Capture most soft enters in multi-line EditTexts and all hard enters.
                        // They supply a zero actionId and a valid KeyEvent rather than
                        // a non-zero actionId and a null event like the previous cases.

                        if (event.getAction() != KeyEvent.ACTION_DOWN)
                            return true; // We capture the event when key is 1st pressed and consume the event when key is released
                    } else
                        return false;

                    // We let the system handle it when the listener is triggered by something that wasn't an enter.

                    // Code from this point on will execute whenever the user presses enter in an attached view, regardless of position,
                    // keyboard, or singleLine status.

                    if (onClickListener != null)
                        onClickListener.onClickEvent();
                } catch (Throwable t) {
                    if (t != null)
                        t.printStackTrace();
                }

                return true;            // Consume the event
            }
        };
    }

    public interface OnFieldEnterKeyPressListener {
        void onClickEvent();
    }
}
