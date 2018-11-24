package uk.ac.ed.inf.coinz;

import java.util.ArrayList;
import java.util.HashMap;

public class my_wallet{

    private Double SHILs;
    private Double DOLRs;
    private Double QUIDs;
    private Double PENYs;
    private HashMap<String, Double> rates;
    private ArrayList<Coin> walletCoinz;

    public my_wallet(HashMap<String,Double> rates, ArrayList<Coin> walletCoinz){
        this.rates = rates;
        this.walletCoinz = walletCoinz;
        SHILs=0.0;
        DOLRs=0.0;
        QUIDs=0.0;
        PENYs=0.0;

        if(!walletCoinz.isEmpty()) {
            for (Coin c : walletCoinz) {
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
    }


    public void addCoinz(String currency, Double amount, String id){ //when adding a coin to the wallet we need to check the currency
        switch (currency) {
            case "SHIL":                                         //and then add it to the relevant sub wallet
                SHILs += amount;
                break;
            case "DOLR":
                DOLRs += amount;
                break;
            case "QUID":
                QUIDs += amount;
                break;
            default:
                PENYs += amount;
                break;
        }

        Coin c = new Coin(currency, amount, id);
        walletCoinz.add(c);
        LoginActivity.firestore_wallet.document(c.getCoinId()).set(c);
    }

    public Double getCoinAmount(String currency){
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
                return getCoinAmount("SHIL") * rates.get("SHIL")
                        + getCoinAmount("DOLR") * rates.get("DOLR")
                        + getCoinAmount("QUID") * rates.get("QUID")
                        + getCoinAmount("PENY") * rates.get("PENY");
        }
    }

    public ArrayList<Coin> getCoinz(){
        return walletCoinz;
    }

    public Boolean contains(String id){ //using a different contains method for our coin objects - to be able to realize if two objects have the exact same contents
        for(Coin c : walletCoinz){
            if(c.getCoinId().equals(id)){
                return true;
            }
        }
        return false;
    }

    public void Delete(Coin c){
        switch (c.getCoinCurrency()) {
            case "SHIL":
                SHILs -= c.getCoinValue();
                break;
            case "DOLR":
                DOLRs -= c.getCoinValue();
                break;
            case "QUID":
                QUIDs -= c.getCoinValue();
                break;
            default:
                PENYs -= c.getCoinValue();
                break;
        }

        LoginActivity.firestore_wallet.document(c.getCoinId()).delete();
    }
}
