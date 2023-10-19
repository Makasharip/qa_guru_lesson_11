import com.codeborne.pdftest.PDF;
import com.codeborne.xlstest.XLS;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.opencsv.CSVReader;
import model.Country;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.Year;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ParsingTest {
    ClassLoader cl = ParsingTest.class.getClassLoader();

    ObjectMapper mapper = new ObjectMapper();



    @Test
    public void zipTest() throws Exception {
        try (InputStream stream = cl.getResourceAsStream("counties.zip");
             ZipInputStream zis = new ZipInputStream(stream)) {

            ZipEntry entry;
            String name;
            while ((entry = zis.getNextEntry()) != null) {
                name = entry.getName();
                System.out.printf("File name: %s \n", name);
                if (entry.getName().contains("Countries_Neighbour.csv")){

                    CSVReader csvReader = new CSVReader(new InputStreamReader(zis));
                    List<String[]> content = csvReader.readAll();
                    Assertions.assertEquals(4, content.size());

                    final String[] firstRow = content.get(1);
                    Assertions.assertArrayEquals(new String[]{"Russia", "China"}, firstRow);
                    System.out.println("CSV File");
                }

                if (entry.getName().contains("Countries_Neighbour.pdf")){
                    PDF pdf = new PDF(zis);

                    Assertions.assertTrue(pdf.text.contains("counties' neighbour"));

                    System.out.println("PDF File");
                }
                if (entry.getName().contains("Countries_Neighbour.xlsx")){
                    XLS xls = new XLS(zis);
                    Assertions.assertEquals("China",
                            xls.excel.getSheetAt(0)
                                    .getRow(1)
                                    .getCell(1)
                                    .getStringCellValue());
                    System.out.println("XLS File");
                }
            }
        }
    }

    @Test
    void verifyJsonContentTest() throws Exception {
        try (
                InputStream Countries = cl.getResourceAsStream("Countries.json");
                InputStreamReader reader = new InputStreamReader(Countries)
        ) {
            Country country = mapper.readValue(reader, Country.class);
            assertThat(country.getName()).isEqualTo("Russia");
            assertThat(country.getName()).isNotNull();
            int currentYear = Year.now().getValue();
            assertThat(country.getAge()).isBetween(1980, currentYear);
            assertEquals(country.getNotes().get(2), "balalayka");
            System.out.println("Successful check json file.");
        }
    }


}


