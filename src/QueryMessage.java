public class QueryMessage extends Message {
    final int maxSteps = 50;

    public QueryMessage(int event, Node startNode){
        super(event, startNode);
    }

    public void move(Node newNode){
        path.push(newNode);
        hasMoved = true;
    }
}