package me.dio.barbearia.domain.model;

public enum ServiceType {

    BARBA("Barba", 30.00),
    CABELO("Cabelo", 40.00),
    COMBO("Barba e Cabelo", 65.00);

    private final String description;

    private final Double price;

    ServiceType(String description, Double price) {
        this.description = description;
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public Double getPrice() {
        return price;
    }
}
