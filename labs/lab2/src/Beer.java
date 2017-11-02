import java.util.ArrayList;

public class Beer {
    private String name;
    private Boolean al;
    private String manufacturer;
    private ArrayList<String> ingridients;
    private Chars chars;

    public String getName() {
        return name;
    }

    public Boolean getAl() {
        return al;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public ArrayList<String> getIngridients() {
        return ingridients;
    }

    public Chars getChars() {
        return chars;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAl(Boolean al) {
        this.al = al;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public void setIngridients(ArrayList<String> ingridients) {
        this.ingridients = ingridients;
    }

    public void setChars(Chars chars) {
        this.chars = chars;
    }

    @Override
    public String toString() {
        String ing = "";
        for (int i = 0; i < ingridients.size(); i++) {
            ing += ingridients.get(i) + " ";
        }
        return "name:" + name + " al:" + al + " manufacturer: " + manufacturer + " ingredients: " + ing +
                "revolNumber: " + chars.getRevolNumber() + " transparency: " + chars.getTransparency() +
                " filtered: " + chars.getFiltered() + " nutritionalValue: " + chars.getNutritionalValue() +
                " spillMethod: " + chars.getSpillMethod();
    }
}
