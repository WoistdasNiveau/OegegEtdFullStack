package at.oegeg.etd.views;

import at.oegeg.etd.DataTransferObjects.DisplayModels.VehicleDisplay;
import at.oegeg.etd.DataTransferObjects.DisplayModels.WorkDisplay;
import at.oegeg.etd.DataTransferObjects.Services.Implementations.VehicleService;
import at.oegeg.etd.DataTransferObjects.Services.Implementations.WorkService;
import at.oegeg.etd.Entities.Enums.Priorities;
import at.oegeg.etd.Repositories.IUserEntityRepository;
import at.oegeg.etd.views.Forms.VehicleForm;
import at.oegeg.etd.views.Forms.WorkForm;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;

import java.util.Comparator;
import java.util.List;

@PageTitle("Details | OegegEtd")
@Route(value = "vehicle", layout = MainLayout.class)
@RolesAllowed({"USER","LEADER","ADMIN"})
public class VehicleDetailsView extends VerticalLayout implements HasUrlParameter<String>
{
    // == private fields ==
    private VehicleDisplay vehicleDisplay;
    private List<WorkDisplay> workDisplay;
    private final VehicleService _vehicleService;
    private final WorkService _workService;
    private String identifier;

    // == view fields ==
    private WorkForm workForm;
    private Grid<VehicleDisplay> vehicleGrid = new Grid<>(VehicleDisplay.class, false);
    private Grid<WorkDisplay> workGrid = new Grid<>(WorkDisplay.class, false);

    private VehicleForm vehicleForm;
    private Button addWorkButton = new Button();

    // == constructor ==
    public VehicleDetailsView(VehicleService vehicleService, IUserEntityRepository userEntityRepository, WorkService workService)
    {
        this._vehicleService = vehicleService;
        this._workService = workService;

        workForm = new WorkForm(userEntityRepository);
        ConfigureVehicleForm();
    }

    // == public methods ==

    @Override
    public void setParameter(BeforeEvent beforeEvent, String s)
    {
        identifier = s;

        try
        {
            vehicleDisplay = _vehicleService.FindVehicleByIdentifier(identifier);
            workDisplay = vehicleDisplay.getWorks();
        }
        catch (Exception ex)
        {
            UI.getCurrent().getPage().setLocation("/");
        }

        ConfigureVehicleGrid();
        ConfigureWorksGrid();
        ConfigureVehicleForm();
        ConfigureWorkForm();

        addWorkButton.setText("Add Work");
        addWorkButton.addClickListener(e -> AddWork());
        HorizontalLayout toolbar = new HorizontalLayout(new H1("Works"),addWorkButton);
        toolbar.setDefaultVerticalComponentAlignment(Alignment.CENTER);

        VerticalLayout content1 = new VerticalLayout(new H1("Vehicle"), vehicleGrid,toolbar, workGrid);
        content1.setFlexGrow(1,vehicleGrid);
        content1.setFlexGrow(8,workGrid);
        HorizontalLayout content2 = new HorizontalLayout(content1,vehicleForm, workForm);
        content2.setFlexGrow(2,content1);
        content2.setFlexGrow(1,vehicleForm);
        content2.setFlexGrow(1,workForm);
        content2.setSizeFull();

        add(
                content2
        );

        CloseEditor();
        CloseWorkEditor();
    }

    // == private methods ==

    private void ConfigureVehicleGrid()
    {
        vehicleGrid.addClassName("list-view");
        vehicleGrid.setAllRowsVisible(true);
        vehicleGrid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);

        vehicleGrid.addColumn(VehicleDisplay::getType).setHeader("Type").setTextAlign(ColumnTextAlign.CENTER);
        vehicleGrid.addColumn(VehicleDisplay::getNumber).setHeader("Number").setTextAlign(ColumnTextAlign.CENTER);
        vehicleGrid.addColumn(VehicleDisplay::getStatus).setHeader("Status").setTextAlign(ColumnTextAlign.CENTER);
        vehicleGrid.addColumn(VehicleDisplay::getStand).setHeader("Stand").setTextAlign(ColumnTextAlign.CENTER);
        vehicleGrid.addColumn(VehicleDisplay::getWorkCount).setHeader("Works").setTextAlign(ColumnTextAlign.CENTER);

        vehicleGrid.getColumns().forEach(col -> col.setAutoWidth(true));
        vehicleGrid.asSingleSelect().addValueChangeListener(t -> EditVehicle(t.getValue()));

        vehicleGrid.setItems(vehicleDisplay);
    }

    private void ConfigureWorksGrid()
    {
        workGrid.addClassName("list-view");
        workGrid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);

        workGrid.addColumn(WorkDisplay::getResponsiblePerson).setHeader("Responsible Person").setSortable(true).setTextAlign(ColumnTextAlign.CENTER)
                .setComparator((r1,r2) -> r1.getResponsiblePerson().compareToIgnoreCase(r2.getResponsiblePerson()));
        workGrid.addColumn(WorkDisplay::getDescription).setHeader("Description").setSortable(true).setTextAlign(ColumnTextAlign.CENTER)
                .setComparator((d1, d2) -> d1.getDescription().compareToIgnoreCase(d2.getDescription()));
        workGrid.addColumn(CreatePrioritiesRenderer()).setHeader("Priority").setSortable(true).setTextAlign(ColumnTextAlign.CENTER)
                .setComparator(Comparator.comparingInt(p -> p.getPriority().ordinal()));

        workGrid.asSingleSelect().addValueChangeListener(e -> EditWork(e.getValue()));
        workGrid.setItems(workDisplay);
    }

    private void ConfigureVehicleForm()
    {
        vehicleForm = new VehicleForm(true);
        vehicleForm.setWidth("25em");

        vehicleForm.AddListener(VehicleForm.SaveEvent.class, this::UpdateVehicle);
        vehicleForm.AddListener(VehicleForm.DeleteEvent.class, this::DeleteVehicle);
        vehicleForm.AddListener(VehicleForm.CloseEvent.class, e -> CloseEditor());
    }

    private void ConfigureWorkForm()
    {
        workForm.setWidth("25em");

        workForm.AddListener(WorkForm.SaveEvent.class, this::UpdateWork);
        workForm.AddListener(WorkForm.DeleteEvent.class, this::DeleteWork);
        workForm.AddListener(WorkForm.CloseEvent.class, e-> CloseWorkEditor());
    }

    private void AddWork()
    {
        workGrid.asSingleSelect().clear();
        EditWork(new WorkDisplay());
    }
    private void EditWork(WorkDisplay display)
    {
        if(display == null)
        {
            CloseWorkEditor();
            return;
        }
        workForm.SetSelectedWork(display);
        workForm.deleteButton.setVisible(display.getIdentifier() != null && !display.getIdentifier().isEmpty());
        workForm.setVisible(true);
        addClassName("editing");
    }
    private void CloseWorkEditor()
    {
        workForm.SetSelectedWork(null);
        workForm.setVisible(false);
        removeClassName("editing");
    }

    private void DeleteWork(WorkForm.DeleteEvent event)
    {
        _workService.DeleteWork(event.getWorkDisplay().getIdentifier());
        ReloadVehicle();
        CloseEditor();
    }

    private void UpdateWork(WorkForm.SaveEvent event)
    {
        _workService.SaveWork(event.getWorkDisplay(), vehicleDisplay.getIdentifier());
        ReloadVehicle();
        CloseEditor();
    }

    private void DeleteVehicle(VehicleForm.DeleteEvent event)
    {
        _vehicleService.DeleteVehicle(event.getVehicle().getIdentifier());
        UI.getCurrent().getPage().setLocation("/vehicles");
    }

    private void UpdateVehicle(VehicleForm.SaveEvent event)
    {
        _vehicleService.UpdateVehicle(event.getVehicle());
        ReloadVehicle();
        CloseEditor();
    }

    private void CloseEditor()
    {
        vehicleForm.SetVehicle(null);
        vehicleForm.setVisible(false);
        removeClassName("editing");
        vehicleGrid.deselectAll();
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

    private void ReloadVehicle()
    {
        vehicleDisplay = _vehicleService.FindVehicleByIdentifier(identifier);
        workDisplay = vehicleDisplay.getWorks();
        vehicleGrid.setItems(vehicleDisplay);
        workGrid.setItems(workDisplay);
    }

    private static ComponentRenderer<Span, WorkDisplay> CreatePrioritiesRenderer()
    {
        return new ComponentRenderer<>(workDisplay ->
        {
            Span priorityNone = new Span(Priorities.NONE.name());
            priorityNone.getElement().getThemeList().add("badge contrast");
            Span result = priorityNone;
            switch (workDisplay.getPriority())
            {
                case NONE ->
                {
                    break;
                }
                case LOW ->
                {
                    Span priorityLow = new Span(Priorities.LOW.name());
                    priorityLow.getElement().getThemeList().add("badge success primary");
                    result = priorityLow;
                }
                case MEDIUM ->
                {
                    Span priorityMedium = new Span(Priorities.MEDIUM.name());
                    priorityMedium.getElement().getThemeList().add("badge pill primary");
                    result = priorityMedium;
                }
                case HIGH ->
                {
                    Span priorityHigh = new Span(Priorities.HIGH.name());
                    priorityHigh.getElement().getThemeList().add("badge error primary");
                    result = priorityHigh;
                }
                case DONE ->
                {
                    Span priorityDone = new Span(Priorities.DONE.name());
                    priorityDone.getElement().getThemeList().add("badge contrast primary");
                    result = priorityDone;
                }
            }
            return result;
        });
    }
}
