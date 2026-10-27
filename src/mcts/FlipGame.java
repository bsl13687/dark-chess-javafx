package mcts;

import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import sample.Main;
import sample.Position;

import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;

public class FlipGame  {
    private static final long serialVersionUID = 136871L;
    private static final int CHESS_WIDTH = 4;
    private static final int CHESS_HEIGHT = 8;
    private static final int CHESS_MAX_DRAW_STEP = 20;
    private int topPlayerScore = 60;
    private int bottomPlayerScore = 60;
    private ChessPiece[][] board = new ChessPiece[8][4];
    private boolean playerTurn = true;//是否为玩家回合
    private int currentPlayer = 1;//0玩家执黑 AI先行 1玩家执红 玩家先行
    private boolean isPlayerWin1 = false;//玩家1是否获胜
    private boolean isPlayerWin2 = false;//玩家2是否获胜
    private boolean isDraw = false;//是否平局
    private int peace_move_step = 0;//无击杀移动步数 超过20步就和棋
    private List<Main.PieceType> pieces = new CopyOnWriteArrayList<>();
    private List<Position> selectedAttackList = new CopyOnWriteArrayList<>();
    private List<Position> selectedMoveList = new CopyOnWriteArrayList<>();
    private int pieces_point = 0;
    private int selectedRow = -1;
    private int selectedCol = -1;
    private boolean isFirstMove = false;
    public void setChessBoard(ChessPiece[][] other){
        for (int row = 0; row < CHESS_HEIGHT; row++) {
            for (int col = 0; col < CHESS_WIDTH; col++) {
                this.board[row][col] = new ChessPiece(other[row][col].type,other[row][col].flipped,other[row][col].selected);
            }
        }
    }
    //深度拷贝数据
    public FlipGame(FlipGame other){
        this.topPlayerScore = other.topPlayerScore;
        this.bottomPlayerScore = other.bottomPlayerScore;
        for (int row = 0; row < CHESS_HEIGHT; row++) {
            for (int col = 0; col < CHESS_WIDTH; col++) {
                this.board[row][col] = new ChessPiece(other.board[row][col].type,other.board[row][col].flipped,other.board[row][col].selected);
            }
        }

        this.playerTurn = other.playerTurn;
        this.currentPlayer = other.currentPlayer;
        this.isPlayerWin1 = other.isPlayerWin1;
        this.isPlayerWin2 = other.isPlayerWin2;

        this.isDraw = other.isDraw;
        this.peace_move_step = other.peace_move_step;
        for(int i = 0;i< other.pieces.size();i++){
            this.pieces.add(other.pieces.get(i));
        }
        this.pieces_point = other.pieces_point;
        for(int i = 0;i< other.selectedAttackList.size();i++){
            this.selectedAttackList.add(other.selectedAttackList.get(i));
        }
        for(int i = 0;i< other.selectedMoveList.size();i++){
            this.selectedMoveList.add(other.selectedMoveList.get(i));
        }
        this.isFirstMove = other.isFirstMove;

    }
    public FlipGame(){

    }



    public boolean getPlayerWin1() {
        return this.isPlayerWin1;
    }
    public boolean getPlayerWin2() {
        return this.isPlayerWin2;
    }

    public boolean isDraw() {
        return isDraw;
    }

    private Main.PieceType convertPieceType(String piece){
        if("黑卒".equals(piece)){
            return Main.PieceType.BLACK_SOLDIER;
        }
        if("红兵".equals(piece)){
            return Main.PieceType.RED_SOLDIER;
        }
        if("黑炮".equals(piece)){
            return Main.PieceType.BLACK_CANNONS;
        }
        if("红炮".equals(piece)){
            return Main.PieceType.RED_CANNONS;
        }
        if("黑车".equals(piece)){
            return Main.PieceType.BLACK_ROOK;
        }
        if("红车".equals(piece)){
            return Main.PieceType.RED_ROOK;
        }
        if("黑马".equals(piece)){
            return Main.PieceType.BLACK_KNIGHT;
        }
        if("红马".equals(piece)){
            return Main.PieceType.RED_KNIGHT;
        }
        if("黑象".equals(piece)){
            return Main.PieceType.BLACK_ELEPHANT;
        }
        if("红象".equals(piece)){
            return Main.PieceType.RED_ELEPHANT;
        }
        if("黑士".equals(piece)){
            return Main.PieceType.BLACK_MANDARINS;
        }
        if("红士".equals(piece)){
            return Main.PieceType.RED_MANDARINS;
        }
        if("黑将".equals(piece)){
            return Main.PieceType.BLACK_KING;
        }
        if("红帅".equals(piece)){
            return Main.PieceType.RED_KING;
        }
        return Main.PieceType.UNFLIPPED;
    }
    private String convertPieceTypeToStr(Main.PieceType piece){
        if( Main.PieceType.BLACK_SOLDIER == piece){
            return "黑卒";
        }
        if(piece == Main.PieceType.RED_SOLDIER){
            return "红兵";
        }
        if(piece == Main.PieceType.BLACK_CANNONS){
            return "黑炮";
        }
        if(piece == Main.PieceType.RED_CANNONS){
            return "红炮";
        }
        if(piece == Main.PieceType.BLACK_ROOK){
            return "黑车";
        }
        if(piece == Main.PieceType.RED_ROOK){
            return "红车";
        }
        if(piece == Main.PieceType.BLACK_KNIGHT){
            return "黑马";
        }
        if(piece == Main.PieceType.RED_KNIGHT){
            return "红马";
        }
        if(piece == Main.PieceType.BLACK_ELEPHANT){
            return "黑象";
        }
        if(piece == Main.PieceType.RED_ELEPHANT){
            return "红象";
        }
        if(piece == Main.PieceType.BLACK_MANDARINS){
            return "黑士";
        }
        if(piece == Main.PieceType.RED_MANDARINS){
            return "红士";
        }
        if(piece == Main.PieceType.BLACK_KING){
            return "黑将";
        }
        if(piece == Main.PieceType.RED_KING){
            return "红帅";
        }
        if(piece == Main.PieceType.EMPTY){
            return " ";
        }
        return "未知";
    }
    
    //初始化棋盘
    void initializeBoard(boolean isFirstMove, Main.PieceType type0, Main.PieceType type1, Main.PieceType type2, Main.PieceType type3) {
        this.playerTurn = isFirstMove;
        this.isFirstMove = isFirstMove;
        if(this.playerTurn){
            currentPlayer = 1;
        }else{
            currentPlayer = 0;
        }
        pieces.clear();
        pieces.add(Main.PieceType.RED_KING);
        pieces.add(Main.PieceType.BLACK_KING);

        for(int i = 0;i<5;i++){
            //为双方添加兵和卒
            pieces.add(Main.PieceType.RED_SOLDIER);
            pieces.add(Main.PieceType.BLACK_SOLDIER);
        }
        for(int i=0;i<2;i++){
            //炮
            pieces.add(Main.PieceType.RED_CANNONS);
            pieces.add(Main.PieceType.BLACK_CANNONS);
            //车
            pieces.add(Main.PieceType.RED_ROOK);
            pieces.add(Main.PieceType.BLACK_ROOK);
            //马
            pieces.add(Main.PieceType.RED_KNIGHT);
            pieces.add(Main.PieceType.BLACK_KNIGHT);

            //象
            pieces.add(Main.PieceType.RED_ELEPHANT);
            pieces.add(Main.PieceType.BLACK_ELEPHANT);
            //士
            pieces.add(Main.PieceType.RED_MANDARINS);
            pieces.add(Main.PieceType.BLACK_MANDARINS);
        }
        Collections.shuffle(pieces);
        boolean flag0 = true;
        boolean flag1 = true;
        boolean flag2 = true;
        boolean flag3 = true;
        //将type0~3移动到最后四位
        for(int i = 0;i<pieces.size();i++){
            if(pieces.get(i) == type0){
                if(i != 28 && flag0){
                    //交换i 和 28
                    Main.PieceType t = pieces.get(28);
                    pieces.set(28,type0);
                    pieces.set(i,t);
                    flag0 = false;
                }
            }
            if(pieces.get(i) == type1){
                if(i != 29 && flag1){
                    //交换i 和 29
                    Main.PieceType t = pieces.get(29);
                    pieces.set(29,type1);
                    pieces.set(i,t);
                    flag1 = false;
                }

            }
            if(pieces.get(i) == type2){
                if(i != 30 && flag2){
                    //交换i 和 30
                    Main.PieceType t = pieces.get(30);
                    pieces.set(30,type2);
                    pieces.set(i,t);
                    flag2 = false;
                }

            }
            if(pieces.get(i) == type3){
                if(i != 31 && flag3){
                    //交换i 和 30
                    Main.PieceType t = pieces.get(31);
                    pieces.set(31,type3);
                    pieces.set(i,t);
                    flag3 = false;
                }
            }
        }
        pieces_point = pieces.size();
//        System.out.println("pieces_point0:"+pieces_point);
        int index = 0;
        for (int row = 0; row < CHESS_HEIGHT; row++) {
            for (int col = 0; col < CHESS_WIDTH; col++) {
                if(index == 5 || index == 12 || index == 19 || index == 26){
                    pieces_point --;
                    board[row][col] =new ChessPiece(pieces.get(pieces_point));
                    board[row][col].setFlipped(true);
                }else{
                    board[row][col] = new ChessPiece(Main.PieceType.UNFLIPPED);
                }

                index++;
            }
        }
    }

    //获取选中棋子的坐标
    private Position getSelectedPosition(){
        boolean flag = false;
        Position res = new Position(-1,-1);
        for (int row = 0; row < CHESS_HEIGHT; row++) {
            for (int col = 0; col < CHESS_WIDTH; col++) {
                if(board[row][col].isSelected()){
                    board[row][col].setSelected(false);
                    flag = true;
                    res.col = col;
                    res.row = row;
                    break;
                }
            }
            if(flag){
                break;
            }
        }
        return res;
    }

    //炮攻击逻辑
    private void attackCannon(int rowIndex,int colIndex,int selectedRowIndex,int selectedColIndex){
        Main.PieceType type = board[selectedRowIndex][selectedColIndex].getType();//炮
        Main.PieceType type2 = board[rowIndex][colIndex].getType();//其它类型
//        if(type == PieceType.EMPTY){
//            return;
//        }
        //判断一下是否为炮攻击范围
        boolean flag = false;
        for(int k = 0;k<selectedAttackList.size();k++){
            if(selectedAttackList.get(k).row == rowIndex && selectedAttackList.get(k).col == colIndex){
                flag = true;
                break;
            }
        }
        if(!flag){
            //不在攻击范围
//            System.out.println("不在攻击范围");
            return;
        }
        boolean flipped = false;
        if(!board[rowIndex][colIndex].flipped){

            //没翻开，需要弹框让用户选择
            CopyOnWriteArrayList<Main.PieceType> pieceTypes = new CopyOnWriteArrayList<>();
            for(int i = 0 ;i<pieces_point;i++){
                pieceTypes.add(pieces.get(i));
            }
            int randomIndex = (int) (Math.random() * pieceTypes.size());
            type2 = pieceTypes.get(randomIndex);


            if(type2 == Main.PieceType.EMPTY){
                //未选择任何数据
//                System.out.println("未选择棋子类型");
                return;
            }

            //交换
            for(int k = 0;k<pieces_point;k++){
                if(pieces.get(k) == type2){
                    pieces.set(k,pieces.get(pieces_point));
                    pieces.set(pieces_point,type2);
                    break;
                }
            }
        }else{
            flipped = true;
        }
        for(int i = 0;i<selectedAttackList.size();i++){
            Position attackPosition = selectedAttackList.get(i);

            if(rowIndex == attackPosition.row && colIndex == attackPosition.col){
                //攻击完成，取消选中
                board[selectedRowIndex][selectedColIndex].setType(Main.PieceType.EMPTY);
                board[selectedRowIndex][selectedColIndex].flipped = true;
                board[rowIndex][colIndex].setType(type);
                board[rowIndex][colIndex].flipped = true;
                if(!flipped){
                    type = pieces.get(pieces_point);
                    pieces_point --;
                }else{

                }

                int score = calcPieceTypeScore(type2);


                String pt = convertPieceTypeToStr(type2);

                //计算分数，并减去对应的分数
                if(currentPlayer == 1){
                    if(pt.indexOf("红") >= 0){
                        bottomPlayerScore = bottomPlayerScore - score;
//                        System.out.println("击杀:"+pt+"*1,得分:-"+score);
                        if(bottomPlayerScore <= 0){
                            isPlayerWin1 = true;
                        }

                    }else{
                        topPlayerScore = topPlayerScore - score;
//                        System.out.println("击杀:"+pt+"*1,得分:+"+score);
                        if(topPlayerScore <= 0){
                            this.isPlayerWin2 = true;
                        }
                    }



                }else {
                    if(pt.indexOf("黑") >= 0){
                        topPlayerScore = topPlayerScore - score;
//                        System.out.println("击杀:"+pt+"*1,得分:-"+score);

                        if(topPlayerScore <= 0){
                            this.isPlayerWin2 = true;
                        }
                    }else{
                        bottomPlayerScore = bottomPlayerScore - score;
                        if(bottomPlayerScore <= 0){
                            this.isPlayerWin1 = true;
                        }
                    }

                }
                peace_move_step = 0;
                playerTurn = !playerTurn;
                if(this.playerTurn){
                    currentPlayer = 1;
                }else{
                    currentPlayer = 0;
                }
            }

        }
    }

    //击杀棋子
    private void attack(int rowIndex,int colIndex,int selectedRowIndex,int selectedColIndex){
        //不能自己攻击自己
        if(rowIndex == selectedRowIndex && colIndex == selectedColIndex){
            return;
        }
        if(selectedColIndex < 0 || selectedRowIndex < 0){
            return;
        }
        Main.PieceType type = board[selectedRowIndex][selectedColIndex].getType();
        if(type == Main.PieceType.EMPTY){
            return;
        }
        if(type == Main.PieceType.BLACK_CANNONS || type == Main.PieceType.RED_CANNONS){
            attackCannon(rowIndex,colIndex,selectedRowIndex,selectedColIndex);
        }else{
            attackOther(rowIndex,colIndex,selectedRowIndex,selectedColIndex);

        }


    }

    private void attackOther(int rowIndex, int colIndex, int selectedRowIndex, int selectedColIndex) {
        Main.PieceType type = board[selectedRowIndex][selectedColIndex].getType();
        if(type == Main.PieceType.EMPTY){
            return;
        }
        if(!board[rowIndex][colIndex].flipped){
            unselectPiece();
        }else{
            for(int i = 0;i<selectedAttackList.size();i++){
                Position attackPosition = selectedAttackList.get(i);

                if(rowIndex == attackPosition.row && colIndex == attackPosition.col){
                    //攻击完成，取消选中
                    Main.PieceType temp =  board[selectedRowIndex][selectedColIndex].getType();
                    type = board[rowIndex][colIndex].getType();
                    board[selectedRowIndex][selectedColIndex].setType(Main.PieceType.EMPTY);
                    board[selectedRowIndex][selectedColIndex].flipped = true;
                    board[rowIndex][colIndex].setType(temp);

                    int score = calcPieceTypeScore(type);

                    String pt = convertPieceTypeToStr(type);

                    //计算分数，并减去对应的分数
                    if(currentPlayer == 1){
                        if(pt.indexOf("红") >= 0){
                            bottomPlayerScore = bottomPlayerScore - score;
//                            System.out.println("击杀:"+pt+"*1,得分:-"+score);

                        }else{
                            topPlayerScore = topPlayerScore - score;
//                            System.out.println("击杀:"+pt+"*1,得分:+"+score);
                        }



                    }else {
                        if(pt.indexOf("黑") >= 0){
                            topPlayerScore = topPlayerScore - score;
//                            System.out.println("击杀:"+pt+"*1,得分:-"+score);
                        }else{
                            bottomPlayerScore = bottomPlayerScore - score;
//                            System.out.println("击杀:"+pt+"*1,得分:+"+score);
                        }

                    }
                    peace_move_step = 0;
                    playerTurn = !playerTurn;
                    if(this.playerTurn){
                        currentPlayer = 1;
                    }else{
                        currentPlayer = 0;
                    }
                }

            }
        }
    }

    //根据棋子类型计算分值
    private int calcPieceTypeScore(Main.PieceType type){
        int score = 0;
        switch (type) {
            case BLACK_KING ://将
            case RED_KING: //帅
                score = 30;
                break;
            case BLACK_MANDARINS://士
            case RED_MANDARINS://士
                score = 10;
                break;
            case BLACK_ELEPHANT://象
            case RED_ELEPHANT://象
            case BLACK_KNIGHT://马
            case BLACK_ROOK://车
            case BLACK_CANNONS://炮
            case RED_KNIGHT://马
            case RED_ROOK://车
            case RED_CANNONS://炮
                score = 5;
                break;
            case BLACK_SOLDIER://卒
            case RED_SOLDIER://兵
                score = 1;
                break;

            default :
                break;
        }
        return score;
    }

    //空间换时间，先把可攻击范围计算出来，攻击时直接判断是否存在在列表中
    private void calcSelectedAttackList(int row,int col){
        selectedAttackList.clear();
        //设置一个游标
        Position cursor = new Position(row,col);
        boolean compareResult = false;

        if(board[row][col].getType() == Main.PieceType.BLACK_CANNONS || board[row][col].getType() == Main.PieceType.RED_CANNONS){
            //游标向上下左右移动
            //left
            int pivotCount = 0;
            if(cursor.col - 2 >= 0){

                for(int k = cursor.col-1;k>=0;k--){
                    //board[cursor.row][k]
                    if(board[cursor.row][k].getType() != Main.PieceType.EMPTY){
                        pivotCount ++;
                        if(pivotCount == 2){
                            //如果翻开了，判断一下是否为我方棋子，不是才加入
                            if(board[cursor.row][k].flipped){
                                String pieceTypeStr = convertPieceTypeToStr(board[cursor.row][k].getType());
                                if(currentPlayer == 1 && pieceTypeStr.indexOf("黑") >= 0){
                                    //玩家执红
                                    selectedAttackList.add(new Position(cursor.row, k));
                                }
                                if(currentPlayer == 0 && pieceTypeStr.indexOf("红") >= 0){
                                    //玩家执黑
                                    selectedAttackList.add(new Position(cursor.row, k));
                                }

                            }else{
                                selectedAttackList.add(new Position(cursor.row, k));
                            }
                            break;
                        }
                    }

                }
            }
            pivotCount = 0;
            //right
            if(cursor.col + 2 < CHESS_WIDTH){
                for(int k = cursor.col+1;k<CHESS_WIDTH;k++){
                    if(board[cursor.row][k].getType() != Main.PieceType.EMPTY){
                        pivotCount ++;
                        if(pivotCount == 2){

                            if(board[cursor.row][k].flipped){
                                String pieceTypeStr = convertPieceTypeToStr(board[cursor.row][k].getType());
                                if(currentPlayer == 1 && pieceTypeStr.indexOf("黑") >= 0){
                                    //玩家执红
                                    selectedAttackList.add(new Position(cursor.row, k));
                                }
                                if(currentPlayer == 0 && pieceTypeStr.indexOf("红") >= 0){
                                    //玩家执黑
                                    selectedAttackList.add(new Position(cursor.row, k));
                                }

                            }else{
                                selectedAttackList.add(new Position(cursor.row, k));
                            }
                            break;
                        }
                    }

                }
            }
            pivotCount = 0;
            //up
            if(cursor.row - 2 >= 0){
                for(int k = cursor.row-1;k>=0;k--){
                    //board[cursor.row][k]
                    if(board[k][cursor.col].getType() != Main.PieceType.EMPTY){
                        pivotCount ++;
                        if(pivotCount == 2){
                            if(board[k][cursor.col].flipped){
                                String pieceTypeStr = convertPieceTypeToStr(board[k][cursor.col].getType());
                                if(currentPlayer == 1 && pieceTypeStr.indexOf("黑") >= 0){
                                    //玩家执红
                                    selectedAttackList.add(new Position(k, cursor.col));
                                }
                                if(currentPlayer == 0 && pieceTypeStr.indexOf("红") >= 0){
                                    //玩家执黑
                                    selectedAttackList.add(new Position(k, cursor.col));
                                }

                            }else{
                                selectedAttackList.add(new Position(k, cursor.col));
                            }
                            break;
                        }
                    }

                }
            }
            pivotCount = 0;
            //down
            if(cursor.row + 2 < CHESS_HEIGHT){
                for(int k = cursor.row+1;k<CHESS_HEIGHT;k++){
                    if(board[k][cursor.col].getType() != Main.PieceType.EMPTY){
                        pivotCount ++;
                        if(pivotCount == 2){
                            if(board[k][cursor.col].flipped){
                                String pieceTypeStr = convertPieceTypeToStr(board[k][cursor.col].getType());
                                if(currentPlayer == 1 && pieceTypeStr.indexOf("黑") >= 0){
                                    //玩家执红
                                    selectedAttackList.add(new Position(k, cursor.col));
                                }
                                if(currentPlayer == 0 && pieceTypeStr.indexOf("红") >= 0){
                                    //玩家执黑
                                    selectedAttackList.add(new Position(k, cursor.col));
                                }

                            }else{
                                selectedAttackList.add(new Position(k, cursor.col));
                            }
                            break;
                        }
                    }

                }
            }

        }else{
            //
            selectedMoveList.clear();
            //left
            if(cursor.row - 1 >= 0 && board[cursor.row-1][cursor.col].flipped){
                compareResult = comparePieceType(board[cursor.row][cursor.col].getType(),board[cursor.row-1][cursor.col].getType());
                if(compareResult){//可以吃子，加入待攻击列表

                    selectedAttackList.add(new Position(cursor.row-1, cursor.col));
                }
            }
            if(cursor.row - 1 >= 0 && board[cursor.row-1][cursor.col].getType() == Main.PieceType.EMPTY){
                selectedMoveList.add(new Position(cursor.row-1, cursor.col));
            }
            //right
            compareResult = false;
            if(cursor.row + 1 < CHESS_HEIGHT && board[cursor.row+1][cursor.col].flipped){
                compareResult = comparePieceType(board[cursor.row][cursor.col].getType(),board[cursor.row+1][cursor.col].getType());
                if(compareResult){//可以吃子，加入待攻击列表
                    selectedAttackList.add(new Position(cursor.row+1, cursor.col));
                }
            }
            if(cursor.row + 1 < CHESS_HEIGHT && board[cursor.row+1][cursor.col].getType() == Main.PieceType.EMPTY){
                selectedMoveList.add(new Position(cursor.row+1, cursor.col));
            }
            //down
            if(cursor.col - 1 >= 0 && board[cursor.row][cursor.col-1].flipped){
                compareResult = comparePieceType(board[cursor.row][cursor.col].getType(),board[cursor.row][cursor.col-1].getType());

                if(compareResult){//可以吃子，加入待攻击列表
                    selectedAttackList.add(new Position(cursor.row, cursor.col-1));
                }
            }
            if( cursor.col - 1 >= 0 && board[cursor.row][cursor.col-1].getType() == Main.PieceType.EMPTY){
                selectedMoveList.add(new Position(cursor.row, cursor.col-1));
            }
            //up
            compareResult = false;
            if(cursor.col + 1 < CHESS_WIDTH && board[cursor.row][cursor.col+1].flipped){
                compareResult = comparePieceType(board[cursor.row][cursor.col].getType(),board[cursor.row][cursor.col+1].getType());
                if(compareResult){//可以吃子，加入待攻击列表
                    selectedAttackList.add(new Position(cursor.row, cursor.col+1));
                }
            }
            if(cursor.col + 1 < CHESS_WIDTH && board[cursor.row][cursor.col+1].getType() == Main.PieceType.EMPTY){
                selectedMoveList.add(new Position(cursor.row, cursor.col+1));
            }



        }
//        for(int i = 0;i<selectedAttackList.size();i++){
//            System.out.println(selectedAttackList.get(i).toString());
//        }


    }
    private void unselectPiece(){
        if(selectedRow >= 0 ){
            board[selectedRow][selectedCol].setSelected(false);
        }
        selectedCol = -1;
        selectedRow = -1;
        //给其它所有棋子设置非选中状态
//        for (int row = 0; row < CHESS_HEIGHT; row++) {
//            for (int col = 0; col < CHESS_WIDTH; col++) {
//                if(board[row][col].getType() != Main.PieceType.EMPTY){
//                    board[row][col].setSelected(false);
//                }
//
//            }
//        }

        selectedAttackList.clear();
        selectedMoveList.clear();
    }

    private void flipPiece(int row, int col, Main.PieceType type) {
        ChessPiece piece = board[row][col];
        piece.type = type;
        if (!piece.isFlipped()) {
            piece.setFlipped(true);
        }
    }


    private boolean compareBlackKing(Main.PieceType type){
        switch (type) {
            case RED_KING: //帅
                return true;
            case RED_MANDARINS://士
                return true;
            case RED_ELEPHANT://象
                return true;
            case RED_KNIGHT://马
                return true;
            case RED_ROOK://车
                return true;
            case RED_CANNONS://炮
                return true;
            case RED_SOLDIER://兵
                return false;
            default :
                return false;
        }
    }
    private boolean compareRedMandarins(Main.PieceType type){
        switch (type) {
            case BLACK_MANDARINS://士
                return true;
            case BLACK_ELEPHANT://象
                return true;
            case BLACK_KNIGHT://马
                return true;
            case BLACK_ROOK://车
                return true;
            case BLACK_CANNONS://炮
                return true;
            case BLACK_SOLDIER://兵
                return true;
            default :
                return false;
        }
    }


    private boolean compareBlackMandarins(Main.PieceType type){
        switch (type) {
            case RED_MANDARINS://士
                return true;
            case RED_ELEPHANT://象
                return true;
            case RED_KNIGHT://马
                return true;
            case RED_ROOK://车
                return true;
            case RED_CANNONS://炮
                return true;
            case RED_SOLDIER://兵
                return true;
            default :
                return false;
        }
    }


    private boolean compareRedElephant(Main.PieceType type){
        switch (type) {
            case BLACK_ELEPHANT://象
                return true;
            case BLACK_KNIGHT://马
                return true;
            case BLACK_ROOK://车
                return true;
            case BLACK_CANNONS://炮
                return true;
            case BLACK_SOLDIER://兵
                return true;
            default :
                return false;
        }
    }


    private boolean compareBlackElephant(Main.PieceType type){
        switch (type) {
            case RED_ELEPHANT://象
                return true;
            case RED_KNIGHT://马
                return true;
            case RED_ROOK://车
                return true;
            case RED_CANNONS://炮
                return true;
            case RED_SOLDIER://兵
                return true;
            default :
                return false;
        }
    }

    private boolean compareRedKnight(Main.PieceType type){
        switch (type) {
            case BLACK_KNIGHT://马
                return true;
            case BLACK_ROOK://车
                return true;
            case BLACK_CANNONS://炮
                return true;
            case BLACK_SOLDIER://兵
                return true;
            default :
                return false;
        }
    }


    private boolean compareBlackKnight(Main.PieceType type){
        switch (type) {
            case RED_KNIGHT://马
                return true;
            case RED_ROOK://车
                return true;
            case RED_CANNONS://炮
                return true;
            case RED_SOLDIER://兵
                return true;
            default :
                return false;
        }
    }

    private boolean compareRedRook(Main.PieceType type){
        switch (type) {
            case BLACK_ROOK://车
                return true;
            case BLACK_CANNONS://炮
                return true;
            case BLACK_SOLDIER://兵
                return true;
            default :
                return false;
        }
    }


    private boolean compareBlackRook(Main.PieceType type){
        switch (type) {
            case RED_ROOK://车
                return true;
            case RED_CANNONS://炮
                return true;
            case RED_SOLDIER://兵
                return true;
            default :
                return false;
        }
    }

    private boolean compareRedCannons(Main.PieceType type){
        return true;
    }


    private boolean compareBlackCannons(Main.PieceType type){
        return true;
    }

    private boolean compareRedSoldier(Main.PieceType type){
        switch (type) {
            case BLACK_KING://将
                return true;
            case BLACK_SOLDIER://兵
                return true;
            default :
                return false;
        }
    }


    private boolean compareBlackSoldier(Main.PieceType type){
        switch (type) {
            case RED_KING://帅
                return true;
            case RED_SOLDIER://兵
                return true;
            default :
                return false;
        }
    }
    private boolean compareRedKing(Main.PieceType type){
        switch (type) {
            case BLACK_KING: //帅
                return true;
            case BLACK_MANDARINS://士
                return true;
            case BLACK_ELEPHANT://象
                return true;
            case BLACK_KNIGHT://马
                return true;
            case BLACK_ROOK://车
                return true;
            case BLACK_CANNONS://炮
                return true;
            case BLACK_SOLDIER://兵
                return false;
            default :
                return false;
        }
    }
    // 比较两个棋子大小
    private boolean comparePieceType(Main.PieceType type1, Main.PieceType type2){


        //分类讨论
        switch (type1) {
            case BLACK_KING ://将
                return this.compareBlackKing(type2);
            case BLACK_MANDARINS://士
                return this.compareBlackMandarins(type2);
            case BLACK_ELEPHANT://象
                return this.compareBlackElephant(type2);
            case BLACK_KNIGHT://马
                return this.compareBlackKnight(type2);
            case BLACK_ROOK://车
                return this.compareBlackRook(type2);
            case BLACK_CANNONS://炮
                return this.compareBlackCannons(type2);
            case BLACK_SOLDIER://卒
                return this.compareBlackSoldier(type2);
            case RED_KING: //帅
                return this.compareRedKing(type2);
            case RED_MANDARINS://士
                return this.compareRedMandarins(type2);
            case RED_ELEPHANT://象
                return this.compareRedElephant(type2);
            case RED_KNIGHT://马
                return this.compareRedKnight(type2);
            case RED_ROOK://车
                return this.compareRedRook(type2);
            case RED_CANNONS://炮
                return this.compareRedCannons(type2);
            case RED_SOLDIER://兵
                return this.compareRedSoldier(type2);
            case UNFLIPPED :
                return false;
            default :
                break;
        }
        return false;
    }
    public void apply(int selectedRowIndex, int selectedColIndex, int rowIndex, int colIndex){

        if (colIndex != -1 && rowIndex != -1 ) {
//            System.out.println("{"+rowIndex+","+colIndex+"}");

            if(board[rowIndex][colIndex].getType() == Main.PieceType.EMPTY){

                if(selectedColIndex >= 0 && board[selectedRowIndex][selectedColIndex].getType() != Main.PieceType.BLACK_CANNONS && board[selectedRowIndex][selectedColIndex].getType() != Main.PieceType.RED_CANNONS){
                    //判断是否可以移动
                    if(Math.abs(selectedRowIndex - rowIndex) + Math.abs(selectedColIndex - colIndex) == 1){
                        board[rowIndex][colIndex].setType(board[selectedRowIndex][selectedColIndex].getType());
                        board[rowIndex][colIndex].flipped = true;
                        board[selectedRowIndex][selectedColIndex].setType(Main.PieceType.EMPTY);
                        board[selectedRowIndex][selectedColIndex].flipped = true;
                        peace_move_step ++;
                        if(peace_move_step > CHESS_MAX_DRAW_STEP){
                            isDraw = true;
                            isPlayerWin1 = false;
                            isPlayerWin2 = false;

                        }
//                        if(peace_move_step >= 14){
//                            System.out.println("还有"+(CHESS_MAX_DRAW_STEP-peace_move_step)+"步和棋");
//                        }
                        playerTurn = !playerTurn;
                        if(this.playerTurn){
                            currentPlayer = 1;
                        }else{
                            currentPlayer = 0;
                        }
                        return ;
                    }

                }else{
//                    System.out.println("空子");
                }
                return;
            }

            //判断是否在可打击范围之内
            if(!board[rowIndex][colIndex].isFlipped()){
                //判断是否存在已经选中棋子



                if(selectedRowIndex < 0){


                    CopyOnWriteArrayList<Main.PieceType> pieceTypes = new CopyOnWriteArrayList<>();
                    for(int i = 0 ;i<pieces_point;i++){
                        pieceTypes.add(pieces.get(i));
                    }
//                    Main.PieceType t = chooseDialog(pieceTypes,"请选择翻开棋子类型:");
                    int randomIndex = (int) (Math.random() * pieceTypes.size());
                    Main.PieceType t = pieceTypes.get(randomIndex);
                    if(t == Main.PieceType.EMPTY){
                        //未选择任何数据
//                        System.out.println("未选择棋子类型");
                        return;
                    }
                    //交换
                    for(int k = 0;k<pieces_point;k++){
                        if(pieces.get(k) == t){
                            pieces.set(k,pieces.get(pieces_point));
                            pieces.set(pieces_point,t);
                            break;
                        }
                    }
                    flipPiece(rowIndex,colIndex,pieces.get(pieces_point));
                    pieces_point --;
                    //翻开棋子
                    peace_move_step = 0;
                    playerTurn = !playerTurn;
                    if(this.playerTurn){
                        currentPlayer = 1;
                    }else{
                        currentPlayer = 0;
                    }
                }else{

                    attack(rowIndex,colIndex,selectedRowIndex,selectedColIndex);

                }

            }else{
                //


                //已经翻开，判断是否选中
                if(board[rowIndex][colIndex].isSelected()){
                    //取消选中
                    board[rowIndex][colIndex].setSelected(false);

//                    System.out.println("取消选中");

                }else{

                    //先检查选中的棋子是否为我方棋子
                    String pieceTypeStr = convertPieceTypeToStr(board[rowIndex][colIndex].type);
                    if(currentPlayer == 1){
                        //玩家执红
                        if(pieceTypeStr.indexOf("黑") >= 0 ){
                            if(selectedRowIndex >= 0){
                                attack(rowIndex,colIndex,selectedRowIndex,selectedColIndex);
                            }else{
//                                System.out.println("不能选中别人的棋子");
                            }

                            return;
                        }
                    }else{
                        //玩家执黑玩家
                        if(pieceTypeStr.indexOf("红") >= 0){
//                            System.out.println("不能选中别人的棋子");
                            if(selectedRowIndex >= 0){
                                attack(rowIndex,colIndex,selectedRowIndex,selectedColIndex);

                            }else{
//                                System.out.println("不能选中别人的棋子");
                            }

                            return;
                        }
                    }
                    unselectPiece();
                    board[rowIndex][colIndex].setSelected(true);
                    selectedRow = rowIndex;
                    selectedCol = colIndex;
                    //将选中棋子的可攻击对象放入列表中
                    calcSelectedAttackList(rowIndex,colIndex);
//                    System.out.println("选中");

                }
            }
        }else{
//            System.out.println("点击空处");
        }
    }

    //获取所有可能，然后随机选一个
    public Node randomSelect(){
        if(peace_move_step >= 20){
            isDraw = true;
        }else{
            if(topPlayerScore <= 0){
                //人输了
                isPlayerWin2 = true;
            }
            if(bottomPlayerScore <= 0){
                //机器输了
                isPlayerWin1 = true;
            }
        }

        if(isPlayerWin1 || isPlayerWin2 || isDraw){
            return null;
        }
        CopyOnWriteArrayList<Node> results = new CopyOnWriteArrayList<>();
//        FlipGame cloneFlipGame = new FlipGame(this);
        //加入所有未翻棋子
        for (int row = 0; row < CHESS_HEIGHT; row++) {
            for (int col = 0; col < CHESS_WIDTH; col++) {
                if(!this.board[row][col].isFlipped()){
                    results.add(new Node(new Node.Move(-1,-1,row,col),this.deepClone()));
                }else{
                    this.unselectPiece();
                    this.apply(-1,-1,row,col);
                    for(int i = 0;i<this.selectedAttackList.size();i++){
                        results.add(new Node(new Node.Move(row,col,this.selectedAttackList.get(i).row,this.selectedAttackList.get(i).col),this.deepClone()));
                    }
                    for(int i = 0;i<this.selectedMoveList.size();i++){
                        results.add(new Node(new Node.Move(row,col,this.selectedMoveList.get(i).row,this.selectedMoveList.get(i).col),this.deepClone()));
                    }

                }
            }
        }
        if(results.size() == 0){
            return null;
        }
        int randomIndex = (int) (Math.random() * results.size());
        return results.get(randomIndex);
    }

    public void initGame(Main other) {
        this.topPlayerScore = other.topPlayerScore;
        this.bottomPlayerScore = other.bottomPlayerScore;
        for (int row = 0; row < CHESS_HEIGHT; row++) {
            for (int col = 0; col < CHESS_WIDTH; col++) {
                this.board[row][col] = new ChessPiece(other.board[row][col].getType(),other.board[row][col].isFlipped(),other.board[row][col].isSelected());
            }
        }

        this.playerTurn = other.playerTurn;
        this.currentPlayer = other.currentPlayer;
        this.isPlayerWin1 = other.isPlayerWin1;
        this.isPlayerWin2 = other.isPlayerWin2;

        this.isDraw = other.isDraw;
        this.peace_move_step = other.peace_move_step;
        for(int i = 0;i< other.pieces.size();i++){
            this.pieces.add(other.pieces.get(i));
        }
        this.pieces_point = other.pieces_point;
        for(int i = 0;i< other.selectedAttackList.size();i++){
            this.selectedAttackList.add(other.selectedAttackList.get(i));
        }
    }

    public void display(){
        System.out.println(topPlayerScore+"---------------"+bottomPlayerScore);
        for (int row = 0; row < CHESS_HEIGHT; row++) {
            for (int col = 0; col < CHESS_WIDTH; col++) {
                System.out.print(convertPieceTypeToStr(this.board[row][col].getType())+"("+this.board[row][col].type+")"+"\t");
            }
            System.out.println();
            System.out.println();
        }
    }

    public boolean isGameOver() {

        if(this.peace_move_step >= 20){
            isDraw = true;
        }else{
            if(topPlayerScore <= 0){
                //人输了
                isPlayerWin2 = true;
            }
            if(bottomPlayerScore <= 0){
                //机器输了
                isPlayerWin1 = true;
            }
        }
        if(!(isPlayerWin1 || isPlayerWin2 || isDraw)){
            if(this.getAllMoves().size() == 0){
                if(currentPlayer == 1){
                    isPlayerWin2 = true;
                }else{
                    isPlayerWin1 = true;
                }
            }
        }
//        System.out.println("isPlayerWin1:"+isPlayerWin1);
//        System.out.println("isPlayerWin2:"+isPlayerWin2);
//        System.out.println("isDraw:"+isDraw);
        return isPlayerWin1 || isPlayerWin2 || isDraw;
    }

    // MCTS需要的方法
    public FlipGame deepClone() {
        return new FlipGame(this);
    }

    public void applyMove(Node.Move move) {
        if (move.x1 == -1) { // 翻开棋子
            apply(-1, -1, move.x2, move.y2);

        } else { // 移动或攻击
            apply(-1,-1,move.x1, move.y1);
            apply(move.x1, move.y1, move.x2, move.y2);

        }
    }

    public int getWinner() {

        if (isPlayerWin1) return 2;//AI胜利得2分
        if (isPlayerWin2) return 0;//玩家胜利不加分
        if (isDraw) return 1;//平局+1分
        return -1;
    }

    public CopyOnWriteArrayList<Node> getAllMoves() {
        CopyOnWriteArrayList<Node> moves = new CopyOnWriteArrayList<>();
        // 添加翻开棋子的移动
        for (int row = 0; row < CHESS_HEIGHT; row++) {
            for (int col = 0; col < CHESS_WIDTH; col++) {
                if (!board[row][col].isFlipped()) {
                    FlipGame cloneFlipGame = this.deepClone();
                    cloneFlipGame.applyMove(new Node.Move(-1, -1, row, col));
                    moves.add(new Node(new Node.Move(-1, -1, row, col),cloneFlipGame));
                }
            }
        }
        // 添加攻击/移动的走法
        for (int row = 0; row < CHESS_HEIGHT; row++) {
            for (int col = 0; col < CHESS_WIDTH; col++) {
                if (board[row][col].isFlipped() &&
                    ((currentPlayer == 1 && convertPieceTypeToStr(board[row][col].getType()).contains("红")) ||
                     (currentPlayer == 0 && convertPieceTypeToStr(board[row][col].getType()).contains("黑")))) {
                    
                    // 模拟选中当前棋子
                    FlipGame tempGame = this.deepClone();
                    tempGame.apply(-1, -1, row, col); // 选中当前棋子
                    
                    // 获取可攻击和移动的位置
                    List<Position> attacks = new CopyOnWriteArrayList<>(tempGame.selectedAttackList);
                    List<Position> movesPos = new CopyOnWriteArrayList<>(tempGame.selectedMoveList);
                    
                    // 添加攻击步骤
                    for (Position pos : attacks) {
                        FlipGame cloneFlipGame = this.deepClone();
                        cloneFlipGame.applyMove(new Node.Move(row, col, pos.row, pos.col));
                        moves.add(new Node(new Node.Move(row, col, pos.row, pos.col), cloneFlipGame));
                    }
                    
                    // 添加移动步骤
                    for (Position pos : movesPos) {
                        FlipGame cloneFlipGame = this.deepClone();
                        cloneFlipGame.applyMove(new Node.Move(row, col, pos.row, pos.col));
                        moves.add(new Node(new Node.Move(row, col, pos.row, pos.col), cloneFlipGame));
                    }

                }
            }
        }
        return moves;
    }

    public CopyOnWriteArrayList<Node> getAllWeightMoves() {
        CopyOnWriteArrayList<Node> moves = new CopyOnWriteArrayList<>();

        // 添加翻开棋子的移动
        for (int row = 0; row < CHESS_HEIGHT; row++) {
            for (int col = 0; col < CHESS_WIDTH; col++) {
                if (!board[row][col].isFlipped()) {

                    FlipGame cloneFlipGame = this.deepClone();
                    cloneFlipGame.applyMove(new Node.Move(-1, -1, row, col));
                    moves.add(new Node(new Node.Move(-1, -1, row, col),cloneFlipGame));

                }
            }
        }
        // 添加攻击/移动的走法
        for (int row = 0; row < CHESS_HEIGHT; row++) {
            for (int col = 0; col < CHESS_WIDTH; col++) {
                if (board[row][col].isFlipped() &&
                        ((currentPlayer == 1 && convertPieceTypeToStr(board[row][col].getType()).contains("红")) ||
                                (currentPlayer == 0 && convertPieceTypeToStr(board[row][col].getType()).contains("黑")))) {

                    // 模拟选中当前棋子
                    FlipGame tempGame = this.deepClone();
                    tempGame.apply(-1, -1, row, col); // 选中当前棋子

                    // 获取可攻击和移动的位置
                    List<Position> attacks = new CopyOnWriteArrayList<>(tempGame.selectedAttackList);
                    List<Position> movesPos = new CopyOnWriteArrayList<>(tempGame.selectedMoveList);

                    // 添加攻击步骤
                    for (Position pos : attacks) {
                        FlipGame cloneFlipGame = this.deepClone();
                        cloneFlipGame.applyMove(new Node.Move(row, col, pos.row, pos.col));
                        //以血量差值作为权重
                        int score = this.bottomPlayerScore - cloneFlipGame.bottomPlayerScore;
                        if(score > 0){
                            moves.add(new Node(new Node.Move(row, col, pos.row, pos.col,score), cloneFlipGame));
                        }else{
                            moves.add(new Node(new Node.Move(row, col, pos.row, pos.col), cloneFlipGame));
                        }

                    }

                    // 添加移动步骤
                    for (Position pos : movesPos) {
                        FlipGame cloneFlipGame = this.deepClone();
                        cloneFlipGame.applyMove(new Node.Move(row, col, pos.row, pos.col));
                        moves.add(new Node(new Node.Move(row, col, pos.row, pos.col), cloneFlipGame));
                    }

                }
            }
        }
        return moves;
    }
}
