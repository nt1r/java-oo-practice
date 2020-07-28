package com.twu.views.login;

import android.os.Bundle;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.RadioButton;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.twu.R;
import com.twu.models.DataContainer;
import com.twu.models.user.AdminUser;
import com.twu.models.user.NormalUser;
import com.twu.models.user.User;
import com.twu.router.NavigationHost;
import com.twu.views.MainFragment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class LoginFragment extends Fragment {
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.login_fragment, container, false);

        /* find view by id */
        MaterialButton loginButton = view.findViewById(R.id.login_button);
        MaterialButton quitButton = view.findViewById(R.id.quit_button);
        TextInputLayout usernameTextInputLayout = view.findViewById(R.id.username_text_input);
        TextInputLayout passwordTextInputLayout = view.findViewById(R.id.password_text_input);
        TextInputEditText usernameEditText = view.findViewById(R.id.username_edit_text);
        TextInputEditText passwordEditText = view.findViewById(R.id.password_edit_text);
        RadioButton normalUserRatioButton = view.findViewById(R.id.normal_user_radio_button);
        RadioButton adminUserRatioButton = view.findViewById(R.id.admin_user_radio_button);
        /* find view by id */

        /* set listeners */
        normalUserRatioButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    passwordTextInputLayout.setVisibility(View.GONE);
                }
            }
        });

        adminUserRatioButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    passwordTextInputLayout.setVisibility(View.VISIBLE);
                }
            }
        });

        quitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Objects.requireNonNull(getActivity()).finish();
                System.exit(0);
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // check if username and password are valid
                if (!isUsernameValid(usernameEditText.getText())) {
                    usernameTextInputLayout.setError(getString(R.string.error_username_empty));
                    return;
                }

                if (passwordTextInputLayout.getVisibility() == View.VISIBLE && !isPasswordValid(passwordEditText.getText())) {
                    passwordTextInputLayout.setError(getString(R.string.error_password_empty));
                    return;
                }

                usernameTextInputLayout.setError(null);
                passwordTextInputLayout.setError(null);

                String username = usernameEditText.getText().toString();
                String password = passwordEditText.getText().toString();
                User user = null;
                if (normalUserRatioButton.isChecked()) {
                    user = new NormalUser(username);
                } else if (adminUserRatioButton.isChecked()) {
                    user = new AdminUser(username, password);
                } else {
                    throw new Error("Unknown Error. Login, Selecting User");
                }

                DataContainer.getInstance().setUser(user);
                List<String> menuItemNameList = generateMenuItemNameList(user);
                DataContainer.getInstance().setMenuItemNameList(menuItemNameList);

                assert user != null;
                boolean loginSuccess = user.login();
                if (loginSuccess) {
                    ((NavigationHost) getActivity()).navigateTo(new MainFragment(), true);
                } else {
                    // TODO: 2020/7/27 login failed. 
                }
            }
        });
        /* set listeners */

        return view;
    }

    private boolean isUsernameValid(Editable username) {
        return username != null && username.length() > 0;
    }

    private boolean isPasswordValid(Editable password) {
        return password != null && password.length() > 0;
    }

    private List<String> generateMenuItemNameList(User user) {
        String[] menuItemNameList = {};
        if (user instanceof NormalUser) {
            menuItemNameList = getResources().getStringArray(R.array.normal_user_menu_item_name_list);
        } else if (user instanceof AdminUser) {
            menuItemNameList = getResources().getStringArray(R.array.admin_user_menu_item_name_list);
        }

        return Arrays.asList(menuItemNameList);
    }
}
