package com.dhmoney.accountservice.service;

import com.dhmoney.accountservice.domain.model.dto.transaction.TransactionDTO;
import com.dhmoney.accountservice.domain.model.dto.transaction.TransactionPublicDTO;
import com.dhmoney.accountservice.exception.ResourceNotFoundException;
import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.Image;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.io.IOException;

@Service
@AllArgsConstructor
public class VoucherService {

    @Autowired
    private final TransactionService transactionService;

    public void export(HttpServletResponse response, Long id, Long transaction_id) throws IOException, ResourceNotFoundException {

        TransactionPublicDTO transaction = transactionService.getTransactionById(id, transaction_id);
        makeDocument(response, transaction);

    }

    private void makeDocument(HttpServletResponse response, TransactionPublicDTO transaction) throws IOException {
        Color grey = new Color(49,49,49);
        Color green = new Color(200,253,60);
        Rectangle pageSize = new Rectangle(400, 600);
        pageSize.setBackgroundColor(green);
        Document document = new Document(pageSize);
        PdfWriter.getInstance(document, response.getOutputStream());

        document.open();

        Image img = Image.getInstance("logo.png");

        img.setAlignment(Paragraph.ALIGN_RIGHT);

        PdfPTable table = new PdfPTable(2);

        Font fontTitle = FontFactory.getFont(FontFactory.HELVETICA_BOLD);
        fontTitle.setSize(22);

        Font fontSubTitle = FontFactory.getFont(FontFactory.HELVETICA_BOLD);
        fontSubTitle.setSize(12);
        fontSubTitle.setColor(Color.WHITE);

        Paragraph title = new Paragraph("COMPROBANTE", fontTitle);
        title.setAlignment(Paragraph.ALIGN_CENTER);


        Font fontParagraph = FontFactory.getFont(FontFactory.HELVETICA_BOLD);
        fontParagraph.setSize(12);
        fontParagraph.setColor(grey);

        PdfPCell cellBlank = new PdfPCell();
        cellBlank.setBackgroundColor(green);
        cellBlank.setBorderColor(green);
        cellBlank.setPadding(20);
        PdfPCell cellSub = new PdfPCell();
        cellSub.setBackgroundColor(grey);
        cellSub.setBorderColor(grey);
        cellSub.setPadding(12);
        cellSub.setHorizontalAlignment(2);
        PdfPCell cellData = new PdfPCell();
        cellData.setBackgroundColor(Color.WHITE);
        cellData.setBorderColor(grey);
        cellData.setPadding(12);
        cellData.setHorizontalAlignment(1);

        table.addCell(cellBlank);
        table.addCell(cellBlank);

        cellSub.setPhrase(new Phrase("ID DE LA TRANSACCION: ", fontSubTitle));
        table.addCell(cellSub);
        cellData.setPhrase(new Phrase(transaction.getId().toString(), fontParagraph));
        table.addCell(cellData);

        cellSub.setPhrase(new Phrase("CUENTA ORIGEN: ", fontSubTitle));
        table.addCell(cellSub);
        cellData.setPhrase(new Phrase(transaction.getOrigin(), fontParagraph));
        table.addCell(cellData);

        cellSub.setPhrase(new Phrase("CUENTA DESTINO: ", fontSubTitle));
        table.addCell(cellSub);
        cellData.setPhrase(new Phrase(transaction.getDestination(), fontParagraph));
        table.addCell(cellData);

        cellSub.setPhrase(new Phrase("FECHA: ", fontSubTitle));
        table.addCell(cellSub);
        cellData.setPhrase(new Phrase(transaction.getDated().toString(), fontParagraph));
        table.addCell(cellData);

        cellSub.setPhrase(new Phrase("MONTO: ", fontSubTitle));
        table.addCell(cellSub);
        cellData.setPhrase(new Phrase(transaction.getAmount().toString(), fontParagraph));
        table.addCell(cellData);

        cellSub.setPhrase(new Phrase("CONCEPTO: ", fontSubTitle));
        table.addCell(cellSub);
        cellData.setPhrase(new Phrase(transaction.getDescription(), fontParagraph));
        table.addCell(cellData);

        cellSub.setPhrase(new Phrase("NOMBRE: ", fontSubTitle));
        table.addCell(cellSub);
        cellData.setPhrase(new Phrase(transaction.getName(), fontParagraph));
        table.addCell(cellData);

        document.add(title);
        document.add(table);
        document.add(img);
        document.close();
    }
}
