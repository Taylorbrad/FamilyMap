package com.example.familymapclientmarktb;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import Client.DataCache;
import Client.ServerProxy;
import Request.LoginRequest;
import Request.RegisterRequest;
import Result.LoginResult;
import Result.PersonResult;
import Result.RegisterResult;

/**
 * create an instance of this fragment.
 */
public class LoginFragment extends Fragment{

    Listener listener;

    String host;
    String port;
    String username;
    String password;
    String firstName;
    String lastName;
    String email;
    String gender;

    public interface Listener {
        void notifyDone();
    }
    public void registerListener(Listener listener) {
        this.listener = listener;
    }


    public LoginFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //Log.i("LoginFragment", "in onCreateView()");

        View view = inflater.inflate(R.layout.fragment_login, container, false);

        EditText hostEdit = view.findViewById(R.id.hostEditText);
        EditText portEdit = view.findViewById(R.id.portEditText);
        EditText usernameEdit = view.findViewById(R.id.usernameEditText);
        EditText passwordEdit = view.findViewById(R.id.passwordEditText);
        EditText firstNameEdit = view.findViewById(R.id.firstNameEditText);
        EditText lastNameEdit = view.findViewById(R.id.lastNameEditText);
        EditText emailEdit = view.findViewById(R.id.emailEditText);

        Button loginButton = view.findViewById(R.id.loginButton);
        Button registerButton = view.findViewById(R.id.registerButton);
        //EditText usernameEdit = findViewById(R.id.usernameEditText);


        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                //Log.i("LoginFragment", "in onText()");
                setHost(hostEdit.getText().toString());
                setPort(portEdit.getText().toString());
                setUsername(usernameEdit.getText().toString());
                setPassword(passwordEdit.getText().toString());
                setFirstName(firstNameEdit.getText().toString());
                setLastName(lastNameEdit.getText().toString());
                setEmail(emailEdit.getText().toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {
                //Log.i("LoginFragment", "in onAfterText()");
                if (usernameEdit.getText().toString().equals("") || passwordEdit.getText().toString().equals(""))
                {
                    loginButton.setEnabled(false);
                }
                else
                {
                    loginButton.setEnabled(true);
                }

                if (usernameEdit.getText().toString().equals("") || passwordEdit.getText().toString().equals("")
                        || firstNameEdit.getText().toString().equals("") || lastNameEdit.getText().toString().equals("")
                        || emailEdit.getText().toString().equals("") || hostEdit.getText().toString().equals("") || portEdit.getText().toString().equals(""))
                {
                    registerButton.setEnabled(false);
                }
                else
                {
                    registerButton.setEnabled(true);
                }
            }
        };
        //Log.i("LoginFragment", "Set Listeners");
        hostEdit.addTextChangedListener(textWatcher);
        portEdit.addTextChangedListener(textWatcher);
        usernameEdit.addTextChangedListener(textWatcher);
        passwordEdit.addTextChangedListener(textWatcher);
        firstNameEdit.addTextChangedListener(textWatcher);
        lastNameEdit.addTextChangedListener(textWatcher);
        emailEdit.addTextChangedListener(textWatcher);

        emailEdit.setText("");

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginRequest login = new LoginRequest(username, password);

                Handler loginHandler = new Handler() {
                    @Override
                    public void handleMessage(@NonNull Message msg) {
                        //super.handleMessage(msg);
                        Bundle bundle = msg.getData();
                        String loginResult = bundle.getString("test");


                        if (loginResult.equals("empty"))
                        {
                            Toast.makeText(view.getContext(),
                                    "Login Failed. Check username and password",
                                    Toast.LENGTH_LONG).show();
                            //System.out.println(loginResult);
                        }
                        else
                        {
                            String toastSuccessMsg = "Users First/Last name: \n" + DataCache.getInstance().getFirstName() + " / " + DataCache.getInstance().getLastName();
                            Toast.makeText(view.getContext(),
                                    toastSuccessMsg,
                                    Toast.LENGTH_LONG).show();
                            //System.out.println(loginResult);
                            listener.notifyDone();
                        }
                    }
                };

                LoginTask backgroundLogin = new LoginTask(loginHandler, login, host, port);
                ExecutorService executor = Executors.newSingleThreadExecutor();

                executor.submit(backgroundLogin);

            }
        });

        registerButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                setGender( ((RadioButton)view.findViewById(((RadioGroup)view.findViewById(R.id.radio_gender)).getCheckedRadioButtonId())).getText().toString() );

                RegisterRequest register = new RegisterRequest(username, password, email, firstName, lastName, gender.toLowerCase(Locale.ROOT));


                Handler registerHandler = new Handler() {
                    @Override
                    public void handleMessage(@NonNull Message msg) {
                        //super.handleMessage(msg);
                        Bundle bundle = msg.getData();
                        String registerResult = bundle.getString("test");

                        if (registerResult.equals("register failed"))
                        {
                            //getApplicationContext()
                            Toast.makeText(view.getContext(),
                                    "Register Failed. Username already in use",
                                    Toast.LENGTH_LONG).show();
                            System.out.println(registerResult);
                        }
                        else
                        {
                            String toastMSG = "Users First/Last name: \n" + DataCache.getInstance().getFirstName() + " / " + DataCache.getInstance().getLastName();
                            Toast.makeText(view.getContext(),
                                    toastMSG,
                                    Toast.LENGTH_LONG).show();
                            listener.notifyDone();
                            //System.out.println("Users First/Last name: " + DataCache.getInstance().getFirstName() + " / " + DataCache.getInstance().getLastName());
                        }
                    }
                };

                RegisterTask backgroundRegister = new RegisterTask(registerHandler, register, host, port);
                ExecutorService executor = Executors.newSingleThreadExecutor();

                executor.submit(backgroundRegister);

            }
        });

        return view;
    }

    public static class LoginTask implements Runnable {

        Handler handler;
        LoginRequest request;
        String serverHost;
        String serverPort;

        public LoginTask(Handler handler, LoginRequest request, String serverHost, String serverPort)
        {
            this.handler = handler;
            this.request = request;
            this.serverHost = serverHost;
            this.serverPort = serverPort;
        }

        @Override
        public void run() {
            ServerProxy proxy = new ServerProxy(this.serverHost, this.serverPort);
            LoginResult result = proxy.login(this.request);
            PersonResult person = proxy.getPerson(DataCache.getInstance().getPersonID(), DataCache.getInstance().getAuthToken());
            String message;
            if (result != null)
            {
                message = "authtoken: " + result.getAuthtoken();
            }
            else
            {
                message = "login failed";
                //fail
            }

            Log.i("LoginFragment", "country: " + DataCache.getInstance().getEvents().get(1).country);
            sendMessage(DataCache.getInstance().getAuthToken());
        }

        private void sendMessage(String result)
        {
            Message returnMessage = Message.obtain();

            Bundle bundle = new Bundle();
            bundle.putString("test", result);
            returnMessage.setData(bundle);

            this.handler.sendMessage(returnMessage);
        }
    }

    public static class RegisterTask implements Runnable {

        Handler handler;
        RegisterRequest request;
        String serverHost;
        String serverPort;

        public RegisterTask(Handler handler, RegisterRequest request, String serverHost, String serverPort)
        {
            this.handler = handler;
            this.request = request;
            this.serverHost = serverHost;
            this.serverPort = serverPort;
        }

        @Override
        public void run() {
            ServerProxy proxy = new ServerProxy(this.serverHost, this.serverPort);
            RegisterResult result = proxy.register(this.request);
            String message = "";

            if (result == null)
            {
                message = "register failed";
                //fail
            }
            else if (result.success)
            {
                message = "register success";

            }

            sendMessage(message);
        }

        private void sendMessage(String result)
        {
            Message returnMessage = Message.obtain();

            Bundle bundle = new Bundle();
            bundle.putString("test", result);
            returnMessage.setData(bundle);

            this.handler.sendMessage(returnMessage);
        }

    }

    public void setPassword(String inPass)
    {
        this.password = inPass;
    }
    public void setUsername(String inUser)
    {
        this.username = inUser;
    }
    public void setHost(String host) {
        this.host = host;
    }
    public void setPort(String port) {
        this.port = port;
    }
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public void setGender(String gender)
    {
        this.gender = gender;
    }
}