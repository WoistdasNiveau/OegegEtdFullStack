package at.oegeg.etd.views;

import at.oegeg.etd.DataTransferObjects.DisplayModels.UserDisplay;
import at.oegeg.etd.DataTransferObjects.DisplayModels.VehicleDisplay;
import at.oegeg.etd.DataTransferObjects.DisplayModels.WorkDisplay;
import at.oegeg.etd.DataTransferObjects.Services.Implementations.EmailSenderService;
import at.oegeg.etd.DataTransferObjects.Services.Implementations.UserService;
import at.oegeg.etd.DataTransferObjects.Services.Implementations.VehicleService;
import at.oegeg.etd.DataTransferObjects.Services.Implementations.WorkService;
import at.oegeg.etd.Entities.UserEntity;
import at.oegeg.etd.views.Forms.UserForm;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;

import java.util.Comparator;
import java.util.Set;

import static at.oegeg.etd.views.CustomRenderer.CreatePrioritiesRenderer;
import static at.oegeg.etd.views.CustomRenderer.RoleGridRenderer;

@PageTitle("User details | OegegEtd")
@Route(value = "user", layout = MainLayout.class)
@RolesAllowed("ADMIN")
public class UserDetailsView extends VerticalLayout implements HasUrlParameter<String>
{
    // == view fields ==
    H2 userTilte = new H2("User");
    H2 responsibleForTitle = new H2("Responsible for");
    TextField responsibleForField = new TextField();
    H2 createdVehiclesTitle = new H2("Created vehicles");
    TextField createdVehiclesField = new TextField();
    H2 updatedVehiclesTitle = new H2("Updated vehicles");
    TextField updatedVehiclesField = new TextField();
    H2 createdWorksTitle = new H2("Created works");
    TextField createdWorksField = new TextField();
    H2 updatedWorksTitle = new H2("Updated works");
    TextField updatedWorksField = new TextField();
    Grid<UserDisplay> userGrid = new Grid<>(UserDisplay.class,false);
    Button resendButton = new Button("Resend email");
    Grid<VehicleDisplay> createdVehiclesGrid = new Grid<>(VehicleDisplay.class, false);
    Grid<VehicleDisplay> updatedVehiclesGrid = new Grid<>(VehicleDisplay.class, false);
    Grid<WorkDisplay> responsibleForGrid = new Grid<>(WorkDisplay.class, false);
    Grid<WorkDisplay> createdWorksGrid = new Grid<>(WorkDisplay.class, false);
    Grid<WorkDisplay> updatedWorksGrid = new Grid<>(WorkDisplay.class, false);

    UserForm userForm;

    // == private fields ==
    private final UserService _userService;
    private final VehicleService _vehicleService;
    private final WorkService _workService;
    private final EmailSenderService _emailSenderService;
    private String userIdentifier;

    // == constructor ==
    public UserDetailsView(UserService _userService, VehicleService _vehicleService, WorkService _workService, EmailSenderService emailSenderService)
    {
        this._userService = _userService;
        this._vehicleService = _vehicleService;
        this._workService = _workService;
        this._emailSenderService = emailSenderService;
    }

    // == public methods ==
    @Override
    public void setParameter(BeforeEvent beforeEvent, String s)
    {
        userIdentifier = s;

        ConfigureResendButton();
        ConfigureUserForm();
        ConfigureGrids();
        ConfigureTextFields();
        userReload();
        ReloadAll();

        SetContent();
    }

    // == private methods ==

    private void SetContent()
    {
        HorizontalLayout userToolBar = new HorizontalLayout(userTilte, resendButton);

        HorizontalLayout responsibleForToolbar = new HorizontalLayout(responsibleForTitle, responsibleForField);
        VerticalLayout responsibleForView = new VerticalLayout(responsibleForToolbar, responsibleForGrid);

        HorizontalLayout createdWorksToolbar = new HorizontalLayout(createdWorksTitle, createdWorksField);
        VerticalLayout createdWorksView = new VerticalLayout(createdWorksToolbar, createdWorksGrid);

        HorizontalLayout updatedWorksToolbar = new HorizontalLayout(updatedWorksTitle, updatedWorksField);
        VerticalLayout updatedWorksView = new VerticalLayout(updatedWorksToolbar, updatedWorksGrid);

        HorizontalLayout createdVehiclesToolbar = new HorizontalLayout(createdVehiclesTitle, createdVehiclesField);
        VerticalLayout createdVehiclesView = new VerticalLayout(createdVehiclesToolbar,createdVehiclesGrid);

        HorizontalLayout updatedVehiclesToolbar = new HorizontalLayout(updatedVehiclesTitle, updatedVehiclesField);
        VerticalLayout updatedVehiclesView = new VerticalLayout(updatedVehiclesToolbar, updatedVehiclesGrid);

        VerticalLayout finalView = new VerticalLayout(userToolBar, userGrid, responsibleForView, createdWorksView, updatedWorksView, createdVehiclesView, updatedVehiclesView);

        finalView.setSizeFull();
        HorizontalLayout formView = new HorizontalLayout(finalView,userForm);
        formView.setFlexGrow(2,finalView);
        formView.setFlexGrow(1,userForm);
        formView.setSizeFull();
        add(
                formView
        );
    }

    private void userReload()
    {
        userGrid.setItems(_userService.FindByIdentifier(userIdentifier));
    }
    private void responibleGridReload(String filter)
    {
        responsibleForGrid.setItems(_workService.FindAllByResponsiblePerson(userIdentifier, filter));
    }

    private void createdWorksGridReload(String filter)
    {
        createdWorksGrid.setItems(_workService.FindAllByCreatedBy(userIdentifier, filter));
    }

    private void updatedWorksGridReload(String filter)
    {
        updatedWorksGrid.setItems(_workService.FindAllByUpdatedBy(userIdentifier,filter));
    }

    private void createdVehiclesReload(String filter)
    {
        createdVehiclesGrid.setItems(_vehicleService.FindAllbyCreatedBy(userIdentifier, filter));
    }

    private void updatedVehiclesGridReload(String filter)
    {
        updatedVehiclesGrid.setItems(_vehicleService.FindAllbyUpdatedBy(userIdentifier,filter));
    }

    private void ReloadAll()
    {
        userReload();
        responibleGridReload("");
        createdWorksGridReload("");
        updatedWorksGridReload("");
        createdVehiclesReload("");
        updatedVehiclesGridReload("");
    }

    private void ConfigureUserForm()
    {
        userForm = new UserForm(_userService);
        userForm.deleteButton.setVisible(true);
        userForm.setWidth("25em");

        userForm.AddListener(UserForm.SaveEvent.class, this::UpdateUser);
        userForm.AddListener(UserForm.DeleteEvent.class, this::DeleteUser);
        userForm.AddListener(UserForm.CloseEvent.class, t -> CloseUserForm());

        userForm.setVisible(false);
    }

    private void OpenUserForm(UserDisplay userDisplay)
    {
        if(userDisplay == null)
        {
            CloseUserForm();
            return;
        }
        userForm.SetSelectedUser(userDisplay);
        userForm.setVisible(true);
       addClassName("editing");
    }

    private void UpdateUser(UserForm.SaveEvent event)
    {
        _userService.SaveUser(event.GetUserDisplay());
        CloseUserForm();
        ReloadAll();
    }

    public void DeleteUser(UserForm.DeleteEvent event)
    {
        _userService.DeleteUser(event.GetUserDisplay().getIdentifier());
        CloseUserForm();
        UI.getCurrent().getPage().setLocation("/user");
    }

    private void CloseUserForm()
    {
        userForm.SetSelectedUser(null);
        userForm.setVisible(false);
        removeClassName("editing");

        userGrid.deselectAll();
    }

    private void ConfigureResendButton()
    {
        UserDisplay user = _userService.FindByIdentifier(userIdentifier);
        resendButton.setVisible(!user.isEnabled());
        resendButton.setEnabled(!user.isEnabled());

        resendButton.addClickListener(t -> _emailSenderService.SendSetPasswortMail("oliver01@kabsi.at", user.getIdentifier(), user.getName()));
    }

    private void ConfigureTextFields()
    {
        responsibleForField.setPlaceholder("Search responsibilities");
        responsibleForField.setClearButtonVisible(true);
        responsibleForField.setValueChangeMode(ValueChangeMode.LAZY);
        responsibleForField.addValueChangeListener(e -> responibleGridReload(responsibleForField.getValue()));

        createdWorksField.setPlaceholder("Search responsibilities");
        createdWorksField.setClearButtonVisible(true);
        createdWorksField.setValueChangeMode(ValueChangeMode.LAZY);
        createdWorksField.addValueChangeListener(e -> createdWorksGridReload(createdWorksField.getValue()));

        updatedWorksField.setPlaceholder("Search responsibilities");
        updatedWorksField.setClearButtonVisible(true);
        updatedWorksField.setValueChangeMode(ValueChangeMode.LAZY);
        updatedWorksField.addValueChangeListener(e -> updatedWorksGridReload(updatedWorksField.getValue()));

        createdVehiclesField.setPlaceholder("Search responsibilities");
        createdVehiclesField.setClearButtonVisible(true);
        createdVehiclesField.setValueChangeMode(ValueChangeMode.LAZY);
        createdVehiclesField.addValueChangeListener(e -> createdVehiclesReload(createdVehiclesField.getValue()));

        updatedVehiclesField.setPlaceholder("Search responsibilities");
        updatedVehiclesField.setClearButtonVisible(true);
        updatedVehiclesField.setValueChangeMode(ValueChangeMode.LAZY);
        updatedVehiclesField.addValueChangeListener(e -> updatedVehiclesGridReload(updatedVehiclesField.getValue()));
    }

    private void ConfigureGrids()
    {
        userGrid.addClassName("list-view");
        userGrid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);

        userGrid.addColumn(UserDisplay::getName).setHeader("Name").setTextAlign(ColumnTextAlign.CENTER).setSortable(true)
                .setComparator((u1,u2) -> u1.getName().compareToIgnoreCase(u2.getName()));

        userGrid.addColumn(UserDisplay::getEmail).setHeader("Email").setTextAlign(ColumnTextAlign.CENTER).setSortable(true)
                .setComparator((u1,u2) -> u1.getEmail().compareToIgnoreCase(u2.getEmail()));

        userGrid.addColumn(UserDisplay::getTelephoneNumber).setHeader("Telephone Number").setTextAlign(ColumnTextAlign.CENTER).setSortable(true)
                .setComparator((u1,u2) -> u1.getTelephoneNumber().compareToIgnoreCase(u2.getTelephoneNumber()));

        userGrid.addColumn(RoleGridRenderer()).setHeader("Role").setTextAlign(ColumnTextAlign.CENTER).setSortable(true)
                .setComparator(Comparator.comparingInt(p -> p.getRole().ordinal()));

        userGrid.addColumn(UserDisplay::isEnabled).setHeader("Enabled").setTextAlign(ColumnTextAlign.CENTER).setSortable(true);

        userGrid.addColumn(UserDisplay::getResponsibleFor).setHeader("Responsible For").setTextAlign(ColumnTextAlign.CENTER).setSortable(true)
                .setComparator(Comparator.comparingLong(UserDisplay::getResponsibleFor));

        userGrid.addColumn(UserDisplay::getCreatedVehicles).setHeader("Created Vehicles").setTextAlign(ColumnTextAlign.CENTER).setSortable(true)
                .setComparator(Comparator.comparingLong(UserDisplay::getCreatedVehicles));

        userGrid.addColumn(UserDisplay::getUpdatedVehicles).setHeader("Updated Vehicles").setTextAlign(ColumnTextAlign.CENTER).setSortable(true)
                .setComparator(Comparator.comparingLong(UserDisplay::getUpdatedVehicles));

        userGrid.addColumn(UserDisplay::getCreatedWorks).setHeader("Created Works").setTextAlign(ColumnTextAlign.CENTER).setSortable(true)
                .setComparator(Comparator.comparingLong(UserDisplay::getCreatedWorks));

        userGrid.addColumn(UserDisplay::getUpdatedWorks).setHeader("Updated Vehicles").setTextAlign(ColumnTextAlign.CENTER).setSortable(true)
                .setComparator(Comparator.comparingLong(UserDisplay::getUpdatedWorks));

        userGrid.getColumns().forEach(col -> col.setAutoWidth(true));
        userGrid.setHeight("6em");

        userGrid.asSingleSelect().addValueChangeListener(t -> OpenUserForm(t.getValue()));


        responsibleForGrid.addClassName("list-view");
        responsibleForGrid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);

        responsibleForGrid.addColumn(WorkDisplay::getVehicle).setHeader("Vehicle").setSortable(true).setTextAlign(ColumnTextAlign.CENTER)
                .setComparator((r1,r2) -> r1.getVehicle().compareToIgnoreCase(r2.getVehicle()));
        responsibleForGrid.addColumn(WorkDisplay::getDescription).setHeader("Description").setSortable(true).setTextAlign(ColumnTextAlign.CENTER)
                .setComparator((d1, d2) -> d1.getDescription().compareToIgnoreCase(d2.getDescription()));
        responsibleForGrid.addColumn(CreatePrioritiesRenderer()).setHeader("Priority").setSortable(true).setTextAlign(ColumnTextAlign.CENTER)
                .setComparator(Comparator.comparingInt(p -> p.getPriority().ordinal()));

        responsibleForGrid.asSingleSelect().addValueChangeListener( t -> UI.getCurrent().getPage().setLocation("vehicle/" + t.getValue().getVehicleIdentifier()));
        responsibleForGrid.setHeight("400px");


        createdWorksGrid.addClassName("list-view");
        createdWorksGrid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);

        createdWorksGrid.addColumn(WorkDisplay::getResponsiblePerson).setHeader("Responsible Person").setSortable(true).setTextAlign(ColumnTextAlign.CENTER)
                .setComparator((r1,r2) -> r1.getResponsiblePerson().compareToIgnoreCase(r2.getResponsiblePerson()));
        createdWorksGrid.addColumn(WorkDisplay::getVehicle).setHeader("Vehicle").setSortable(true).setTextAlign(ColumnTextAlign.CENTER)
                .setComparator((r1,r2) -> r1.getVehicle().compareToIgnoreCase(r2.getVehicle()));
        createdWorksGrid.addColumn(WorkDisplay::getDescription).setHeader("Description").setSortable(true).setTextAlign(ColumnTextAlign.CENTER)
                .setComparator((d1, d2) -> d1.getDescription().compareToIgnoreCase(d2.getDescription()));
        createdWorksGrid.addColumn(CreatePrioritiesRenderer()).setHeader("Priority").setSortable(true).setTextAlign(ColumnTextAlign.CENTER)
                .setComparator(Comparator.comparingInt(p -> p.getPriority().ordinal()));

        createdWorksGrid.asSingleSelect().addValueChangeListener( t -> UI.getCurrent().getPage().setLocation("vehicle/" + t.getValue().getVehicleIdentifier()));
        createdWorksGrid.setHeight("400px");


        updatedWorksGrid.addClassName("list-view");
        updatedWorksGrid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);

        updatedWorksGrid.addColumn(WorkDisplay::getResponsiblePerson).setHeader("Responsible Person").setSortable(true).setTextAlign(ColumnTextAlign.CENTER)
                .setComparator((r1,r2) -> r1.getResponsiblePerson().compareToIgnoreCase(r2.getResponsiblePerson()));
        updatedWorksGrid.addColumn(WorkDisplay::getVehicle).setHeader("Vehicle").setSortable(true).setTextAlign(ColumnTextAlign.CENTER)
                .setComparator((r1,r2) -> r1.getVehicle().compareToIgnoreCase(r2.getVehicle()));
        updatedWorksGrid.addColumn(WorkDisplay::getDescription).setHeader("Description").setSortable(true).setTextAlign(ColumnTextAlign.CENTER)
                .setComparator((d1, d2) -> d1.getDescription().compareToIgnoreCase(d2.getDescription()));
        updatedWorksGrid.addColumn(CreatePrioritiesRenderer()).setHeader("Priority").setSortable(true).setTextAlign(ColumnTextAlign.CENTER)
                .setComparator(Comparator.comparingInt(p -> p.getPriority().ordinal()));

        updatedWorksGrid.asSingleSelect().addValueChangeListener( t -> UI.getCurrent().getPage().setLocation("vehicle/" + t.getValue().getVehicleIdentifier()));
        updatedWorksGrid.setHeight("400px");


        createdVehiclesGrid.addClassName("list-view");
        createdVehiclesGrid.setSizeFull();
        createdVehiclesGrid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);

        createdVehiclesGrid.addColumn(VehicleDisplay::getType).setHeader("Type").setSortable(true).setTextAlign(ColumnTextAlign.CENTER);
        createdVehiclesGrid.addColumn(VehicleDisplay::getNumber).setHeader("Number").setSortable(true).setTextAlign(ColumnTextAlign.CENTER);
        createdVehiclesGrid.addColumn(VehicleDisplay::getStatus).setHeader("Status").setSortable(true).setTextAlign(ColumnTextAlign.CENTER);
        createdVehiclesGrid.addColumn(VehicleDisplay::getStand).setHeader("Stand").setSortable(true).setTextAlign(ColumnTextAlign.CENTER);
        createdVehiclesGrid.addColumn(VehicleDisplay::getWorkCount).setHeader("Works").setSortable(true).setTextAlign(ColumnTextAlign.CENTER);

        createdVehiclesGrid.asSingleSelect().addValueChangeListener( t -> UI.getCurrent().getPage().setLocation("vehicle/" + t.getValue().getIdentifier()));
        createdVehiclesGrid.setHeight("400px");


        updatedVehiclesGrid.addClassName("list-view");
        updatedVehiclesGrid.setSizeFull();
        updatedVehiclesGrid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);

        updatedVehiclesGrid.addColumn(VehicleDisplay::getType).setHeader("Type").setSortable(true).setTextAlign(ColumnTextAlign.CENTER);
        updatedVehiclesGrid.addColumn(VehicleDisplay::getNumber).setHeader("Number").setSortable(true).setTextAlign(ColumnTextAlign.CENTER);
        updatedVehiclesGrid.addColumn(VehicleDisplay::getStatus).setHeader("Status").setSortable(true).setTextAlign(ColumnTextAlign.CENTER);
        updatedVehiclesGrid.addColumn(VehicleDisplay::getStand).setHeader("Stand").setSortable(true).setTextAlign(ColumnTextAlign.CENTER);
        updatedVehiclesGrid.addColumn(VehicleDisplay::getWorkCount).setHeader("Works").setSortable(true).setTextAlign(ColumnTextAlign.CENTER);

        updatedVehiclesGrid.asSingleSelect().addValueChangeListener( t -> UI.getCurrent().getPage().setLocation("vehicle/" + t.getValue().getIdentifier()));
        updatedVehiclesGrid.setHeight("400px");
    }

}




























