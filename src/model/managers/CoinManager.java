package model.managers;

public class CoinManager {
    private int balance = 0;

    // Adds coins
    public void earn(int amount){
        if(amount > 0){
            balance += amount;
        }
    }

    // Removes coins
    public boolean spend(int amount){
        if(amount <= 0){
            return true;
        }
        if(balance < amount){
            return false;
        }
        balance -= amount;
        return true;
    }
    // Checks if player can afford
    public boolean canAfford(int amount){
        return balance >= amount;
    }
    public int getBalance(){
        return balance;
    }
    public void setBalance(int v){
        balance = Math.max(0, v);
    }

}
