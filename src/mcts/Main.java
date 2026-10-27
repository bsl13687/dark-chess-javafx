package mcts;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Arrays;
import java.util.Scanner;
//测试用的
public class Main {
//    public static void main(String[] args) {
//        FlipGame flipGame = null;
//        flipGame.initializeBoard(false, sample.Main.PieceType.BLACK_CANNONS,sample.Main.PieceType.BLACK_CANNONS,sample.Main.PieceType.RED_CANNONS,sample.Main.PieceType.RED_CANNONS);
//        ChessPiece[][] chessPieces = new ChessPiece[8][4];
//        for (int row = 0; row < 8; row++) {
//            for (int col = 0; col < 4; col++) {
//                chessPieces[row][col] = new ChessPiece(sample.Main.PieceType.EMPTY,true,false);
//                if(col == 0 && row == 0){
//                    chessPieces[row][col].setType(sample.Main.PieceType.BLACK_KING);
//                }
//                if(col == 0 && row == 1){
//                    chessPieces[row][col].setType(sample.Main.PieceType.RED_KING);
//                }
//            }
//        }
//        flipGame.setChessBoard(chessPieces);


//        try {
//
//            FileInputStream fileIn = new FileInputStream(new File("E:" + File.separator + "FlipTest.txt"));
//            ObjectInputStream in = new ObjectInputStream(fileIn);
//            flipGame = (FlipGame) in.readObject();
//            in.close();
//            fileIn.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        } catch (ClassNotFoundException e) {
//            e.printStackTrace();
//        }
//        flipGame.display();
//        CopyOnWriteArrayList<Node> allMoves = flipGame.getAllMoves();
//        for(Node n:allMoves){
//            System.out.println(n.getMove().toString());
//        }

//        MonteCarloTreeSearch search = new MonteCarloTreeSearch();
//        Node t = search.findBestMove(flipGame);
//
//        if (t != null) {
//            System.out.println(t.getMove());
//        } else {
//            System.out.println("没有找到有效的移动步骤");
//        }
//       Scanner in = new Scanner(System.in);
//       while (true) {
//
//           Node root = search.findBestMove(flipGame);
//
//           if (root == null || root.x1 < 0 || root.y1 < 0 || root.x2 < 0 || root.y2 < 0) {
//               System.out.println("AI无法找到有效移动，游戏结束！");
//               break;
//           }
//           System.out.println("-------------棋盘信息--------------");
//           flipGame.apply(root.x1, root.y1, root.x2, root.y2);
//
//           flipGame.display();
//           System.out.println("机器已走");
//           if (flipGame.getPlayerWin1() || flipGame.getPlayerWin2() || flipGame.isDraw()) {
//               break;
//           }
//
//           int[] pos = new int[4];
//           System.out.print("\n请玩家输入行1：");
//           pos[0] = in.nextInt() - 1;
//           System.out.print("输入列1：");
//           pos[1] = in.nextInt() - 1;
//           System.out.print("\n输入行2：");
//           pos[2] = in.nextInt() - 1;
//           System.out.print("输入列2：");
//           pos[3] = in.nextInt() - 1;
//           flipGame.apply(pos[0], pos[1], pos[2], pos[3]);
//           System.out.println("-------------棋盘信息--------------");
//           flipGame.display();
//           if (flipGame.getPlayerWin1() || flipGame.getPlayerWin2() || flipGame.isDraw()) {
//               break;
//           }
//       }
//
//       if (flipGame.isGameOver()) {
//           if (flipGame.getPlayerWin1()) {
//               System.out.println("\n机器人胜利！！！");
//           } else if(flipGame.getPlayerWin2()) {
//               System.out.println("\n玩家胜利！！！");
//           }else{
//               System.out.println("\n平局");
//           }
//       }
//       in.close();
//    }
}
