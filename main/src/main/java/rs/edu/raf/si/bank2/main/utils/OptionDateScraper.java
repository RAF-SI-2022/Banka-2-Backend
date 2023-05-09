package rs.edu.raf.si.bank2.main.utils;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class OptionDateScraper {

    public List<LocalDate> scrape() {
        String url = "https://finance.yahoo.com/quote/NVDA/options?p=NVDA&straddle=false";
        Document doc = null;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM d, yyyy", Locale.US);
        List<LocalDate> dropDownDates = new ArrayList<>();
        try {
            doc = Jsoup.connect(url).get();
            Element select = doc.selectFirst("select");
            if (select != null) {
                Elements options = select.select("option");
                dropDownDates = options.stream()
                        .map(date -> LocalDate.parse(date.text(), formatter))
                        .collect(Collectors.toList());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return dropDownDates;
    }
}
