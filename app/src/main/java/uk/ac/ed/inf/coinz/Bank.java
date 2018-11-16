package uk.ac.ed.inf.coinz;

import java.util.ArrayList;
import java.util.HashMap;

public class Bank {
    private int numOfCoinzToday;
    private Double SHILs;
    private Double DOLRs;
    private Double QUIDs;
    private Double PENYs;
    HashMap<String,Double> rates;
    public ArrayList<Coin> bankCoinz;

    public Bank(HashMap<String,Double> rates, ArrayList<Coin> bankCoinz){
        this.rates = rates;
        this.bankCoinz = bankCoinz;
        SHILs=0.0;
        DOLRs=0.0;
        QUIDs=0.0;
        PENYs=0.0;
    }

    public void addCoinz(String currency, Double amount, String id){
        if(currency=="SHIL"){
            SHILs+=amount;
        }
        else if(currency=="DOLR"){
            DOLRs+=amount;
        }
        else if(currency=="QUID"){
            QUIDs+=amount;
        }
        else{
            PENYs+=amount;
        }
        Coin c = new Coin(currency, amount, id);
        bankCoinz.add(c);
        numOfCoinzToday++;
    }

    public Double getCoinAmount(String currency, Double[] rates){
        if(currency=="SHIL"){
            return SHILs;
        }
        else if(currency=="DOLR"){
            return DOLRs;
        }
        else if(currency=="QUID"){
            return QUIDs;
        }
        else if(currency=="PENY"){
            return PENYs;
        }
        else{
            Double total = getCoinAmount("SHIL", rates)*rates[0]
                    +getCoinAmount("DOLR", rates)*rates[1]
                    +getCoinAmount("QUID", rates)*rates[2]
                    +getCoinAmount("PENY", rates)*rates[3];
            return total;
        }
    }
}
