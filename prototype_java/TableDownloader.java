
import org.jsoup.nodes.Attributes;
import org.jsoup.select.Elements;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.Jsoup;
import org.jsoup.Connection;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class TableDownloader{

    private static final String DOWNLOAD_URL = "http://ichart.finance.yahoo.com/table.csv";
    private static final String LKUP_URL_1ST_HLF = "http://finance.yahoo.com/q/hp?s=";
    private static final String LKUP_URL_2ND_HLF = "+Historical+Prices";
    private static final String ADJ_CLOSE_FIXED = "Adj_Close";
    private static final String ADJ_CLOSE = "Adj Close";
    private static final String STRT_MONTH_SEL = "selstart";
    private static final String END_MONTH_SEL = "selend";
    private static final String SEL_MONTH_KEY = "selected";
    private static final String START_DAY = "startday";
    private static final String START_YEAR = "startyear";
    private static final String END_DAY = "endday";
    private static final String END_YEAR = "endyear";
    private static final String VAL = "value";
    private static final String START_MONTH_P = "d";
    private static final String START_DAY_P = "b";
    private static final String START_YEAR_P = "c";
    private static final String END_DAY_P = "e";
    private static final String END_MONTH_P = "a";
    private static final String END_YEAR_P = "f";
    private static final String SYMBOL_P = "s";
    private static final String SUFFIX = ".csv";
    private static final String G_P = "g";
    private static final String IGNORE_P = "ignore";
    private static final String USAGE = "USAGE: TableDownloader <SYMBOL>";
    private static final String EXAMPLE = "EXAMPLE: TableDownloader ARMH";

    public static void main(String[] args){
        if(args.length > 0 && args[0] != null){
            TableDownloader td = new TableDownloader();
            TableRequestParams trp = td.scrape(args[0]);
            td.fetch(trp);
        }else{
            System.out.println(USAGE);
            System.out.println(EXAMPLE);
            System.exit(0);
        }
    }

    private TableRequestParams scrape(String symbol){
        Document doc = null;
        try {
            doc = Jsoup.connect(LKUP_URL_1ST_HLF + symbol + LKUP_URL_2ND_HLF).get();
        }catch(IOException io){
            io.printStackTrace();
        }
        Element start_month_select = doc.getElementById(STRT_MONTH_SEL);
        Elements start_months = start_month_select.children();
        Element start_month = null;
        for(Element month : start_months){
            Attributes start_opt_attr = month.attributes();
            if(start_opt_attr.hasKey(SEL_MONTH_KEY)){
                start_month = month;
            }
        }
        Element start_day = doc.getElementById(START_DAY);
        Element start_year = doc.getElementById(START_YEAR);
        Element end_month_select = doc.getElementById(END_MONTH_SEL);
        Elements end_months = end_month_select.children();
        Element end_month = null;
        for(Element month : end_months){
            Attributes end_opt_attr = month.attributes();
            if(end_opt_attr.hasKey(SEL_MONTH_KEY)){
                end_month = month;
            }
        }
        Element end_day = doc.getElementById(END_DAY);
        Element end_year = doc.getElementById(END_YEAR);

        TableRequestParams trp = new TableRequestParams();

        trp.start_month = start_month.attr(VAL);
        trp.start_day = start_day.attr(VAL);
        trp.start_year = start_year.attr(VAL);
        trp.end_month = end_month.attr(VAL);
        trp.end_day = end_day.attr(VAL);
        trp.end_year = end_year.attr(VAL);
        trp.symbol = symbol;

        return trp;
    }

    private void fetch(TableRequestParams trp){
        Connection csvTableURL = Jsoup.connect(DOWNLOAD_URL);
        csvTableURL.data(START_MONTH_P,trp.start_month);
        csvTableURL.data(START_DAY_P,trp.start_day);
        csvTableURL.data(START_YEAR_P,trp.start_year);
        csvTableURL.data(END_DAY_P,trp.end_day);
        csvTableURL.data(END_MONTH_P,trp.end_month);
        csvTableURL.data(END_YEAR_P,trp.end_year);
        csvTableURL.data(SYMBOL_P,trp.symbol);
        csvTableURL.data(G_P,START_MONTH_P);
        csvTableURL.data(IGNORE_P,SUFFIX);
        try{
            Document doc = csvTableURL.get();
            FileWriter fstream = new FileWriter(trp.symbol + SUFFIX);
            BufferedWriter out = new BufferedWriter(fstream);
            String csvText = doc.body().ownText();
            csvText = csvText.replace(ADJ_CLOSE,ADJ_CLOSE_FIXED);
            for(String line : csvText.split(" ")){
                out.write(line + "\n");
            }
            out.close();
        }catch(IOException io){
            io.printStackTrace();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    private class TableRequestParams{
        public String start_month;
        public String start_day;
        public String start_year;
        public String end_month;
        public String end_day;
        public String end_year;
        public String symbol;
    }
}
