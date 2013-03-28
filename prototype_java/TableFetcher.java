//Open a URL Stream
//Response resultImageResponse = Jsoup.connect(imageLocation).cookies(cookies).ignoreContentType(true).execute();

// output here
//FileOutputStream out = (new FileOutputStream(new java.io.File(outputFolder + name)));
//out.write(resultImageResponse.bodyAsBytes());           // resultImageResponse.body() is where the image's contents are.
//out.close();

import org.jsoup.*;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import java.io.IOException;
import java.io.FileWriter;
import java.io.BufferedWriter;

public class TableFetcher{
    public static void main(String[] args){
        Connection csvTableURL = Jsoup.connect("http://ichart.finance.yahoo.com/table.csv");
        csvTableURL.data("d","2");
        csvTableURL.data("b","17");
        csvTableURL.data("c","1998");
        csvTableURL.data("e","24");
        csvTableURL.data("a","3");
        csvTableURL.data("f","2013");
        csvTableURL.data("g","d");
        csvTableURL.data("igore",".csv");
        csvTableURL.data("s","ARMH");
        try{
            Document doc = csvTableURL.get();
            FileWriter fstream = new FileWriter("ARMH.csv");
            BufferedWriter out = new BufferedWriter(fstream);
            String csvText = doc.body().ownText();
            csvText = csvText.replace("Adj Close","Adj_Close");
            for(String line : csvText.split(" ")){
                out.write(line + "\n");
            }
            out.close();
        }catch(IOException io){
            System.out.println("TROUBLE!");
        }catch(Exception e){
            System.out.println("REAL TROUBLE!");
        }
    }
}
