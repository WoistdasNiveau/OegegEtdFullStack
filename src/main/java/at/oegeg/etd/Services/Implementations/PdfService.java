package at.oegeg.etd.Services.Implementations;

import at.oegeg.etd.Entities.VehicleEntity;
import at.oegeg.etd.Entities.WorkEntity;
import com.itextpdf.text.*;
import com.itextpdf.text.html.WebColors;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.stream.Stream;

@Service
public class PdfService
{
    public byte[] GenerateVehiclePdf(VehicleEntity entity) throws IOException, DocumentException
    {
        Document document = new Document();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        File file = new File("F:\\Programming\\OEGEG\\OegegEtdTry5.1\\table.pdf");
        //file.createNewFile();
        PdfWriter.getInstance(document, stream);

        document.open();
        Font font = FontFactory.getFont(FontFactory.HELVETICA,20,BaseColor.BLACK);

        Paragraph title = new Paragraph("Vehicle");
        title.setSpacingAfter(20f);
        title.setAlignment(Element.ALIGN_CENTER);
        title.setFont(font);

        PdfPTable table = new PdfPTable(5);
        table.setWidthPercentage(100);

        AddVehicleTableHeader(table);
        AddVehicleRows(table, entity);

        Paragraph workTitle = new Paragraph("Works");
        workTitle.setSpacingAfter(20f);
        workTitle.setAlignment(Element.ALIGN_CENTER);
        workTitle.setFont(font);

        PdfPTable workTabel = new PdfPTable(3);
        table.setWidthPercentage(100);
        AddWorkHeader(workTabel);
        AddWorkRows(workTabel, entity);


        document.add(title);
        document.add(table);
        document.add(workTitle);
        document.add(workTabel);
        document.close();
        return stream.toByteArray();
    }

    // == private methods ==
    private void AddVehicleTableHeader(PdfPTable table)
    {
        Stream.of("Type", "Number", "Status", "Stand","Works")
                .forEach(columnTilte ->
                {
                    PdfPCell header = new PdfPCell();
                    header.setBackgroundColor(BaseColor.LIGHT_GRAY);
                    header.setBorderWidth(1);
                    header.setPhrase(new Phrase(columnTilte));
                    table.addCell(header);
                });
    }
    private void AddVehicleRows(PdfPTable table, VehicleEntity entity)
    {
        table.addCell(entity.getType());
        table.addCell(entity.getNumber());
        table.addCell(entity.getStatus());
        table.addCell(entity.getStand());
        table.addCell(String.valueOf(entity.getWorks().size()));
    }
    private void AddWorkHeader(PdfPTable table)
    {
        Stream.of("ResponsiblePerson", "Description", "Priority")
                .forEach(columnTilte ->
                {
                    PdfPCell header = new PdfPCell();
                    header.setBackgroundColor(BaseColor.LIGHT_GRAY);
                    header.setBorderWidth(1);
                    header.setPhrase(new Phrase(columnTilte));
                    table.addCell(header);
                });
    }

    private void AddWorkRows(PdfPTable table, VehicleEntity vehicle)
    {
        BaseColor colorGrey = WebColors.getRGBColor("#CCCCCC");
        PdfPCell personCell;
        PdfPCell descriptionCell;
        PdfPCell priorityCell;
        for (WorkEntity work: vehicle.getWorks())
        {
            if(vehicle.getWorks().indexOf(work) % 2 != 0)
            {
                personCell = new PdfPCell(new Phrase(work.getResponsiblePerson() != null ? work.getResponsiblePerson().getName() : "-"));
                descriptionCell = new PdfPCell(new Phrase(work.getDescription()));
                priorityCell = new PdfPCell(new Phrase(work.getPriority().toString()));
                personCell.setBackgroundColor(colorGrey);
                descriptionCell.setBackgroundColor(colorGrey);
                priorityCell.setBackgroundColor(colorGrey);
            }
            else
            {
                personCell = new PdfPCell(new Phrase(work.getResponsiblePerson() != null ? work.getResponsiblePerson().getName() : "-"));
                descriptionCell = new PdfPCell(new Phrase(work.getDescription()));
                priorityCell = new PdfPCell(new Phrase(work.getPriority().toString()));
            }
            switch (work.getPriority())
            {
                case LOW ->
                {
                    personCell.setBackgroundColor(new BaseColor(24,132,68));
                    descriptionCell.setBackgroundColor(new BaseColor(24,132,68));
                    priorityCell.setBackgroundColor(new BaseColor(24,132,68));
                }
                case MEDIUM ->
                {
                    personCell.setBackgroundColor(new BaseColor(8,108,244));
                    descriptionCell.setBackgroundColor(new BaseColor(8,108,244));
                    priorityCell.setBackgroundColor(new BaseColor(8,108,244));
                }
                case HIGH ->
                {
                    personCell.setBackgroundColor(new BaseColor(232,28,20));
                    descriptionCell.setBackgroundColor(new BaseColor(232,28,20));
                    priorityCell.setBackgroundColor(new BaseColor(232,28,20));
                }
            }
            table.addCell(personCell);
            table.addCell(descriptionCell);
            table.addCell(priorityCell);
        }
    }
}
