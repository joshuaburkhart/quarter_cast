import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.jsoup.nodes.Attributes;
import java.io.IOException;

public class Scrape{
    public static void main(String[] args){

        Document doc = null;

        try {
            doc = Jsoup.connect("http://finance.yahoo.com/q/hp?s=ARMH+Historical+Prices").get();
        }catch(IOException io){
            System.out.println("ERROR");
            System.exit(1);
        }

        //System.out.println(doc);

        Element start_month_select = doc.getElementById("selstart");
        //System.out.println(start_month);
        Elements start_months = start_month_select.children();
        Element start_month = null;
        for(Element month : start_months){
            Attributes start_opt_attr = month.attributes();
            if(start_opt_attr.hasKey("selected")){
                start_month = month;
            }
        }
        Element start_day = doc.getElementById("startday");
        //System.out.println(start_day);
        Element start_year = doc.getElementById("startyear");
        //System.out.println(start_year);
        
        Element end_month_select = doc.getElementById("selend");
        //System.out.println(end_month);
        Elements end_months = end_month_select.children();
        Element end_month = null;
        for(Element month : end_months){
            Attributes end_opt_attr = month.attributes();
            if(end_opt_attr.hasKey("selected")){
                end_month = month;
            }
        }
        Element end_day = doc.getElementById("endday");
        //System.out.println(end_day);
        Element end_year = doc.getElementById("endyear");
        //System.out.println(end_year);
        
        String start_month_val = start_month.attr("value");
        String start_day_val = start_day.attr("value");
        String start_year_val = start_year.attr("value");
        String end_month_val = end_month.attr("value");
        String end_day_val = end_day.attr("value");
        String end_year_val = end_year.attr("value");

        System.out.println(start_month_val);
        System.out.println(start_day_val);
        System.out.println(start_year_val);
        System.out.println(end_month_val);
        System.out.println(end_day_val);
        System.out.println(end_year_val);

    }}
