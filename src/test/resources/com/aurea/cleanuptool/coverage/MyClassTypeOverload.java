package testcover;

public class MyClassTypeOverload {

    public String getLiteralValue() {
        return "Literal String";
    }

    public String getLigeralValue(String gotcha) {
        return "Literal String";
    }

    private String getLiteralValuePrivate() {
        return "Literal String Private";
    }

}
