import java.util.ArrayList;
import java.util.Random;

public class Environment {

    private Node[][] nodes;
    private Node[] queryNodes;
    private ArrayList<Event> events;
    private int xSize, ySize, eventChance, timer;
    private float queryNodesSuccessRate;

    /**
     * Environment()
     * Default constructor
     *
     * @param height
     * @param width
     * @param queryNodes
     * @param eventChance
     * @param agentChance
     */
    public Environment(int height, int width, int queryNodes, int eventChance, int agentChance){
        this.xSize = width;
        this.ySize = height;
        this.eventChance = eventChance;
        this.nodes = new Node[xSize][ySize];
        this.timer = 0;
        this.queryNodes = new Node[queryNodes];
        this.events = new ArrayList<>();

        for(int y = 0; y < ySize; y++){
            for(int x = 0; x < xSize; x++){
                nodes[x][y] = new Node(new Position(x, y), agentChance);

                if(x > 0){
                    if(nodes[x-1][y] != null){
                        nodes[x][y].setNeighbour(nodes[x-1][y], Direction.WEST);
                    }

                    if(y > 0){
                        if(nodes[x-1][y-1] != null){
                            nodes[x][y].setNeighbour(nodes[x-1][y-1], Direction.NORTHWEST);
                        }
                    }
                }

                if(y > 0) {
                    if (nodes[x][y - 1] != null) {
                        nodes[x][y].setNeighbour(nodes[x][y - 1], Direction.NORTH);
                    }
                    if(x < xSize-1) {
                        if (nodes[x + 1][y - 1] != null) {
                            nodes[x][y].setNeighbour(nodes[x + 1][y - 1], Direction.NORTHEAST);
                        }
                    }
                }
                //första noden har inga grannar

                //kolla så att granne finns åt hållet

                //andra noden lägger till nod 0,0
                //nod 0,0 lägger till andra noden
                //nod 0,2 lägger till 0,1
                //nod 0,1 lägger till 0,2

                //nod 1,0 lägger till 0,0 och 0,1
                //nod 0,0 lägger till 1,0
                //nod 0,1 lägger till 1,0
            }
        }

        randomizeQueryNodes();
    }

    /**
     * method only used for testing
     * @return
     */
    public Node[] getQueryNodes(){
        return queryNodes;
    }
    /**
     * method only used for testing
     * @return
     */
    public Node getNode(Position position){
        return nodes[position.getX()][position.getY()];
    }

    public void timetick(){
        Random random = new Random();

         if(timer % 400 == 0 && !events.isEmpty()){
            for(int i = 0 ; i < queryNodes.length ; i++){
                //random event från eventlistan förfrågas
                queryNodes[i].createQueryMessage(random.nextInt(events.size()));
            }
        }

        for(int y = 0; y < ySize; y++){
            for(int x = 0; x < xSize; x++){
                if(random.nextInt(eventChance) == 1){
                   events.add(nodes[x][y].createEvent(events.size(), timer));
                }
                nodes[x][y].update();
                //varje nod kollar och hanterar första meddelandet i kön
            }
        }

        queryNodesSuccessRate = 0;
        for(int i = 0 ; i < queryNodes.length ; i++){
            queryNodesSuccessRate += queryNodes[i].getSuccessRate();
        }

        timer++;
    }

    private void randomizeQueryNodes(){
        Random random = new Random();
        for(int i = 0; i < queryNodes.length; i++){
            int randomX = random.nextInt(xSize);
            int randomY = random.nextInt(ySize);

            if(!nodes[randomX][randomY].getQueryNode()) {
                nodes[randomX][randomY].setQueryNode();
                queryNodes[i] = nodes[randomX][randomY];
            } else {
                i--;
            }

        }
    }

    public int getTimer(){
        return timer;
    }

    public float getQueryNodesSuccessRate(){
        return queryNodesSuccessRate /= queryNodes.length;
    }

    public int getQueriesSent(){
        return getQueriesSent();
    }

    public int getNumOfEvents(){
        return events.size();
    }

 /*  private void createEvent(Node node){
        Event event = new Event(events.size(), timer, node.getPosition());

        Random random = new Random();
        if(random.nextInt(1) > 0){
            //lägg till agenten i nodens kö
            node.createAgentMessage(event.getEventId());
        }
    }
*/

}
