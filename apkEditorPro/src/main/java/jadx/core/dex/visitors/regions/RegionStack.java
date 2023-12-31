package jadx.core.dex.visitors.regions;

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Deque;
import java.util.HashSet;
import java.util.Set;

import jadx.core.dex.nodes.BlockNode;
import jadx.core.dex.nodes.IRegion;
import jadx.core.dex.nodes.MethodNode;
import jadx.core.utils.exceptions.JadxOverflowException;

final class RegionStack {
    //private static final Logger LOG = LoggerFactory.getLogger(RegionStack.class);
    private static final boolean DEBUG = false;

    private static final int REGIONS_STACK_LIMIT = 1000;

    static {
        if (DEBUG) {
            //LOG.debug("Debug enabled for {}", RegionStack.class);
        }
    }

    private final Deque<State> stack;
    private State curState;

    public RegionStack(MethodNode mth) {
        if (DEBUG) {
            //LOG.debug("New RegionStack: {}", mth);
        }
        this.stack = new ArrayDeque<State>();
        this.curState = new State();
    }

    public void push(IRegion region) {
        stack.push(curState);
        if (stack.size() > REGIONS_STACK_LIMIT) {
            throw new JadxOverflowException("Regions stack size limit reached");
        }
        curState = curState.copy();
        curState.region = region;
        if (DEBUG) {
            //LOG.debug("Stack push: {}: {}", size(), curState);
        }
    }

    public void pop() {
        curState = stack.pop();
        if (DEBUG) {
            //LOG.debug("Stack  pop: {}: {}", size(), curState);
        }
    }

    /**
     * Add boundary(exit) node for current stack frame
     *
     * @param exit boundary node, null will be ignored
     */
    public void addExit(BlockNode exit) {
        if (exit != null) {
            curState.exits.add(exit);
        }
    }

    public void addExits(Collection<BlockNode> exits) {
        for (BlockNode exit : exits) {
            addExit(exit);
        }
    }

    public void removeExit(BlockNode exit) {
        if (exit != null) {
            curState.exits.remove(exit);
        }
    }

    public boolean containsExit(BlockNode exit) {
        return curState.exits.contains(exit);
    }

    public IRegion peekRegion() {
        return curState.region;
    }

    public int size() {
        return stack.size();
    }

    @Override
    public String toString() {
        return "Region stack size: " + size() + ", last: " + curState;
    }

    private static final class State {
        final Set<BlockNode> exits;
        IRegion region;

        public State() {
            exits = new HashSet<BlockNode>(4);
        }

        private State(State c) {
            exits = new HashSet<BlockNode>(c.exits);
        }

        public State copy() {
            return new State(this);
        }

        @Override
        public String toString() {
            return "Region: " + region + ", exits: " + exits;
        }
    }
}
