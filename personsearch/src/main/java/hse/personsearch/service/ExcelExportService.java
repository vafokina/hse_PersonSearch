package hse.personsearch.service;

import hse.personsearch.domain.Report;
import hse.personsearch.domain.ReportLink;
import java.io.IOException;
import java.util.List;
import javax.servlet.http.HttpServletResponse;

public interface ExcelExportService {

    void export(HttpServletResponse response, Report report, List<ReportLink> links) throws IOException;
}
