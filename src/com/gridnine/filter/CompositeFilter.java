package com.gridnine.filter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

/**
 * CompositeFilter состоит из групп именованных предикатов связанных 'ИЛИ',
 * а внутри каждой группы все условия объединяются 'И'.
 * Все условия добавляется в текущую группу, а при вызове метода 'or()' создаётся новая группа.
 * Для применения фильтра необходимо передать список объектов в метод 'filter'.
 * Так же есть метод filterParallel для запуска параллельной фильтрации.
 * Тип <S> необходим для того чтобы методы negate() и or() работали без приведения типов
 */

public abstract class CompositeFilter<T, S extends CompositeFilter<T, S>> {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yy HH:mm");
    private final List<List<NamedPredicate<T>>> filters;
    private String name;
    public CompositeFilter() {
        name = "Unnamed Composite Filter";
        filters = new ArrayList<>();
    }

    public List<List<NamedPredicate<T>>> getFilters() {
        return filters;
    }

    public S negate() {
        if (filters.isEmpty() || filters.get(0).isEmpty()) {
            return self();
        }
        int lastGroupIndex = filters.size() - 1;
        int lastFilterInGroupIndex = filters.get(lastGroupIndex).size() - 1;
        NamedPredicate<T> namedPredicate = filters.get(lastGroupIndex).get(lastFilterInGroupIndex);
        namedPredicate.setPredicate(Predicate.not(namedPredicate.getPredicate()));
        namedPredicate.setName("НЕ " + namedPredicate.getName());
        return self();
    }

    protected void addToCurrentGroup(NamedPredicate<T> filter) {
        if (filters.isEmpty()) {
            filters.add(new ArrayList<>());
        }
        filters.get(filters.size() - 1).add(filter);
    }

    protected void addTimeFilter(String name, LocalDateTime time, Predicate<T> predicate) {
        addToCurrentGroup(new NamedPredicate<>(name + time.format(FORMATTER), predicate));
    }

    protected void addTimeRangeFilter(String name, LocalDateTime start, LocalDateTime end, Predicate<T> predicate) {
        addToCurrentGroup(new NamedPredicate<>(name + start.format(FORMATTER)
                + " - " + end.format(FORMATTER), predicate));
    }

    protected <F> void addFilter(String name, F value, Predicate<T> predicate) {
        addToCurrentGroup(new NamedPredicate<>(name + value, predicate));
    }

    protected <F> void addRangeFilter(String name, F value, F end, Predicate<T> predicate) {
        addToCurrentGroup(new NamedPredicate<>(name + value + " - " + end, predicate));
    }

    public S or() {
        if (filters.isEmpty() || filters.get(filters.size()-1).isEmpty()) {
            return self();
        }
        filters.add(new ArrayList<>());
        return self();
    }

    @SuppressWarnings("unchecked")
    protected S self() {
        return (S) this;
    }

    protected Predicate<T> joinFilters() {
        return filters.stream()
                .map(group -> group.stream().map(NamedPredicate::getPredicate)
                        .reduce(Predicate::and).orElse(x -> true))
                .reduce(Predicate::or).orElse(x -> false);
    }

    public List<T> filter(List<T> items) {
        return items.stream().filter(joinFilters()).toList();
    }

    public List<T> filterParallel(List<T> items) {
        return items.stream().parallel().filter(joinFilters()).toList();
    }


    public String getName() {
        return name;
    }

    public S setName(String name) {
        this.name = name;
        return self();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("FlightCompositeFilter: ").append(name).append("\n");
        for (int i = 0; i < filters.size(); i++) {
            sb.append("  Группа ").append(i + 1).append(": \n");
            for (NamedPredicate<T> predicate : filters.get(i)) {
                sb.append("    ").append(predicate.getName()).append("\n");
            }
        }
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CompositeFilter<?, ?> that)) return false;
        return Objects.equals(name, that.name) &&
                Objects.deepEquals(filters, that.filters);
    }

    @Override
    public int hashCode() {
        return Objects.hash(filters, name);
    }
}
