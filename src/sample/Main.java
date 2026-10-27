package sample;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Pair;
import mcts.FlipGame;
import mcts.MonteCarloTreeSearch;
import mcts.Node;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main extends Application {

    private static final int TILE_SIZE = 64;
    private static final int SPACING = 2;
    private static final String ASSETS_IMAGE_PATH = "assets/image/";
    private static final int CHESS_WIDTH = 4;
    private static final int CHESS_HEIGHT = 8;
    private static final int CHESS_MAX_DRAW_STEP = 20;
    public int topPlayerScore = 60;
    public int bottomPlayerScore = 60;
    public GridPane chessBoard;
    public ChessPiece[][] board = new ChessPiece[8][4];
    public boolean playerTurn = true;//是否为玩家回合
    public int currentPlayer = 1;//0玩家执黑 AI先行 1玩家执红 玩家先行
    public boolean isPlayerWin1 = false;//AI是否获胜
    public boolean isPlayerWin2 = false;//玩家是否获胜
    public boolean isDraw = false;//是否平局
    public int peace_move_step = 0;//无击杀移动步数 超过20步就和棋
    public boolean isFirstMove = false;

//    private ExecutorService executor = Executors.newSingleThreadExecutor();

    public List<PieceType> pieces = new CopyOnWriteArrayList<>();
    public List<Position> selectedAttackList = new CopyOnWriteArrayList<>();

    private GridPane backgroundPlayerPane;
    private Text bottomPlayerScoreText = new Text("Score:0");
    private Text topPlayerScoreText = new Text("Score:0");


    public int pieces_point = 0;
    // 棋子类型和图片映射
    public enum PieceType {
        BLACK_KING, BLACK_MANDARINS, BLACK_ELEPHANT, BLACK_KNIGHT, BLACK_ROOK, BLACK_CANNONS, BLACK_SOLDIER,
        RED_KING, RED_MANDARINS, RED_ELEPHANT, RED_KNIGHT, RED_ROOK, RED_CANNONS, RED_SOLDIER, UNFLIPPED,EMPTY
    }

    public static class ChessPiece {
        PieceType type;
        boolean flipped;
        boolean selected;
        ImageView imageView;


        ChessPiece(PieceType type) {
            this.type = type;
            this.flipped = false;
            if(type == PieceType.EMPTY){
                this.imageView = new ImageView();
            }else{
                this.imageView = new ImageView(getImage(type));
                DropShadow borderEffect = new DropShadow();
                imageView.setEffect(borderEffect);
            }


            imageView.setFitWidth(TILE_SIZE);
            imageView.setFitHeight(TILE_SIZE);
            this.selected = false;
        }

        public ChessPiece(PieceType type, boolean flipped, ImageView imageView) {
            this.type = type;
            this.flipped = flipped;
            this.imageView = null;
            this.selected = false;
            this.setType(type);
        }

        public void setType(PieceType type) {
            this.type = type;
            if(this.type == PieceType.EMPTY){
                this.selected = false;
                this.flipped = false;
                this.imageView.setVisible(false);
                imageView.setFitWidth(TILE_SIZE);
                imageView.setFitHeight(TILE_SIZE);
            }else{
                this.imageView.setVisible(true);
                this.imageView.setImage(getImage(this.type));

            }
        }

        public PieceType getType() {
            return type;
        }

        public boolean isFlipped() {
            return flipped;
        }

        public void setFlipped(boolean flipped) {
            this.flipped = flipped;
        }

        public ImageView getImageView() {
            return imageView;
        }

        public boolean isSelected() {
            return selected;
        }

        public void setSelected(boolean selected) {
            this.selected = selected;
            DropShadow borderEffect = new DropShadow();
            if(this.selected){
                borderEffect.setColor(Color.GREEN);
                borderEffect.setOffsetX(0);
                borderEffect.setOffsetY(0);
                borderEffect.setWidth(20);
                borderEffect.setHeight(20);
            }
            imageView.setEffect(borderEffect);
        }
    }

    @Override
    public void start(Stage primaryStage) {
        alertDialog();
        setupUI(primaryStage);

    }
    private CopyOnWriteArrayList<String> initPieceList = new CopyOnWriteArrayList<>();

    private void alertDialog() {
        Dialog<Pair<String, String>> dialog = new Dialog<>();
        dialog.setTitle("棋局初始化");
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));
        ChoiceBox<String> settingsChoiceBox0 = new ChoiceBox<>();
        CopyOnWriteArrayList<String> settingsChoices0 = new CopyOnWriteArrayList<>();
        settingsChoices0.add("我方执红(红方先手)");
        settingsChoices0.add("我方执黑(黑方后手)");
        ChoiceBox<String> choiceBox0 = new ChoiceBox<>();
        ChoiceBox<String> choiceBox1 = new ChoiceBox<>();
        ChoiceBox<String> choiceBox2 = new ChoiceBox<>();
        ChoiceBox<String> choiceBox3 = new ChoiceBox<>();

        List<String> choices0 = new CopyOnWriteArrayList<>();
        choices0.add("黑将");
        choices0.add("红帅");

        choices0.add("黑士");
//        choices0.add("黑士");
        choices0.add("红士");
//        choices0.add("红士");

        choices0.add("黑象");
//        choices0.add("黑象");
        choices0.add("红象");
//        choices0.add("红象");

        choices0.add("黑马");
//        choices0.add("黑马");
        choices0.add("红马");
//        choices0.add("红马");

        choices0.add("黑车");
//        choices0.add("黑车");
        choices0.add("红车");
//        choices0.add("红车");

        choices0.add("黑炮");
//        choices0.add("黑炮");
        choices0.add("红炮");
//        choices0.add("红炮");

        choices0.add("黑卒");
//        choices0.add("黑卒");
//        choices0.add("黑卒");
//        choices0.add("黑卒");
//        choices0.add("黑卒");
        choices0.add("红兵");
//        choices0.add("红兵");
//        choices0.add("红兵");
//        choices0.add("红兵");
//        choices0.add("红兵");
//        choices0.add("红兵");
        List<String> choices1 = new CopyOnWriteArrayList<>();
        choices1.addAll(choices0);
        List<String> choices2 = new CopyOnWriteArrayList<>();
        choices2.addAll(choices0);
        List<String> choices3 = new CopyOnWriteArrayList<>();
        choices3.addAll(choices0);
        choiceBox0.setItems(FXCollections.observableArrayList(choices0));
        choiceBox1.setItems(FXCollections.observableArrayList(choices1));
        choiceBox2.setItems(FXCollections.observableArrayList(choices2));
        choiceBox3.setItems(FXCollections.observableArrayList(choices3));

        settingsChoiceBox0.setItems(FXCollections.observableArrayList(settingsChoices0));
        settingsChoiceBox0.getSelectionModel().selectFirst();
        grid.add(choiceBox0,0,0);
        grid.add(choiceBox1,1,0);
        grid.add(choiceBox2,2,0);
        grid.add(choiceBox3,3,0);
        grid.add(settingsChoiceBox0,0,1,4,1);
        ButtonType initButtonType = new ButtonType("初始化", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(initButtonType, ButtonType.CANCEL);

        dialog.getDialogPane().setContent(grid);
        ObjectProperty<String> externalProperty0 = new SimpleObjectProperty<>();
        ObjectProperty<String> externalProperty1 = new SimpleObjectProperty<>();
        ObjectProperty<String> externalProperty2 = new SimpleObjectProperty<>();
        ObjectProperty<String> externalProperty3 = new SimpleObjectProperty<>();

        ObjectProperty<String> settingsExternalProperty0 = new SimpleObjectProperty<>();
        choiceBox0.valueProperty().bindBidirectional(externalProperty0);
        choiceBox1.valueProperty().bindBidirectional(externalProperty1);
        choiceBox2.valueProperty().bindBidirectional(externalProperty2);
        choiceBox3.valueProperty().bindBidirectional(externalProperty3);

        settingsChoiceBox0.valueProperty().bindBidirectional(settingsExternalProperty0);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == initButtonType) {
                String piece0 = externalProperty0.getValue();
                String piece1 = externalProperty1.getValue();
                String piece2 = externalProperty2.getValue();
                String piece3 = externalProperty3.getValue();
                return new Pair<>(settingsExternalProperty0.getValue(), piece0+","+piece1+","+piece2+","+piece3);
            }
            return null;
        });
        Optional<Pair<String, String>> result = dialog.showAndWait();
        if(result.isPresent()){
            result.ifPresent(initSelectedPiece -> {
                String firstMove = initSelectedPiece.getKey();
                if("我方执红(红方先手)".equals(firstMove)){
                    isFirstMove = true;
                }
                System.out.println("InitSelectedPiece=" + initSelectedPiece.getValue());
                String[] initSelectedPieceList = initSelectedPiece.getValue().split(",");
                boolean flag = false;
                for(int k = 0;k<initSelectedPieceList.length;k++){
                    if("null".equals(initSelectedPieceList[k])){
                        System.out.println("有未选择的选项，初始化失败，将使用默认配置进行初始化");
                        flag = true;
                        break;
                    }
                }
                if(flag){
                    initSelectedPieceList[0] = "黑卒";
                    initSelectedPieceList[1] = "红兵";
                    initSelectedPieceList[2] = "黑卒";
                    initSelectedPieceList[3] = "红兵";
                }
                initializeBoard(isFirstMove,convertPieceType(initSelectedPieceList[3]),convertPieceType(initSelectedPieceList[2]),convertPieceType(initSelectedPieceList[1]),convertPieceType(initSelectedPieceList[0]));

            });
        }else {
            String[] initSelectedPieceList = new String[4];
            initSelectedPieceList[0] = "黑卒";
            initSelectedPieceList[1] = "红兵";
            initSelectedPieceList[2] = "黑卒";
            initSelectedPieceList[3] = "红兵";
            System.out.println("有未选择的选项，初始化失败，将使用默认配置进行初始化");
            initializeBoard(false,convertPieceType(initSelectedPieceList[3]),convertPieceType(initSelectedPieceList[2]),convertPieceType(initSelectedPieceList[1]),convertPieceType(initSelectedPieceList[0]));
        }

    }
    private PieceType convertPieceType(String piece){
        if("黑卒".equals(piece)){
            return PieceType.BLACK_SOLDIER;
        }
        if("红兵".equals(piece)){
            return PieceType.RED_SOLDIER;
        }
        if("黑炮".equals(piece)){
            return PieceType.BLACK_CANNONS;
        }
        if("红炮".equals(piece)){
            return PieceType.RED_CANNONS;
        }
        if("黑车".equals(piece)){
            return PieceType.BLACK_ROOK;
        }
        if("红车".equals(piece)){
            return PieceType.RED_ROOK;
        }
        if("黑马".equals(piece)){
            return PieceType.BLACK_KNIGHT;
        }
        if("红马".equals(piece)){
            return PieceType.RED_KNIGHT;
        }
        if("黑象".equals(piece)){
            return PieceType.BLACK_ELEPHANT;
        }
        if("红象".equals(piece)){
            return PieceType.RED_ELEPHANT;
        }
        if("黑士".equals(piece)){
            return PieceType.BLACK_MANDARINS;
        }
        if("红士".equals(piece)){
            return PieceType.RED_MANDARINS;
        }
        if("黑将".equals(piece)){
            return PieceType.BLACK_KING;
        }
        if("红帅".equals(piece)){
            return PieceType.RED_KING;
        }
        return PieceType.UNFLIPPED;
    }
    private String convertPieceTypeToStr(PieceType piece){
        if( PieceType.BLACK_SOLDIER == piece){
            return "黑卒";
        }
        if(piece == PieceType.RED_SOLDIER){
            return "红兵";
        }
        if(piece == PieceType.BLACK_CANNONS){
            return "黑炮";
        }
        if(piece == PieceType.RED_CANNONS){
            return "红炮";
        }
        if(piece == PieceType.BLACK_ROOK){
            return "黑车";
        }
        if(piece == PieceType.RED_ROOK){
            return "红车";
        }
        if(piece == PieceType.BLACK_KNIGHT){
            return "黑马";
        }
        if(piece == PieceType.RED_KNIGHT){
            return "红马";
        }
        if(piece == PieceType.BLACK_ELEPHANT){
            return "黑象";
        }
        if(piece == PieceType.RED_ELEPHANT){
            return "红象";
        }
        if(piece == PieceType.BLACK_MANDARINS){
            return "黑士";
        }
        if(piece == PieceType.RED_MANDARINS){
            return "红士";
        }
        if(piece == PieceType.BLACK_KING){
            return "黑将";
        }
        if(piece == PieceType.RED_KING){
            return "红帅";
        }
        return "未知";
    }

    //初始化棋盘
    private void initializeBoard(boolean isFirstMove,PieceType type0,PieceType type1,PieceType type2,PieceType type3) {

        this.playerTurn = isFirstMove;
        this.isFirstMove = isFirstMove;
        if(this.playerTurn){
            currentPlayer = 1;
        }else{
            currentPlayer = 0;
        }
        pieces.clear();
        pieces.add(PieceType.RED_KING);
        pieces.add(PieceType.BLACK_KING);

        for(int i = 0;i<5;i++){
            //为双方添加兵和卒
            pieces.add(PieceType.RED_SOLDIER);
            pieces.add(PieceType.BLACK_SOLDIER);
        }
        for(int i=0;i<2;i++){
            //炮
            pieces.add(PieceType.RED_CANNONS);
            pieces.add(PieceType.BLACK_CANNONS);
            //车
            pieces.add(PieceType.RED_ROOK);
            pieces.add(PieceType.BLACK_ROOK);
            //马
            pieces.add(PieceType.RED_KNIGHT);
            pieces.add(PieceType.BLACK_KNIGHT);

            //象
            pieces.add(PieceType.RED_ELEPHANT);
            pieces.add(PieceType.BLACK_ELEPHANT);
            //士
            pieces.add(PieceType.RED_MANDARINS);
            pieces.add(PieceType.BLACK_MANDARINS);
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
                    PieceType t = pieces.get(28);
                    pieces.set(28,type0);
                    pieces.set(i,t);
                    flag0 = false;
                }
            }
            if(pieces.get(i) == type1){
                if(i != 29 && flag1){
                    //交换i 和 29
                    PieceType t = pieces.get(29);
                    pieces.set(29,type1);
                    pieces.set(i,t);
                    flag1 = false;
                }

            }
            if(pieces.get(i) == type2){
                if(i != 30 && flag2){
                    //交换i 和 30
                    PieceType t = pieces.get(30);
                    pieces.set(30,type2);
                    pieces.set(i,t);
                    flag2 = false;
                }

            }
            if(pieces.get(i) == type3){
                if(i != 31 && flag3){
                    //交换i 和 30
                    PieceType t = pieces.get(31);
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
                    board[row][col] = new ChessPiece(PieceType.UNFLIPPED);
                }

                index++;
            }
        }
        if(!this.isFirstMove){
            //AI选择
//            System.out.println("("+mRoot.x1+","+mRoot.y1+")=>("+mRoot.x2+","+mRoot.y2+")");
            aiMove();
        }
    }
    //初始化棋盘
    private void initializeBoard() {
        pieces.clear();
        pieces.add(PieceType.RED_KING);
        pieces.add(PieceType.BLACK_KING);

        for(int i = 0;i<5;i++){
            //为双方添加兵和卒
            pieces.add(PieceType.RED_SOLDIER);
            pieces.add(PieceType.BLACK_SOLDIER);
        }
        for(int i=0;i<2;i++){
            //炮
            pieces.add(PieceType.RED_CANNONS);
            pieces.add(PieceType.BLACK_CANNONS);
            //车
            pieces.add(PieceType.RED_ROOK);
            pieces.add(PieceType.BLACK_ROOK);
            //马
            pieces.add(PieceType.RED_KNIGHT);
            pieces.add(PieceType.BLACK_KNIGHT);

            //象
            pieces.add(PieceType.RED_ELEPHANT);
            pieces.add(PieceType.BLACK_ELEPHANT);
            //士
            pieces.add(PieceType.RED_MANDARINS);
            pieces.add(PieceType.BLACK_MANDARINS);
        }
        Collections.shuffle(pieces);
        pieces_point = pieces.size();
        for (int row = 0; row < CHESS_HEIGHT; row++) {
            for (int col = 0; col < CHESS_WIDTH; col++) {
                board[row][col] = new ChessPiece(PieceType.UNFLIPPED);
            }
        }
    }
    private ImageView clickChessPiece[][] = new ImageView[8][4];

    private void setupUI(Stage stage) {

        backgroundPlayerPane = new GridPane();
        backgroundPlayerPane.setPadding(new Insets(10));
        backgroundPlayerPane.setHgap(SPACING);
        backgroundPlayerPane.setVgap(SPACING);
        backgroundPlayerPane.setAlignment(Pos.CENTER);
        backgroundPlayerPane.setGridLinesVisible(true);

        topPlayerScoreText.setFont(Font.font(null,20));
        if(currentPlayer == 1){
            topPlayerScoreText.setFill(Color.BLACK);
        }else{
            topPlayerScoreText.setFill(Color.RED);
        }
        topPlayerScoreText.setText("Score:"+this.topPlayerScore);
        VBox topBox = new VBox(topPlayerScoreText);
//        topBox.setMaxSize(140,25);
        topBox.setMinWidth(80);
//        topBox.setFillWidth(true);
//        topBox.setMinWidth(300);

//        topPlayerScoreText.setOnMouseClicked(new EventHandler<MouseEvent>() {
//            @Override
//            public void handle(MouseEvent event) {
//                //点开设置
//            }
//        });
        bottomPlayerScoreText.setFont(Font.font(null,20));
        if(currentPlayer == 1){
            bottomPlayerScoreText.setFill(Color.RED);
        }else{
            bottomPlayerScoreText.setFill(Color.BLACK);
        }
        bottomPlayerScoreText.setText("Score:"+this.bottomPlayerScore);
        VBox bottomBox = new VBox(bottomPlayerScoreText);
//        bottomBox.setMaxSize(100,25);
        bottomBox.setMinWidth(80);
//        bottomBox.setMinWidth(300);

        for (int row = 0; row < CHESS_HEIGHT+2; row++) {
            for (int col = 0; col < CHESS_WIDTH+2; col++) {

                if(row == 0 && col == 0){
//                    backgroundPlayerPane.add(topPlayerScoreText,col,row);

                    backgroundPlayerPane.add(topBox,col,row);
                }else if(row == CHESS_HEIGHT+1 && col == CHESS_WIDTH+1){

//                    backgroundPlayerPane.add(bottomPlayerScoreText,col,row);
                    backgroundPlayerPane.add(bottomBox,col,row);
                }else if(row>=1 && row <= CHESS_HEIGHT && col >= 1 && col <= CHESS_WIDTH){
                    clickChessPiece[row-1][col-1] = new ImageView();

                    clickChessPiece[row-1][col-1].setOnMouseClicked(this::handleClick);
                    clickChessPiece[row-1][col-1].setPickOnBounds(true);
                    clickChessPiece[row-1][col-1].setFitWidth(TILE_SIZE);
                    clickChessPiece[row-1][col-1].setFitHeight(TILE_SIZE);

                    backgroundPlayerPane.add(clickChessPiece[row-1][col-1],col,row);
                }else{

                }
            }
        }



        chessBoard = new GridPane();
        chessBoard.setPadding(new Insets(10));
        chessBoard.setHgap(SPACING);
        chessBoard.setVgap(SPACING);
        chessBoard.setGridLinesVisible(true);
        chessBoard.setAlignment(Pos.CENTER);




        for (int row = 0; row < CHESS_HEIGHT; row++) {
            for (int col = 0; col < CHESS_WIDTH; col++) {
                ChessPiece piece = board[row][col];
//                piece.imageView.setOnMouseClicked(this::handleClick);
                chessBoard.add(piece.imageView, col, row);
            }
        }

        Image bgImage = new Image(ASSETS_IMAGE_PATH+"Background.jpg");
        BackgroundImage backgroundImage= new BackgroundImage(bgImage, BackgroundRepeat.NO_REPEAT,BackgroundRepeat.NO_REPEAT,BackgroundPosition.CENTER,BackgroundSize.DEFAULT);
        chessBoard.setBackground(new Background(backgroundImage));


        StackPane root = new StackPane();
        //下面是显示层，上面是控制层
        root.getChildren().addAll(chessBoard,backgroundPlayerPane);
        Scene scene = new Scene(root);
        
        stage.setTitle("翻翻棋");
        stage.setScene(scene);
        stage.show();
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
    private Main main;
    //点击事件处理
    private void handleClick(MouseEvent event) {
        ImageView clickedImage = (ImageView) event.getSource();
        Integer colIndex = GridPane.getColumnIndex(clickedImage);
        Integer rowIndex = GridPane.getRowIndex(clickedImage);
        if (colIndex != null && rowIndex != null) {
            //判断当前这格的状态是否翻开
            Position selectPosition = getSelectedPosition();
            int selectedRowIndex = selectPosition.row;
            int selectedColIndex = selectPosition.col;
            this.apply(selectedRowIndex,selectedColIndex,rowIndex,colIndex,false);
        }

    }
    private void apply(int selectedRowIndex,int selectedColIndex,int rowIndex,int colIndex,boolean isAiMove){
        if(isPlayerWin1){
            alert("AI胜利");
            return;
        }
        if(isPlayerWin2){
            alert("玩家胜利");
            return;
        }
        if(isDraw){
            alert("和棋了");
            return;
        }
        if(!isAiMove){
            if(!playerTurn){
                alert("当前非玩家回合，请等待AI思考完成");
                return;
            }
        }





        if (colIndex != -1 && rowIndex != -1) {
            if(isAiMove){

            }else{
                colIndex = colIndex - 1;
                rowIndex = rowIndex - 1;
            }

            if(rowIndex < 0 || colIndex<0){
                return;
            }
            System.out.println("click:["+colIndex+","+rowIndex+"]"+convertPieceTypeToStr(board[rowIndex][colIndex].getType()));
            if(board[rowIndex][colIndex].getType() == PieceType.EMPTY){

                if(selectedColIndex >= 0 && board[selectedRowIndex][selectedColIndex].getType() != PieceType.BLACK_CANNONS && board[selectedRowIndex][selectedColIndex].getType() != PieceType.RED_CANNONS){
                    //判断是否可以移动
                    if(Math.abs(selectedRowIndex - rowIndex) + Math.abs(selectedColIndex - colIndex) == 1){
                        board[rowIndex][colIndex].setType(board[selectedRowIndex][selectedColIndex].getType());
                        board[rowIndex][colIndex].flipped = true;
                        board[selectedRowIndex][selectedColIndex].setType(PieceType.EMPTY);
                        board[selectedRowIndex][selectedColIndex].flipped = true;
                        peace_move_step ++;
                        if(peace_move_step > CHESS_MAX_DRAW_STEP){
                            isDraw = true;
                            isPlayerWin1 = false;
                            isPlayerWin2 = false;

                        }
                        if(peace_move_step >= 14){
                            System.out.println("还有"+(CHESS_MAX_DRAW_STEP-peace_move_step)+"步和棋");
                        }
                        playerTurn = !playerTurn;
                        if(this.playerTurn){
                            currentPlayer = 1;
                        }else{
                            currentPlayer = 0;
                            aiMove();


                        }
                        return;
                    }

                }else{
                    System.out.println("空子");
                }
                return;
            }

            //判断是否在可打击范围之内
            if(!board[rowIndex][colIndex].isFlipped()){
                //判断是否存在已经选中棋子



                if(selectedRowIndex < 0){


                    CopyOnWriteArrayList<PieceType> pieceTypes = new CopyOnWriteArrayList<>();
                    for(int i = 0 ;i<pieces_point;i++){
                        pieceTypes.add(pieces.get(i));
                    }
                    PieceType t = chooseDialog(pieceTypes,"请选择翻开棋子类型:");
                    if(t == PieceType.EMPTY){
                        //未选择任何数据
                        System.out.println("未选择棋子类型");
                        return;
                    }
                    if(t != pieces.get(pieces_point)){
                        //交换
                        for(int k = 0;k<pieces_point;k++){
                            if(pieces.get(k) == t){
                                pieces.set(k,pieces.get(pieces_point));
                                pieces.set(pieces_point,t);
                                break;
                            }
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
                        aiMove();
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

                    System.out.println("取消选中");

                }else{

                    //先检查选中的棋子是否为我方棋子
                    String pieceTypeStr = convertPieceTypeToStr(board[rowIndex][colIndex].type);
                    if(currentPlayer == 1){
                        //玩家执红
                        if(pieceTypeStr.indexOf("黑") >= 0 ){
                            if(selectedRowIndex >= 0){
                                attack(rowIndex,colIndex,selectedRowIndex,selectedColIndex);
                            }else{
                                System.out.println("不能选中别人的棋子");
                                alert("不能选中别人的棋子");
                            }

                            return;
                        }
                    }else{
                        //玩家执黑
                        if(pieceTypeStr.indexOf("红") >= 0){
//                            System.out.println("不能选中别人的棋子");
                            if(selectedRowIndex >= 0){
                                attack(rowIndex,colIndex,selectedRowIndex,selectedColIndex);

                            }else{
                                System.out.println("不能选中别人的棋子");
                            }

                            return;
                        }
                    }
                    unselectPiece();
                    board[rowIndex][colIndex].setSelected(true);
                    //将选中棋子的可攻击对象放入列表中
                    calcSelectedAttackList(rowIndex,colIndex);
                    System.out.println("选中");

                }
            }
        }else{
            System.out.println("点击空处");
        }
    }
    //提示
    private void alert(String msg){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("提示");
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
    //带棋子选项的提示
    private PieceType chooseDialog(CopyOnWriteArrayList<PieceType> list,String msg){
        CopyOnWriteArrayList<String> pieceChooseList = new CopyOnWriteArrayList<>();
        for(int i = 0;i<list.size();i++){
            pieceChooseList.add(convertPieceTypeToStr(list.get(i)));
        }
        ChoiceDialog<String> dialog = new ChoiceDialog<>(pieceChooseList.get(0), pieceChooseList);
        dialog.setTitle("提示");
        dialog.setContentText(msg);
        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()){
            System.out.println("你选择的是: " + result.get());
            return convertPieceType(result.get());
        }else{
            return PieceType.EMPTY;
        }

    }
    //炮攻击逻辑
    private void attackCannon(int rowIndex,int colIndex,int selectedRowIndex,int selectedColIndex){
        PieceType type = board[selectedRowIndex][selectedColIndex].getType();//炮
        PieceType type2 = board[rowIndex][colIndex].getType();//其它类型
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
            System.out.println("不在攻击范围");
            return;
        }
        boolean flipped = false;
        if(!board[rowIndex][colIndex].flipped){

            //没翻开，需要弹框让用户选择
            CopyOnWriteArrayList<PieceType> pieceTypes = new CopyOnWriteArrayList<>();
            for(int i = 0 ;i<pieces_point;i++){
                pieceTypes.add(pieces.get(i));
            }

            type2 = chooseDialog(pieceTypes,"请选择被击杀的棋子类型:");
            if(type2 == PieceType.EMPTY){
                //未选择任何数据
                System.out.println("未选择棋子类型");
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
                board[selectedRowIndex][selectedColIndex].setType(PieceType.EMPTY);
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
                        System.out.println("击杀:"+pt+"*1,得分:-"+score);
                        bottomPlayerScoreText.setText("Score:"+bottomPlayerScore);
                        if(bottomPlayerScore <= 0){
                            this.isPlayerWin1 = true;
                        }

                    }else{
                        topPlayerScore = topPlayerScore - score;
                        System.out.println("击杀:"+pt+"*1,得分:+"+score);
                        topPlayerScoreText.setText("Score:"+topPlayerScore);
                        if(topPlayerScore <= 0){
                            this.isPlayerWin2 = true;
                        }
                    }



                }else {
                    if(pt.indexOf("黑") >= 0){
                        topPlayerScore = topPlayerScore - score;
                        System.out.println("击杀:"+pt+"*1,得分:-"+score);
                        topPlayerScoreText.setText("Score:"+topPlayerScore);

                        if(topPlayerScore <= 0){
                            this.isPlayerWin2 = true;
                        }
                    }else{
                        bottomPlayerScore = bottomPlayerScore - score;
                        System.out.println("击杀:"+pt+"*1,得分:+"+score);
                        bottomPlayerScoreText.setText("Score:"+bottomPlayerScore);
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
                    aiMove();
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
        PieceType type = board[selectedRowIndex][selectedColIndex].getType();
        if(type == PieceType.EMPTY){
            return;
        }
        if(type == PieceType.BLACK_CANNONS || type == PieceType.RED_CANNONS){
            attackCannon(rowIndex,colIndex,selectedRowIndex,selectedColIndex);
        }else{
            attackOther(rowIndex,colIndex,selectedRowIndex,selectedColIndex);

        }


    }

    private void attackOther(int rowIndex, int colIndex, int selectedRowIndex, int selectedColIndex) {
        PieceType type = board[selectedRowIndex][selectedColIndex].getType();
        if(type == PieceType.EMPTY){
            return;
        }
        if(!board[rowIndex][colIndex].flipped){
            unselectPiece();
        }else{
            for(int i = 0;i<selectedAttackList.size();i++){
                Position attackPosition = selectedAttackList.get(i);

                if(rowIndex == attackPosition.row && colIndex == attackPosition.col){
                    //攻击完成，取消选中
                    PieceType temp =  board[selectedRowIndex][selectedColIndex].getType();
                    type = board[rowIndex][colIndex].getType();
                    board[selectedRowIndex][selectedColIndex].setType(PieceType.EMPTY);
                    board[selectedRowIndex][selectedColIndex].flipped = true;
                    board[rowIndex][colIndex].setType(temp);

                    int score = calcPieceTypeScore(type);

                    String pt = convertPieceTypeToStr(type);

                    //计算分数，并减去对应的分数
                    if(currentPlayer == 1){
                        if(pt.indexOf("红") >= 0){
                            bottomPlayerScore = bottomPlayerScore - score;
                            System.out.println("击杀:"+pt+"*1,得分:-"+score);
                            bottomPlayerScoreText.setText("Score:"+bottomPlayerScore);

                        }else{
                            topPlayerScore = topPlayerScore - score;
                            System.out.println("击杀:"+pt+"*1,得分:+"+score);
                            topPlayerScoreText.setText("Score:"+topPlayerScore);
                        }



                    }else {
                        if(pt.indexOf("黑") >= 0){
                            topPlayerScore = topPlayerScore - score;
                            System.out.println("击杀:"+pt+"*1,得分:-"+score);
                            topPlayerScoreText.setText("Score:"+topPlayerScore);
                        }else{
                            bottomPlayerScore = bottomPlayerScore - score;
                            System.out.println("击杀:"+pt+"*1,得分:+"+score);
                            bottomPlayerScoreText.setText("Score:"+bottomPlayerScore);
                        }

                    }
                    peace_move_step = 0;
                    playerTurn = !playerTurn;
                    if(this.playerTurn){
                        currentPlayer = 1;
                    }else{
                        currentPlayer = 0;
                        aiMove();
                    }
                }

            }
        }
    }

    //根据棋子类型计算分值
    private int calcPieceTypeScore(PieceType type){
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

        if(board[row][col].getType() == PieceType.BLACK_CANNONS || board[row][col].getType() == PieceType.RED_CANNONS){
            //游标向上下左右移动
            //left
            int pivotCount = 0;
            if(cursor.col - 2 >= 0){

                for(int k = cursor.col-1;k>=0;k--){
                    //board[cursor.row][k]
                    if(board[cursor.row][k].getType() != PieceType.EMPTY){
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
                    if(board[cursor.row][k].getType() != PieceType.EMPTY){
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
                    if(board[k][cursor.col].getType() != PieceType.EMPTY){
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
                    if(board[k][cursor.col].getType() != PieceType.EMPTY){
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
            //left
            if(cursor.row - 1 >= 0 && board[cursor.row-1][cursor.col].flipped){
                System.out.println("2"+(cursor.row - 1 > 0));
                compareResult = comparePieceType(board[cursor.row][cursor.col].getType(),board[cursor.row-1][cursor.col].getType());

                if(compareResult){//可以吃子，加入待攻击列表

                    selectedAttackList.add(new Position(cursor.row-1, cursor.col));
                }
            }
            //right
            compareResult = false;
            if(cursor.row + 1 < CHESS_HEIGHT && board[cursor.row+1][cursor.col].flipped){
                compareResult = comparePieceType(board[cursor.row][cursor.col].getType(),board[cursor.row+1][cursor.col].getType());
                if(compareResult){//可以吃子，加入待攻击列表
                    selectedAttackList.add(new Position(cursor.row+1, cursor.col));
                }
            }
            //down
            if(cursor.col - 1 >= 0 && board[cursor.row][cursor.col-1].flipped){
                compareResult = comparePieceType(board[cursor.row][cursor.col].getType(),board[cursor.row][cursor.col-1].getType());

                if(compareResult){//可以吃子，加入待攻击列表
                    selectedAttackList.add(new Position(cursor.row, cursor.col-1));
                }
            }
            //up
            compareResult = false;
            if(cursor.col + 1 < CHESS_WIDTH && board[cursor.row][cursor.col+1].flipped){
                compareResult = comparePieceType(board[cursor.row][cursor.col].getType(),board[cursor.row][cursor.col+1].getType());
                if(compareResult){//可以吃子，加入待攻击列表
                    selectedAttackList.add(new Position(cursor.row, cursor.col+1));
                }
            }



        }
        for(int i = 0;i<selectedAttackList.size();i++){
            System.out.println(selectedAttackList.get(i).toString());
        }


    }
    private void unselectPiece(){
        //给其它所有棋子设置非选中状态
        for (int row = 0; row < CHESS_HEIGHT; row++) {
            for (int col = 0; col < CHESS_WIDTH; col++) {
                if(board[row][col].getType() != PieceType.EMPTY){
                    board[row][col].setSelected(false);
                }

            }
        }
        selectedAttackList.clear();
    }

    private void flipPiece(int row, int col,PieceType type) {
        ChessPiece piece = board[row][col];
        piece.type = type;
        if (!piece.isFlipped()) {
            piece.setFlipped(true);
            piece.imageView.setImage(getImage(piece.type));
        }
    }

    private void aiMove() {
        if(isPlayerWin1){
            alert("AI胜利");
            return;
        }
        if(isPlayerWin2){
            alert("玩家胜利");
            return;
        }
        if(isDraw){
            alert("和棋了");
            return;
        }
        main = this;
        //开始思考
        new Thread(()-> {
                //1.创建拷贝计算对象
                System.out.println("AI开始思考");
                FlipGame cloneFlipGame = new FlipGame();
                cloneFlipGame.initGame(main);
                MonteCarloTreeSearch search = new MonteCarloTreeSearch(4);
                Node bestMove = search.findBestMove(cloneFlipGame);
            Platform.runLater(() -> {
                if(bestMove != null){
                    System.out.println("AI选择:"+bestMove.getMove().toString());
                    if(bestMove.getMove().x1 >= 0){
                        main.apply(-1,-1,bestMove.getMove().x1,bestMove.getMove().y1,true);
                    }
                    main.apply(bestMove.getMove().x1,bestMove.getMove().y1,bestMove.getMove().x2,bestMove.getMove().y2,true);
//
                }else {
//                    System.out.println("AI未找到合适解法");
                    alert("AI未找到合适解法");
                    if(isPlayerWin1){
                        alert("AI胜利");
                        return;
                    }
                    if(isPlayerWin2){
                        alert("玩家胜利");
                        return;
                    }
                    if(isDraw){
                        alert("和棋了");
                        return;
                    }
                }

            });



        }).start();
    }

    private static Image getImage(PieceType type) {
        String filename = "";
        switch (type) {
            case BLACK_KING ://将
                filename = "BlackKing.png";
                break;
            case BLACK_MANDARINS://士
                filename = "BlackMandarins.png";
                break;
            case BLACK_ELEPHANT://象
                filename = "BlackElephant.png";
                break;
            case BLACK_KNIGHT://马
                filename = "BlackKnight.png";
                break;
            case BLACK_ROOK://车
                filename = "BlackRook.png";
                break;
            case BLACK_CANNONS://炮
                filename = "BlackCannons.png";
                break;
            case BLACK_SOLDIER://卒
                filename = "BlackSoldier.png";
                break;
            case RED_KING: //帅
                filename = "RedKing.png";
                break;
            case RED_MANDARINS://士
                filename = "RedMandarins.png";
                break;
            case RED_ELEPHANT://象
                filename = "RedElephant.png";
                break;
            case RED_KNIGHT://马
                filename = "RedKnight.png";
                break;
            case RED_ROOK://马
                filename = "RedRook.png";
                break;
            case RED_CANNONS://炮
                filename = "RedCannons.png";
                break;
            case RED_SOLDIER://兵
                filename = "RedSoldier.png";
                break;
            default :
                filename = "Item.png";
            break;
        };
        return new Image(ASSETS_IMAGE_PATH + filename);
    }
    private boolean compareBlackKing(PieceType type){
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
    private boolean compareRedMandarins(PieceType type){
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


    private boolean compareBlackMandarins(PieceType type){
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


    private boolean compareRedElephant(PieceType type){
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


    private boolean compareBlackElephant(PieceType type){
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

    private boolean compareRedKnight(PieceType type){
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


    private boolean compareBlackKnight(PieceType type){
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

    private boolean compareRedRook(PieceType type){
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


    private boolean compareBlackRook(PieceType type){
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

    private boolean compareRedCannons(PieceType type){
        return true;
    }


    private boolean compareBlackCannons(PieceType type){
       return true;
    }

    private boolean compareRedSoldier(PieceType type){
        switch (type) {
            case BLACK_KING://将
                return true;
            case BLACK_SOLDIER://兵
                return true;
            default :
                return false;
        }
    }


    private boolean compareBlackSoldier(PieceType type){
        switch (type) {
            case RED_KING://帅
                return true;
            case RED_SOLDIER://兵
                return true;
            default :
                return false;
        }
    }
    private boolean compareRedKing(PieceType type){
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
    private boolean comparePieceType(PieceType type1,PieceType type2){


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

    public static void main(String[] args) {
        launch(args);
    }

    // 蒙特卡洛树搜索实现
    private static class MCTS {
        private final ChessPiece[][] originalBoard;

        MCTS(ChessPiece[][] board) {
            this.originalBoard = deepCopy(board);
        }

        Position findBestMove(int iterations) {
            Map<Position, Double> moveScores = new HashMap<>();
            List<Position> possibleMoves = getPossibleMoves();

            for (int i = 0; i < iterations; i++) {
                Position move = possibleMoves.get(i % possibleMoves.size());
                double score = simulateMove(move);
                moveScores.put(move, moveScores.getOrDefault(move, 0.0) + score);
            }

            return Collections.max(moveScores.entrySet(),
                    Map.Entry.comparingByValue()).getKey();
        }

        private double simulateMove(Position move) {
            ChessPiece[][] simulatedBoard = deepCopy(originalBoard);
            // 实现模拟逻辑...
            return Math.random(); // 示例返回值
        }

        private List<Position> getPossibleMoves() {
            List<Position> moves = new CopyOnWriteArrayList<>();
            for (int row = 0; row < CHESS_HEIGHT; row++) {
                for (int col = 0; col < CHESS_WIDTH; col++) {
                    if (!originalBoard[row][col].isFlipped()) {
                        moves.add(new Position(row, col));
                    }
                }
            }
            return moves;
        }

        private ChessPiece[][] deepCopy(ChessPiece[][] original) {
            ChessPiece[][] copyChessPiece = new ChessPiece[CHESS_HEIGHT][CHESS_WIDTH];
            for(int row = 0;row<CHESS_HEIGHT;row++){
                for(int col = 0;col< CHESS_WIDTH;col++){
                    copyChessPiece[row][col] = new ChessPiece(original[row][col].getType(),original[row][col].isFlipped(),original[row][col].getImageView());
                }
            }
            return original;
        }
    }


}
