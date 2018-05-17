import java.util.Stack;

public abstract class Message {
    protected Stack<Node> path;
    protected int eventId;
    public boolean hasMoved;

    /**
     * Message()
     * Default constructor
     *
     * @param eventId
     * @param startNode
     */
    public Message(int eventId, Node startNode){
        this.eventId = eventId;
        path = new Stack<Node>();
        path.push(startNode);
        this.hasMoved = false;
    }

    public Stack<Node> getPath(){
        return path;
    }

    public int getEventId(){
        return eventId;
    }

}