import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class CreatePDF {
    public static void main(String[] args) {
        // Generate the main document
        generatePDF(
            "C:/wealthzone/report_content.txt", 
            "C:/wealthzone/AI_Integration_in_Logistics.pdf",
            "AI INTEGRATION IN LOGISTICS",
            "Navigating the Future of Supply Chains"
        );
        
        // Generate the all-inclusive Final Documentation
        generatePDF(
            "C:/wealthzone/final_documentation.txt", 
            "C:/wealthzone/Final_Documentation.pdf",
            "FINAL PROJECT DOCUMENTATION",
            "Source Code, Authenticity Metrics, & Comprehensive Essay"
        );
    }



    public static void generatePDF(String inputFilePath, String outputFilePath, String mainTitle, String subtitleTitle) {
        Color navyBlue = new Color(0, 51, 102);
        Color darkGray = new Color(80, 80, 80);
        Color black = Color.BLACK;

        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage(PDRectangle.A4);
            document.addPage(page);

            PDPageContentStream contentStream = new PDPageContentStream(document, page);
            
            float margin = 50;
            float yStart = page.getMediaBox().getHeight() - margin;
            float width = page.getMediaBox().getWidth() - 2 * margin;
            float currentY = yStart;
            
            String dateStr = LocalDate.now().format(DateTimeFormatter.ofPattern("MMMM dd, yyyy"));
            
            contentStream.setNonStrokingColor(navyBlue);
            contentStream.setFont(PDType1Font.HELVETICA_BOLD, 18);
            float titleWidth = PDType1Font.HELVETICA_BOLD.getStringWidth(mainTitle) / 1000 * 18;
            contentStream.beginText();
            contentStream.newLineAtOffset((page.getMediaBox().getWidth() - titleWidth) / 2, currentY);
            contentStream.showText(mainTitle);
            contentStream.endText();
            currentY -= 25;
            
            contentStream.setNonStrokingColor(darkGray);
            contentStream.setFont(PDType1Font.HELVETICA, 14);
            float subtitleWidth = PDType1Font.HELVETICA.getStringWidth(subtitleTitle) / 1000 * 14;
            contentStream.beginText();
            contentStream.newLineAtOffset((page.getMediaBox().getWidth() - subtitleWidth) / 2, currentY);
            contentStream.showText(subtitleTitle);
            contentStream.endText();
            currentY -= 25;

            contentStream.setFont(PDType1Font.HELVETICA_OBLIQUE, 12);
            String dateLine = "Date Prepared: " + dateStr;
            float dateWidth = PDType1Font.HELVETICA_OBLIQUE.getStringWidth(dateLine) / 1000 * 12;
            contentStream.beginText();
            contentStream.newLineAtOffset((page.getMediaBox().getWidth() - dateWidth) / 2, currentY);
            contentStream.showText(dateLine);
            contentStream.endText();
            currentY -= 30; 
            
            contentStream.setStrokingColor(navyBlue);
            contentStream.moveTo(margin, currentY + 15);
            contentStream.lineTo(page.getMediaBox().getWidth() - margin, currentY + 15);
            contentStream.setLineWidth(1.5f);
            contentStream.stroke();
            currentY -= 15;
            
            contentStream.setNonStrokingColor(black);
            contentStream.beginText();
            contentStream.setFont(PDType1Font.HELVETICA, 12);
            contentStream.newLineAtOffset(margin, currentY);

            List<String> lines = Files.readAllLines(Paths.get(inputFilePath), StandardCharsets.UTF_8);
            
            for (int i = 0; i < lines.size(); i++) {
                String line = lines.get(i);
                
                if (i < 2 && line.contains("The AI Revolution in Logistics")) {
                    continue; 
                }

                boolean isHeader = line.length() > 0 && line.length() < 60 && !line.endsWith(".") && !line.endsWith(",") && !line.startsWith("- ") && !line.contains(":");
                
                if (isHeader) {
                    if (currentY <= margin + 80) {
                        contentStream.endText();
                        contentStream.close();
                        
                        page = new PDPage(PDRectangle.A4);
                        document.addPage(page);
                        contentStream = new PDPageContentStream(document, page);
                        contentStream.beginText();
                        contentStream.newLineAtOffset(margin, yStart);
                        currentY = yStart;
                    }

                    contentStream.setNonStrokingColor(navyBlue);
                    contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);
                } else {
                    contentStream.setNonStrokingColor(black);
                    contentStream.setFont(PDType1Font.HELVETICA, 12);
                }

                List<String> wrappedLines = wrapText(line, isHeader ? PDType1Font.HELVETICA_BOLD : PDType1Font.HELVETICA, 12, width);
                
                if (wrappedLines.isEmpty()) {
                    contentStream.newLineAtOffset(0, -16.0f);
                    currentY -= 16.0f;
                    continue;
                }

                for (int wLineIdx = 0; wLineIdx < wrappedLines.size(); wLineIdx++) {
                    String wrappedLine = wrappedLines.get(wLineIdx);
                    
                    if (currentY <= margin + 20) { 
                        contentStream.endText();
                        contentStream.close();
                        
                        page = new PDPage(PDRectangle.A4);
                        document.addPage(page);
                        contentStream = new PDPageContentStream(document, page);
                        if (isHeader) {
                            contentStream.setNonStrokingColor(navyBlue);
                            contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);
                        } else {
                            contentStream.setNonStrokingColor(black);
                            contentStream.setFont(PDType1Font.HELVETICA, 12);
                        }
                        contentStream.beginText();
                        contentStream.newLineAtOffset(margin, yStart);
                        currentY = yStart;
                    }
                    
                    String sanitizedLine = sanitize(wrappedLine);
                    boolean isLastLine = (wLineIdx == wrappedLines.size() - 1);
                    boolean justify = !isHeader && !isLastLine;

                    if (justify) {
                        String[] words = sanitizedLine.split(" ");
                        if (words.length > 1) {
                            float textWidthWithoutSpaces = 0;
                            for (String w : words) {
                                textWidthWithoutSpaces += PDType1Font.HELVETICA.getStringWidth(w) / 1000 * 12;
                            }
                            float spaceWidth = (width - textWidthWithoutSpaces) / (words.length - 1);
                            
                            float currentRelativeX = 0;
                            for (int wIdx = 0; wIdx < words.length; wIdx++) {
                                contentStream.showText(words[wIdx]);
                                if (wIdx < words.length - 1) {
                                    float moveX = (PDType1Font.HELVETICA.getStringWidth(words[wIdx]) / 1000 * 12) + spaceWidth;
                                    contentStream.newLineAtOffset(moveX, 0);
                                    currentRelativeX += moveX;
                                }
                            }
                            contentStream.newLineAtOffset(-currentRelativeX, -16.0f);
                        } else {
                            contentStream.showText(sanitizedLine);
                            contentStream.newLineAtOffset(0, -16.0f);
                        }
                    } else {
                        contentStream.showText(sanitizedLine);
                        contentStream.newLineAtOffset(0, -16.0f);
                    }
                    currentY -= 16.0f;
                }
                
                if (isHeader) {
                    contentStream.newLineAtOffset(0, -8.0f);
                    currentY -= 8.0f;
                }
            }
            
            contentStream.endText();
            contentStream.close();
            
            document.save(new File(outputFilePath));
            System.out.println("Generated: " + outputFilePath);
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private static List<String> wrapText(String text, PDType1Font font, float fontSize, float maxWidth) throws IOException {
        List<String> lines = new ArrayList<>();
        if (text == null || text.trim().isEmpty()) {
            return lines;
        }
        
        String[] words = text.split(" ");
        StringBuilder currentLine = new StringBuilder();
        
        for (String word : words) {
            String tempLine = currentLine.length() == 0 ? word : currentLine + " " + word;
            float textWidth = font.getStringWidth(sanitize(tempLine)) / 1000 * fontSize;
            
            if (textWidth > maxWidth) {
                lines.add(currentLine.toString());
                currentLine = new StringBuilder(word);
            } else {
                currentLine.append(currentLine.length() == 0 ? word : " " + word);
            }
        }
        if (currentLine.length() > 0) {
            lines.add(currentLine.toString());
        }
        
        return lines;
    }
    
    private static String sanitize(String text) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            if (c == 8211 || c == 8212) c = '-'; 
            else if (c == 8216 || c == 8217) c = '\''; 
            else if (c == 8220 || c == 8221) c = '"'; 
            if (c >= 32 && c <= 126) {
                sb.append(c);
            }
        }
        return sb.toString();
    }
}
