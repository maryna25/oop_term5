public class Chars {
    private int revolNumber;
    private int transparency;
    private Boolean filtered;
    private int nutritionalValue;
    private String spillMethodMaterial;
    private int spillMethodCapacity;

    public int getRevolNumber() {
        return revolNumber;
    }

    public int getTransparency() {
        return transparency;
    }

    public Boolean getFiltered() {
        return filtered;
    }

    public int getNutritionalValue() {
        return nutritionalValue;
    }

    public int getSpillMethodCapacity() {
        return spillMethodCapacity;
    }

    public String getSpillMethodMaterial() {
        return spillMethodMaterial;
    }

    public void setRevolNumber(int revolNumber) {
        this.revolNumber = revolNumber;
    }

    public void setTransparency(int transparency) {
        this.transparency = transparency;
    }

    public void setFiltered(Boolean filtered) {
        this.filtered = filtered;
    }

    public void setNutritionalValue(int nutritionalValue) {
        this.nutritionalValue = nutritionalValue;
    }

    public void setSpillMethodMaterial(String spillMethodMaterial) {
        this.spillMethodMaterial = spillMethodMaterial;
    }

    public void setSpillMethodCapacity(int spillMethodCapacity) {
        this.spillMethodCapacity = spillMethodCapacity;
    }
}
