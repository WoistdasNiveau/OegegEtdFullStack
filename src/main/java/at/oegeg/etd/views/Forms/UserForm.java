package at.oegeg.etd.views.Forms;

import at.oegeg.etd.DataTransferObjects.DisplayModels.UserDisplay;
import at.oegeg.etd.DataTransferObjects.Services.Implementations.UserService;
import at.oegeg.etd.Entities.Enums.Role;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.shared.Registration;

import static at.oegeg.etd.views.CustomRenderer.RoleRenderer;

public class UserForm extends FormLayout
{
    // == binder ==
    Binder<UserDisplay> binder = new BeanValidationBinder<>(UserDisplay.class);

    // == view Fields ==
    TextField nameField = new TextField("Name");
    TextField emailField = new TextField("Email");
    TextField telephoneField = new TextField("Telephone Number");
    public Select<Role> roleSelect = new Select<>();
    Button saveButton = new Button("Save");
    public Button deleteButton = new Button("Delete");
    Button cancelButton = new Button("Cancel");

    // == private fields ==
    private final UserService _userService;
    private UserDisplay selectedUser;

    // == constructor ==
    public UserForm(UserService userService)
    {
        _userService = userService;

        ConfigureButtonLayout();
        ConfigureBinder();
        ConfigureRoleBox();
        ConfigureContent();
    }

    // == public methods ==
    public void SetSelectedUser(UserDisplay userDisplay)
    {
        selectedUser = userDisplay;
        binder.readBean(userDisplay);
    }

    // == private methods ==
    private void ConfigureContent()
    {
        HorizontalLayout buttonLayout = new HorizontalLayout(saveButton,deleteButton,cancelButton);
        add(
                nameField,
                emailField,
                telephoneField,
                roleSelect,
                buttonLayout
        );
    }

    private void ConfigureBinder()
    {
        binder.bind(nameField, UserDisplay::getName, UserDisplay::setName);
        binder.bind(emailField, UserDisplay::getEmail, UserDisplay::setEmail);
        binder.bind(telephoneField, UserDisplay::getTelephoneNumber, UserDisplay::setTelephoneNumber);
        binder.bind(roleSelect, UserDisplay::getRole, UserDisplay::setRole);
    }

    private void ConfigureRoleBox()
    {
        roleSelect.setLabel("Role");
        roleSelect.setRenderer(RoleRenderer());
        roleSelect.setItems(Role.values());
    }

    private Component ConfigureButtonLayout()
    {
        saveButton.setText("Save");
        deleteButton.setText("Delete");
        cancelButton.setText("Cancel");

        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        deleteButton.addThemeVariants(ButtonVariant.LUMO_ERROR);
        cancelButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        saveButton.addClickListener(event -> ValidateAndSave());
        deleteButton.addClickListener(event -> fireEvent(new UserForm.DeleteEvent(this, selectedUser)));
        cancelButton.addClickListener(event -> fireEvent(new UserForm.CloseEvent(this)));

        saveButton.addClickShortcut(Key.ENTER);
        cancelButton.addClickShortcut(Key.ESCAPE);

        return new HorizontalLayout(saveButton, deleteButton, cancelButton);
    }

    private void ValidateAndSave()
    {
        try
        {
            binder.writeBean(selectedUser);
            fireEvent(new SaveEvent(this, selectedUser));
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }


    // == Events ==
    public static abstract class UserFormEvent extends ComponentEvent<UserForm>
    {
        private UserDisplay userDisplay;
        protected UserFormEvent(UserForm source, UserDisplay userDisplay)
        {
            super(source,false);
            this.userDisplay = userDisplay;
        }

        public UserDisplay GetUserDisplay()
        {
            return userDisplay;
        }
    }

    public static class SaveEvent extends UserForm.UserFormEvent
    {
        SaveEvent(UserForm source, UserDisplay response)
        {
            super(source,response);
        }
    }

    public static class DeleteEvent extends UserForm.UserFormEvent
    {
        DeleteEvent(UserForm source, UserDisplay response)
        {
            super(source,response);
        }
    }

    public static class CloseEvent extends UserForm.UserFormEvent
    {
        CloseEvent(UserForm source)
        {
            super(source,null);
        }
    }

    public <T extends ComponentEvent<?>> Registration AddListener(Class<T> eventType, ComponentEventListener<T> listener)
    {
        return getEventBus().addListener(eventType,listener);
    }
}






















