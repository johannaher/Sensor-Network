
public class Main {
    public static void main(String[] args) {
        Environment environment = new Environment(50, 50, 4, 10000, 2);

        for (int i = 0; i <= 10000; i++) {
            //System.out.println("\n" + environment.getTimer());

            environment.timetick();
        }
    }
}