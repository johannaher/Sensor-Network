import java.util.*;
import java.lang.*;


public class RoutingTable {

    //private Node currentNode;
    private HashMap<Integer, Stack<Node>> eventPaths;

    /**
     * Constructor
     *
     */

    public RoutingTable(){
        this.eventPaths = new HashMap<>();
    }

    public boolean checkEvent(int eventId){
        if(!eventPaths.isEmpty()){
            return (eventPaths.containsKey(eventId));
        }
        return false;
    }

    /**
     * syncTables()
     * Duplicate the routingTables
     */
    public void syncTables(RoutingTable otherTable){

        for(Integer i: eventPaths.keySet()){

            if(otherTable.eventPaths.containsKey(i) ){

                if(eventPaths.get(i).size() > otherTable.eventPaths.get(i).size()){
                    eventPaths.replace(i, otherTable.eventPaths.get(i));
                }else if(eventPaths.get(i).size() < otherTable.eventPaths.get(i).size()){
                    otherTable.eventPaths.replace(i, eventPaths.get(i));
                }
            }else{
                otherTable.eventPaths.put(i,eventPaths.get(i));
            }
        }
        if(otherTable.eventPaths.size() > eventPaths.size()){
            otherTable.syncTables(this);
        }
    }

    public void setEventPath(int eventId, Stack<Node> path){
        if(eventPaths.containsKey(eventId)){
            eventPaths.replace(eventId, path);
        } else {
            eventPaths.put(eventId, path);
        }
    }

    public Stack<Node> getEventPath(int eventId){
        return eventPaths.get(eventId);
    }
}
