package uk.ac.ed.inf.coinz;


import java.util.ArrayList;
import java.util.HashMap;

public class Bank {
    private int numOfCoinzToday;
    private Double SHILs;
    private Double DOLRs;
    private Double QUIDs;
    private Double PENYs;
    private HashMap<String,Double> rates;
    private ArrayList<Coin> bankCoinz;

    public Bank(HashMap<String,Double> rates, ArrayList<Coin> bankCoinz){
        this.rates = rates;
        this.bankCoinz = bankCoinz;
        SHILs=0.0;
        DOLRs=0.0;
        QUIDs=0.0;
        PENYs=0.0;

        if(!bankCoinz.isEmpty()) {
            for (Coin c : bankCoinz)
                switch (c.getCoinCurrency()) {
                    case "SHIL":
                        SHILs += c.getCoinValue();
                        break;
                    case "DOLR":
                        DOLRs += c.getCoinValue();
                        break;
                    case "QUID":
                        QUIDs += c.getCoinValue();
                        break;
                    default:
                        PENYs += c.getCoinValue();
                        break;
                }
        }

    }

    public Double getCoinAmount(String currency, HashMap<String, Double>  rates){
        if(bankCoinz.isEmpty()) return 0.0;

        switch (currency) {
            case "SHIL":
                return SHILs;
            case "DOLR":
                return DOLRs;
            case "QUID":
                return QUIDs;
            case "PENY":
                return PENYs;
            default:
                return getCoinAmount("SHIL", rates) * rates.get("SHIL")
                        + getCoinAmount("DOLR", rates) * rates.get("DOLR")
                        + getCoinAmount("QUID", rates) * rates.get("QUID")
                        + getCoinAmount("PENY", rates) * rates.get("PENY");
        }
    }

    public ArrayList<Coin> getCoinz(){return bankCoinz;}


}
