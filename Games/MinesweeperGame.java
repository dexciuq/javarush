//package com.javarush.games.minesweeper;

import com.javarush.engine.cell.*;
import java.util.*;

public class MinesweeperGame extends Game{

    private final static int SIDE = 9;

    private int countClosedTiles = SIDE * SIDE;

    private int score = 0;

    private final static String MINE = "\uD83D\uDCA3";

    private final static String FLAG = "\uD83D\uDEA9";

    private int countMinesOnField;

    private int countFlags;

    private boolean isGameStopped;

    private GameObject[][] gameField = new GameObject[SIDE][SIDE];

    private void createGame(){
        for (int i = 0; i < SIDE; i++){
            for (int j = 0; j < SIDE; j++){
                setCellValue(j, i, "");
                setCellColor(j, i, Color.SKYBLUE);
                if (getRandomNumber(10) == 0){
                    gameField[j][i] = new GameObject(i, j, true);
                    ++countMinesOnField;
                } else {
                    gameField[j][i] = new GameObject(i, j, false);
                }
            }
        }
        countFlags = countMinesOnField;
        countMineNeighbors();
    }

    private void countMineNeighbors(){
        for (int i = 0; i < SIDE; i++){
            for (int j = 0; j < SIDE; j++){
                if (!gameField[j][i].isMine){
                    List<GameObject> neightbors = getNeighbors(gameField[j][i]);
                    for (int k = 0; k < neightbors.size(); k++){
                        if (neightbors.get(k).isMine) gameField[j][i].countMineNeighbors++;
                    }

                }
            }
        }
    }

    public List<GameObject> getNeighbors(GameObject gameObject){
        List<GameObject> neightbors = new ArrayList<>();
        for (int y = gameObject.y - 1; y <= gameObject.y + 1; y++) {
            for (int x = gameObject.x - 1; x <= gameObject.x + 1; x++) {
                if (y < 0 || y >= SIDE) continue;
                if (x < 0 || x >= SIDE) continue;
                if (gameField[y][x] == gameObject) continue;
                neightbors.add(gameField[y][x]);
            }
        }
        return neightbors;
    }

    private void openTile(int x, int y){
        if (isGameStopped || gameField[y][x].isOpen || gameField[y][x].isFlag) return;
        if (!gameField[y][x].isMine) {
            gameField[y][x].isOpen = true;
            --countClosedTiles;
            score += 5;
            setScore(score);
            setCellColor(x, y, Color.WHITE);
            if (gameField[y][x].countMineNeighbors == 0) {
                setCellValue(x, y, "");
                List<GameObject> neightbors = getNeighbors(gameField[y][x]);
                for (int i = 0; i < neightbors.size(); i++){
                    if (!neightbors.get(i).isOpen) {
                        openTile(neightbors.get(i).x, neightbors.get(i).y);
                    }
                }

            } else {
                setCellNumber(x, y, gameField[y][x].countMineNeighbors);
            }
        } else {
            setCellValueEx(x, y, Color.RED, MINE);
            gameOver();
        }
        if (countClosedTiles == countMinesOnField) win();
    }

    private void markTile(int x, int y){
        if (!isGameStopped){
            if (!gameField[y][x].isOpen){
                if (countFlags == 0 && !gameField[y][x].isFlag) return;
                else if (gameField[y][x].isFlag){
                    setCellValue(x, y, "");
                    setCellColor(x, y, Color.SKYBLUE);
                    gameField[y][x].isFlag = false;
                    ++countFlags;
                }
                else if (!gameField[y][x].isFlag) {
                    setCellValue(x, y, FLAG);
                    setCellColor(x, y, Color.YELLOW);
                    gameField[y][x].isFlag = true;
                    --countFlags;
                }
            }
        }
    }

    private void printMines(){
        for (int i = 0; i < SIDE; i++){
            for (int j = 0; j < SIDE; j++){
                if (gameField[j][i].isMine){
                    setCellValueEx(i, j, Color.RED, MINE);
                }
            }
        }
    }

    @Override
    public void onMouseLeftClick(int x, int y){
        if (isGameStopped) restart();
        else openTile(x, y);
    }

    @Override
    public void onMouseRightClick(int x, int y){
        markTile(x, y);
    }

    private void restart(){
        isGameStopped = false;
        countClosedTiles = SIDE * SIDE;
        countMinesOnField = 0;
        score = 0;
        setScore(score);
        createGame();
    }

    private void gameOver(){
        isGameStopped = true;
        printMines();
        showMessageDialog(Color.BLACK, "\nGAME OVER!!!\tYour score: " + score, Color.WHITE, 25);
    }

    private void win(){
        isGameStopped = true;
        showMessageDialog(Color.BLACK, "\nCongratulations, WINNER!!!\tYour score: " + score, Color.WHITE, 25);
    }

    @Override
    public void initialize(){
        setScreenSize(SIDE, SIDE);
        createGame();
    }

}
