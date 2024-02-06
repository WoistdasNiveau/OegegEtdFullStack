package at.oegeg.etd.views;

import at.oegeg.etd.Services.Implementations.UserService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.login.AbstractLogin;
import com.vaadin.flow.component.login.LoginForm;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.router.*;
import com.vaadin.flow.server.auth.AnonymousAllowed;

@Route("login")
@PageTitle("Login | OegegEtd")
@AnonymousAllowed
public class LoginView extends VerticalLayout implements BeforeEnterObserver
{
    // == private fields ==
    private LoginForm login = new LoginForm();
    private Dialog forgotPasswordDialog;

    // == private final fields ==
    private final UserService _userService;

    public LoginView(UserService userService)
    {
        _userService = userService;

        forgotPasswordDialog = SetUpDialog();

        addClassName("login-view");
        setSizeFull();

        setJustifyContentMode(JustifyContentMode.CENTER);
        setAlignItems(Alignment.CENTER);

        login.setAction("login");
        login.addForgotPasswordListener(this::OnForgotPasswordClicked);

        add(new H1("Test Application"), login, forgotPasswordDialog);
    }




    @Override
    public void beforeEnter(BeforeEnterEvent beforeEnterEvent)
    {
        if (beforeEnterEvent.getLocation()
                .getQueryParameters()
                .getParameters()
                .containsKey("error"))
        {
            login.setError(true);
        }
    }

    // == private methods ==
    private void OnForgotPasswordClicked(AbstractLogin.ForgotPasswordEvent forgotPasswordEvent)
    {
        forgotPasswordDialog.open();
    }

    private Dialog SetUpDialog()
    {
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle("Forgot Password");
        dialog.close();

        VerticalLayout verticalLayout = new VerticalLayout();
        EmailField emailField = new EmailField();
        emailField.setLabel("Email address");
        emailField.getElement().setAttribute("name", "email");
        emailField.setClearButtonVisible(true);
        emailField.setErrorMessage("Please enter your email adress");
        verticalLayout.add(emailField);

        Button resetPasswordButton = new Button("Reset Email");
        resetPasswordButton.addClickListener(t ->
        {
            if(emailField.isInvalid())
                return;
            _userService.ResetPassword(emailField.getValue());
            dialog.close();
        });
        Button cancelButton = new Button("Cancel", e -> dialog.close());

        dialog.add(verticalLayout);
        dialog.getFooter().add(cancelButton);
        dialog.getFooter().add(resetPasswordButton);

        return dialog;
    }

}
