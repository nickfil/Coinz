package uk.ac.ed.inf.coinz;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import static com.mapbox.mapboxsdk.Mapbox.getApplicationContext;

public class my_wallet{

    private Double SHILs;
    private Double DOLRs;
    private Double QUIDs;
    private Double PENYs;
    public HashMap<String, Double> rates;
    public ArrayList<Coin> walletCoinz;

    public my_wallet(HashMap<String,Double> rates, ArrayList<Coin> walletCoinz){
        this.rates = rates;
        this.walletCoinz = walletCoinz;
        SHILs=0.0;
        DOLRs=0.0;
        QUIDs=0.0;
        PENYs=0.0;
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
        Coin thisCoin = new Coin(currency, amount, id);
        walletCoinz.add(thisCoin);
    }

    public Double getCoinAmount(String currency){
        if(currency.equals("SHIL")){
            return SHILs;
        }
        else if(currency.equals("DOLR")){
            return DOLRs;
        }
        else if(currency.equals("QUID")){
            return QUIDs;
        }
        else if(currency.equals("PENY")){
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
        walletCoinz.clear();
    }

    public ArrayList<Coin> getCoinz(){
        return walletCoinz;
    }

    public void saveWallet(){
        DateFormat df = new SimpleDateFormat("yyyy/MM/dd");
        Date date = new Date();
        String dt = df.format(date);

        String cn;
        int i=0;
        for(Coin c : walletCoinz){
            cn = c.getCoinCurrency()+":"+c.getCoinValue()+":"+c.getCoinId();
            SaveSharedPreference.setWalletCoin(getApplicationContext(), "walletcoin"+":"+String.valueOf(i), cn);
            i++;
        }
    }

    public Boolean contains(String id){
        for(Coin c : walletCoinz){
            if(c.getCoinId().equals(id)){
                return true;
            }
        }
        return false;
    }

}
