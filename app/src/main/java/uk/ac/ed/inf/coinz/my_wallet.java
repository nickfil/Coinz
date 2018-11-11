package uk.ac.ed.inf.coinz;

import java.util.ArrayList;
import java.util.Queue;

public class my_wallet{

    private Double SHILs;
    private Double DOLRs;
    private Double QUIDs;
    private Double PENYs;
    public ArrayList<String> coinIDs = new ArrayList<String>();

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
        coinIDs.add(id);
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
        else{
            return PENYs;
        }
    }

    public void wipeWallet(){
        SHILs=0.0;
        DOLRs=0.0;
        QUIDs=0.0;
        PENYs=0.0;
    }

}
