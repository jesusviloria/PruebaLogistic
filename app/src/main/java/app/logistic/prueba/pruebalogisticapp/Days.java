package app.logistic.prueba.pruebalogisticapp;



public class Days {

    private String icon;
    private int temperature;
    private String day;


    public Days(String icon, int temperature, String day){
        this.icon=icon;
        this.temperature=temperature;
        this.day = day;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public int getTemperature() {
        return temperature;
    }

    public void setTemperature(int temperature) {
        this.temperature = temperature;
    }
}
