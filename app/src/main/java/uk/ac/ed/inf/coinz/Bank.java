package uk.ac.ed.inf.coinz;

import java.util.ArrayList;

public class Bank {
    private int numOfCoinzToday;
    private Double SHILs;
    private Double DOLRs;
    private Double QUIDs;
    private Double PENYs;
    public ArrayList<String> bankCoinIDs = new ArrayList<String>();

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
        bankCoinIDs.add(id);
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
