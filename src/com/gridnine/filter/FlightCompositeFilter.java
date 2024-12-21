package com.gridnine.filter;

import com.gridnine.model.Flight;

import java.io.Serializable;
import java.time.LocalDateTime;

public class FlightCompositeFilter extends CompositeFilter<Flight, FlightCompositeFilter> implements Serializable {

    public FlightCompositeFilter() {
        super();
    }

    public FlightCompositeFilter byArrivalAfter(LocalDateTime time) {
        addTimeFilter("Прибытие после ", time, f -> f.getArrivalDate().isAfter(time));
        return this;
    }

    public FlightCompositeFilter byArrivalBefore(LocalDateTime time) {
        addTimeFilter("Прибытие до ", time, f -> f.getArrivalDate().isBefore(time));
        return this;
    }

    public FlightCompositeFilter byArrivalBetween(LocalDateTime time1, LocalDateTime time2) {
        addTimeRangeFilter("Прибытие между ", time1, time2,
                f -> f.getArrivalDate().isAfter(time1) && f.getArrivalDate().isBefore(time2));
        return this;
    }

    public FlightCompositeFilter byArrivalEquals(LocalDateTime time) {
        addTimeFilter("Отбытие ровно в ", time, f -> f.getArrivalDate().equals(time));
        return this;
    }

    public FlightCompositeFilter byDepartureAfter(LocalDateTime time) {
        addTimeFilter("Отправление после ", time, f -> f.getDepartureDate().isAfter(time));
        return this;
    }

    public FlightCompositeFilter byDepartureBefore(LocalDateTime time) {
        addTimeFilter("Отправление до ", time, f -> f.getDepartureDate().isBefore(time));
        return this;
    }

    public FlightCompositeFilter byDepartureBetween(LocalDateTime time1, LocalDateTime time2) {
        addTimeRangeFilter("Отправление между ", time1, time2,
                f -> f.getDepartureDate().isAfter(time1) && f.getDepartureDate().isBefore(time2));
        return this;
    }

    public FlightCompositeFilter byDepartureEquals(LocalDateTime time) {
        addTimeFilter("Отправление ровно в ", time, f -> f.getDepartureDate().equals(time));
        return this;
    }

    public FlightCompositeFilter byLayoverMinutesMoreThan(long layoverMinutes) {
        addFilter("Ожидание пересадки (минут) более ", layoverMinutes,
                f -> f.getLayoverMinutes() > layoverMinutes);
        return this;
    }

    public FlightCompositeFilter byLayoverMinutesLessThan(long layoverMinutes) {
        addFilter("Ожидание пересадки (минут) менее ", layoverMinutes,
                f -> f.getLayoverMinutes() < layoverMinutes);
        return this;
    }

    public FlightCompositeFilter byLayoverMinutesBetween(long layoverMinutes1, long layoverMinutes2) {
        addRangeFilter("Ожидание пересадки (минут) период ", layoverMinutes1, layoverMinutes2,
                f -> f.getLayoverMinutes() >= layoverMinutes1 && f.getLayoverMinutes() <= layoverMinutes2);
        return this;
    }

    public FlightCompositeFilter byLayoverMinutesEquals(long layoverMinutes) {
        addFilter("Ожидание пересадки (минут) ровно ", layoverMinutes,
                f -> f.getLayoverMinutes() == layoverMinutes);
        return this;
    }

    /**
     * Совершенно бесполезный фильтр, сделанный для задания, эта проверка должна быть при создании Segment
     * Так же замечу что самый длинный рейс в мире на текущий момент <24часов, тоже было бы неплохо добавить
     * соответствующую проверку сюда
     */
    public FlightCompositeFilter excludeWrongSegments() {
        addToCurrentGroup(new NamedPredicate<>("Исключить рейсы в которых есть сегменты с перепутанным временем",
                f -> f.getSegments().stream().anyMatch(s -> s.getDepartureDate().isBefore(s.getArrivalDate()))));
        return this;
    }
}