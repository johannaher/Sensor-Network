public class QueryMessage extends Message {
    public static int maxSteps = 50;
    private boolean resent;

    /**
     * QueryMessage()
     * Default constructor
     *
     * @param event
     * @param startNode
     */
    public QueryMessage(int event, Node startNode){
        super(event, startNode);
        resent = false;
    }

    public void move(Node newNode){
        path.push(newNode);
        hasMoved = true;
    }

    public boolean hasBeenResent(){
        return resent;
    }

    public void resend(){
        resent = true;
    }
}