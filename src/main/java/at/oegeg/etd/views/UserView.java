package at.oegeg.etd.views;

import at.oegeg.etd.DataTransferObjects.DisplayModels.UserDisplay;
import at.oegeg.etd.Services.Implementations.UserService;
import at.oegeg.etd.views.Forms.UserForm;
import at.oegeg.etd.views.States.UserState;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;
import lombok.Getter;
import lombok.Setter;

import java.util.Comparator;
import java.util.Objects;

import static at.oegeg.etd.Security.SecurityService.GetAuthorities;
import static at.oegeg.etd.views.Renderers.BadgeRenderer.RoleGridRenderer;
import static at.oegeg.etd.views.Renderers.IsEnabledRenderer.EnabledRenderer;

@PageTitle("User | OegegEtd")
@Route(value = "users", layout = MainLayout.class)
@RolesAllowed({"LEADER","ADMIN"})
@Getter
@Setter
public class UserView extends VerticalLayout
{
    // == fields ==
    private final UserService _userService;

    // == view fields ==
    private TextField filterText = new TextField();
    private Button addUserButton = new Button("Add User");
    private Grid<UserDisplay> userGrid = new Grid<>(UserDisplay.class, false);
    private UserForm userForm;
    private UserState _userState;

    // == constructor ==
    public UserView(UserService userService, UserState state)
    {
        _userService = userService;
        _userState = state;

        ConfigureGrid();
        ConfigureTextField();
        ConfigureUserForm();
        ConfigureAddUserButton();

        SetContent();
    }

    // == private methods ==
    private void SetContent()
    {
       HorizontalLayout toolbar = new HorizontalLayout(filterText, addUserButton);
       VerticalLayout content1 = new VerticalLayout(toolbar, userGrid);
       HorizontalLayout content2 = new HorizontalLayout(content1, userForm);
       content2.setFlexGrow(2,content1);
       content2.setFlexGrow(1,userForm);
       content2.setSizeFull();

       add(
            content2
                );
       CloseUserForm();
    }
    private void ConfigureGrid()
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

        userGrid.addColumn(EnabledRenderer()).setHeader("Enabled").setTextAlign(ColumnTextAlign.CENTER).setSortable(true);

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

        if(Objects.requireNonNull(GetAuthorities()).contains("ROLE_ADMIN"))
            userGrid.asSingleSelect().addValueChangeListener(t -> NavigateToDetails(t.getValue().getIdentifier()));

        ReloadUsers("");
    }

    private void NavigateToDetails(String token)
    {
        _userState.setIdentifier(token);
        UI.getCurrent().getPage().setLocation("user/" + token);
    }

    private void ConfigureTextField()
    {
        filterText.setPlaceholder("Search by Name");
        filterText.setClearButtonVisible(true);
        filterText.setValueChangeMode(ValueChangeMode.LAZY);
        filterText.addValueChangeListener(t -> ReloadUsers(t.getValue()));
    }
    private void ReloadUsers(String name)
    {
        userGrid.setItems(_userService.GetAllUsers(name));
    }

    private void ConfigureUserForm()
    {
        userForm = new UserForm(_userService);
        userForm.setWidth("25em");
        userForm.deleteButton.setVisible(false);

        userForm.AddListener(UserForm.SaveEvent.class, this::CreateUser);
        userForm.AddListener(UserForm.CloseEvent.class, t -> CloseUserForm());
    }

    private void ConfigureAddUserButton()
    {
        addUserButton.addClickListener(t -> OpenUserForm(new UserDisplay()));
    }

    private void CreateUser(UserForm.SaveEvent event)
    {
        _userService.SaveUser(event.GetUserDisplay());
        ReloadUsers(filterText.getValue());
        CloseUserForm();
    }

    private void OpenUserForm(UserDisplay userDisplay)
    {
        if(userDisplay == null)
        {
            CloseUserForm();
            return;
        }
        userForm.SetSelectedUser(userDisplay);
        //userForm.deleteButton.setVisible(userDisplay.getIdentifier() != null && !userDisplay.getIdentifier().isEmpty());
        userForm.setVisible(true);
        addClassName("editing");
    }
    private void CloseUserForm()
    {
        userForm.SetSelectedUser(null);
        userForm.setVisible(false);
        removeClassName("editing");
    }
}































