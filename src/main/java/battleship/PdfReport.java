package battleship;

import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.apache.pdfbox.pdmodel.*;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.common.PDRectangle;

import java.io.IOException;
import java.util.List;

public class PdfReport {

    public static void exportMovesToPDF(List<IMove> moves, String filename) {

        try (PDDocument document = new PDDocument()) {

            PDPage page = new PDPage(PDRectangle.A4);
            document.addPage(page);

            PDPageContentStream content = new PDPageContentStream(document, page);

            content.beginText();
            content.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA), 12);            content.setLeading(16);
            content.newLineAtOffset(50, 750);

            content.showText("Relatório de Jogadas - Battleship");
            content.newLine();

            for (IMove move : moves) {

                content.showText("Jogada nº " + move.getNumber());
                content.newLine();

                for (IPosition pos : move.getShots()) {
                    content.showText("Tiro: " + pos.toString());
                    content.newLine();
                }

                content.newLine();
            }

            content.endText();
            content.close();

            document.save(filename);

            System.out.println("PDF criado: " + filename);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}