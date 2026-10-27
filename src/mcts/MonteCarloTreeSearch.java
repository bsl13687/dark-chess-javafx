package mcts;

import java.util.*;
import java.util.concurrent.*;

public class MonteCarloTreeSearch {

    private static final double EXPLORATION_FACTOR = 1.414;
    private static final int TIME_LIMIT_MS = 10000;//搜索5秒
    private static final int THREAD_POOL_MAX_SIZE = 8;//最大线程数
    private int threadPoolSize;
    public MonteCarloTreeSearch(int threadPoolSize){
        if(threadPoolSize<=2){
            threadPoolSize = 2;
        }
        if(threadPoolSize >= THREAD_POOL_MAX_SIZE){
            threadPoolSize = THREAD_POOL_MAX_SIZE;
        }
        this.threadPoolSize = threadPoolSize;
    }

    // 核心搜索方法
    public Node findBestMove(FlipGame initialState) {
        Node rootNode = new Node(initialState);
        
        final long endTime = System.currentTimeMillis() + TIME_LIMIT_MS;

        final CountDownLatch countDownLatch = new CountDownLatch(threadPoolSize);
        for (int i = 0;i<threadPoolSize;i++){
            new Thread(()->{
                    while (true) {
                        Node node = rootNode;
                        FlipGame gameState = node.getGameState().deepClone();

                        // 1. 选择阶段
                        node = selectNode(node, gameState);
                        if (!node.isTerminal()) {
                            node = expandNode(node, gameState);
                        }

                        // 3. 模拟阶段
                        int result = simulate(gameState);

                        // 4. 反向传播
                        backPropagate(node, result);
                        if (System.currentTimeMillis() >= endTime) {

                            break;
                        }
                    }
                    countDownLatch.countDown();
                }).start();
        }

        try {
            countDownLatch.await();
        }catch (InterruptedException e){
            e.printStackTrace();
        }


        return getBestChild(rootNode);
    }

    // 选择阶段（带UCB优化）
    private Node selectNode(Node node, FlipGame gameState) {
        while (!node.isTerminal() && node.isFullyExpanded()) {
            Node bestChild = null;
            double bestValue = Double.NEGATIVE_INFINITY;
            
            for (Node child : node.getChildren()) {
                double ucbValue = calculateUCB(child);
                if (ucbValue > bestValue) {
                    bestValue = ucbValue;
                    bestChild = child;
                }
            }
            
            if (bestChild != null) {
                node = bestChild;
                gameState.applyMove(node.getMove());
            } else {
                break;
            }
        }
        return node;
    }

    // UCB计算公式
    private double calculateUCB(Node node) {
        //没访问过估值无穷大
        if (node.getVisitCount() == 0) {
            return Double.POSITIVE_INFINITY;
        }
        
        double exploitation = node.getWinScore() / node.getVisitCount();
        double exploration = EXPLORATION_FACTOR * 
            Math.sqrt(Math.log(node.getParent().getVisitCount()) / node.getVisitCount());
        
        return exploitation + exploration;
    }

    // 扩展节点
    private Node expandNode(Node node, FlipGame gameState) {
        List<Node> possibleMoves = generateMoves(gameState);
        for (Node move : possibleMoves) {
            if (!node.containsChild(move)) {
                Node newChild = new Node(move.getMove(), gameState.deepClone());
                node.addChild(newChild);
                if (possibleMoves.size() == 1) {
                    return newChild;
                }
            }
        }
        return node.selectRandomChild();
    }

    // 生成合法移动
    private List<Node> generateMoves(FlipGame gameState) {
        List<Node> moves = gameState.getAllMoves();

        for (Node move : moves) {

            FlipGame clone = gameState.deepClone();
            clone.applyMove(move.getMove());
            moves.add(new Node(move.getMove(), clone));
        }
        return moves;
    }

    // 模拟阶段（带启发式）
    private int simulate(FlipGame gameState) {
//        while (!gameState.isGameOver()) {
//            Node randomMove = selectHeuristicMove(gameState);
//            gameState.applyMove(randomMove.getMove());
//        }
        while (true){
            if(gameState.isGameOver()){
                break;
            }
            Node randomMove = selectHeuristicMove(gameState);
            gameState.applyMove(randomMove.getMove());
        }
        return gameState.getWinner();
    }

    // 带启发式的移动选择
    private Node selectHeuristicMove(FlipGame gameState) {
//        List<Node> moves = gameState.getAllMoves();
        List<Node> moves = gameState.getAllWeightMoves();

        // 优先选择攻击性移动
//        moves.sort((a, b) ->
//            Integer.compare(b.getMove().attackValue(), a.getMove().attackValue()));
        //需要通过权重计算
        int sum = 0;
        int randomIndex = -1;
        for (Node node : moves) {
            sum += node.getMove().weight;
        }
        Random rand = new Random();
        int r = rand.nextInt(sum);
        int currentSum = 0;
        for (int i = 0; i < moves.size(); i++) {
            currentSum += moves.get(i).getMove().weight;
            if (currentSum > r) {
                randomIndex = i;
                break;
            }
        }
        if (randomIndex != -1) { //没选出来，那就直接随机返回一个
            randomIndex = (int) (Math.random() * moves.size());
        }


        return moves.get(randomIndex);
    }

    // 反向传播
    private void backPropagate(Node node, int result) {
        while (node != null) {
            node.update(result);
            node = node.getParent();
        }
    }

    // 选择最佳子节点
    private Node getBestChild(Node node) {
        List<Node> children = node.getChildren();
        for(Node n:children){
            System.out.println(n.getMove().toString()+",visit_count:"+n.getVisitCount()+",win_score:"+n.getWinScore());
        }
        return node.getChildren().stream()
            .max(Comparator.comparingDouble(c -> c.getVisitCount()))
            .orElse(null);
    }
}