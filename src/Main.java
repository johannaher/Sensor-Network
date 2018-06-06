/**
 * main
 */
public class Main {
    public static void main(String[] args) {
        Environment environment = new Environment(50, 50, 4, 10000, 0.5f);

        for (int i = 0; i <= 10000; i++) {
            environment.timetick();
        }

        System.out.println("Events created: " + environment.getNumOfEvents());
        System.out.println("Success rate: " + environment.getQueryNodesSuccessRate()*100 + "%");
    }
}