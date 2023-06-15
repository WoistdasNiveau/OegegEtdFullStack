package at.oegeg.etd.views.Forms;

import at.oegeg.etd.DataTransferObjects.DisplayModels.UserDisplay;
import at.oegeg.etd.Services.Implementations.UserService;
import at.oegeg.etd.Entities.Enums.Role;
import at.oegeg.etd.views.Converters.TelephoneNumberConverter;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.validator.EmailValidator;
import com.vaadin.flow.shared.Registration;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.regex.Pattern;

import static at.oegeg.etd.views.Renderers.BadgeRenderer.RoleRenderer;

public class UserForm extends FormLayout
{
    // == binder ==
    Binder<UserDisplay> binder = new BeanValidationBinder<>(UserDisplay.class);

    // == view Fields ==
    TextField nameField = new TextField("Name");
    EmailField emailField = new EmailField("Email");
    NumberField telephoneField = new NumberField("Telephone Number");
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
        ConfigureFields();
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

    private void ConfigureFields()
    {
        nameField.setClearButtonVisible(true);
        nameField.addValueChangeListener(t -> FieldsValid());

        emailField.setErrorMessage("Must be a valid Email");
        emailField.setClearButtonVisible(true);
        emailField.addValueChangeListener(t -> FieldsValid());

        telephoneField.setClearButtonVisible(true);
        telephoneField.addValueChangeListener(t -> FieldsValid());

        roleSelect.setValue(Role.USER);
        roleSelect.addValueChangeListener(t -> FieldsValid());
    }

    private void FieldsValid()
    {
        String emailRegex = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
        Pattern emailPattern = Pattern.compile(emailRegex);

        boolean nameValid = !nameField.isEmpty();
        boolean emailValid = !emailField.isEmpty() && emailPattern.matcher(emailField.getValue()).matches();
        boolean roleValid = !roleSelect.isEmpty();
        saveButton.setEnabled(nameValid && emailValid && roleValid);
    }

    private void ConfigureBinder()
    {
        binder.forField(nameField)
                .asRequired()
                .bind(UserDisplay::getName, UserDisplay::setName);
        binder.forField(emailField)
                .asRequired()
                .withValidator(new EmailValidator("Enter a valid email adress!"))
                .bind(UserDisplay::getEmail, UserDisplay::setEmail);
        binder.forField(telephoneField)
                .withConverter(new TelephoneNumberConverter())
                .bind(UserDisplay::getTelephoneNumber, UserDisplay::setTelephoneNumber);
        binder.forField(roleSelect).asRequired().bind(UserDisplay::getRole, UserDisplay::setRole);

    }

    private void ConfigureRoleBox()
    {
        roleSelect.setLabel("Role");
        roleSelect.setRenderer(RoleRenderer());
        roleSelect.setItems(Role.values());
        roleSelect.setValue(Role.USER);
        roleSelect.setEnabled(selectedUser == null || !SecurityContextHolder.getContext().getAuthentication().getName().equals(selectedUser.getIdentifier()));
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






















