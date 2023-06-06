package at.oegeg.etd.views;

import at.oegeg.etd.DataTransferObjects.Services.Implementations.UserService;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import jakarta.annotation.security.RolesAllowed;

@PageTitle("User | OegegEtd")
@Route(value = "init")
@AnonymousAllowed
public class SetInitialPasswordView extends VerticalLayout implements HasUrlParameter<String>
{
    // == view fields ==
    H2 title = new H2("Oegeg Etd | Set Password");
    PasswordField choosePasswordField = new PasswordField("Choose password");
    PasswordField confirmPasswordField = new PasswordField("Confirm password");
    Button setPasswordButton = new Button("Set password");

    // == fields ==
    private String identifier;
    private final UserService _userService;

    // == constructor ==
    public SetInitialPasswordView(UserService userService)
    {
        _userService = userService;

        ConfigureButton();

        SetContent();
    }

    // == public methods ==
    @Override
    public void setParameter(BeforeEvent beforeEvent, String s)
    {
        identifier = s;

        if(!_userService.ExistsByIdentifier(identifier))
        {
            UI.getCurrent().getPage().setLocation("/login");
            return;
        }
    }

    // == private methods ==
    private void SetContent()
    {
        VerticalLayout layout = new VerticalLayout(title,choosePasswordField, confirmPasswordField,setPasswordButton);
        layout.setSizeFull();
        layout.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        layout.setAlignItems(FlexComponent.Alignment.CENTER);
        add(
                layout
        );
    }

    private void ConfigureButton()
    {
        setPasswordButton.addClickListener(t -> SetPasswort());
    }

    private void SetPasswort()
    {
        if(choosePasswordField.getValue().equals(confirmPasswordField.getValue()))
        {
            _userService.SetInitialPassword(identifier, choosePasswordField.getValue());
            UI.getCurrent().getPage().setLocation("/login");
        }
    }
}






















