package com.gridnine.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * По условию рейсов и фильтров может быть очень много, поэтому для оптимизации
 * я решил выделить отдельные поля для тех свойств, которые фильтруются чаще всего.
 * (количество пересадок, время ожидания пересадки, даты начала и конца рейса)
 * (Было бы неплохо ещё добавить общее время рейса, но пока этого не надо...)
 * Так же все проверки связанные непосредственно с созданием рейса я перенёс в конструктор рейса
 * Для удобства проверки я добавил нумерацию рейсов.
 */

public class Flight {
    private int id;
    private static int lastId=0;
    private final List<Segment> segments;
    private final int segmentCount;
    private final LocalDateTime departureDate;
    private final LocalDateTime arrivalDate;
    private final long layoverMinutes;

    public Flight(final List<Segment> segments) {
        id=++lastId;
        this.segments = segments;
        this.segmentCount = segments.size();
        this.departureDate = segments.get(0).getDepartureDate();
        this.arrivalDate = segments.get(segmentCount - 1).getArrivalDate();
        this.layoverMinutes = segmentCount == 1 ? 0 : countLayover(segments);
    }

    public Flight(final LocalDateTime... dates) {
        this(buildSegments(dates));
    }

    private static ArrayList<Segment> buildSegments(LocalDateTime[] dates) {
        if ((dates.length % 2) != 0) {
            throw new IllegalArgumentException("you must pass an even number of dates");
        }
        ArrayList<Segment> segs = new ArrayList<>(dates.length / 2);
        for (int i = 0; i < (dates.length - 1); i += 2) {
            segs.add(new Segment(dates[i], dates[i + 1]));
        }
        return segs;
    }

    private static long countLayover(List<Segment> segments) {
        long layoverCounter = 0;
        for (int i = 0; i < segments.size() - 1; i++) {
            layoverCounter += ChronoUnit.MINUTES.between(segments.get(i).getArrivalDate(), segments.get(i + 1).getDepartureDate());
        }
        return layoverCounter;
    }

    public int getSegmentCount() {
        return segmentCount;
    }

    public LocalDateTime getDepartureDate() {
        return departureDate;
    }

    public LocalDateTime getArrivalDate() {
        return arrivalDate;
    }

    public List<Segment> getSegments() {
        return segments;
    }

    public long getLayoverMinutes() {
        return layoverMinutes;
    }

    @Override
    public String toString() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd-MM-yy HH:mm");
        return "Рейс: "+id+", пересадок " + (segmentCount - 1) + ", ожидание " + layoverMinutes + " минут" + ", отправление " + dtf.format(departureDate) + ", прибытие " + dtf.format(arrivalDate) + ", перелёты: " + segments.stream().map(Object::toString).collect(Collectors.joining(" "));
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}