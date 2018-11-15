package uk.ac.ed.inf.coinz;

import java.util.ArrayList;
import java.util.HashMap;

public class my_wallet{

    private Double SHILs;
    private Double DOLRs;
    private Double QUIDs;
    private Double PENYs;
    public HashMap<String, Double> rates = new HashMap<>();
    public ArrayList<String> walletCoinIDs = new ArrayList<>();

    public my_wallet(HashMap<String,Double> rates, ArrayList<String> walletCoinIDs){
        this.rates = rates;
        this.walletCoinIDs = walletCoinIDs;
        SHILs=0.0;
        DOLRs=0.0;
        QUIDs=0.0;
        PENYs=0.0;
    }

    public HashMap<String, Double> getRates() {
        return rates;
    }

    public void addCoinz(String currency, Double amount, String id){ //when adding a coin to the wallet we need to check the currency
        if(currency=="SHIL"){                                        //and then add it to the relevant sub wallet
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
        walletCoinIDs.add(id);
    }

    public Double getCoinAmount(String currency){
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
            Double total = getCoinAmount("SHIL")*rates.get("SHIL")
                          +getCoinAmount("DOLR")*rates.get("DOLR")
                          +getCoinAmount("QUID")*rates.get("QUID")
                          +getCoinAmount("PENY")*rates.get("PENY");
            return total;
        }
    }

    public void wipeWallet(){
        SHILs=0.0;
        DOLRs=0.0;
        QUIDs=0.0;
        PENYs=0.0;
    }
    public ArrayList<String> getCoinIDs(){
        return walletCoinIDs;
    }

}
