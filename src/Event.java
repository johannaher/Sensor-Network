public class Event {

    private int eventId;
    private int timestamp;
    private Node eventNode;

    /**
     * Event()
     * Default constructor
     *
     * @param id
     * @param time
     * @param node
     */
    public Event(int id, int time, Node node){
        this.eventId = id;
        this.timestamp = time;
        this.eventNode = node;
    }

    public int getEventId(){
        return eventId;
    }

    public int getTimestamp(){
        return timestamp;
    }

    public Node getEventPosition(){
        return eventNode;
    }
}
