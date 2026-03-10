package state;
import decorator.Coffee;
import facade.CoffeeVendingMachine;

public interface VendingMachineState {
    void selectCoffee(CoffeeVendingMachine machine, Coffee coffee);
    void insertMoney(CoffeeVendingMachine machine, int amount);
    void dispenseCoffee(CoffeeVendingMachine machine);
    void cancel(CoffeeVendingMachine machine);
}
