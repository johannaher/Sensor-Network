public enum Direction {
    NORTH, NORTHEAST, EAST, SOUTHEAST, SOUTH, SOUTHWEST, WEST, NORTHWEST;

    public Direction opposite(){
        switch (this){
            case NORTH: return Direction.SOUTH;
            case NORTHEAST: return Direction.SOUTHWEST;
            case EAST: return Direction.WEST;
            case SOUTHEAST: return Direction.NORTHWEST;
            case SOUTH: return Direction.NORTH;
            case SOUTHWEST: return Direction.NORTHEAST;
            case WEST: return Direction.EAST;
            case NORTHWEST: return Direction.SOUTHEAST;
            default: throw new IllegalStateException(this + " has no opposite. Weird!");
        }
    }
}
