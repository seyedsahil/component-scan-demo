package demo;

public enum Strategy {

    PROTOTYPE("prototype"),
    SINGLETON("single");

    private String strategy;

    Strategy(String strategy) {
        this.strategy = strategy;
    }

    public String getStrategy() {
        return strategy;
    }
}
