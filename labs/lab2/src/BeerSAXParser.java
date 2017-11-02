import java.util.ArrayList;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class BeerSAXParser {
    public static void main(String[] args) throws Exception {
        SAXParserFactory parserFactor = SAXParserFactory.newInstance();
        SAXParser parser = parserFactor.newSAXParser();
        SAXHandler handler = new SAXHandler();
        parser.parse("Beer.xml", handler);

        //Printing the list of employees obtained from XML
        for ( Beer b : handler.beerList){
            System.out.println(b);
        }
    }
}

class SAXHandler extends DefaultHandler {
    ArrayList<Beer> beerList = new ArrayList<>();
    Beer b = null;
    Chars ch = null;
    ArrayList<String> ingredients;
    String content = null;
    @Override
    //Triggered when the start of tag is found.
    public void startElement(String uri, String localName, String qName, Attributes attributes)
            throws SAXException {
        switch(qName){
            case "beer":
                b = new Beer();
                break;
            case "ingridients":
                ingredients = new ArrayList<>();
                break;
            case "chars":
                ch = new Chars();
                break;
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        switch(qName){
            case "beer":
                beerList.add(b);
                break;
            case "ingridients":
                b.setIngridients(ingredients);
                break;
            case "chars":
                b.setChars(ch);
                break;
            case "name":
                b.setName(content);
                break;
            case "al":
                b.setAl(Boolean.parseBoolean(content));
                break;
            case "manufacturer":
                b.setManufacturer(content);
                break;
            case "ingridient":
                ingredients.add(content);
                break;
            case "revolNumber":
                ch.setRevolNumber(Integer.parseInt(content));
                break;
            case "transparency":
                ch.setTransparency(Integer.parseInt(content));
                break;
            case "filtered":
                ch.setFiltered(Boolean.parseBoolean(content));
                break;
            case "nutritionalValue":
                ch.setNutritionalValue(Integer.parseInt(content));
                break;
            case "spillMethod":
                ch.setSpillMethod(content);
                break;
        }
    }

    @Override
    public void characters(char[] ch, int start, int length)
            throws SAXException {
        content = String.copyValueOf(ch, start, length).trim();
    }

}
