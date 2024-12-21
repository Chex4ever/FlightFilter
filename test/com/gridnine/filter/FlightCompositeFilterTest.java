package com.gridnine.filter;

import com.gridnine.model.Flight;
import com.gridnine.testing.FlightBuilder;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FlightCompositeFilterTest {
    List<Flight> flights = FlightBuilder.createFlights();
//Рейс: 1, пересадок 0, ожидание 0 минут, отправление 25-12-24 00:57, прибытие 25-12-24 02:57, перелёты: [25-12-2024 00:57|25-12-2024 02:57]
//Рейс: 2, пересадок 1, ожидание 60 минут, отправление 25-12-24 00:57, прибытие 25-12-24 05:57, перелёты: [25-12-2024 00:57|25-12-2024 02:57] [25-12-2024 03:57|25-12-2024 05:57]
//Рейс: 3, пересадок 0, ожидание 0 минут, отправление 19-12-24 00:57, прибытие 25-12-24 00:57, перелёты: [19-12-2024 00:57|25-12-2024 00:57]
//Рейс: 4, пересадок 0, ожидание 0 минут, отправление 25-12-24 00:57, прибытие 24-12-24 18:57, перелёты: [25-12-2024 00:57|24-12-2024 18:57]
//Рейс: 5, пересадок 1, ожидание 180 минут, отправление 25-12-24 00:57, прибытие 25-12-24 06:57, перелёты: [25-12-2024 00:57|25-12-2024 02:57] [25-12-2024 05:57|25-12-2024 06:57]
//Рейс: 6, пересадок 2, ожидание 180 минут, отправление 25-12-24 00:57, прибытие 25-12-24 07:57, перелёты: [25-12-2024 00:57|25-12-2024 02:57] [25-12-2024 03:57|25-12-2024 04:57] [25-12-2024 06:57|25-12-2024 07:57]

    @Test
    void testNegate() {
        FlightCompositeFilter f0 = new FlightCompositeFilter();
        assertEquals(0,f0.getFilters().size());
        f0.negate();
        assertEquals(0,f0.getFilters().size());
        f0.or();
        assertEquals(0,f0.getFilters().size());
        FlightCompositeFilter f1 = new FlightCompositeFilter().byDepartureAfter(LocalDateTime.now());
        assertEquals(6,flights.size());
        List<Flight> result1 = f1.filter(flights);
        assertEquals(5,result1.size());
        f1.negate();
        List<Flight> result2 = f1.filter(flights);
        assertEquals(1, result2.size());
    }

    @Test
    void testOr() {
        FlightCompositeFilter f1 = new FlightCompositeFilter();
        assertEquals(0,f1.getFilters().size());
        f1.or();
        assertEquals(0,f1.getFilters().size());
        f1.byLayoverMinutesLessThan(60);
        assertEquals(1,f1.getFilters().size());
        List<Flight> result1 = f1.filter(flights);
        assertEquals(3,result1.size());
        f1.or().byLayoverMinutesEquals(180);
        assertEquals(2,f1.getFilters().size());
        List<Flight> result2 = f1.filter(flights);
        assertEquals(5,result2.size());
    }

    @Test
    void testFilterParallel() {
        FlightCompositeFilter f0 = new FlightCompositeFilter().byLayoverMinutesMoreThan(120).byArrivalBetween(LocalDateTime.now(),LocalDateTime.now().plusDays(10));
        List<Flight> result = f0.filterParallel(flights);
        assertEquals(2,result.size());
    }

    @Test
    void testName() {
        FlightCompositeFilter f0 = new FlightCompositeFilter().byDepartureAfter(LocalDateTime.now());
        assertEquals("Unnamed Composite Filter",f0.getName());
        f0.setName("Test");
        assertEquals("Test",f0.getName());

    }

    @Test
    void testToString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yy HH:mm");

        FlightCompositeFilter f5 = new FlightCompositeFilter()
                .byDepartureBefore(LocalDateTime.now()).negate()
                .excludeWrongSegments()
                .byLayoverMinutesMoreThan(120).negate()
                .or()
                .byLayoverMinutesMoreThan(120)
                .setName("Тестовый фильтр");
        String expected = "FlightCompositeFilter: Тестовый фильтр\n" +
                "  Группа 1: \n" +
                "    НЕ Отправление до "+LocalDateTime.now().format(formatter)+"\n" +
                "    Исключить рейсы в которых есть сегменты с перепутанным временем\n" +
                "    НЕ Ожидание пересадки (минут) более 120\n" +
                "  Группа 2: \n" +
                "    Ожидание пересадки (минут) более 120\n";
        assertEquals(expected,f5.toString());
    }

    @Test
    void testEquals() {
        FlightCompositeFilter f0 = new FlightCompositeFilter().byDepartureAfter(LocalDateTime.now()).setName("test1");
        FlightCompositeFilter f1 = f0;
        FlightCompositeFilter f2 = new FlightCompositeFilter().byDepartureAfter(LocalDateTime.now()).setName("test1");
        FlightCompositeFilter f3 = new FlightCompositeFilter().byDepartureAfter(LocalDateTime.now()).setName("test1");
        assertEquals(f0,f1);
        assertEquals(f1,f2);
        assertEquals(f1.hashCode(),f2.hashCode());
        assertEquals(f1,f3);
        f2.setName("test2");
        assertNotEquals(f1,f2);
        f3.byDepartureBetween(LocalDateTime.now(),LocalDateTime.now().plusDays(10));
        assertNotEquals(f1,f3);

    }
}