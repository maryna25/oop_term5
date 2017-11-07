import java.util.ArrayList;
import java.util.List;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.stream.StreamSource;


public class BeerStaxParser {
    public static void main(String[] args) throws XMLStreamException {
        List<Beer> beerList = null;
        Beer currentBeer = null;
        Chars ch = null;
        ArrayList<String> ingredients = new ArrayList<>();
        String content = null;
        XMLInputFactory factory = XMLInputFactory.newInstance();
        StreamSource source = new StreamSource("Beer.xml");
        XMLStreamReader reader = factory.createXMLStreamReader(source);

        while(reader.hasNext()){
            int event = reader.next();
            switch(event){
                case XMLStreamConstants.START_ELEMENT:
                    if ("beer".equals(reader.getLocalName())) {
                        currentBeer = new Beer();
                    }
                    if("beers".equals(reader.getLocalName())) {
                        beerList = new ArrayList<>();
                    }
                    if("ingridients".equals(reader.getLocalName())) {
                        ingredients = new ArrayList<>();
                    }
                    if("chars".equals(reader.getLocalName())) {
                        ch = new Chars();
                    }
                    break;

                case XMLStreamConstants.CHARACTERS:
                    content = reader.getText().trim();
                    break;

                case XMLStreamConstants.END_ELEMENT:
                    switch(reader.getLocalName()){
                        case "beer":
                            beerList.add(currentBeer);
                            break;
                        case "ingridients":
                            currentBeer.setIngridients(ingredients);
                            break;
                        case "chars":
                            currentBeer.setChars(ch);
                            break;
                        case "name":
                            currentBeer.setName(content);
                            break;
                        case "al":
                            currentBeer.setAl(Boolean.parseBoolean(content));
                            break;
                        case "manufacturer":
                            currentBeer.setManufacturer(content);
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
                        case "capacity":
                            ch.setSpillMethodCapacity(Integer.parseInt(content));
                            break;
                        case "material":
                            ch.setSpillMethodMaterial(content);
                            break;
                    }
                    break;

                case XMLStreamConstants.START_DOCUMENT:
                    beerList = new ArrayList<>();
                    break;
            }

        }

        //Print
        for ( Beer b : beerList){
            System.out.println(b);
        }

    }
}
