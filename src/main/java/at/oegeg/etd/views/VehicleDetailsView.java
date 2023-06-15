package at.oegeg.etd.views;

import at.oegeg.etd.DataTransferObjects.DisplayModels.VehicleDisplay;
import at.oegeg.etd.DataTransferObjects.DisplayModels.WorkDisplay;
import at.oegeg.etd.Services.Implementations.UserService;
import at.oegeg.etd.Services.Implementations.VehicleService;
import at.oegeg.etd.Services.Implementations.WorkService;
import at.oegeg.etd.views.Forms.VehicleForm;
import at.oegeg.etd.views.Forms.WorkForm;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamResource;
import jakarta.annotation.security.RolesAllowed;

import java.io.ByteArrayInputStream;
import java.util.Comparator;
import java.util.Objects;

import static at.oegeg.etd.Security.SecurityService.GetAuthorities;
import static at.oegeg.etd.views.Renderers.BadgeRenderer.CreatePrioritiesRenderer;

@PageTitle("Details | OegegEtd")
@Route(value = "vehicle", layout = MainLayout.class)
@RolesAllowed({"USER","LEADER","ADMIN"})
public class VehicleDetailsView extends VerticalLayout implements HasUrlParameter<String>
{
    // == private fields ==
    private final VehicleService _vehicleService;
    private final WorkService _workService;
    private String identifier;

    // == view fields ==
    WorkForm workForm;
    Grid<VehicleDisplay> vehicleGrid = new Grid<>(VehicleDisplay.class, false);
    Grid<WorkDisplay> workGrid = new Grid<>(WorkDisplay.class, false);
    VehicleForm vehicleForm;
    Button addWorkButton = new Button();
    Button downloadPdfButton = new Button("Download Pdf");
    Anchor downloadPdfAnchor;


    // == constructor ==
    public VehicleDetailsView(VehicleService vehicleService, UserService userEntityRepository, WorkService workService)
    {
        this._vehicleService = vehicleService;
        this._workService = workService;

        workForm = new WorkForm(userEntityRepository);
        ConfigureVehicleForm();

        addWorkButton.setEnabled(Objects.requireNonNull(GetAuthorities()).contains("ROLE_ADMIN") ||
                Objects.requireNonNull(GetAuthorities()).contains("ROLE_LEADER"));
        addWorkButton.setVisible(Objects.requireNonNull(GetAuthorities()).contains("ROLE_ADMIN") ||
                Objects.requireNonNull(GetAuthorities()).contains("ROLE_LEADER"));
    }

    // == public methods ==

    @Override
    public void setParameter(BeforeEvent beforeEvent, String s)
    {
        identifier = s;
        try
        {
            ReloadGrids("");

            VehicleDisplay vehicle = _vehicleService.FindVehicleByIdentifier(identifier);
            long i = (long)_vehicleService.GetWorkCount(identifier);
            vehicle.setWorkCount(i);
            //byte[] pdf = _vehicleService.DownloadVehiclePdf(identifier);
//
            //StreamResource resource = new StreamResource(vehicle.getNumber() + ".pdf", () -> new ByteArrayInputStream(pdf));
//
            //downloadPdfAnchor = new Anchor(resource, "Open pdf");
            //downloadPdfAnchor.getElement().setAttribute("target", "_blank");
        }
        catch (Exception ex)
        {
            UI.getCurrent().getPage().setLocation("/vehicles");
        }

        ConfigurePdfButton();
        ConfigureVehicleGrid();
        ConfigureWorksGrid();
        ConfigureVehicleForm();
        ConfigureWorkForm();

        addWorkButton.setText("Add Work");
        addWorkButton.addClickListener(e -> AddWork());
        HorizontalLayout toolbar = new HorizontalLayout(new H1("Works"),addWorkButton, downloadPdfAnchor);
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
    private void ConfigurePdfButton()
    {
        downloadPdfButton.addClickListener(t -> DownloadVehiclePdf());
    }

    private void DownloadVehiclePdf()
    {
        try
        {
            VehicleDisplay vehicle = _vehicleService.FindVehicleByIdentifier(identifier);
            byte[] pdf = _vehicleService.DownloadVehiclePdf(identifier);

            StreamResource resource = new StreamResource(vehicle.getNumber() + ".pdf", () -> new ByteArrayInputStream(pdf));

            downloadPdfAnchor = new Anchor(resource, "Open pdf");
            downloadPdfAnchor.getElement().setAttribute("target", "_blank");
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

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
        if(Objects.requireNonNull(GetAuthorities()).contains("ROLE_ADMIN") || Objects.requireNonNull(GetAuthorities()).contains("ROLE_LEADER"))
            vehicleGrid.asSingleSelect().addValueChangeListener(t -> EditVehicle(t.getValue()));
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


        if(Objects.requireNonNull(GetAuthorities()).contains("ROLE_ADMIN") || Objects.requireNonNull(GetAuthorities()).contains("ROLE_LEADER"))
            workGrid.asSingleSelect().addValueChangeListener(e -> EditWork(e.getValue()));
    }

    private void ReloadGrids(String filter)
    {
        VehicleDisplay vehicles = _vehicleService.FindVehicleByIdentifier(identifier);
        vehicles.setWorkCount(_vehicleService.GetWorkCount(identifier));
        vehicleGrid.setItems(vehicles);
        workGrid.setItems(_workService.FindAllByVehicle(identifier, filter));
        DownloadVehiclePdf();
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
        ReloadGrids("");
    }

    private void DeleteWork(WorkForm.DeleteEvent event)
    {
        _workService.DeleteWork(event.getWorkDisplay().getIdentifier());
        ReloadGrids("");
        CloseWorkEditor();
        ReloadGrids("");
    }

    private void UpdateWork(WorkForm.SaveEvent event)
    {
        _workService.SaveWork(event.getWorkDisplay(), identifier);
        ReloadGrids("");
        CloseWorkEditor();
    }

    private void DeleteVehicle(VehicleForm.DeleteEvent event)
    {
        _vehicleService.DeleteVehicle(event.getVehicle().getIdentifier());
        UI.getCurrent().getPage().setLocation("/vehicles");
    }

    private void UpdateVehicle(VehicleForm.SaveEvent event)
    {
        _vehicleService.UpdateVehicle(event.getVehicle());
        ReloadGrids("");
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
}
