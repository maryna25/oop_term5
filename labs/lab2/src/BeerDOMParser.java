import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import java.util.ArrayList;

public class BeerDOMParser {
    public static void main(String[] args) throws Exception {
        DocumentBuilderFactory factory =
                DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse("Beer.xml");

        ArrayList<Beer> beers = new ArrayList<>();

        NodeList beerNodeList = document.getDocumentElement().getElementsByTagName("beer");
        for (int i = 0; i < beerNodeList.getLength(); i++) {
            Node node = beerNodeList.item(i);
            Beer b = new Beer();
            Chars ch = new Chars();
            ArrayList<String> ingredients = new ArrayList<>();

            NodeList childNodes = node.getChildNodes();
            for (int j = 0; j < childNodes.getLength(); j++) {
                Node cNode = childNodes.item(j);
                if (cNode instanceof Element) {
                    String content = cNode.getLastChild().getTextContent().trim();
                    switch (cNode.getNodeName()) {
                        case "name":
                            b.setName(content);
                            break;
                        case "al":
                            b.setAl(Boolean.parseBoolean(content));
                            break;
                        case "manufacturer":
                            b.setManufacturer(content);
                            break;
                        case "ingridients":
                            NodeList ingredientsChildNodes = cNode.getChildNodes();
                            for (int l = 0; l < ingredientsChildNodes.getLength(); l++) {
                                Node ingredientsNode = ingredientsChildNodes.item(l);
                                if (ingredientsNode instanceof Element) {
                                    String ingContent = ingredientsNode.getLastChild().getTextContent().trim();
                                    if (ingredientsNode.getNodeName() == "ingridient"){
                                        ingredients.add(ingContent);
                                    }
                                }
                            }
                            break;
                        case "chars":
                            NodeList charsChildNodes = cNode.getChildNodes();
                            for (int l = 0; l < charsChildNodes.getLength(); l++) {
                                Node charsNode = charsChildNodes.item(l);
                                if (charsNode instanceof Element) {
                                    String charsContent = charsNode.getLastChild().getTextContent().trim();
                                    switch (charsNode.getNodeName()) {
                                        case "revolNumber":
                                            ch.setRevolNumber(Integer.parseInt(charsContent));
                                            break;
                                        case "transparency":
                                            ch.setTransparency(Integer.parseInt(charsContent));
                                            break;
                                        case "filtered":
                                            ch.setFiltered(Boolean.parseBoolean(charsContent));
                                            break;
                                        case "nutritionalValue":
                                            ch.setNutritionalValue(Integer.parseInt(charsContent));
                                            break;
                                        case "spillMethod":
                                            ch.setSpillMethod(charsContent);
                                            break;
                                    }
                                }
                            }
                            break;
                    }
                }
                b.setIngridients(ingredients);
                b.setChars(ch);
            }
            beers.add(b);

        }

        for (Beer b : beers) {
            System.out.println(b);
        }

    }
}
