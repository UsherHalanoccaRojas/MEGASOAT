package Utils;

import Modelo.Entidades.Venta;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

public class PDFExtractor {

    public Venta extraerDatosSOAT(File pdfFile) {
        Venta venta = new Venta();
        venta.setPdfUrl(pdfFile.getAbsolutePath());

        try (PDDocument document = PDDocument.load(pdfFile)) {
            PDFTextStripper stripper = new PDFTextStripper();
            String texto = stripper.getText(document);

            String normalizado = texto.toUpperCase().replaceAll("\\s+", " ");

            Pattern placaPattern = Pattern.compile("PLACA[:\\s]+([A-Z0-9]{5,7})");
            Matcher placaMatcher = placaPattern.matcher(normalizado);
            if (placaMatcher.find()) {
                venta.setPlaca(placaMatcher.group(1));
            } else {

                Pattern placaFallback = Pattern.compile("\\b([A-Z]{3}[0-9]{3}|[0-9]{5,7})\\b");
                Matcher fallbackMatcher = placaFallback.matcher(normalizado);
                venta.setPlaca(fallbackMatcher.find() ? fallbackMatcher.group(1) : "DESCONOCIDO");
            }

            if (normalizado.contains("POSITIVA")) venta.setAseguradora("La Positiva");
            else if (normalizado.contains("PACIFICO") || normalizado.contains("PACÍFICO")) venta.setAseguradora("Pacífico");
            else if (normalizado.contains("MAPFRE")) venta.setAseguradora("Mapfre");
            else if (normalizado.contains("RIMAC")) venta.setAseguradora("Rimac");
            else if (normalizado.contains("QUALITAS") || normalizado.contains("QUÁLITAS")) venta.setAseguradora("Qualitas");
            else if (normalizado.contains("HDI")) venta.setAseguradora("HDI");
            else venta.setAseguradora("Otra");

            // Prima (acepta S/. o solo número con decimales)
            Pattern primaPattern = Pattern.compile("(S/.\\s*([0-9]+(\\.[0-9]{1,2})?)|\\b([0-9]{2,4}\\.[0-9]{2})\\b)");
            Matcher primaMatcher = primaPattern.matcher(normalizado);
            if (primaMatcher.find()) {
                try {
                    String valStr = primaMatcher.group(0).replace("S/.", "").replace("S/", "").trim();
                    venta.setPrima(Double.parseDouble(valStr));
                } catch (Exception e) {
                    venta.setPrima(0.0);
                }
            } else {
                venta.setPrima(0.0);
            }

            Pattern tipoVehiculoPattern = Pattern.compile("(AUTOMOVIL|MOTOCICLETA|VEHICULO MENOR|CAMIONETA|PARTICULAR)",
                                                          Pattern.CASE_INSENSITIVE);
            Matcher tipoMatcher = tipoVehiculoPattern.matcher(normalizado);
            venta.setTipoVehiculo(tipoMatcher.find() ? tipoMatcher.group(1).toUpperCase() : "DESCONOCIDO");

            Pattern fechaPattern = Pattern.compile("(\\d{2}/[A-Z]{3}/\\d{4})");
            Matcher fechaMatcher = fechaPattern.matcher(normalizado);
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MMM/yyyy", Locale.ENGLISH);
            if (fechaMatcher.find()) {
                try {
                    venta.setFechaEmision(sdf.parse(fechaMatcher.group(1)));
                } catch (ParseException e) { /* ignorar */ }
            }
            if (fechaMatcher.find()) {
                try {
                    venta.setFechaPago(sdf.parse(fechaMatcher.group(1)));
                } catch (ParseException e) { /* ignorar */ }
            }

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        return venta;
    }
}
