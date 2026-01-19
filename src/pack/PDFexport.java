package pack;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import javax.swing.*;
import javax.swing.table.TableModel;
import java.io.File;
import java.io.IOException;


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
                PDPage page = new PDPage();
                doc.addPage(page);

                PDFont font = PDType1Font.HELVETICA_BOLD;

                // pobeiranie danych z tabeli
                JTable table = mainWindow.getTableManager().getTable();
                TableModel model = table.getModel();
                int rowCount = model.getRowCount();
                int colCount = model.getColumnCount();

                PDPageContentStream contentStream = new PDPageContentStream(doc, page);
                contentStream.beginText();
                contentStream.setFont(font, 12);
                contentStream.newLineAtOffset(50, 700);
                contentStream.showText("Lista użytkowników");
                contentStream.endText();

                contentStream.beginText();
                contentStream.newLineAtOffset(50, 730);
                // TUTAJ DODAJ LOGIKE GENEROWANIA TEGO TEKSTU DO STODKA
                contentStream.endText();

                // zamknij strumien
                contentStream.close();

                // zapisz
                doc.save(selectedFile);
                doc.close();

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
