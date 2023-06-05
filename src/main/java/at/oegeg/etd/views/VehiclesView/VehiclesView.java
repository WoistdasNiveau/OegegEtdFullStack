package at.oegeg.etd.views.VehiclesView;

import at.oegeg.etd.DataTransferObjects.Response.VehicleResponse;
import at.oegeg.etd.DataTransferObjects.Services.Implementations.VehicleService;
import at.oegeg.etd.views.MainLayout;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import jakarta.annotation.security.RolesAllowed;


@PageTitle("Vehicles | OegegEtd")
@Route(value = "vehicles", layout = MainLayout.class)
@RouteAlias(value = "", layout = MainLayout.class)
@RolesAllowed({"USER","LEADER","ADMIN"})
public class VehiclesView extends VerticalLayout
{
    // == properties ==
    Grid<VehicleResponse> vehicleGrid = new Grid<>(VehicleResponse.class,false);
    TextField filterText = new TextField();
    VehicleForm vehicleForm;

    // == private fields ==
    private final VehicleService _vehicleService;

    public VehiclesView(VehicleService vehicleService)
    {
        _vehicleService = vehicleService;
        addClassName("list-view");
        setSizeFull();

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
        vehicleForm = new VehicleForm();
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

        Button addVehicleButton = new Button("Add Vehicle");
        addVehicleButton.addClickListener(e -> AddVehicle());

        HorizontalLayout toolbar = new HorizontalLayout(filterText,addVehicleButton);
        toolbar.addClassName("toolbar");
        return toolbar;

    }

    private void AddVehicle()
    {
        vehicleGrid.asSingleSelect().clear();
        EditVehicle(new VehicleResponse());
    }

    private void configureGrid()
    {
        vehicleGrid.addClassName("list-view");
        vehicleGrid.setSizeFull();

        //vehicleGrid.setColumns("Type", "Number", "Status", "Stand", "Works");
        vehicleGrid.addColumn(VehicleResponse::getType).setHeader("Type").setSortable(true).setTextAlign(ColumnTextAlign.CENTER);
        vehicleGrid.addColumn(VehicleResponse::getNumber).setHeader("Number").setSortable(true).setTextAlign(ColumnTextAlign.CENTER);
        vehicleGrid.addColumn(VehicleResponse::getStatus).setHeader("Status").setSortable(true).setTextAlign(ColumnTextAlign.CENTER);
        vehicleGrid.addColumn(VehicleResponse::getStand).setHeader("Stand").setSortable(true).setTextAlign(ColumnTextAlign.CENTER);
        vehicleGrid.addColumn(VehicleResponse::getWorkCount).setHeader("Works").setSortable(true).setTextAlign(ColumnTextAlign.CENTER);

        vehicleGrid.getColumns().forEach(col -> col.setAutoWidth(true));

        vehicleGrid.asSingleSelect().addValueChangeListener(e -> EditVehicle(e.getValue()));
    }

    private void EditVehicle(VehicleResponse vehicle)
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

}
