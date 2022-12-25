package com.dhmoney.accountservice.controller;

import com.dhmoney.accountservice.exception.ResourceNotFoundException;
import com.dhmoney.accountservice.service.VoucherService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

@RestController
@AllArgsConstructor
@RequestMapping("/accounts")
public class VoucherController {
    @Autowired
    private final VoucherService voucherService;

    @GetMapping("/{id}/activities/{transaction_id}/comprobante")
    public void generatePDF(HttpServletResponse response, @PathVariable Long id, @PathVariable Long transaction_id) throws IOException, ResourceNotFoundException {
        response.setContentType("application/pdf");
        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd:hh:mm:ss");
        String currentDateTime = dateFormatter.format(new Date());

        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=pdf_" + currentDateTime + ".pdf";
        response.setHeader(headerKey, headerValue);

        this.voucherService.export(response, id, transaction_id);
    }
}
