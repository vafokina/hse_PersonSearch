package hse.personsearch.service.impl;

import hse.personsearch.domain.Report;
import hse.personsearch.domain.ReportLink;
import hse.personsearch.service.ExcelExportService;
import java.io.IOException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import org.apache.poi.common.usermodel.HyperlinkType;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Hyperlink;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

@Service
public class ExcelExportServiceImpl implements ExcelExportService {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")
            .withLocale(new Locale("RU"))
            .withZone( ZoneId.of("UTC"));

    private XSSFWorkbook workbook;
    private XSSFSheet sheet;

    private void writeHeaderLine(Report report) {
        sheet = workbook.createSheet("Отчет");

        Row row = sheet.createRow(0);

        CellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setBold(true);
        font.setFontHeight(12);
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);

        createCell(row, 1, "Отчет №" + report.getId(), style);
        createCell(row, 3, "Дата составления:", style);
        if (report.getUpdateDate() != null) {
            createCell(row, 4, formatter.format(report.getUpdateDate()), style);
        }

        Row nextRow = sheet.createRow(2);

        createCell(nextRow, 0, "№", style);
        createCell(nextRow, 1, "Ссылка", style);
        createCell(nextRow, 2, "Заголовок", style);
        createCell(nextRow, 3, "Описание", style);
        createCell(nextRow, 4, "Дата публикации", style);
    }

    private void createCell(Row row, int columnCount, Object value, CellStyle style) {
        sheet.autoSizeColumn(columnCount);
        Cell cell = row.createCell(columnCount);
        if (value == null) {
            cell.setCellValue("");
        } else if (value instanceof Integer) {
            cell.setCellValue((Integer) value);
        } else if (value instanceof Boolean) {
            cell.setCellValue((Boolean) value);
        } else if (value instanceof Instant) {
            cell.setCellValue(formatter.format((Instant) value));
        } else {
            cell.setCellValue((String) value);
        }
        cell.setCellStyle(style);
    }

    private void createCellWithLink(Row row, int columnCount, String url, CellStyle style) {
        sheet.autoSizeColumn(columnCount);
        Cell cell = row.createCell(columnCount);
        cell.setCellValue(url);

        CreationHelper helper = workbook.getCreationHelper();
        Hyperlink link = helper.createHyperlink(HyperlinkType.URL);
        link.setAddress(url);
        cell.setHyperlink(link);

        cell.setCellStyle(style);
    }

    private void writeDataLines(List<ReportLink> links) {
        int rowCount = 3;
        int n = 1;

        CellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setFontHeight(12);
        style.setFont(font);
        style.setWrapText(true);
        style.setVerticalAlignment(VerticalAlignment.CENTER);

        CellStyle dateStyle = workbook.createCellStyle();
        XSSFFont dateFont = workbook.createFont();
        dateFont.setFontHeight(12);
        dateStyle.setFont(dateFont);
        dateStyle.setAlignment(HorizontalAlignment.CENTER);
        dateStyle.setVerticalAlignment(VerticalAlignment.CENTER);

        CellStyle linkStyle = workbook.createCellStyle();
        XSSFFont linkFont = workbook.createFont();
        linkFont.setUnderline(XSSFFont.U_SINGLE);
        linkFont.setColor(IndexedColors.BLUE.getIndex());
        linkFont.setFontHeight(12);
        linkStyle.setFont(linkFont);
        linkStyle.setVerticalAlignment(VerticalAlignment.CENTER);

        for (ReportLink link : links) {
            Row row = sheet.createRow(rowCount++);
            int columnCount = 0;

            createCell(row, columnCount++, n++, dateStyle);
            createCellWithLink(row, columnCount++, link.getUrl(), linkStyle);
            createCell(row, columnCount++, link.getTitle(), style);
            createCell(row, columnCount++, link.getDescription(), style);
            createCell(row, columnCount, link.getPublishDate(), dateStyle);

            row.setHeight((short)-1);
        }

        XSSFSheet sheet = workbook.getSheetAt(0);
        sheet.setColumnWidth(1, 50 * 256);
        sheet.setColumnWidth(2, 50 * 256);
        sheet.setColumnWidth(3, 75 * 256);
    }

    @Override
    public void export(HttpServletResponse response, Report report, List<ReportLink> links) throws IOException {
        workbook = new XSSFWorkbook();
        writeHeaderLine(report);
        writeDataLines(links);

        ServletOutputStream outputStream = response.getOutputStream();
        workbook.write(outputStream);
        workbook.close();

        outputStream.close();
    }
}
