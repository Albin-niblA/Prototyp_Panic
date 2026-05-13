package model.managers;

public class MyntManager {
    private int balance = 0;

    //Lägger till mynt
    public void earn(int amount){
        if(amount > 0){
            balance += amount;
        }
    }

    //Tar bort mynt
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
    //kolla om man har råd
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
