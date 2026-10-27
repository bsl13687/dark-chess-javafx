package mcts;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class Node {
    private final Move move;
    private FlipGame gameState;
    private Node parent;
    private List<Node> children;
    private volatile int visitCount;
    private volatile double winScore;

    public Node() {
        this.move = new Move(-1, -1, -1, -1);
        this.children = new CopyOnWriteArrayList<>();
    }

    public Node(Move move, FlipGame gameState) {
        this.move = move;
        this.gameState = gameState;
        this.children = new CopyOnWriteArrayList<>();
    }

    public Node(FlipGame gameState) {
        this.move = new Move(-1, -1, -1, -1);
        this.gameState = gameState;
        this.children = new CopyOnWriteArrayList<>();
    }

    // 核心方法
    public FlipGame getGameState() {
        return gameState;
    }

    public FlipGame deepClone() {
        return new FlipGame(this.gameState);
    }

    public boolean isTerminal() {
        return gameState.isGameOver();
    }

    public boolean isFullyExpanded() {
        return children.size() == gameState.getAllMoves().size();
    }

    public List<Node> getChildren() {
        return children;
    }

    public void addChild(Node child) {
        child.setParent(this);
        children.add(child);
    }

    public Node selectRandomChild() {
        return children.get((int) (Math.random() * children.size()));
    }

    public Node getParent() {
        return parent;
    }

    public void setParent(Node parent) {
        this.parent = parent;
    }

    public Move getMove() {
        return move;
    }

    public int getVisitCount() {
        return visitCount;
    }

    public double getWinScore() {
        return winScore;
    }

    public void update(int result) {
        this.visitCount++;
        this.winScore += result;
    }

    public boolean containsChild(Node move) {
        return children.stream().anyMatch(c -> c.getMove().equals(move.getMove()));
    }

    // 移动类
    public static class Move {
        public final int x1;
        public final int y1;
        public final int x2;
        public final int y2;
        public final int weight;

        public Move(int x1, int y1, int x2, int y2,int weight) {
            this.x1 = x1;
            this.y1 = y1;
            this.x2 = x2;
            this.y2 = y2;
            this.weight = weight;
        }
        public Move(int x1, int y1, int x2, int y2) {
            this(x1,y1,x2,y2,1);
        }

        public int attackValue() {
            return (x1 == -1) ? 0 : 1; // 翻开棋子得分为0，攻击得分为1

        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;
            Move move = (Move) obj;
            return x1 == move.x1 && y1 == move.y1 && x2 == move.x2 && y2 == move.y2;
        }

        @Override
        public String toString() {
            return "Move{" +
                    "x1=" + x1 +
                    ", y1=" + y1 +
                    ", x2=" + x2 +
                    ", y2=" + y2 +
                    '}';
        }
    }
}
