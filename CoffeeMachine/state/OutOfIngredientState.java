package state;

import decorator.Coffee;
import facade.CoffeeVendingMachine;

public class OutOfIngredientState implements VendingMachineState {
    @Override
    public void selectCoffee(CoffeeVendingMachine m, Coffee c) {
        System.out.println("Sorry, we are sold out.");
    }

    @Override
    public void insertMoney(CoffeeVendingMachine m, int a) {
        System.out.println("Sorry, we are sold out. Money refunded.");
    }

    @Override
    public void dispenseCoffee(CoffeeVendingMachine m) {
        System.out.println("Sorry, we are sold out.");
    }

    @Override
    public void cancel(CoffeeVendingMachine machine) {
        System.out.println("Refunding " + machine.getMoneyInserted());
        machine.reset();
        machine.setState(new ReadyState());
    }
}
