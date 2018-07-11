package app.logistic.prueba.pruebalogisticapp;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;



public class AdapterDays extends RecyclerView.Adapter<AdapterDays.ViewHolder> {

    private List<Days> days;

    private String CLEARDAY = "clear-day";
    private String CLEARNIGHT = "clear-night";
    private String RAIN = "rain";
    private String SNOW = "snow";
    private String SLEET = "sleet";
    private String WIND = "wind";
    private String FOG = "fog";
    private String CLOUDY = "cloudy";
    private String CLOUDYDAYICON = "partly-cloudy-day";
    private String CLOUDYNIGHTICON = "partly-cloudy-night";
    private String HAIL = "hail";
    private String THUNDERSTORM = "thunderstorm";
    private String TORNADO = "tornado";

    public AdapterDays(List<Days> contacts) {
        days = contacts;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View contactView = inflater.inflate(R.layout.activity_row_days, parent, false);

        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(contactView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Days day = days.get(position);

        // Set item views based on your views and data model
        holder.day.setText(day.getDay());

        holder.temperature.setText(String.valueOf(day.getTemperature())+"°");

        String icon = day.getIcon();

        if(icon.equals(CLEARDAY)){
            holder.imgWeather.setImageResource(R.drawable.clear_day_black);
        }else if(icon.equals(CLEARNIGHT)){
            holder.imgWeather.setImageResource(R.drawable.clear_night_black);
        }else if(icon.equals(RAIN)){
            holder.imgWeather.setImageResource(R.drawable.rain_black);
        }else if(icon.equals(SNOW)){
            holder.imgWeather.setImageResource(R.drawable.snow_black);
        }else if(icon.equals(SLEET)){
            holder.imgWeather.setImageResource(R.drawable.sleet_black);
        }else if(icon.equals(WIND)){
            holder.imgWeather.setImageResource(R.drawable.wind_black);
        }else if(icon.equals(FOG)){
            holder.imgWeather.setImageResource(R.drawable.fog_black);
        }else if(icon.equals(CLOUDY)){
            holder.imgWeather.setImageResource(R.drawable.cloudy_black);
        }else if(icon.equals(CLOUDYDAYICON)){
            holder.imgWeather.setImageResource(R.drawable.cloudy_day_black);
        }else if(icon.equals(CLOUDYNIGHTICON)){
            holder.imgWeather.setImageResource(R.drawable.cloudly_night_black);
        }else if(icon.equals(HAIL)){
            holder.imgWeather.setImageResource(R.drawable.hail_black);
        }else if(icon.equals(THUNDERSTORM)){
            holder.imgWeather.setImageResource(R.drawable.thunderstorm_black);
        }else if(icon.equals(TORNADO)){
            holder.imgWeather.setImageResource(R.drawable.tornado_black);
        }



    }

    @Override
    public int getItemCount() {
        return days.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        // Your holder should contain a member variable
        // for any view that will be set as you render a row
        public TextView day, temperature;
        public ImageView imgWeather;

        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        public ViewHolder(View itemView) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);

            day = (TextView) itemView.findViewById(R.id.day);
            temperature = (TextView) itemView.findViewById(R.id.temperatureDay);
            imgWeather = (ImageView) itemView.findViewById(R.id.weatherDay);
        }
    }


}
