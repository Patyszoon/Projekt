package pack;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import javax.swing.*;
import javax.swing.table.TableModel;
import java.awt.*;
import java.io.File;
import java.io.IOException;

import be.quodlibet.boxable.BaseTable;
import be.quodlibet.boxable.Cell;
import be.quodlibet.boxable.HorizontalAlignment;
import be.quodlibet.boxable.Row;
import be.quodlibet.boxable.VerticalAlignment;

public class PDFexport {
    private MainWindow mainWindow;

    public PDFexport(MainWindow mainWindow) {
        this.mainWindow = mainWindow;
    }

    public void exportTableToPDF() throws IOException{
        if(!mainWindow.getTableManager().isTableSelected()){
            JOptionPane.showMessageDialog(mainWindow, "Najpierw wybierz tabelę!", "BRAK DANYCH", JOptionPane.WARNING_MESSAGE);
            return;
        }

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Zapisz jako PDF");

        // dialog wyboru pliku
        int userChoice = fileChooser.showSaveDialog(mainWindow);

        if(userChoice == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();

            // pliki z rozszrzeniem .pdf
            if (!selectedFile.getName().toLowerCase().endsWith(".pdf")) {
                selectedFile = new File(selectedFile.getAbsolutePath() + ".pdf");
            }

            // jesli plik juz istnieje
            if (selectedFile.exists()) {
                int overwrite = JOptionPane.showConfirmDialog(mainWindow,
                        "Plik już istnieje. Czy chcesz go zastąpić?",
                        "Potwierdź zapis",
                        JOptionPane.YES_NO_OPTION);

                if (overwrite != JOptionPane.YES_OPTION) {
                    return; // anuluj jesli nie chce nadpisac
                }
            }

            PDDocument doc = null;

            try {
                doc = new PDDocument();

                // tworzenie strony
                PDPage page = new PDPage(PDRectangle.A4);
                doc.addPage(page);

                PDFont font = PDType0Font.load(doc,PDFexport.class.getResourceAsStream("/ARIAL.ttf"),true);

                // pobeiranie danych z tabeli
                JTable table = mainWindow.getTableManager().getTable();
                TableModel model = table.getModel();
                int rowCount = model.getRowCount();
                int colCount = model.getColumnCount();

                // TYTUL
                PDPageContentStream contentStream = new PDPageContentStream(doc, page);
                contentStream.beginText();
                contentStream.setFont(font, 12);
                contentStream.newLineAtOffset(50, 700);
                contentStream.showText("Lista użytkowników");
                contentStream.endText();

                float margin = 50;

                // starting y position is whole page height subtracted by top and bottom margin
                float yStartNewPage = page.getMediaBox().getHeight() - (2 * margin);
                float tableWidth = page.getMediaBox().getWidth() - (2 * margin);
                float yStart = 680;
                float bottomMargin = 70;

                BaseTable dataTable = new BaseTable(yStart, yStartNewPage, bottomMargin, tableWidth, margin, doc, page, true,
                        true);

                Row<PDPage> headerRow = dataTable.createRow(20f);

                for (int col = 0; col < colCount; col++){
                    Cell<PDPage> cell = headerRow.createCell(100.0f/colCount, model.getColumnName(col));

                    cell.setFont(font);
                    cell.setFontSize(12);
                    cell.setFillColor(Color.LIGHT_GRAY);
                    cell.setTextColor(Color.BLACK);
                    cell.setAlign(HorizontalAlignment.CENTER);

                }

                dataTable.addHeaderRow(headerRow);  // oznaaczony jako wiersz naglowka

                // DANE Z TABELI
                for(int row = 0; row < rowCount; row++) {
                    Row<PDPage> dataRow = dataTable.createRow(15f);

                    for(int col = 0; col < colCount; col++){
                       Object value = model.getValueAt(row, col);
                       String text = "";
                       if(value != null){ text = value.toString(); }

                        Cell<PDPage> cell = dataRow.createCell(100.0f / colCount, text);
                        cell.setFont(font);

                        if (row % 2 == 0) {
                            cell.setFillColor(new Color(255, 200, 220));
                        } else {
                            cell.setFillColor(new Color(255, 150, 180));
                        }


                    }
                }
                // narysuj
                dataTable.draw();
                contentStream.close();

                // zapisz
                doc.save(selectedFile);
                JOptionPane.showMessageDialog(mainWindow, "PDF został pomyślnie wygenerowany!", "Sukces", JOptionPane.INFORMATION_MESSAGE);


            } catch (IOException e) {
                JOptionPane.showMessageDialog(mainWindow,"Błąd podczas tworzenia PDF: " + e.getMessage(),"Błąd", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            } finally {
                if(doc != null){
                    doc.close();
                }
            }

        }

    }

}
