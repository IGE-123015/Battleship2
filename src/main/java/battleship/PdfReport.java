package battleship;

import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.apache.pdfbox.pdmodel.*;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.common.PDRectangle;

import java.io.IOException;
import java.util.List;

public class PdfReport {

    private static final float FONT_SIZE    = 12;
    private static final float LINE_LEADING = 16;
    private static final float MARGIN_X     = 50;
    private static final float MARGIN_Y     = 750;
    private static final String REPORT_TITLE = "Relatório de Jogadas - Battleship";

    public static void exportMovesToPDF(List<IMove> moves, String filename) {

        try (PDDocument document = new PDDocument()) {

            PDPage page = new PDPage(PDRectangle.A4);
            document.addPage(page);

            PDPageContentStream content = new PDPageContentStream(document, page);

            content.beginText();
            content.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA), FONT_SIZE);
            content.setLeading(LINE_LEADING);
            content.newLineAtOffset(MARGIN_X, MARGIN_Y);

            content.showText(REPORT_TITLE);
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