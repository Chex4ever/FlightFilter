import com.gridnine.filter.FlightCompositeFilter;
import com.gridnine.model.Flight;
import com.gridnine.testing.FlightBuilder;

import java.time.LocalDateTime;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        System.out.println("------------------Тестовый набор рейсов--------------------");
        List<Flight> flights = FlightBuilder.createFlights();
        flights.forEach(System.out::println);

        FlightCompositeFilter filter1 = new FlightCompositeFilter()
                .byDepartureBefore(LocalDateTime.now()).negate()
                .setName("фильтр 1 - Исключить вылеты до текущего момента времени");
        System.out.println(filter1);
        List<Flight> filteredFlights1 = filter1.filter(flights);
        filteredFlights1.forEach(System.out::println);
        System.out.println("------------------------------------------------------\n");

        FlightCompositeFilter filter2 =  new FlightCompositeFilter()
                .excludeWrongSegments()
                .setName("фильтр 2 - Исключить сегменты с датой прилёта раньше даты вылета");
        System.out.println(filter2);
        List<Flight> filteredFlights2 = filter2.filter(filteredFlights1);
        filteredFlights2.forEach(System.out::println);
        System.out.println("------------------------------------------------------\n");

        FlightCompositeFilter filter3 =  new FlightCompositeFilter()
                .byLayoverMinutesMoreThan(120).negate()
                .setName("фильтр 3 - Исключить перелеты, где общее время, проведённое на земле, превышает два часа");
        System.out.println(filter3);
        List<Flight> filteredFlights3 = filter3.filter(filteredFlights2);
        filteredFlights3.forEach(System.out::println);
        System.out.println("------------------------------------------------------\n");

        FlightCompositeFilter filter4 = new FlightCompositeFilter()
                .byDepartureBefore(LocalDateTime.now()).negate()
                .excludeWrongSegments()
                .byLayoverMinutesMoreThan(120).negate()
                .setName("фильтр 4 - Пробуем все фильтры сразу, сравниваем с предыдущим");

        System.out.println(filter4);
        List<Flight> filteredFlights4 = filter4.filter(flights);
        filteredFlights4.forEach(System.out::println);
    }
}