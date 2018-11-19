package uk.ac.ed.inf.coinz;

public class Coin { //coin object, contains a currency, value and an id

    private String  id;
    private String currency;
    private Double value;

    public Coin(String currency, Double value, String id){
        this.id=id;
        this.currency=currency;
        this.value=value;
    }

    public Integer getIcon(){
        switch (currency){
            case "SHIL":
                return R.drawable.blue;
            case "DOLR":
                return R.drawable.green;
            case "QUID":
                return R.drawable.yellow;
            default:
                return R.drawable.red;
        }
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
