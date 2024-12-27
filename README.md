Abstract generic serializable filter for learning purpose that can compose multiple predicates with any configuration of AND/OR reductions, negates, parallel streams (if U need) and simple method-chaining using, like this:

    FlightCompositeFilter f = new FlightCompositeFilter()
                .byDepartureBefore(LocalDateTime.now()).negate()
                .excludeWrongSegments()
                .byLayoverMinutesMoreThan(120).negate()
                .or()
                .byLayoverMinutesMoreThan(120)
                .setName("Тестовый фильтр");

than 'f.filter(flights)' returns filtered list
