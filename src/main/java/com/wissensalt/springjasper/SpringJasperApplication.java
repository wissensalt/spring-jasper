package com.wissensalt.springjasper;

import com.wissensalt.springjasper.model.Employee;
import com.wissensalt.springjasper.repository.EmployeeRepository;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ResourceUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
@SpringBootApplication
public class SpringJasperApplication {

  private final EmployeeRepository employeeRepository;

  public static void main(String[] args) {
    SpringApplication.run(SpringJasperApplication.class, args);
  }

  private enum Type {
    PDF, HTML
  }

  @GetMapping(value = "/report/{type}", produces = {
      MediaType.APPLICATION_PDF_VALUE,
      MediaType.TEXT_HTML_VALUE
  })
  public ResponseEntity<byte[]> generateReport(@PathVariable("type") Type type)
      throws IOException, JRException {
    final File file = ResourceUtils.getFile("classpath:employee.jrxml");
    final JasperReport jasperReport = JasperCompileManager.compileReport(file.getAbsolutePath());
    final List<Employee> employees = employeeRepository.findAll();
    final JRDataSource dataSource = new JRBeanCollectionDataSource(employees);
    final Map<String, Object> parameters = new HashMap<>();
    final JasperPrint jasperPrint = JasperFillManager.fillReport(
        jasperReport,
        parameters,
        dataSource);
    final String initialReportName = "report-" + new Date().getTime();
    String reportPath = "./";
    String reportName = null;
    byte[] result;
    if (type == Type.PDF) {
      reportName = initialReportName + ".pdf";
      reportPath += reportName;
      JasperExportManager.exportReportToPdfFile(jasperPrint, reportPath);
    }

    if (type == Type.HTML) {
      reportName = initialReportName + ".html";
      reportPath += reportName;
      JasperExportManager.exportReportToHtmlFile(jasperPrint, reportPath);
    }

    if (!StringUtils.hasText(reportPath)) {
      throw new RuntimeException("Failed to generate report");
    }

    result = Files.readAllBytes(Paths.get(reportPath));

    return new ResponseEntity<>(result, getHttpHeaders(type, reportName), HttpStatus.OK);
  }

  private HttpHeaders getHttpHeaders(Type type, String reportName) {
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(type == Type.PDF ? MediaType.APPLICATION_PDF : MediaType.TEXT_HTML);
    headers.add("content-disposition", "inline;filename=" + reportName);
    headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");

    return headers;
  }

}
