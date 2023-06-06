package at.oegeg.etd.views;

import at.oegeg.etd.DataTransferObjects.DisplayModels.VehicleDisplay;
import at.oegeg.etd.DataTransferObjects.Services.Implementations.VehicleService;
import at.oegeg.etd.views.Forms.VehicleForm;
import at.oegeg.etd.views.MainLayout;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import jakarta.annotation.security.RolesAllowed;

import java.util.Objects;

import static at.oegeg.etd.Security.SecurityService.GetAuthorities;


@PageTitle("Vehicles | OegegEtd")
@Route(value = "vehicles", layout = MainLayout.class)
@RouteAlias(value = "", layout = MainLayout.class)
@RolesAllowed({"USER","LEADER","ADMIN"})
public class VehiclesView extends VerticalLayout
{
    // == properties ==
    Grid<VehicleDisplay> vehicleGrid = new Grid<>(VehicleDisplay.class,false);
    TextField filterText = new TextField();
    VehicleForm vehicleForm;
    Button addVehicleButton;

    // == private fields ==
    private final VehicleService _vehicleService;

    public VehiclesView(VehicleService vehicleService)
    {
        _vehicleService = vehicleService;
        addClassName("list-view");
        setSizeFull();

        addVehicleButton = new Button("Add Vehicle");

        addVehicleButton.setEnabled(Objects.requireNonNull(GetAuthorities()).contains("ROLE_ADMIN") ||
                Objects.requireNonNull(GetAuthorities()).contains("ROLE_LEADER"));
        addVehicleButton.setVisible(Objects.requireNonNull(GetAuthorities()).contains("ROLE_ADMIN") ||
                Objects.requireNonNull(GetAuthorities()).contains("ROLE_LEADER"));

        configureGrid();
        configureVehicleForm();

        add(
                getToolbar(),
                getContent()
        );
        
        UpdateVehicleList();
        CloseEditor();
    }

    private void CloseEditor()
    {
        vehicleForm.SetVehicle(null);
        vehicleForm.setVisible(false);
        removeClassName("editing");
    }

    private void UpdateVehicleList()
    {
        vehicleGrid.setItems(_vehicleService.FindAllVehicles(filterText.getValue()));
    }

    private Component getContent()
    {
        HorizontalLayout content = new HorizontalLayout(vehicleGrid, vehicleForm);
        content.setFlexGrow(2,vehicleGrid);
        content.setFlexGrow(1,vehicleForm);
        content.setSizeFull();

        return content;
    }

    private void configureVehicleForm()
    {
        vehicleForm = new VehicleForm(false);
        vehicleForm.setWidth("25em");

        vehicleForm.AddListener(VehicleForm.SaveEvent.class, this::SaveVehicle);
        vehicleForm.AddListener(VehicleForm.DeleteEvent.class, this::DeleteVehicle);
        vehicleForm.AddListener(VehicleForm.CloseEvent.class, e -> CloseEditor());
    }

    private void DeleteVehicle(VehicleForm.DeleteEvent event)
    {
        _vehicleService.DeleteVehicle(event.getVehicle().getIdentifier());
        UpdateVehicleList();
        CloseEditor();
    }

    private void SaveVehicle(VehicleForm.SaveEvent event)
    {
        _vehicleService.SaveVehicle(event.getVehicle());
        UpdateVehicleList();
        CloseEditor();
    }

    private Component getToolbar()
    {
        filterText.setPlaceholder("Filter by name");
        filterText.setClearButtonVisible(true);
        filterText.setValueChangeMode(ValueChangeMode.LAZY);
        filterText.addValueChangeListener(e -> UpdateVehicleList());

        //Button addVehicleButton = new Button("Add Vehicle");
        addVehicleButton.addClickListener(e -> AddVehicle());

        HorizontalLayout toolbar = new HorizontalLayout(filterText,addVehicleButton);
        toolbar.addClassName("toolbar");
        return toolbar;

    }

    private void AddVehicle()
    {
        vehicleGrid.asSingleSelect().clear();
        EditVehicle(new VehicleDisplay());
    }

    private void configureGrid()
    {
        vehicleGrid.addClassName("list-view");
        vehicleGrid.setSizeFull();
        vehicleGrid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);

        //vehicleGrid.setColumns("Type", "Number", "Status", "Stand", "Works");
        vehicleGrid.addColumn(VehicleDisplay::getType).setHeader("Type").setSortable(true).setTextAlign(ColumnTextAlign.CENTER);
        vehicleGrid.addColumn(VehicleDisplay::getNumber).setHeader("Number").setSortable(true).setTextAlign(ColumnTextAlign.CENTER);
        vehicleGrid.addColumn(VehicleDisplay::getStatus).setHeader("Status").setSortable(true).setTextAlign(ColumnTextAlign.CENTER);
        vehicleGrid.addColumn(VehicleDisplay::getStand).setHeader("Stand").setSortable(true).setTextAlign(ColumnTextAlign.CENTER);
        vehicleGrid.addColumn(VehicleDisplay::getWorkCount).setHeader("Works").setSortable(true).setTextAlign(ColumnTextAlign.CENTER);

        vehicleGrid.asSingleSelect().addValueChangeListener(t -> GoToDetailsPage(t.getValue()));
        vehicleGrid.getColumns().forEach(col -> col.setAutoWidth(true));

    }

    private void EditVehicle(VehicleDisplay vehicle)
    {
        if(vehicle == null)
        {
            CloseEditor();
            return;
        }
        vehicleForm.SetVehicle(vehicle);
        vehicleForm.setVisible(true);
        addClassName("editing");
    }

    private void GoToDetailsPage(VehicleDisplay display)
    {
        UI.getCurrent().getPage().setLocation("vehicle/" + display.getIdentifier());
    }

    //private ComponentRenderer<Button, VehicleDisplay> WorksColumnRenderer()
    //{
    //    return new ComponentRenderer<Button, VehicleDisplay>(t ->
    //    {
    //        Button worksButton = new Button(String.valueOf(t.getWorkCount()));
    //        worksButton.getElement().setProperty("Vehicle",t.getIdentifier());
    //        worksButton.addClickListener(this::WorksColumnClickListener);
    //        return worksButton;
    //    });
    //}
    private void WorksColumnClickListener(ClickEvent<Button> buttonClickEvent)
    {
        UI.getCurrent().getPage().setLocation("vehicle/" + buttonClickEvent.getSource().getElement().getProperty("Vehicle"));
    }

}
