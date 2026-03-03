package model;

public class SolidPart  {
    private final Topology topology;
    private final int primitiveCount;
    private final int startIndex;

    public SolidPart(final Topology topology, final int primitiveCount, final int startIndex) {
        this.topology = topology;
        this.primitiveCount = primitiveCount;
        this.startIndex = startIndex;
    }

    public Topology getTopology() {
        return topology;
    }

    public int getPrimitiveCount() {
        return primitiveCount;
    }

    public int getStartIndex() {
        return startIndex;
    }
}
