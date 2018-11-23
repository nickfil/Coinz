package uk.ac.ed.inf.coinz;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import static com.mapbox.mapboxsdk.Mapbox.getApplicationContext;

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
                if(c.getCoinCurrency().equals("SHIL")){
                    SHILs+=c.getCoinValue();
                }
                else if(c.getCoinCurrency().equals("DOLR")){
                    DOLRs+=c.getCoinValue();
                }
                else if(c.getCoinCurrency().equals("QUID")){
                    QUIDs+=c.getCoinValue();
                }
                else{
                    PENYs+=c.getCoinValue();
                }
            }
        }
    }

    public HashMap<String, Double> getRates() {
        return rates;
    }

    public void addCoinz(String currency, Double amount, String id){ //when adding a coin to the wallet we need to check the currency
        if(currency.equals("SHIL")){                                        //and then add it to the relevant sub wallet
            SHILs+=amount;
        }
        else if(currency.equals("DOLR")){
            DOLRs+=amount;
        }
        else if(currency.equals("QUID")){
            QUIDs+=amount;
        }
        else{
            PENYs+=amount;
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

    public void wipeWallet(){
        SHILs=0.0;
        DOLRs=0.0;
        QUIDs=0.0;
        PENYs=0.0;

        for(Coin coin : walletCoinz){
            LoginActivity.firestore_wallet.document(coin.getCoinId()).delete();
        }
        walletCoinz.clear();

    }

    public ArrayList<Coin> getCoinz(){
        return walletCoinz;
    }

    public Boolean contains(String id){
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
