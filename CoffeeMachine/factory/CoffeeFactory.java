package factory;

import decorator.Coffee;
import enums.CoffeeType;
import template.Cappuccino;
import template.Espresso;
import template.Latte;

public class CoffeeFactory {
    public static Coffee createCoffee(CoffeeType type) {
        switch (type) {
            case ESPRESSO:
                return new Espresso();
            case LATTE:
                return new Latte();
            case CAPPUCCINO:
                return new Cappuccino();
            default:
                throw new IllegalArgumentException("Unsupported coffee type: " + type);
        }
    }
}