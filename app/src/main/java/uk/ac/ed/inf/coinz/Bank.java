package uk.ac.ed.inf.coinz;

import java.util.ArrayList;
import java.util.HashMap;

import static com.mapbox.mapboxsdk.Mapbox.getApplicationContext;

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
        if(!bankCoinz.isEmpty()) {
            for (Coin c : bankCoinz) {
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
        numOfCoinzToday = SaveSharedPreference.getNumofBankedToday(getApplicationContext());

    }

    public boolean addCoinz(String currency, Double amount, String id){
        if(numOfCoinzToday<=25) {
            if (currency == "SHIL") {
                SHILs += amount;
            } else if (currency == "DOLR") {
                DOLRs += amount;
            } else if (currency == "QUID") {
                QUIDs += amount;
            } else {
                PENYs += amount;
            }
            Coin c = new Coin(currency, amount, id);
            bankCoinz.add(c);
            numOfCoinzToday++;
            saveBank();
            return true;
        }
        return false;
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

    public ArrayList<Coin> getCoinz(){
        return bankCoinz;
    }


    public void saveBank(){
        String cn;
        int i=0;

        for(Coin c : bankCoinz){
            cn = c.getCoinCurrency()+":"+c.getCoinValue()+":"+c.getCoinId();
            SaveSharedPreference.setBankCoin(getApplicationContext(), "bankcoin"+":"+String.valueOf(i), cn);
            i++;
        }

        SaveSharedPreference.setNumofBankedToday(getApplicationContext(),numOfCoinzToday );

    }
}
