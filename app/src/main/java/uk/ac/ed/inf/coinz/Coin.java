package uk.ac.ed.inf.coinz;

public class Coin {

    private String  id;
    private String currency;
    private Double value;

    public Coin(String currency, Double value, String id){
        this.id=id;
        this.currency=currency;
        this.value=value;
    }

    public String getCoinId(){
        return id;
    }
    public String getCoinCurrency(){
        return currency;
    }
    public Double getCoinValue(){
        return value;
    }
}
